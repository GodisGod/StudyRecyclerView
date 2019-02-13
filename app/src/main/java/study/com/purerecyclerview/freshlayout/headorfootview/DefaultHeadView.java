package study.com.purerecyclerview.freshlayout.headorfootview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.baseinterface.HeadView;

/**
 * Created by  HONGDA on 2018/12/19.
 */
public class DefaultHeadView extends FrameLayout implements HeadView {
    private TextView tvTip;
    private ImageView arrow;
    private ProgressBar progressBar;

    public DefaultHeadView(Context context) {
        this(context, null);
    }

    public DefaultHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("LHD", "DefaultHeadView  init");
        View view = LayoutInflater.from(context).inflate(R.layout.layout_default_header, null);
        addView(view);
        tvTip = (TextView) view.findViewById(R.id.header_tv);
        arrow = (ImageView) view.findViewById(R.id.header_arrow);
        progressBar = (ProgressBar) view.findViewById(R.id.header_progress);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(float progress, float all) {
        if (progress >= all) {
            tvTip.setText("松开刷新");
            arrow.setRotation(180);
        } else {
            tvTip.setText("下拉加载");
            arrow.setRotation(0);
        }
    }

    @Override
    public void finishing(float progress, float all) {

    }

    @Override
    public void loading() {
        arrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tvTip.setText("刷新中...");
    }

    @Override
    public void normal() {
        arrow.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        tvTip.setText("下拉刷新");
    }

    @Override
    public View getView() {
        return this;
    }
}
