package study.com.purerecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by  HONGDA on 2018/12/6.
 */
public class PurePullRecyclerView extends RecyclerView {

    public PurePullRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public PurePullRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PurePullRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }


}
