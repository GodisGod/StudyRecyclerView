package study.com.purerecyclerview.freshlayout.adapter;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by  HONGDA on 2018/12/24.
 */
public class HeadAndFootAdapter<T extends RecyclerView.Adapter> extends RecyclerView.Adapter {

    private static int BASE_ITEM_TYPE_HEAD = 1000;
    private static int BASE_ITEM_TYPE_FOOT = 2000;

    //头部view底部view的容器
    protected SparseArrayCompat<View> headViews;
    protected SparseArrayCompat<View> footViews;

    protected T realAdapter;

    public HeadAndFootAdapter(T realAdapter) {
        //别忘记调用父类的构造函数
        super();
        headViews = new SparseArrayCompat<>();
        footViews = new SparseArrayCompat<>();
        this.realAdapter = realAdapter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int key) {
        //如果是头部
        if (isHeadKey(key)) {
            //根据key获取头部的position
            int headPosition = headViews.indexOfKey(key);
            //根据position获取view
            View headView = headViews.valueAt(headPosition);
            return new HeadHolder(headView);
        }
        if (isFootKey(key)) {
            int footPosition = footViews.indexOfKey(key);
            View footView = footViews.valueAt(footPosition);
            return new FootHolder(footView);
        }
        return realAdapter.onCreateViewHolder(viewGroup, key);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (isHead(position) || isFoot(position)) {

        } else {
            int itemPosition = position - getHeadCount();
            realAdapter.onBindViewHolder(viewHolder, itemPosition);
        }
    }

    @Override
    public int getItemCount() {
        return getHeadCount() + getRealCount() + getFootCount();
    }


    @Override
    public int getItemViewType(int position) {
        if (isHead(position)) {
            return headViews.keyAt(position);//每一个headView对应一个viewType (0 - headViews.size )
        }
        if (isFoot(position)) {//每一个footView对应一个viewType
            return footViews.keyAt(position - getHeadCount() - getRealCount());
        }
        //查询源码可知，当不调用getItemViewType方法的时候，也就是只有一种布局类型的时候，默认返回是0
        //这里我们需要返回真实的adapter的position
        Log.i("LHD", "");
        return realAdapter.getItemViewType(position - getHeadCount());
    }

    public void addHeadView(View view) {
        headViews.put(BASE_ITEM_TYPE_HEAD++, view);
        int realPosition = headViews.indexOfValue(view);
        notifyItemInserted(realPosition);
    }

    public void addFootView(View view) {
        footViews.put(BASE_ITEM_TYPE_FOOT++, view);
        int realPosition = footViews.indexOfValue(view) + getHeadCount() + getRealCount();
        notifyItemInserted(realPosition);
    }

    public void removeHeadView(View view) {
        int realPosition = headViews.indexOfValue(view);
        if (realPosition < 0) return;
        headViews.removeAt(realPosition);
        notifyItemRemoved(realPosition);
    }

    public void removeFootView(View view) {
        int index = footViews.indexOfValue(view);
        if (index < 0) return;
        footViews.removeAt(index);
        index = index + getHeadCount() + getRealCount();
        notifyItemRemoved(index);
    }

    /**
     * 解决GridLayoutManager添加head和foot的问题
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            //这个方法返回的是当前位置的 item 跨度大小,也就是当前item占据几个item的位置
            //返回1代表，当前item占据1个item的位置，返回2代表当前item占据两个item的位置，以此类推
            //返回gridLayoutManager.getSpanCount()代表占据SpanCount个item的位置
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHead(position) || isFoot(position)) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }


        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * 解决瀑布流的问题
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (isHead(position) || isFoot(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
                layoutParams.setFullSpan(true);
            }
        } else {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
                layoutParams.setFullSpan(false);
            }
        }
    }

    /**
     * 判断是否是头部
     */
    protected boolean isHead(int position) {
        return position < getHeadCount();
    }

    //key 即是 viewType
    private boolean isHeadKey(int key) {
        return headViews.indexOfKey(key) >= 0;
    }

    private boolean isFootKey(int key) {
        return footViews.indexOfKey(key) >= 0;
    }

    /**
     * 判断是否是底部
     */
    protected boolean isFoot(int position) {
        return position >= getHeadCount() + getRealCount();
    }

    /**
     * @return 返回头部view数量
     */
    public int getHeadCount() {
        return headViews.size();
    }

    /**
     * @return 返回底部view数量
     */
    public int getFootCount() {
        return footViews.size();
    }

    /**
     * @return 返回真实的item数量
     */
    private int getRealCount() {
        return realAdapter.getItemCount();
    }

    //viewHolder
    private class HeadHolder extends RecyclerView.ViewHolder {
        public HeadHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class FootHolder extends RecyclerView.ViewHolder {
        public FootHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
