package study.com.purerecyclerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.freshlayout.BaseRefreshListener;
import study.com.purerecyclerview.freshlayout.layout.PureRefreshLayout;
import study.com.purerecyclerview.freshlayout.adapter.RecyclerViewAdapter;
import study.com.purerecyclerview.freshlayout.layout.PureRefreshLayout2;

public class PurRecyclerviewActivity extends AppCompatActivity {

    private PureRefreshLayout2 pullToRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pur_recyclerview);

        pullToRefreshLayout = findViewById(R.id.recycler_layout);
        recyclerView = findViewById(R.id.recycler_view);

        list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add("PullToRefreshLayout" + i);
        }

        adapter = new RecyclerViewAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });


    }

}
