package study.com.purerecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.headfoot.HeadFootAdapter;

public class TestAddHeadAndFootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> list;
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
        headFootAdapter = new HeadFootAdapter()
    }
}
