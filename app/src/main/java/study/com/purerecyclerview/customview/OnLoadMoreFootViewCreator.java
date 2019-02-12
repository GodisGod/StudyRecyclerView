package study.com.purerecyclerview.customview;

import android.content.Context;
import android.view.View;

/**
 * Created by  鸿达 on 2019/2/12.
 * 没有bug(*^▽^*)
 */
public interface OnLoadMoreFootViewCreator {

    /**
     * 上拉
     *
     * @param distance 滑动距离
     */
    void startPull(float distance);

    /**
     * 松手加载更多
     */
    void releaseToLoadMore(float distance);

    void executeAnim(int mark);

    /**
     * 加载中
     */
    void loading();

    /**
     * 加载结束
     */
    void finishLoading();

    View getLoadMoreView(Context context);

    //由于自定义的LoadMoreView可能多种多样并且高度不一，
    // 所以我们需要在自定义的recyclerView里进行添加操作
    //这样只让自定义的LoadMoreView负责构建View即可
//    boolean addFootView(Context context, RecyclerView recyclerView);

    int getLoadMoreViewHeight(Context context);
}
