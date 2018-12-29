package study.com.purerecyclerview.headfoot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import study.com.purerecyclerview.R;
import study.com.purerecyclerview.freshlayout.adapter.HeadAndFootAdapter;

public class HeadFootAdapter extends HeadAndFootAdapter {

    private List<String> list;

    public HeadFootAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int key) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder itemHolder = (ItemHolder) viewHolder;
        itemHolder.tv.setText(list.get(position));
    }

//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tv;

        public ItemHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }

}
