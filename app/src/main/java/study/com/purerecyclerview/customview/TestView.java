package study.com.purerecyclerview.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.adapter.HeadAndFootAdapter;
import study.com.purerecyclerview.headfoot.HeadFootAdapter;
import study.com.purerecyclerview.util.LogUtil;
import study.com.purerecyclerview.viewcreator.DefaultLoadFooterCreator;

/**
 * Created by  HONGDA on 2019/1/10.
 * 它不该为了兼容各种需求而变得臃肿
 * 而应该为了实现某一个定制化的需求变得最精简
 */
public class TestView extends RecyclerView {

    //默认状态
    private int curState = STATE_DEFAULT;
    //    初始
    public final static int STATE_DEFAULT = 0;
    //    正在上拉
    public final static int STATE_PULLING = 1;
    //    松手加载
    public final static int STATE_RELEASE_TO_LOAD = 2;
    //    加载中
    public final static int STATE_LOADING = 3;
    //     没有更多
    public final static int STATE_NO_MORE = 4;
    //     加载完成
    public final static int STATE_FINISH = 5;

    private float mLoadRatio = 0.5f;

    private HeadAndFootAdapter headFootAdapter;
    private Adapter realAdapter;

    private View loadMoreView;
    private View bottomView;

    //    用于测量高度的加载View
    private int loadViewHeight = 0;

    private float downY = 0;
    private float currentY = 0;
    private float distance = 0;
    //这个标志位通过判断是否是滑动到底部后继续上拉的动作，来决定是否改变bottomView的高度
    private boolean isBottom = false;//是否执行动画
    private float ratio = 0.5f;//滑动距离和头部view下拉高度的比率，默认是3

    private static int foot_height_max;//最大滑动距离
    //回弹动画
    private ValueAnimator anim;
    //动画时间长度
    private static final long ANIM_TIME = 300;

    //用于记录滑动过程中,底部临界动画的触发
    private int tempState = DefaultFootViewCreator.STATE_PULL_TO_RELEASE;

    private OnLoadMoreFootViewCreator onLoadMoreFootViewCreator;

