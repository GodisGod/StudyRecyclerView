package study.com.purerecyclerview.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class Utils {

    /**
     * 根据传入的真实item数量和每个itemView的高度计算是否占满全屏
     * @param recyclerView
     * @param realCount
     * @return
     */
    public static boolean isRecyclerViewFullscreen(RecyclerView recyclerView, int realCount) {
//        int actionBarHeight = getActionBarHeight(recyclerView.getContext());
//        int statusBarHeight = getStatusBarHeight(recyclerView.getContext());
//        int totalItemCount = recyclerView.getAdapter().getItemCount();//headCount + realItemCount
//        int lastVisibleItemPosition = findLastVisibleItemPosition(recyclerView);
//        int lastItemPosition = totalItemCount - headCount;
//        //获取屏幕上最后一个可见的view
//        View lastVisibleView = recyclerView.getLayoutManager().findViewByPosition(totalItemCount - 1);//最后一个count的view
//        //recyclerView.getChildCount()：recyclerView保持的view个数
//        LogUtil.i("recyclerView.getLayoutManager().getItemCount() = " + recyclerView.getLayoutManager().getItemCount() + "   recyclerView.getChildCount() = " + recyclerView.getChildCount());
//        LogUtil.e("lastVisibleItemPosition = " + lastVisibleItemPosition);
//        for (int i = 0; i < totalItemCount; i++) {
//            View view = recyclerView.getLayoutManager().findViewByPosition(i);
//            LogUtil.i("获取itemView = " + i + " view = " + view + "    view.getHeight() = " + ((view == null) ? "" : view.getHeight()));
//        }
//        //判断是否是最后一个view
//        if (lastItemPosition > 0) {//不是最后一个view
////            View nextView = recyclerView.getLayoutManager().findViewByPosition(lastItemPosition);
//            Log.e("LHD", "isRecyclerViewFullscreen = lastItemPosition = " + lastItemPosition + "   headCount = " + headCount + "  totalItemCount = " + totalItemCount + "  statusBarHeight = " + statusBarHeight);
//            if (lastVisibleView == null) return false;
//            int nextViewHeight = lastVisibleView.getHeight();
//            if (lastVisibleView != null) {
//                int[] location = new int[2];
//                lastVisibleView.getLocationOnScreen(location);
//                int x = location[0];
//                int y = location[1];
        int screenHeight = getScreenHeight(recyclerView.getContext());
//                int viewHeight = lastVisibleView.getHeight();
//
//                int viewBottomToTop = y + viewHeight + nextViewHeight;//最后一个完全可见的view的下一个view的底部距离顶部的高度
//                LogUtil.i("isRecyclerViewFullscreen x = " + x + "  y = " + y + "    screenHeight = " + screenHeight + "   lastItemPosition = " + lastItemPosition
//                        + "   viewHeight = " + viewHeight + "   viewBottomToTop = " + viewBottomToTop);
//                if (viewBottomToTop > (screenHeight - statusBarHeight)) {
//                    LogUtil.i(">>>>>>>>>>>>>>isRecyclerViewFullscreen = 全屏");
//                    return true;
//                } else {
//                    LogUtil.i(">>>>>>>>>>>>>>isRecyclerViewFullscreen = 未满全屏");
//                    return false;
//                }
//            }
//
//        } else {//是最后一个view
//            return false;
//        }
//        boolean isFull = recyclerView.getChildCount() < recyclerView.getLayoutManager().getItemCount();
//        LogUtil.e("recyclerView.getChildCount() = " + recyclerView.getChildCount() + "    recyclerView.getLayoutManager().getItemCount() = " + recyclerView.getLayoutManager().getItemCount());
//        /*(position[1] + lastchild.getHeight() + lastBottomMargin + bottomMargin + padding) >= height || */
//        LogUtil.i("是否全屏 isFull = " + isFull);

//        final boolean[] isFull = new boolean[1];
////        通过是否滑动到底部来判断是否满屏
//        RecycleViewScrollHelper recycleViewScrollHelper = new RecycleViewScrollHelper(new RecycleViewScrollHelper.OnScrollPositionChangedListener() {
//            @Override
//            public void onScrollToTop() {
//                LogUtil.i("onScrollToTop");
//            }
//
//            @Override
//            public void onScrollToBottom() {
//                LogUtil.i("onScrollToBottom");
//                isFull[0] = true;
//            }
//
//            @Override
//            public void onScrollToUnknown(boolean isTopViewVisible, boolean isBottomViewVisible) {
//                LogUtil.i("onScrollToUnknown  isTopViewVisible = " + isTopViewVisible + "  isBottomViewVisible = " + isBottomViewVisible);
//            }
//        });
//        recycleViewScrollHelper.attachToRecycleView(recyclerView);
//        LogUtil.i("是否全屏 isFull = " + isFull + "    " + isFull[0]);
        boolean isFull = false;
        int firstPosition = findFirstCompletelyVisibleItemPosition(recyclerView);
        int lastPosition = findLastVisibleItemPosition(recyclerView);
        int viewHeight = DisplayUtil.dp2Px(recyclerView.getContext(), 200f);
        int totalHeight = viewHeight* realCount;
        isFull = totalHeight > screenHeight;
        LogUtil.e("realCount = " + realCount + "  firstPosition = " + firstPosition + "  lastPosition = " + lastPosition + "  totalHeight = " + totalHeight + "  screenHeight = " + screenHeight + " isFull = " + isFull+"   viewHeight = "+viewHeight);

        return isFull;
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

    public static int getActionBarHeight(Context context) {
        TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float h = actionbarSizeTypedArray.getDimension(0, 0);
        int actionBarHeight = (int) h;
        return actionBarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        LogUtil.i("状态栏高度 = " + result);
        return result;
    }

}
