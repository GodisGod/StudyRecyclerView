package study.com.purerecyclerview.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import study.com.purerecyclerview.customview.creator.DefaultRefreshHeadViewCreator;
import study.com.purerecyclerview.customview.creator.OnRefreshHeadViewCreator;
import study.com.purerecyclerview.customview.interfaces.OnRefreshListener;
import study.com.purerecyclerview.freshlayout.adapter.HeadAndFootAdapter;
import study.com.purerecyclerview.util.LogUtil;

/**
 * Created by  鸿达 on 2019/2/21.
 */
public class RefreshRecyclerView extends RecyclerView {

    //默认状态
    private int curState = STATE_DEFAULT;
    //    初始
    private final static int STATE_DEFAULT = 0;
    //    正在上拉
    private final static int STATE_PULLING = 1;
    //    松手加载
    private final static int STATE_RELEASE_TO_REFRESH = 2;
    //    加载中
    private final static int STATE_LOADING = 3;

    private HeadAndFootAdapter headAndFootAdapter;
    private Adapter realAdapter;

    private View refreshView;
    private View topView;

    //用于测量加载View的高度
    private int refreshViewHeight = 0;

    private float downY = 0;
    private float distance = 0;
    //是否在顶部,每次顶部动画结束需要重置
    private boolean isTop = false;
    //滑动距离和头部view下拉高度的比率，默认是0.5
    private float ratio = 0.5f;

    //滑动的最小距离
    private int touchSlope;
    //最大滑动距离
    private static int head_height_max;
    //回弹动画
    private ValueAnimator anim;
    //动画时间长度
    private static final long ANIM_TIME = 200;

    //上一状态：用于记录滑动过程中,底部临界动画的触发
    private int lastState = STATE_DEFAULT;

    private OnRefreshHeadViewCreator onRefreshHeadViewCreator;
    private OnRefreshListener refreshListener;

    public RefreshRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (topView == null) {
            topView = new View(context);
            ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
            //该view的高度不能为0，否则将无法判断是否已滑动到顶部
            topView.setLayoutParams(layoutParams);
            // 设置默认LayoutManager
            setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            //获取顶部刷新view
            onRefreshHeadViewCreator = new DefaultRefreshHeadViewCreator();
            refreshView = onRefreshHeadViewCreator.getRefreshView(context, this);
        }