    public TestView(@NonNull Context context) {
        this(context, null);
    }


    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        onLoadMoreFootViewCreator = new DefaultFootViewCreator();
        loadMoreView = onLoadMoreFootViewCreator.getLoadMoreView(context);
        //构建bottomView，无需开放给自定义footCreator
//        bottomView = new View(context);
//        bottomView.setBackgroundColor(Color.RED);
        bottomView = LayoutInflater.from(context).inflate(R.layout.view_bottom, null);
        ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        //该view的高度不能为0，否则将无法判断是否已滑动到底部
        bottomView.setLayoutParams(layoutParams);
        //获取loadMoreView的高度
        loadViewHeight = onLoadMoreFootViewCreator.getLoadMoreViewHeight(context);
        foot_height_max = loadViewHeight * 4;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        realAdapter = adapter;
        headFootAdapter = new HeadFootAdapter(realAdapter);
        super.setAdapter(headFootAdapter);
        addFooterView(loadMoreView);
        addFooterView(bottomView);
        //初始化loadView的位置
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        LogUtil.i("setAdapter = loadViewHeight = " + loadViewHeight + "   marginLayoutParams.bottomMargin = " + marginLayoutParams.bottomMargin);
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin - loadViewHeight - 1);
        setLayoutParams(marginLayoutParams);
    }

    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null !");
        } else if (headFootAdapter == null) {
            throw new IllegalStateException("u must set a adapter first !");
        } else {
            headFootAdapter.addFootView(view);
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

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curState = STATE_DEFAULT;
                LogUtil.i("LHD  MotionEvent.ACTION_DOWN " + isBottom());
                isBottom = isBottom();//记录是否处理抬起的手势
                if (isBottom) {//如果沒有滑动到底部不处理
                    downY = e.getRawY();
                    currentY = downY;
                } else {
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isBottom) return super.onTouchEvent(e);
                currentY = e.getRawY();
                float dy = (currentY - downY) * ratio;
//                LogUtil.i("LHD 计算dy1 = " + dy + "   currentY = " + currentY + "   downY = " + downY);
                if (dy < 0) {
                    dy = Math.min(foot_height_max, Math.abs(dy));
                    dy = Math.max(0, Math.abs(dy));
                    LogUtil.i("LHD 计算dy2 = " + dy);
                    //滑动footView
                    scollFoot(dy);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //如果不是滑动到底部的抬起动作，则不处理
                LogUtil.i("LHD  MotionEvent.ACTION_UP + " + isBottom);
                if (isBottom) {
                    currentY = e.getRawY();
                    float dy2 = (currentY - downY) * ratio;
                    if (dy2 > 0) break;//如果是滑动到底部以后，再往上滑动，则不处理
                    distance = Math.abs(dy2);
                    LogUtil.i("LHD 计算dy3 = " + distance);
                    checkState(distance);
                    if (distance > 0) {
                        startAction();
                    }
                }
                break;
        }
//        LogUtil.i("LHD 传递触摸事件");
        return super.onTouchEvent(e);
    }

    /**
     * 根据上拉距离检查状态
     *
     * @param dy
     */
    private void checkState(float dy) {
        if (dy >= loadViewHeight) {
            //释放刷新
            curState = STATE_RELEASE_TO_LOAD;
        } else {
            //上拉加载更多
            curState = STATE_PULLING;
        }
    }

    /**
     * 核心方法
     * 根据上拉的距离判断状态，执行不同的动画
     * 状态1：上拉中未触发刷新，上拉->刷新->结束刷新
     * 状态2：触发刷新，上拉->结束刷新
     */
    private void startAction() {
        LogUtil.i("LHD startAction = " + curState);
        if (curState == STATE_PULLING) {
            //回到起始位置
            createAnimatorTranslationY((int) distance, 1);
        } else if (curState == STATE_RELEASE_TO_LOAD) {
            curState = STATE_LOADING;
            //进入刷新状态
            createAnimatorTranslationY((int) distance, loadViewHeight);
            if (onLoadMoreListener != null) {
                onLoadMoreListener.loading();
            }
        }
    }

    private ViewGroup.LayoutParams bottomLp;

    private void scollFoot(float dy) {
        //该view的高度不能为0，否则将无法判断是否已滑动到底部
        //dy的高度决定了bottomView的高度，所以不可随意设置
        if (dy < 1) dy = 1;
        if (bottomView != null) {
//            LogUtil.i("LHD 设置bottomView的高度 = " + dy);
            bottomLp = bottomView.getLayoutParams();
            bottomLp.height = (int) dy;
            bottomView.setLayoutParams(bottomLp);
        }

        //0<dy<loadViewHeight 的时候是DefaultFootViewCreator.STATE_PULL_TO_RELEASE;
        //dy>loadViewHeight 的时候是 DefaultFootViewCreator.STATE_RELEASE_TO_LOADING;
        //dy = loadViewHeight的时候出发临界动画
        //判断实时状态
        int tempDy = (int) dy;
//        LogUtil.i("LHD scollFoot = " + tempDy);
        if (tempDy > loadViewHeight) {
            tempState = DefaultFootViewCreator.STATE_RELEASE_TO_LOADING;
        } else if (tempDy < loadViewHeight) {
            tempState = DefaultFootViewCreator.STATE_PULL_TO_RELEASE;
        }

        //判断临界点动画
        if (tempDy == loadViewHeight) {
            LogUtil.i("LHD 判断临界点动画 = " + dy + "   " + tempState + "   loadViewHeight = " + loadViewHeight + "  tempDy = " + tempDy);
            onLoadMoreFootViewCreator.executeAnim(tempState);
        }
    }


    /**
     * 判断是否滑动到底部
     */
    private boolean isBottom() {
        return !ViewCompat.canScrollVertically(this, 1);
    }

    /**
     * 创建回弹动画
     *
     * @param start bottomView的起始高度
     * @param end   bottomView的结束高度为1,该view的高度不能为0，否则将无法判断是否已滑动到底部
     */
    public void createAnimatorTranslationY(final int start, final int end) {
        anim = ValueAnimator.ofInt(start, end);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                LogUtil.i("LHD 正在执行回弹动画 = " + value + "   state = " + curState + "  start = " + start + "  end = " + end);
                value = Math.max(1, value);//bottomView的高度不能为0，否则将无法判断是否已滑动到底部
                //value的高度决定了bottomView的高度，所以不可随意设置start和end的值，start和end的值必须为bottomView的高度值
                scollFoot(value);
                if (value == 1) {//value的最小值是1,说明回到了最初状态
                    curState = STATE_DEFAULT;
                    //将recyclerView的marginBottom值，使loadMoreView重新回到布局外面
                    resetRecyclerView();
                } else if (value == loadViewHeight) {//说明回到了刷新状态
                    curState = STATE_LOADING;
                    LogUtil.i("LHD 说明回到了刷新状态 = " + curState);
                }
                //动画结束
                if (value == end) {
                    LogUtil.i("LHD 动画结束时的状态 = " + curState);
                    if (curState == STATE_DEFAULT) {   //重置底部UI
                        onLoadMoreFootViewCreator.finishLoading();
                    } else { //改变底部UI为刷新中
                        onLoadMoreFootViewCreator.loading();
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
    public void finishLoadMore() {
        //回到起始位置
        createAnimatorTranslationY(loadViewHeight, 1);
    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void resetRecyclerView() {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        LogUtil.i("resetRecyclerView = loadViewHeight = " + loadViewHeight);
        marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, -loadViewHeight - 1);
        setLayoutParams(marginLayoutParams);
        //别忘了重置标志位
        isBottom = false;
    }

}
