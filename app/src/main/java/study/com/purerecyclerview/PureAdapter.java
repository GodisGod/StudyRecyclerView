package study.com.purerecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by  HONGDA on 2018/12/6.
 */
public class PureAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> list;
    private LayoutInflater inflater;
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_NORMAL = 1;
    private final static int TYPE_FOOT = 2;

    public PureAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int type = getItemViewType(i);
        if (type == TYPE_HEAD) {
            View view = inflater.inflate(R.layout.head, viewGroup, false);
            HeadHolder headHolder = new HeadHolder(view);
            return headHolder;
        } else {
            View view = inflater.inflate(R.layout.item, viewGroup, false);
            return new NormalHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HeadHolder) {
            HeadHolder headHolder = (HeadHolder) viewHolder;
        } else {
            NormalHolder normalHolder = (NormalHolder) viewHolder;
            normalHolder.textView.setText(list.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_NORMAL;
        }
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public NormalHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item);
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder {
        public HeadHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        public FootHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