        touchSlope = ViewConfiguration.get(getContext()).getScaledTouchSlop();//这个值其实是8
        setItemAnimator(null);//不使用动画
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        realAdapter = adapter;
        headAndFootAdapter = new HeadAndFootAdapter(realAdapter);
        super.setAdapter(headAndFootAdapter);
        if (refreshView != null) {
            headAndFootAdapter.addHeadView(topView);
            headAndFootAdapter.addHeadView(refreshView);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (refreshView != null && refreshViewHeight == 0) {
            refreshView.measure(0, 0);
            refreshViewHeight = refreshView.getLayoutParams().height;
            head_height_max = refreshViewHeight * 4;
            //初始化loadView的位置，将view置于界面之外
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin - refreshViewHeight - 1, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
            setLayoutParams(marginLayoutParams);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        //正在执行动画不处理
        if (anim != null && anim.isRunning()) {
            return super.onTouchEvent(e);
        }
        //正在加载数据不处理
        if (curState == STATE_LOADING) {
            return super.onTouchEvent(e);
        }

        if (refreshView == null) return super.onTouchEvent(e);

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curState = STATE_DEFAULT;
                LogUtil.i("LHD  refresh MotionEvent.ACTION_DOWN " + isTop());
                isTop = isTop();//记录是否处理抬起的手势
                if (isTop) {//如果沒有滑动到底部不处理
                    downY = e.getRawY();
                } else {
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTop) return super.onTouchEvent(e);
                float dy = (e.getRawY() - downY) * ratio;
//                LogUtil.i("LHD refresh 计算dy1 = " + dy + "   curState = " + curState + "   downY = " + downY);
                if (dy > 0) {
                    dy = Math.min(head_height_max, Math.abs(dy));
                    dy = Math.max(0, Math.abs(dy));
                    if (dy < touchSlope) return super.onTouchEvent(e);
                    //滑动footView
                    scollHead(dy);
                    //当手指先向下滑动再向上滑动的时候,topView的高度变化了，但同时recyclerView也在向上位移
                    //为了始终保持recyclerView在最顶部，所以需要同步位移recyclerView，来保持topView的顶部始终在(marginLayoutParams.topMargin - refreshViewHeight - 1)的位置
                    //这样可以抵消recyclerView本身移动的影响，使recyclerView的位置只受topView的高度影响
                    scrollToPosition(0);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //如果不是滑动到底部的抬起动作，则不处理
                LogUtil.i("refresh  MotionEvent.ACTION_UP + " + isTop);
                if (isTop) {
                    float dy2 = (e.getRawY() - downY) * ratio;
                    if (dy2 < 0) break;//如果是滑动到底部以后，再往上滑动，则不处理
                    distance = Math.abs(dy2);
                    LogUtil.i("refresh refresh 计算dy3 = " + distance);
                    startAction(distance);
                }
                break;
        }
//        LogUtil.i("LHD 传递触摸事件");
        return super.onTouchEvent(e);
    }

    /**
     * 根据上拉距离检查实时状态
     *
     * @param footHeight loadViewHeight不可为0
     *                   footHeight = 0 的时候是 STATE_DEFAULT
     *                   0 < footHeight < refreshViewHeight 的时候是 STATE_PULLING
     *                   footHeight > refreshViewHeight       的时候是 STATE_RELEASE_TO_REFRESH
     * @return 返回当前状态
     */
    private int checkState(float footHeight) {
        if (footHeight >= refreshViewHeight) {
            //释放刷新
            curState = STATE_RELEASE_TO_REFRESH;
        } else if (footHeight > 1) {
            //下拉刷新
            curState = STATE_PULLING;
        } else {
            curState = STATE_DEFAULT;
            //重置标志位
            isTop = false;
        }
        return curState;
    }

    /**
     * 核心方法
     * 根据上拉的距离判断状态，执行不同的动画
     * 状态1：上拉中未触发刷新，上拉->刷新->结束刷新
     * 状态2：触发刷新，上拉->结束刷新
     */
    private void startAction(float distance) {
        LogUtil.i("startAction = " + curState + "   distance = " + distance + "   refreshViewHeight = " + refreshViewHeight);
        checkState(distance);
        LogUtil.i("startAction curState = " + curState + "   refreshListener = " + refreshListener);
        if (curState == STATE_PULLING) {   //回到起始位置，pull->default
            createAnimatorTranslationY((int) distance, 1);
        } else if (curState == STATE_RELEASE_TO_REFRESH) {//进入刷新状态，release->loading
            curState = STATE_LOADING;
            createAnimatorTranslationY((int) distance, refreshViewHeight);
            if (refreshListener != null) {
                refreshListener.refreshLoding();
            }
        }
    }

    private ViewGroup.LayoutParams topLp;

    /**
     * 改变topView高度
     *
     * @param footHeight footHeight的高度决定了topView的高度，所以不可随意设置
     */
    private void scollHead(float footHeight) {
        //该view的高度不能为0，否则将无法判断是否已滑动到底部
        if (footHeight < 1) footHeight = 1;
        if (topView != null) {
            LogUtil.i("LHD 设置topView的高度 = " + footHeight);
            topLp = topView.getLayoutParams();
            topLp.height = (int) footHeight;
            topView.setLayoutParams(topLp);
        }
        checkState(footHeight);
        //由于触摸点采集的问题，dy可能直接从100变到102,所以不可以通过 if(footHeight == refreshViewHeight)来判断临界点
        //而应该使用状态的变化来判断临界点
        if (curState != lastState) {//状态不一致则执行临界点动画
//            LogUtil.i("LHD 判断临界点动画 = " + footHeight + "   " + curState + "   refreshViewHeight = " + refreshViewHeight);
            onRefreshHeadViewCreator.executeAnim(curState);
        }
        //记录状态
        lastState = curState;
    }

    /**
     * 判断是否滑动到底部
     */
    private boolean isTop() {
        return !ViewCompat.canScrollVertically(this, -1);
    }

    /**
     * 创建回弹动画
     *
     * @param start TopView的起始高度
     * @param end   TopView的结束高度为1,该view的高度不能为0，否则将无法判断是否已滑动到底部
     *              start - end = refreshView露出高度的变化值
     */
    private void createAnimatorTranslationY(final int start, final int end) {
        //正在执行动画不处理
        if (anim != null && anim.isRunning()) {
            anim.end();
            anim.cancel();
            anim = null;
        }
        anim = ValueAnimator.ofInt(start, end);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                LogUtil.i("refresh 正在执行回弹动画 = " + value + "   state = " + curState + "  start = " + start + "  end = " + end);
                value = Math.max(1, value);
                scollHead(value);
                if (value == end) {
//                    LogUtil.i("LHD 动画结束时的状态 = " + curState);
                    if (curState == STATE_DEFAULT) {   //重置底部UI
                        onRefreshHeadViewCreator.finishRefresh();
                    } else { //改变底部UI为刷新中
                        onRefreshHeadViewCreator.loading();
                    }
                }
                requestLayout();
            }
        });
        anim.start();
    }

    /**
     * 结束刷新
     */
    public void finishRefresh() {
        headAndFootAdapter.notifyDataSetChanged();
        createAnimatorTranslationY(refreshViewHeight, 1);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.refreshListener = onRefreshListener;
    }
}
