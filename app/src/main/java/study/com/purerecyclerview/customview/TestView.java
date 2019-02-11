package study.com.purerecyclerview.customview;

import android.animation.ValueAnimator;
import android.content.Context;
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
    private boolean canExitAnim = false;//是否执行动画
    private float ratio = 0.5f;//滑动距离和头部view下拉高度的比率，默认是3

    private static int foot_height_max;//最大滑动距离
    //回弹动画
    private ValueAnimator anim;
    //动画时间长度
    private static final long ANIM_TIME = 300;

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
        loadMoreView = LayoutInflater.from(context).inflate(R.layout.layout_header, null);
        loadMoreView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.head_height)));
        bottomView = new View(context);
        ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        //该view的高度不能为0，否则将无法判断是否已滑动到底部
        bottomView.setLayoutParams(layoutParams);
        loadViewHeight = (int) getResources().getDimension(R.dimen.head_height);
        foot_height_max = loadViewHeight * 2;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
//        if (loadMoreView != null && loadViewHeight == 0) {
//            loadMoreView.measure(0, 0);
//            loadViewHeight = loadMoreView.getLayoutParams().height;
//            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
//            LogUtil.i("onMeasure = loadViewHeight = " + loadViewHeight);
//            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin - loadViewHeight - 1);
//            setLayoutParams(marginLayoutParams);
//        }
        super.onMeasure(widthSpec, heightSpec);
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
        LogUtil.i("onMeasure = loadViewHeight = " + loadViewHeight);
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
                if (isBottom()) {//如果沒有滑动到底部不处理
                    downY = e.getY();
                    currentY = downY;
                    canExitAnim = true;//记录是否处理抬起的手势
                } else {
                    canExitAnim = false;
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                currentY = e.getY();
                float dy = (currentY - downY) * ratio;
//                LogUtil.i("LHD 计算dy1 = " + dy + "   currentY = " + currentY + "   downY = " + downY);
                if (dy < 0) {
                    dy = Math.min(foot_height_max, Math.abs(dy));
                    dy = Math.max(0, Math.abs(dy));
//                    LogUtil.i("LHD 计算dy2 = " + dy);
                    //滑动footView
                    scollFoot(dy);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //如果不是滑动到底部的抬起动作，则不处理
                LogUtil.i("LHD  MotionEvent.ACTION_UP + " + canExitAnim);
                if (canExitAnim) {
                    currentY = e.getY();
                    float dy2 = (currentY - downY) * ratio;
                    distance = Math.abs(dy2);
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
        if (dy >= getResources().getDimension(R.dimen.head_height)) {
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
            createAnimatorTranslationY((int) distance, 0);
        } else if (curState == STATE_RELEASE_TO_LOAD) {
            curState = STATE_LOADING;
            //进入刷新状态
            createAnimatorTranslationY((int) distance, loadViewHeight);
        }
    }

    private ViewGroup.LayoutParams bottomLp;

    private void scollFoot(float dy) {
        if (bottomView != null) {
            bottomLp = bottomView.getLayoutParams();
            bottomLp.height = (int) dy;
            bottomView.setLayoutParams(bottomLp);
        }
    }


    /**
     * 判断是否滑动到底部
     */
    private boolean isBottom() {
        return !ViewCompat.canScrollVertically(this, 1);
    }

    /**
     * 创建动画
     */
    public void createAnimatorTranslationY(final int start, final int end) {
        anim = ValueAnimator.ofInt(start, end);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                LogUtil.i("LHD 开始执行回弹动画 = " + value + "   state = " + curState + "  start = " + start + "  end = " + end);
//                if (curState == STATE_RELEASE_TO_LOAD) {
//                    scollFoot(value);
//                } else {
//
//                }
                value = Math.max(1, value);//bottomView的高度不能为0，否则将无法判断是否已滑动到底部
                scollFoot(value);
                if (value == 1) {
                    curState = STATE_DEFAULT;
                } else {
                    //todo
//                    curState = STATE_LOADING;
                }
                if (value == end) {

                }
                requestLayout();
            }
        });
        anim.start();
    }

}
