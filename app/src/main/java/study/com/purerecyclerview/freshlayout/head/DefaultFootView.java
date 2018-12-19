package study.com.purerecyclerview.freshlayout.head;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.FooterView;

/**
 * Created by  HONGDA on 2018/12/17.
 */
public class DefaultFootView extends FrameLayout implements FooterView {
    private TextView tvTip;
    private ImageView arrow;
    private ProgressBar progressBar;

    public DefaultFootView(Context context) {
        this(context, null);
    }

    public DefaultFootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_default_footer, null);
        addView(view);
        tvTip = view.findViewById(R.id.header_tv);
        arrow = view.findViewById(R.id.header_arrow);
        progressBar = view.findViewById(R.id.header_progress);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(float progress, float all) {
        if (progress >= all) {
            tvTip.setText("松开加载更多");
            arrow.setRotation(0);
        } else {
            tvTip.setText("上拉加载");
            arrow.setRotation(180);
        }
    }

    @Override
    public void finishing(float progress, float all) {

    }

    @Override
    public void loading() {
        arrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tvTip.setText("加载中...");
    }

    @Override
    public void normal() {
        arrow.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        tvTip.setText("上拉加载");
    }

    @Override
    public View getView() {
        return this;
    }

}
