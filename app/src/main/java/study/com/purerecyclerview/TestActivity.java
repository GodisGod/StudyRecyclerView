package study.com.purerecyclerview;

import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.customview.LoadMoreRecyclerView;
import study.com.purerecyclerview.freshlayout.adapter.LoadMoreAdapter;
import study.com.purerecyclerview.freshlayout.adapter.RecyclerViewAdapter;
import study.com.purerecyclerview.freshlayout.adapter.TestAdapter;
import study.com.purerecyclerview.viewcreator.DefaultLoadFooterCreator;
import study.com.purerecyclerview.viewcreator.LoadFooterCreator;

public class TestActivity extends AppCompatActivity {

    private FrameLayout framTop;
    private int i = 0;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    //    private RecyclerViewAdapter adapter;
    private TestAdapter adapter;
    private List<String> list;

    //自定义view
    private LoadMoreRecyclerView loadMoreRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        framTop = findViewById(R.id.fram_top);

        loadMoreRecyclerView = findViewById(R.id.load_more_recycler_view);
        list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("PullToRefreshLayout" + i);
        }
        adapter = new TestAdapter(list);
        loadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loadMoreRecyclerView.setAdapter(adapter);
        initListener();

//        LoadFooterCreator mLoadFooterCreator = new DefaultLoadFooterCreator();
//        View mLoadView = mLoadFooterCreator.getLoadView(this, null);
//        View mNoMoreView = mLoadFooterCreator.getNoMoreView(this, null);
    }

    private void initListener() {
        findViewById(R.id.go_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ViewCompat.setTranslationY(framTop, (i = i + 2));
//                recyclerView.scrollBy(0, (i = i + 5));
            }
        });
        findViewById(R.id.go_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 2; i++) {
                    list.add("上拉加载更多" + i);
                }
                adapter.notifyDataSetChanged();
//                ViewCompat.setTranslationY(framTop, (i = i - 2));
//                recyclerView.scrollBy(0, (i = i - 5));
            }
        });
    }

}
