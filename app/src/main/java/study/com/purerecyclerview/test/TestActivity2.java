package study.com.purerecyclerview.test;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.customview.RefreshRecyclerView;
import study.com.purerecyclerview.customview.interfaces.OnRefreshListener;
import study.com.purerecyclerview.freshlayout.adapter.TestAdapter;
import study.com.purerecyclerview.util.LogUtil;
import study.com.purerecyclerview.util.Utils;

public class TestActivity2 extends AppCompatActivity {

    private RefreshRecyclerView recyclerView;
    private TestAdapter adapter;
    private List<String> list;
    private Button btnAddOne;
    private Button btnDeleteOne;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        recyclerView = findViewById(R.id.test_recycler_view);
        btnAddOne = findViewById(R.id.btn_add_one);
        btnDeleteOne = findViewById(R.id.btn_delete_one);
        list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            list.add("test_recycler_view " + i);
        }

        adapter = new TestAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        handler = new Handler();
        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void refreshLoding() {
                LogUtil.i(">>>>>>>>>>>>>>>>>>recyclerView.setOnRefreshListener>>>>>>>>>>>>>>>>>>>>>>");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("finishRefresh 結束刷新");
                        recyclerView.finishRefresh();
                    }
                }, 1000);
            }
        });
        btnAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add("增加一条数据");
//                adapter.notifyDataSetChanged();
                recyclerView.getAdapter().notifyDataSetChanged();
                LogUtil.i("LHD adapter.getItemCount() = " + adapter.getItemCount() + "    recyclerView.getChildCount() = " + recyclerView.getChildCount()
                        + "   Manager().getChildCount() =  " + recyclerView.getLayoutManager().getChildCount() + "   Manager().getItemCount() = " + recyclerView.getLayoutManager().getItemCount());
                //实验证明 recyclerView.getLayoutManager().getChildCount() = recyclerView.getChildCount() 并且这个数量会随着数据源的变化而不断的变化
                //原因：recyclerView顾名思义就是它会回收View，比如你有1000条数据，当你显示在屏幕上的可能只有3/4条，recyclerView的adapter.getItemCount()会返回给你正确的数量，即1000条
                //但是recyclerView本身是不会持有1000个View对象的,由于它的回收作用，它可能只持有3/4个View对象，这个数量是根据当前屏幕正在显示的或即将要显示的View数量决定的
                //StackOverFlow上关于此问题的解答:https://stackoverflow.com/questions/38147000/recyclerview-getchildcount-is-returning-different-number-of-child-everytime
                //RecyclerView does what is indicated by its name. It recycles views. Say you have a list of 1000 entries but at any given time 3-4 of them are shown on the screen. RecyclerView's adapter always holds all of those children so calling recyclerView.getAdapter().getItemCount(); will return a 1000.
                //However RecyclerView only holds some of the items which are shown and are "close" to being shown so recyclerView.getChildCount() will return a smaller and not-constant value. For most applications you should then use the method provided by the adapter.
                boolean isFull = Utils.isRecyclerViewFullscreen(recyclerView, recyclerView.getHeightCount());

                LogUtil.i("是否全屏 isFull = " + isFull);
            }
        });
        btnDeleteOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(list.size() - 1);
//                adapter.notifyDataSetChanged();
                recyclerView.getAdapter().notifyDataSetChanged();
                LogUtil.i("LHD adapter.getItemCount() = " + adapter.getItemCount() + "    recyclerView.getChildCount() = " + recyclerView.getChildCount()
                        + "   Manager().getChildCount() =  " + recyclerView.getLayoutManager().getChildCount() + "   Manager().getItemCount() = " + recyclerView.getLayoutManager().getItemCount());
                boolean isFull = Utils.isRecyclerViewFullscreen(recyclerView, recyclerView.getHeightCount());
                LogUtil.i("是否全屏 isFull = " + isFull);
            }
        });
    }

}
