package study.com.purerecyclerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.customview.OnLoadMoreListener;
import study.com.purerecyclerview.customview.LoadMoreRecyclerView;
import study.com.purerecyclerview.headfoot.HeadFootRealAdapter;
import study.com.purerecyclerview.util.NoSnapItemAnimator;

public class TestActivity extends AppCompatActivity {

    private LoadMoreRecyclerView recyclerView;
    private List<String> list;
    private HeadFootRealAdapter headFootRealAdapter;
    private Handler handler;
    private CheckBox checkBox;

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
        checkBox = findViewById(R.id.check_no_more);
//        recyclerView.getItemAnimator().setChangeDuration(0);//无效
//        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);//无效
//        recyclerView.setItemAnimator(new NoSnapItemAnimator());
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loading() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (checkBox.isChecked()) {
                            recyclerView.finishLoadMore(0);
                            recyclerView.setNoMore(checkBox.isChecked());
                        } else {
                            for (int i = 0; i < 2; i++) {
                                list.add("加载更多的数据" + i);
                            }
                            recyclerView.finishLoadMore(2);
                        }
                    }
                }, 1000);
            }
        });

    }

}
