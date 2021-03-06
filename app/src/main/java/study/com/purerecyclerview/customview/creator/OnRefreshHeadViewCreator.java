package study.com.purerecyclerview.customview.creator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by  鸿达 on 2019/2/21.
 */
public interface OnRefreshHeadViewCreator {
    /**
     * 上拉
     *
     * @param distance 滑动距离
     */
    void startPull(float distance);

    /**
     * 松手加载更多
     */
    void releaseRefresh(float distance);

    /**
     * 执行临界动画
     *
     * @param state 当前状态
     */
    void executeAnim(int state);

    /**
     * 加载中
     */
    void loading();

    /**
     * 加载结束
     */
    void finishRefresh();

    /**
     * 获取加载更多的View
     *
     * @param context
     * @return
     */
    View getRefreshView(Context context, RecyclerView recyclerView);

    //由于自定义的LoadMoreView可能多种多样并且高度不一，
    // 所以我们需要在自定义的recyclerView里进行添加操作
    //这样只让自定义的LoadMoreView负责构建View即可
//    boolean addFootView(Context context, RecyclerView recyclerView);

}
