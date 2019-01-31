package study.com.purerecyclerview;

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

import study.com.purerecyclerview.headfoot.HeadFootAdapter;
import study.com.purerecyclerview.headfoot.HeadFootRealAdapter;

public class TestAddHeadAndFootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> list;
    private HeadFootRealAdapter headFootRealAdapter;
    private HeadFootAdapter headFootAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_add_head_and_foot);

        recyclerView = findViewById(R.id.recycler_head_foot);

        list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            list.add("PullToRefreshLayout" + i);
        }

        headFootRealAdapter = new HeadFootRealAdapter(list);
        headFootAdapter = new HeadFootAdapter(headFootRealAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(headFootAdapter);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.btn_add_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headFootAdapter.addHeadView(getHeaderView());
            }
        });
        findViewById(R.id.btn_remove_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headerViews.size() == 0) return;
                View view = headerViews.remove(headerViews.size() - 1);
                headFootAdapter.removeHeadView(view);
            }
        });
        findViewById(R.id.btn_add_foot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headFootAdapter.addFootView(getFooterView());
            }
        });
        findViewById(R.id.btn_remove_foot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (footerViews.size() == 0) return;
                View view = footerViews.remove(footerViews.size() - 1);
                headFootAdapter.removeFootView(view);
            }
        });
    }

    private ArrayList<View> headerViews = new ArrayList<>();
    private ArrayList<View> footerViews = new ArrayList<>();

    private View getHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_header, recyclerView, false);
        headerViews.add(view);
        return view;
    }

    private View getFooterView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_header, recyclerView, false);
        footerViews.add(view);
        return view;
    }

}
