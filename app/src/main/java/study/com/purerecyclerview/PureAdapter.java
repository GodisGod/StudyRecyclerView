package study.com.purerecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
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

    private List<View> headViews;
    private List<View> footViews;

    private int headSize = 0;
    private int footSize = 0;
    private int headAndNormalSize = 0;

    public PureAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        headAndNormalSize = list.size() + headSize;
        inflater = LayoutInflater.from(context);
        headViews = new ArrayList<>();
        footViews = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int type = getItemViewType(i);
        Log.i("LHD", "type = " + type);
        if (type == TYPE_HEAD) {
            View view = inflater.inflate(R.layout.head, viewGroup, false);
            return new HeadHolder(view);
        } else if (type == TYPE_NORMAL) {
            View view = inflater.inflate(R.layout.item, viewGroup, false);
            return new NormalHolder(view);
        } else {
            View view = inflater.inflate(R.layout.foot, viewGroup, false);
            return new FootHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HeadHolder) {
            HeadHolder headHolder = (HeadHolder) viewHolder;
        } else if (viewHolder instanceof NormalHolder) {
            NormalHolder normalHolder = (NormalHolder) viewHolder;
            normalHolder.textView.setText(list.get(getNormalPosition(i)));
        } else {
            FootHolder footHolder = (FootHolder) viewHolder;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + headViews.size() + footViews.size();
    }

    //    private View getItemView(int position) {
//        if (position < headSize) {
//            return headViews.get(position);
//        } else if (position < headAndNormalSize) {
//            //todo
//            return null;
//        } else {
//            return footViews.get(position);
//        }
//    }
//
//    private int getHeadPosition(int position) {
//
//    }
//
    private int getNormalPosition(int position) {
        //position = 6 headSize = 3  position:0~2 都是headSize position:3~6是normalSize
        //即：position  head   position  normal
        //          0           0           3           0
        //          1           1           4           1
        //          2           2           5           2
        //所以normalSize = position - headSize
        return position - headViews.size();
    }
//
//    private int getFootPosition(int position) {
//
//    }

    @Override
    public int getItemViewType(int position) {
        //headSize = 4 position = 0 ~ 3
        Log.i("LHD", "position = " + position + "  heasSize = " + headSize + "   head+normal = " + headAndNormalSize);
        if (position < headSize) {
            return TYPE_HEAD;
        } else if (position < headAndNormalSize) {
            return TYPE_NORMAL;
        } else {
            return TYPE_FOOT;
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

    //对外方法
    public void addHeadView(View view) {
        headViews.add(view);
        headSize = headViews.size();
        headAndNormalSize = headSize + getNormalItemSize();
        notifyDataSetChanged();
    }

    public void addFootView(View view) {
        footViews.add(view);
        footSize = footViews.size();
        notifyDataSetChanged();
    }

    private int getNormalItemSize() {
        return list.size();
    }

}
