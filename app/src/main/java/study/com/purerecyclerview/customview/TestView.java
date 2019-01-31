package study.com.purerecyclerview.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import study.com.purerecyclerview.R;

/**
 * Created by  HONGDA on 2019/1/10.
 */
public class TestView extends RecyclerView {
    public TestView(@NonNull Context context) {
        super(context);
        init();
    }


    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
//        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_load_more, null);
    }
}
