package study.com.purerecyclerview.freshlayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import study.com.purerecyclerview.R;

/**
 * Created by  HONGDA on 2019/1/3.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ItemHolder> {

    private List<String> list;

    public TestAdapter(List<String> list) {
        Log.i("LHD", "TestAdapter初始化");
        this.list = list;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        Log.i("LHD", "TestAdapter onBindViewHolder = " + list.get(i) + "   i = " + i);
        itemHolder.tv.setText(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tv;

        public ItemHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }


}
