package study.com.purerecyclerview.freshlayout.layout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.BaseRefreshListener;
import study.com.purerecyclerview.freshlayout.FooterView;
import study.com.purerecyclerview.freshlayout.HeadRefreshView;
import study.com.purerecyclerview.freshlayout.LoadMoreView;
import study.com.purerecyclerview.freshlayout.State;
import study.com.purerecyclerview.freshlayout.ViewStatus;
import study.com.purerecyclerview.util.DisplayUtil;

/**
 * Created by  HONGDA on 2018/12/17.
 * 头部有两个方案
 * 1、根据下拉距离动态改变头部高度，视觉上会有缺陷，适合头部动画是从小到大渐变的头部
 * 2、将布局放到屏幕之外，根据下拉距离滑动到屏幕内部
 * 本类采用方案1实现
 */
public class PureRefreshLayout extends FrameLayout {

    private HeadRefreshView headView;
    private FooterView footerView;
    private View childView;

    private static final long ANIM_TIME = 300;
    private static int HEAD_HEIGHT = 60;
    private static int FOOT_HEIGHT = 60;

    private static int head_height;
    private static int head_height_2;
    private static int foot_height;
    private static int foot_height_2;

    private float mTouchY;
    private float mCurrentY;

    private boolean canLoadMore = true;
    private boolean canRefresh = true;
    private boolean isRefresh;
    private boolean isLoadMore;

    //滑动的最小距离
    private int mTouchSlope;

    private BaseRefreshListener refreshListener;

    private View loadingView, errorView, emptyView;
    private int loading = R.layout.layout_loading, empty = R.layout.layout_empty, error = R.layout.layout_error;

    public PureRefreshLayout(Context context) {
        this(context, null);
    }

    public PureRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PureRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, defStyleAttr, 0);
        error = a.getResourceId(R.styleable.PullToRefreshLayout_view_error, error);
        loading = a.getResourceId(R.styleable.PullToRefreshLayout_view_loading, loading);
        empty = a.getResourceId(R.styleable.PullToRefreshLayout_view_empty, empty);

        init();
    }

    private void init() {
        head_height = DisplayUtil.dp2Px(getContext(), HEAD_HEIGHT);
        foot_height = DisplayUtil.dp2Px(getContext(), FOOT_HEIGHT);
        head_height_2 = DisplayUtil.dp2Px(getContext(), HEAD_HEIGHT * 2);
        foot_height_2 = DisplayUtil.dp2Px(getContext(), FOOT_HEIGHT * 2);
        if (getChildCount() != 1) {
            new IllegalArgumentException("must only one child");
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childView = getChildAt(0);
        addHeadView();
        addFooterView();
    }

    private void addHeadView() {
        //防止重复添加
        if (headView == null) {
            headView = new HeadRefreshView(getContext());
        } else {
            removeView(headView.getView());
        }

        //头部方案1
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.gravity = Gravity.TOP;
        headView.getView().setLayoutParams(lp);

        //头部方案2
//        headView.getView().setTranslationY(-foot_height);

        if (headView.getView().getParent() != null) {
            ((ViewGroup) headView.getView().getParent()).removeAllViews();
        }

        addView(headView.getView(), 0);
    }

    private void addFooterView() {
        if (footerView == null) {
            footerView = new LoadMoreView(getContext());
        } else {
            removeView(footerView.getView());
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.BOTTOM;
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
                float dura = (mCurrentY - mTouchY) / 3.0f;
                if (dura > 0 && canRefresh) {
                    dura = Math.min(head_height_2, dura);
                    dura = Math.max(0, dura);
                    //头部方案1
                    //根据下拉距离改变头部高度
                    headView.getView().getLayoutParams().height = (int) dura;
                    //头部方案2
                    //将布局放到屏幕之外，根据下拉距离滑动到屏幕内部

                    //同步向下移动recyclerview
                    ViewCompat.setTranslationY(childView, dura);
                    requestLayout();
                    headView.progress(dura, head_height);
                } else {
                    if (canLoadMore) {
                        dura = Math.min(foot_height_2, Math.abs(dura));
                        dura = Math.max(0, Math.abs(dura));
                        footerView.getView().getLayoutParams().height = (int) dura;
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
                    if (dy1 >= head_height) {
                        createAnimatorTranslationY(State.REFRESH,
                                dy1 > head_height_2 ? head_height_2 : dy1, head_height,
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
                    } else if (dy1 > 0 && dy1 < head_height) {
                        setFinish(dy1, State.REFRESH);
                        headView.normal();
                    }
                } else {
                    if (canLoadMore) {
                        if (Math.abs(dy1) >= foot_height) {
                            createAnimatorTranslationY(State.LOADMORE, Math.abs(dy1) > foot_height_2 ? foot_height_2 : Math.abs(dy1), foot_height, new CallBack() {
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


    private boolean canChildScrollDown() {
        if (childView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(childView, 1);
    }

    private boolean canChildScrollUp() {
        if (childView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(childView, -1);
    }

    /**
     * 创建动画
     */
    public void createAnimatorTranslationY(@State.REFRESH_STATE final int state, final int start,
                                           final int purpose, final CallBack callBack) {
        final ValueAnimator anim;
        anim = ValueAnimator.ofInt(start, purpose);
        anim.setDuration(ANIM_TIME);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (state == State.REFRESH) {
                    headView.getView().getLayoutParams().height = value;
                    ViewCompat.setTranslationY(childView, value);
                    if (purpose == 0) { //代表结束加载
                        headView.finishing(value, head_height_2);
                    } else {
                        headView.progress(value, head_height);
                    }
                } else {
                    footerView.getView().getLayoutParams().height = value;
                    ViewCompat.setTranslationY(childView, -value);
                    if (purpose == 0) { //代表结束加载
                        footerView.finishing(value, head_height_2);
                    } else {
                        footerView.progress(value, foot_height);
                    }
                }
                if (value == purpose) {
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
    private void setFinish(int height, @State.REFRESH_STATE final int state) {
        createAnimatorTranslationY(state, height, 0, new CallBack() {
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

    private void setFinish(@State.REFRESH_STATE int state) {
        if (state == State.REFRESH) {
            if (headView != null && headView.getView().getLayoutParams().height > 0 && isRefresh) {
                setFinish(head_height, state);
            }
        } else {
            if (footerView != null && footerView.getView().getLayoutParams().height > 0 && isLoadMore) {
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
        setFinish(State.REFRESH);
    }

    /**
     * 结束加载更多
     */
    public void finishLoadMore() {
        setFinish(State.LOADMORE);
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
    public void setHeaderView(HeadRefreshView mHeaderView) {
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
        head_height_2 = DisplayUtil.dp2Px(getContext(), dp);
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
        foot_height_2 = DisplayUtil.dp2Px(getContext(), dp);
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
        head_height_2 = DisplayUtil.dp2Px(getContext(), dp);
        foot_height_2 = DisplayUtil.dp2Px(getContext(), dp);
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
        head_height_2 = DisplayUtil.dp2Px(getContext(), refresh);
        foot_height_2 = DisplayUtil.dp2Px(getContext(), loadMore);
    }


}
