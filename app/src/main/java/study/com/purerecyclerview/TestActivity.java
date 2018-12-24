package study.com.purerecyclerview;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.freshlayout.adapter.RecyclerViewAdapter;

public class TestActivity extends AppCompatActivity {

    private FrameLayout framTop;
    private int i = 0;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerViewAdapter adapter;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        framTop = findViewById(R.id.fram_top);
        recyclerView = findViewById(R.id.recycler_content);
        list = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            list.add("PullToRefreshLayout" + i);
        }

        adapter = new RecyclerViewAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.go_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ViewCompat.setTranslationY(framTop, (i = i + 2));
                recyclerView.scrollBy(0, (i = i + 5));
            }
        });
        findViewById(R.id.go_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ViewCompat.setTranslationY(framTop, (i = i - 2));
                recyclerView.scrollBy(0, (i = i - 5));
            }
        });
    }

}
