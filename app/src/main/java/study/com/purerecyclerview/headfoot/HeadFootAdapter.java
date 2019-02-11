package study.com.purerecyclerview.headfoot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.adapter.HeadAndFootAdapter;

public class HeadFootAdapter extends HeadAndFootAdapter {

    public HeadFootAdapter(RecyclerView.Adapter realAdapter) {
        super(realAdapter);
    }


}
