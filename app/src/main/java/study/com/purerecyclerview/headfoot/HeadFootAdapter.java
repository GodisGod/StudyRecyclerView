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

    public HeadFootAdapter(RecyclerView.Adapter realAdapter) {
        super(realAdapter);
    }

//    public HeadFootAdapter(List<String> list) {
//        this.list = list;
//    }
//
//    @Override
//    protected ItemHolder onCreateItemViewHolder(@NonNull ViewGroup viewGroup, int key) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, null);
//        return new ItemHolder(view);
//    }
//
//    @Override
//    protected void onBindItemViewHolder(@NonNull ItemHolder itemHolder, int position) {
//        itemHolder.tv.setText(list.get(position));
//    }
//
//    @Override
//    protected int getRealCount() {
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
