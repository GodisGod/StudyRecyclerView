package study.com.purerecyclerview.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.adapter.HeadAndFootAdapter;
import study.com.purerecyclerview.headfoot.HeadFootAdapter;
import study.com.purerecyclerview.util.DisplayUtil;
import study.com.purerecyclerview.util.LogUtil;
import study.com.purerecyclerview.viewcreator.LoadFooterCreator;

/**
 * Created by  HONGDA on 2019/1/10.
 */
public class TestView extends RecyclerView {

    private int mState = STATE_DEFAULT;
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

    private float mLoadRatio = 0.5f;

    private HeadAndFootAdapter headFootAdapter;
    private Adapter realAdapter;

    private View loadMoreView;
    private View bottomView;

    //    用于测量高度的加载View
    private int mLoadViewHeight = 0;
    private float mFirstY = 0;
    private boolean mPulling = false;
    //    是否可以上拉加载
    private boolean mLoadMoreEnable = true;
    //    回弹动画
    private ValueAnimator valueAnimator;
    //    加载监听
    private OnLoadListener mOnLoadListener;

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
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (loadMoreView != null && mLoadViewHeight == 0) {
            loadMoreView.measure(0, 0);
            mLoadViewHeight = loadMoreView.getLayoutParams().height;
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            LogUtil.i("onMeasure = mLoadViewHeight = " + mLoadViewHeight);
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin - mLoadViewHeight - 1);
            setLayoutParams(marginLayoutParams);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        realAdapter = adapter;
        headFootAdapter = new HeadFootAdapter(realAdapter);
        super.setAdapter(headFootAdapter);
        addFooterView(loadMoreView);
        addFooterView(bottomView);
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
        return super.onTouchEvent(e);
    }

}
