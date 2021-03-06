package study.com.purerecyclerview.customview.creator;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.customview.LoadMoreRecyclerView;
import study.com.purerecyclerview.util.LogUtil;

/**
 * Created by  鸿达 on 2019/2/12.
 * 没有bug(*^▽^*)
 */
public class DefaultFootViewCreator implements OnLoadMoreFootViewCreator {

    private View loadMoreView;
    private View noMoreView;
    private ImageView imgArrow;
    private ProgressBar progressBar;
    private TextView tvTip;

    private int rotationDuration = 200;
    private ValueAnimator ivAnim;

    @Override
    public void startPull(float distance) {
        switchImg(false);
        imgArrow.setImageResource(R.drawable.arrow_down);
        imgArrow.setRotation(0f);
        tvTip.setText("上拉加载");
    }

    @Override
    public void releaseToLoadMore(float distance) {
        switchImg(false);
        imgArrow.setImageResource(R.drawable.arrow_down);
        imgArrow.setRotation(-180f);
        tvTip.setText("松手立即加载");
    }

    @Override
    public void executeAnim(int state) {
        switchImg(false);
        if (state == LoadMoreRecyclerView.STATE_RELEASE_TO_LOAD) {
            startArrowAnim(0f);
            tvTip.setText("松手立即加载");
        } else if (state == LoadMoreRecyclerView.STATE_PULLING) {
            imgArrow.setRotation(-180f);
            tvTip.setText("上拉加载");
        }
    }

    @Override
    public void loading() {
        switchImg(true);
        tvTip.setText("刷新中...");
    }

    @Override
    public void finishLoading() {
        switchImg(false);
        tvTip.setText("上拉加载");
    }

    @Override
    public View getLoadMoreView(Context context, RecyclerView recyclerView) {
        if (loadMoreView == null) {
            //传入recyclerView之前必须已经设置了setLayoutManager,否则将inflater失败
            // 报错：Caused by: java.lang.IllegalStateException: RecyclerView has no LayoutManager study.com.purerecyclerview.customview.LoadMoreRecyclerView
            loadMoreView = LayoutInflater.from(context).inflate(R.layout.layout_header, recyclerView, false);
            imgArrow = loadMoreView.findViewById(R.id.header_arrow);
            progressBar = loadMoreView.findViewById(R.id.header_progress);
            tvTip = loadMoreView.findViewById(R.id.header_tv);
        }
        return loadMoreView;
    }

    @Override
    public View getNoMoreView(Context context, RecyclerView recyclerView) {
        if (noMoreView == null) {
            noMoreView = LayoutInflater.from(context).inflate(R.layout.view_no_more, recyclerView, false);
        }
        return noMoreView;
    }

    private void startArrowAnim(float roration) {
        if (ivAnim != null) {
            ivAnim.removeAllUpdateListeners();
            ivAnim.cancel();
        }
        final float fRoration = roration;
        float startRotation = imgArrow.getRotation();
        LogUtil.i("startArrowAnim");
        ivAnim = ObjectAnimator.ofFloat(startRotation, fRoration).setDuration(rotationDuration);
        ivAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imgArrow.setRotation((Float) animation.getAnimatedValue());
                if (((Float) animation.getAnimatedValue()) == fRoration) {
                    ivAnim.removeAllUpdateListeners();
                    ivAnim.cancel();
                }
            }
        });
        ivAnim.start();
    }

    private void switchImg(boolean isLoading) {
        imgArrow.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

}
