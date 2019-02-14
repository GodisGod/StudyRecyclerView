package study.com.purerecyclerview.freshlayout.layout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.baseinterface.BaseRefreshListener;
import study.com.purerecyclerview.freshlayout.baseinterface.FooterView;
import study.com.purerecyclerview.freshlayout.baseinterface.HeadView;
import study.com.purerecyclerview.freshlayout.State;
import study.com.purerecyclerview.freshlayout.ViewStatus;
import study.com.purerecyclerview.freshlayout.headorfootview.DefaultFootView;
import study.com.purerecyclerview.freshlayout.headorfootview.DefaultHeadView;
import study.com.purerecyclerview.util.DisplayUtil;

/**
 * Created by  HONGDA on 2018/12/17.
 * 头部有两个方案
 * 1、根据下拉距离动态改变头部高度
 * 2、将布局放到屏幕之外，根据下拉距离滑动到屏幕内部
 * 本类采用方案2实现
 */
public class PureRefreshLayout2 extends FrameLayout {

    private HeadView headView;
    private FooterView footerView;
    private RecyclerView childView;

    private static final long ANIM_TIME = 300;
    private static int HEAD_HEIGHT = 50;
    private static int FOOT_HEIGHT = 50;

    private static int head_height;
    private static int head_height_max;//最大滑动距离
    private static int foot_height;
    private static int foot_height_max;//最大滑动距离

    private float mTouchY;
    private float mCurrentY;

    private boolean canLoadMore = true;
    private boolean canRefresh = true;
    private boolean isRefresh;
    private boolean isLoadMore;

    //滑动的最小距离
    private int mTouchSlope;
    private float ratio = 3.0f;//滑动距离和头部view下拉高度的比率，默认是3

    private BaseRefreshListener refreshListener;

    private View loadingView, errorView, emptyView;
    private int loading = R.layout.layout_loading, empty = R.layout.layout_empty, error = R.layout.layout_error;

    public PureRefreshLayout2(Context context) {
        this(context, null);
    }

