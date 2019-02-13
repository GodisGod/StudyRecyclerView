package study.com.purerecyclerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.customview.OnLoadMoreListener;
import study.com.purerecyclerview.customview.LoadMoreRecyclerView;
import study.com.purerecyclerview.headfoot.HeadFootRealAdapter;

public class TestActivity extends AppCompatActivity {

    private LoadMoreRecyclerView recyclerView;
    private List<String> list;
    private HeadFootRealAdapter headFootRealAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            list.add("PullToRefreshLayout" + i);
        }

        headFootRealAdapter = new HeadFootRealAdapter(list);
        recyclerView = findViewById(R.id.recycler_test2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(headFootRealAdapter);
        handler = new Handler();

        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loading() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 2; i++) {
                            list.add("加载更多的数据" + i);
                        }
                        recyclerView.finishLoadMore(2);
                    }
                }, 1000);
            }
        });
    }

}
