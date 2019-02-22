package study.com.purerecyclerview.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class Utils {

    public static boolean isRecyclerViewFullscreen(RecyclerView recyclerView, int headCount) {
        int itemCount = recyclerView.getAdapter().getItemCount();//headCount + realItemCount
        int lastVisibleItemPosition = findLastVisibleItemPosition(recyclerView);
        int itemPosition = lastVisibleItemPosition - headCount;
        //获取屏幕上最后一个可见的view
        View lastVisibleView = recyclerView.getLayoutManager().findViewByPosition(lastVisibleItemPosition);
        //判断是否是最后一个view
        if (lastVisibleItemPosition < itemCount) {//不是最后一个view
            View nextView = recyclerView.getLayoutManager().findViewByPosition(lastVisibleItemPosition + 1);
            Log.e("LHD", "isRecyclerViewFullscreen = lastVisibleItemPosition = " + lastVisibleItemPosition + "   headCount = " + headCount + "  itemCount = " + itemCount
                    + "  itemPosition = " + itemPosition);
            if (nextView == null) return false;
            int nextViewHeight = nextView.getHeight();
            if (lastVisibleView != null) {
                int[] location = new int[2];
                lastVisibleView.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int screenHeight = getScreenHeight(recyclerView.getContext());
                int viewHeight = lastVisibleView.getHeight();
                int viewBottomToTop = y + viewHeight + nextViewHeight;//最后一个完全可见的view的下一个view的底部距离顶部的高度
                LogUtil.i("isRecyclerViewFullscreen x = " + x + "  y = " + y + "    screenHeight = " + screenHeight + "   lastVisibleItemPosition = " + lastVisibleItemPosition
                        + "   viewHeight = " + viewHeight + "   viewBottomToTop = " + viewBottomToTop);
                if (viewBottomToTop > screenHeight) {
                    LogUtil.i(">>>>>>>>>>>>>>isRecyclerViewFullscreen = 全屏");
                    return true;
                } else {
                    LogUtil.i(">>>>>>>>>>>>>>isRecyclerViewFullscreen = 未满全屏");
                    return false;
                }
            }

        } else {//是最后一个view
            return false;
        }

        /*(position[1] + lastchild.getHeight() + lastBottomMargin + bottomMargin + padding) >= height || */
        return false;
    }

    public static int findFirstCompletelyVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPosition;
        if (layoutManager instanceof LinearLayoutManager) {
            firstPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            firstPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(lastPositions);
            firstPosition = findMin(lastPositions);
        } else {
            throw new RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }
        return firstPosition;
    }

    public static int findLastVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int lastPosition;
        if (layoutManager instanceof LinearLayoutManager) {
            lastPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            lastPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
            lastPosition = findMax(lastPositions);
        } else {
            throw new RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }
        return lastPosition;
    }

    public static int findMax(int[] lastPositions) {
        int max = 0;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION) {
                if (max < value) max = value;
            }
        }
        return max;
    }

    public static int findMin(int[] firstPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : firstPositions) {
            if (value != RecyclerView.NO_POSITION) {
                if (min > value) min = value;
            }
        }
        return min;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
