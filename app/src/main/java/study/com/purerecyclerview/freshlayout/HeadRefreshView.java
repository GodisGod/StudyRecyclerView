package study.com.purerecyclerview.freshlayout;

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
 * Created by  HONGDA on 2018/12/17.
 */
public class HeadRefreshView extends FrameLayout implements HeadView {

    private TextView tvTip;
    private ImageView arrow;
    private ProgressBar progressBar;

    public HeadRefreshView(Context context) {
        this(context, null);
    }

    public HeadRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.i("LHD", "HeadRefreshView  init");
        View view = LayoutInflater.from(context).inflate(R.layout.layout_header, null);
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
        float s = progress / all;
        if (s >= 0.9f) {
            arrow.setRotation(180);
        } else {
            arrow.setRotation(0);
        }
        if (progress >= all - 10) {
            tvTip.setText("松开刷新");
        } else {
            tvTip.setText("下拉加载");
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