    public PureRefreshLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PureRefreshLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, defStyleAttr, 0);
        error = a.getResourceId(R.styleable.PullToRefreshLayout_view_error, error);
        loading = a.getResourceId(R.styleable.PullToRefreshLayout_view_loading, loading);
        empty = a.getResourceId(R.styleable.PullToRefreshLayout_view_empty, empty);

        init();
    }

    private void init() {
        float height = getResources().getDimension(R.dimen.head_height);
        Log.i("LHD", "dimen height = " + height);
        head_height = DisplayUtil.dp2Px(getContext(), HEAD_HEIGHT);
        foot_height = DisplayUtil.dp2Px(getContext(), FOOT_HEIGHT);
        head_height_max = DisplayUtil.dp2Px(getContext(), HEAD_HEIGHT * 2);
        foot_height_max = DisplayUtil.dp2Px(getContext(), FOOT_HEIGHT * 2);
        mTouchSlope = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (getChildCount() != 1) {
            new IllegalArgumentException("must only one child");
        }
        Log.i("LHD", "head_height = " + head_height + "   head_height_max = " + head_height_max);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childView = (RecyclerView) getChildAt(0);
        addHeadView();
        addFooterView();
    }

    private void addHeadView() {
        //防止重复添加
        if (headView == null) {
            headView = new DefaultHeadView(getContext());
        } else {
            removeView(headView.getView());
        }

        //头部方案2:使用marginTop控制头部view
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, head_height);
        layoutParams.topMargin = -head_height;
        headView.getView().setLayoutParams(layoutParams);

        if (headView.getView().getParent() != null) {
            ((ViewGroup) headView.getView().getParent()).removeAllViews();
        }

        addView(headView.getView(), 0);
    }

    private void addFooterView() {
        if (footerView == null) {
            footerView = new DefaultFootView(getContext());
        } else {
            removeView(footerView.getView());
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, foot_height);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.bottomMargin = -foot_height;
        footerView.getView().setLayoutParams(layoutParams);
        if (footerView.getView().getParent() != null)
            ((ViewGroup) footerView.getView().getParent()).removeAllViews();
        addView(footerView.getView());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canLoadMore && !canRefresh) return super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mCurrentY;
                if (canRefresh) {
                    boolean canChildScrollUp = canChildScrollUp();
                    //如果滑动距离超过最小滑动距离并且RecyclerView不能继续滚动，则拦截触摸事件，此时触摸事件由layout处理，而不会传递给RecyclerView
                    if (dy > mTouchSlope && !canChildScrollUp) {
                        headView.begin();
                        return true;
                    }
                }
                if (canLoadMore) {
                    boolean canChildScrollDown = canChildScrollDown();
                    if (dy < -mTouchSlope && !canChildScrollDown) {
                        footerView.begin();
                        return true;
                    }
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefresh || isLoadMore) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dura = (mCurrentY - mTouchY) / ratio;
                //dura >0 表示是向下滑动即下拉  < 0 表示是向上滑动即上拉
                if (dura > 0 && canRefresh) {
                    dura = Math.min(head_height_max, dura);//滑动的最大距离是head_height_max
                    dura = Math.max(0, dura);//todo
                    //头部方案2
                    //将布局放到屏幕之外，根据下拉距离滑动到屏幕内部
                    LayoutParams layoutParams = (LayoutParams) headView.getView().getLayoutParams();
                    Log.i("LHD", "位移距离 ：" + dura + "   (-head_height+dura = " + (-head_height + dura));
                    layoutParams.topMargin = (int) (-head_height + dura);
                    headView.getView().setLayoutParams(layoutParams);
                    //同步向下移动recyclerview
                    ViewCompat.setTranslationY(childView, dura);
                    requestLayout();
                    headView.progress(dura, head_height);
                } else {
                    if (canLoadMore) {
                        dura = Math.min(foot_height_max, Math.abs(dura));
                        dura = Math.max(0, Math.abs(dura));
                        //滑动footView
                        LayoutParams layoutParams = (LayoutParams) footerView.getView().getLayoutParams();
                        Log.i("LHD", "位移距离 ：" + dura + "   (-head_height+dura = " + (-foot_height + dura));
                        layoutParams.bottomMargin = (int) (-foot_height + dura);
                        footerView.getView().setLayoutParams(layoutParams);
                        //同步向上移动recyclerview
                        ViewCompat.setTranslationY(childView, -dura);
                        requestLayout();
                        footerView.progress(dura, foot_height);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float currentY = event.getY();
                final int dy1 = (int) (currentY - mTouchY) / 3;
                if (dy1 > 0 && canRefresh) {
                    if (dy1 >= head_height) {//超过刷新距离，释放后要进入刷新状态，此时头部高度要固定为head_height
                        //最大滑动距离是head_height_max
                        int start = dy1 > head_height_max ? head_height_max : dy1;
                        //从最大距离松手到刷新的动画
                        createAnimatorTranslationY(State.REFRESH, start, head_height, new CallBack() {
                            @Override
                            public void onSuccess() {
                                isRefresh = true;//刷新中
                                if (refreshListener != null) {
                                    refreshListener.refresh();
                                }
                                headView.loading();//改变headView的状态
                            }
                        });
                    } else {//没超过刷新距离则不刷新
                        setFinish(dy1, State.REFRESH);
                        headView.normal();
                    }
                } else {
                    if (canLoadMore) {
                        if (Math.abs(dy1) >= foot_height) {
                            int start = Math.abs(dy1) > foot_height_max ? foot_height_max : Math.abs(dy1);
                            createAnimatorTranslationY(State.LOADMORE, start, foot_height, new CallBack() {
                                @Override
                                public void onSuccess() {
                                    isLoadMore = true;
                                    if (refreshListener != null) {
                                        refreshListener.loadMore();
                                    }
                                    footerView.loading();
                                }
                            });
                        } else {
                            setFinish(Math.abs(dy1), State.LOADMORE);
                            footerView.normal();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断RecyclerView是否可以继续向下滚动
     *
     * @return
     */
    private boolean canChildScrollUp() {
        if (childView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(childView, -1);
    }

    /**
     * 判断RecyclerView是否可以继续向上滚动
     *
     * @return
     */
    private boolean canChildScrollDown() {
        if (childView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(childView, 1);
    }

    /**
     * 创建动画
     */
    public void createAnimatorTranslationY(@State.REFRESH_STATE final int state, final int start,
                                           final int end, final CallBack callBack) {
        final ValueAnimator anim;
        anim = ValueAnimator.ofInt(start, end);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (state == State.REFRESH) {
                    Log.i("LHD", "动画 value = " + value + "   purpose = " + end);
                    LayoutParams layoutParams = (LayoutParams) headView.getView().getLayoutParams();
                    layoutParams.topMargin = -head_height + value;
                    headView.getView().setLayoutParams(layoutParams);
                    ViewCompat.setTranslationY(childView, value);
                    if (end == 0) { //代表结束加载
                        headView.finishing(value, head_height_max);
                    } else {
                        headView.progress(value, head_height);
                    }
                } else {
                    LayoutParams layoutParams = (LayoutParams) footerView.getView().getLayoutParams();
                    layoutParams.bottomMargin = -foot_height + value;
                    footerView.getView().setLayoutParams(layoutParams);
                    ViewCompat.setTranslationY(childView, -value);
                    if (end == 0) { //代表结束加载
                        footerView.finishing(value, head_height_max);
                    } else {
                        footerView.progress(value, foot_height);
                    }
                }
                if (value == end) {
                    if (callBack != null)
                        callBack.onSuccess();
                }
                requestLayout();
            }

        });
        anim.start();
    }

    /**
     * 结束下拉刷新
     */
    private void setFinish(int start, @State.REFRESH_STATE final int state) {
        createAnimatorTranslationY(state, start, 0, new CallBack() {
            @Override
            public void onSuccess() {
                if (state == State.REFRESH) {
                    isRefresh = false;
                    headView.normal();
                } else {
                    isLoadMore = false;
                    footerView.normal();
                }
            }
        });
    }

    /**
     * @param state   State.REFRESH 下拉刷新  State.LOADMORE 上拉加载
     * @param hasMore 如果是上拉记载更多，true表示有更多数据,false表示无更多数据
     */
    private void setFinish(@State.REFRESH_STATE int state, boolean hasMore) {
        if (state == State.REFRESH) {
            FrameLayout.LayoutParams layoutParams = (LayoutParams) headView.getView().getLayoutParams();
            Log.i("LHD", "finish = " + layoutParams.topMargin);
            if (headView != null && layoutParams.topMargin == 0 && isRefresh) {
                setFinish(head_height, state);
            }
        } else {
            FrameLayout.LayoutParams layoutParams = (LayoutParams) footerView.getView().getLayoutParams();
            if (footerView != null && layoutParams.bottomMargin == 0 && isLoadMore) {
                setFinish(foot_height, state);
            }
        }
    }

    public interface CallBack {
        void onSuccess();
    }

    public void setRefreshListener(BaseRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    private void showLoadingView() {
        if (loadingView == null) {
            loadingView = LayoutInflater.from(getContext()).inflate(loading, null);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            addView(loadingView, layoutParams);
        } else {
            loadingView.setVisibility(VISIBLE);
        }
    }

    private void showEmptyView() {
        if (emptyView == null) {
            emptyView = LayoutInflater.from(getContext()).inflate(empty, null);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            addView(emptyView, layoutParams);
        } else {
            emptyView.setVisibility(VISIBLE);
        }
    }

    private void showErrorView() {
        if (errorView == null) {
            errorView = LayoutInflater.from(getContext()).inflate(error, null);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            addView(errorView, layoutParams);
        } else {
            errorView.setVisibility(VISIBLE);
        }
    }

    private void hideView(View view) {
        if (view != null)
            view.setVisibility(GONE);
    }

    private void switchView(int status) {
        switch (status) {
            case ViewStatus.CONTENT_STATUS:
                hideView(loadingView);
                hideView(emptyView);
                hideView(errorView);

                childView.setVisibility(VISIBLE);
                break;
            case ViewStatus.LOADING_STATUS:
                hideView(childView);
                hideView(emptyView);
                hideView(errorView);

                showLoadingView();
                break;
            case ViewStatus.EMPTY_STATUS:
                hideView(childView);
                hideView(loadingView);
                hideView(errorView);

                showEmptyView();
                break;
            case ViewStatus.ERROR_STATUS:
                hideView(childView);
                hideView(loadingView);
                hideView(emptyView);

                showErrorView();
                break;
            default:
                hideView(loadingView);
                hideView(emptyView);
                hideView(errorView);

                childView.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 设置展示view (error,empty,loading)
     */
    public void showView(@ViewStatus.VIEW_STATUS int status) {
        switchView(status);
    }

    /**
     * 获取view (error,empty,loading)
     */
    public View getView(@ViewStatus.VIEW_STATUS int status) {
        switch (status) {
            case ViewStatus.EMPTY_STATUS:
                return emptyView;
            case ViewStatus.LOADING_STATUS:
                return loadingView;
            case ViewStatus.ERROR_STATUS:
                return errorView;
            case ViewStatus.CONTENT_STATUS:
                return childView;
        }
        return null;
    }

    public void autoRefresh() {
        createAnimatorTranslationY(State.REFRESH,
                0, head_height,
                new CallBack() {
                    @Override
                    public void onSuccess() {
                        isRefresh = true;
                        if (refreshListener != null) {
                            refreshListener.refresh();
                        }
                        headView.loading();
                    }
                });
    }

    /**
     * 结束刷新
     */
    public void finishRefresh() {
        setFinish(State.REFRESH, false);
    }

    /**
     * 结束加载更多
     */
    public void finishLoadMore(boolean hasMore) {
        setFinish(State.LOADMORE, hasMore);
    }

    /**
     * 设置是否启用加载更多
     */
    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    /**
     * 设置是否启用下拉刷新
     */
    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    /**
     * 设置是下拉刷新头部
     *
     * @param mHeaderView 需实现 HeadView 接口
     */
    public void setHeaderView(HeadView mHeaderView) {
        this.headView = mHeaderView;
        addHeadView();
    }

    /**
     * 设置是下拉刷新尾部
     *
     * @param mFooterView 需实现 FooterView 接口
     */
    public void setFooterView(FooterView mFooterView) {
        this.footerView = mFooterView;
        addFooterView();
    }


    /**
     * 设置刷新控件的高度
     *
     * @param dp 单位为dp
     */
    public void setHeadHeight(int dp) {
        head_height = DisplayUtil.dp2Px(getContext(), dp);
    }

    /**
     * 设置加载更多控件的高度
     *
     * @param dp 单位为dp
     */
    public void setFootHeight(int dp) {
        foot_height = DisplayUtil.dp2Px(getContext(), dp);
    }

    /**
     * 同时设置加载更多控件和刷新控件的高度
     *
     * @param dp 单位为dp
     */
    public void setAllHeight(int dp) {
        head_height = DisplayUtil.dp2Px(getContext(), dp);
        foot_height = DisplayUtil.dp2Px(getContext(), dp);
    }

    /**
     * 同时设置加载更多控件和刷新控件的高度
     *
     * @param refresh  刷新控件的高度 单位为dp
     * @param loadMore 加载控件的高度 单位为dp
     */
    public void setAllHeight(int refresh, int loadMore) {
        head_height = DisplayUtil.dp2Px(getContext(), refresh);
        foot_height = DisplayUtil.dp2Px(getContext(), loadMore);
    }

    /**
     * 设置刷新控件的下拉的最大高度 且必须大于本身控件的高度  最佳为2倍
     *
     * @param dp 单位为dp
     */
    public void setMaxHeadHeight(int dp) {
        if (head_height >= DisplayUtil.dp2Px(getContext(), dp)) {
            return;
        }
        head_height_max = DisplayUtil.dp2Px(getContext(), dp);
    }

    /**
     * 设置加载更多控件的上拉的最大高度 且必须大于本身控件的高度  最佳为2倍
     *
     * @param dp 单位为dp
     */
    public void setMaxFootHeight(int dp) {
        if (foot_height >= DisplayUtil.dp2Px(getContext(), dp)) {
            return;
        }
        foot_height_max = DisplayUtil.dp2Px(getContext(), dp);
    }

    /**
     * 同时设置加载更多控件和刷新控件的最大高度 且必须大于本身控件的高度  最佳为2倍
     *
     * @param dp 单位为dp
     */
    public void setAllMaxHeight(int dp) {
        if (head_height >= DisplayUtil.dp2Px(getContext(), dp)) {
            return;
        }
        if (foot_height >= DisplayUtil.dp2Px(getContext(), dp)) {
            return;
        }
        head_height_max = DisplayUtil.dp2Px(getContext(), dp);
        foot_height_max = DisplayUtil.dp2Px(getContext(), dp);
    }

    /**
     * 同时设置加载更多控件和刷新控件的最大高度 且必须大于本身控件的高度  最佳为2倍
     *
     * @param refresh  刷新控件下拉的最大高度 单位为dp
     * @param loadMore 加载控件上拉的最大高度 单位为dp
     */
    public void setAllMaxHeight(int refresh, int loadMore) {
        if (head_height >= DisplayUtil.dp2Px(getContext(), refresh)) {
            return;
        }
        if (foot_height >= DisplayUtil.dp2Px(getContext(), loadMore)) {
            return;
        }
        head_height_max = DisplayUtil.dp2Px(getContext(), refresh);
        foot_height_max = DisplayUtil.dp2Px(getContext(), loadMore);
    }


}
