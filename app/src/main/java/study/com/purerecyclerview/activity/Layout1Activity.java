package study.com.purerecyclerview.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.adapter.TestAdapter;
import study.com.purerecyclerview.freshlayout.baseinterface.BaseRefreshListener;
import study.com.purerecyclerview.freshlayout.layout.PureRefreshLayout;

public class Layout1Activity extends AppCompatActivity {


    private PureRefreshLayout pullToRefreshLayout;
    private RecyclerView recyclerView;
    private TestAdapter adapter;
    private List<String> list;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout1);

        pullToRefreshLayout = findViewById(R.id.recycler_layout);
        recyclerView = findViewById(R.id.recycler_view);
        checkBox = findViewById(R.id.check_has_more);

        list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add("PullToRefreshLayout" + i);
        }

        adapter = new TestAdapter(list);

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
                        for (int i = 0; i < 2; i++) {
                            list.add("上拉加载更多" + i);
                        }
                        adapter.notifyDataSetChanged();
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });


    }
}
