package study.com.purerecyclerview.freshlayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by  HONGDA on 2019/1/3.
 */
public class LoadMoreAdapter<T extends RecyclerView.Adapter> extends HeadAndFootAdapter {

    private static final int ITEM_TYPE_LOAD = 1;
    private static final int ITEM_TYPE_BOTTOM = 2;

    private Context context;
    private T realAdapter;

    private View loadMoreView;//上拉加载更多的View
    private View bottomView;//通过改变bottomView的高度来实现上拉

    public LoadMoreAdapter(Context context, T realAdapter) {
        super(realAdapter);
        Log.i("LHD", "LoadMoreAdapter初始化");
        this.context = context;
        this.realAdapter = realAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int key) {
        if (key == ITEM_TYPE_LOAD) {
            return new RecyclerView.ViewHolder(loadMoreView) {
            };
        }
        if (key == ITEM_TYPE_BOTTOM) {
            return new RecyclerView.ViewHolder(bottomView) {
            };
        }
        return super.onCreateViewHolder(viewGroup, key);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (isLoadMorePosition(position) || isBottomPosition(position)) return;
        super.onBindViewHolder(viewHolder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //判断是否滑动到底部，需要显示loadMoreView
    private boolean isLoadMorePosition(int position) {
        if (loadMoreView == null) return false;
        return position == getItemCount() - 2;
    }

    //判断是否滑动到底部，需要显示bottomView
    private boolean isBottomPosition(int position) {
        if (bottomView == null) return false;
        return position == getItemCount() - 1;
    }

    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        notifyItemChanged(getItemCount() - 2);
    }

    public void setBottomView(View bottomView) {
        this.bottomView = bottomView;
        notifyItemChanged(getItemCount() - 1);
    }

}
