package study.com.purerecyclerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.customview.OnLoadMoreListener;
import study.com.purerecyclerview.customview.TestView;
import study.com.purerecyclerview.headfoot.HeadFootRealAdapter;

public class Test2Activity extends AppCompatActivity {

    private TestView recyclerView;
    private List<String> list;
    private HeadFootRealAdapter headFootRealAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
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
                        headFootRealAdapter.notifyDataSetChanged();
                        recyclerView.finishLoadMore();
                    }
                }, 3000);
            }
        });
    }

}
