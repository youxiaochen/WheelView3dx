package chen.you.wheel;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by you on 2019/9/25.
 * 作QQ:86207610
 */

final class WheelUtils {

    private WheelUtils() {}

    static int sss(int itemSize) {
        return 0;
    }

    /**
     * 通过三角一边和弧度计算出半径大小
     * @param itemSize
     * @param degree
     * @return
     */
    static PointF triangleDegree(int itemSize, float degree) {
        double radians = Math.toRadians(degree / 2.0f);
        float sizeLength = itemSize / 2.f;
        float height = (float) (sizeLength / Math.tan(radians));
        float radius = (float) (sizeLength / Math.sin(radians));
        return new PointF(height, radius);
    }

    /**
     * 根据item的大小(弧的长度),和item对应的旋转角度,计算出滑轮轴的半径
     * @param radian
     * @param degree
     * @return
     */
    static double radianToRadio(int radian, float degree) {
        return (radian / 2.f) / Math.tan(Math.toRadians(degree / 2.f));
    }

    /**
     * 根据方向代码创建view layoutparams
     *
     * 如果水平布局时,最好指定高度大小,  垂直布局时最定宽度大小
     *
     * @param orientation
     * @param size
     * @return
     */
    static FrameLayout.LayoutParams createLayoutParams(int orientation, int size) {
        if (orientation == WheelView.WHEEL_VERTICAL) {
            return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
        } else {
            return new FrameLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * 获取控件的中心x位置
     * @param view
     * @return
     */
    static int getViewCenterX(View view) {
        return (view.getLeft() + view.getRight()) >> 1;
    }

    /**
     * 获取控件的中心y位置
     * @param view
     * @return
     */
    static int getViewCenterY(View view) {
        return (view.getTop() + view.getBottom()) >> 1;
    }

}