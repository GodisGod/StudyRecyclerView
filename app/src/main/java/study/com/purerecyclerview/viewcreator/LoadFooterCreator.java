package study.com.purerecyclerview.viewcreator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016/9/28.
 */
public interface LoadFooterCreator {

    /**
     * 上拉
     *
     * @param distance 距离
     * @return 是否继续上拉
     */
    boolean onStartPull(float distance, int lastState);

    /**
     * 松手加载
     *
     * @param distance 距离
     * @return 是否继续上拉
     */
    boolean onReleaseToLoad(float distance, int lastState);

    /**
     * 开始加载
     */
    void onStartLoading();

    /**
     * 加载结束
     */
    void onStopLoad();

    View getLoadView(Context context, RecyclerView recyclerView);

    View getNoMoreView(Context context, RecyclerView recyclerView);

    ;

}
