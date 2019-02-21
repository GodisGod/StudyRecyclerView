package study.com.purerecyclerview.customview.creator;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.customview.LoadMoreRecyclerView;
import study.com.purerecyclerview.util.LogUtil;

/**
 * Created by  鸿达 on 2019/2/21.
 */
public class DefaultRefreshHeadViewCreator implements OnRefreshHeadViewCreator {

    private View refreshView;
    private ImageView imgArrow;
    private ProgressBar progressBar;
    private TextView tvTip;

    private int rotationDuration = 200;
    private ValueAnimator ivAnim;

    @Override
    public void startPull(float distance) {
//        switchImg(false);
//        imgArrow.setImageResource(R.drawable.arrow_down);
//        imgArrow.setRotation(-180f);
//        tvTip.setText("上拉加载");
    }

    @Override
    public void releaseRefresh(float distance) {
//        switchImg(false);
//        imgArrow.setImageResource(R.drawable.arrow_down);
//        imgArrow.setRotation(0f);
//        tvTip.setText("松手立即刷新");
    }

    @Override
    public void executeAnim(int state) {
        switchImg(false);
        if (state == LoadMoreRecyclerView.STATE_RELEASE_TO_LOAD) {
            startArrowAnim(-180f);
            tvTip.setText("松手立即刷新");
        } else if (state == LoadMoreRecyclerView.STATE_PULLING) {
            imgArrow.setRotation(0f);
            tvTip.setText("下拉刷新");
        }
    }

    @Override
    public void loading() {
        switchImg(true);
        tvTip.setText("刷新中...");
    }

    @Override
    public void finishRefresh() {
        switchImg(false);
        tvTip.setText("下拉刷新");
    }

    @Override
    public View getRefreshView(Context context, RecyclerView recyclerView) {
        if (refreshView == null) {
            refreshView = LayoutInflater.from(context).inflate(R.layout.layout_header, recyclerView, false);
            imgArrow = refreshView.findViewById(R.id.header_arrow);
            progressBar = refreshView.findViewById(R.id.header_progress);
            tvTip = refreshView.findViewById(R.id.header_tv);
        }
        return refreshView;
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
