package study.com.purerecyclerview.freshlayout;

import android.view.View;

/**
 * Created by  HONGDA on 2018/12/17.
 */
public interface FooterView {

    /**
     * 开始下拉
     */
    void begin();

    /**
     * 回调的精度,单位为px
     *
     * @param progress 当前高度
     * @param all      总高度
     */
    void progress(float progress, float all);

    void finishing(float progress, float all);

    void loading();

    /**
     * 看不见的状态
     */
    void normal();

    View getView();

}
