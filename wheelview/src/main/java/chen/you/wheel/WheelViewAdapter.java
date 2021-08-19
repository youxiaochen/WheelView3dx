package chen.you.wheel;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by you on 2019/9/27.
 * 作QQ: 86207610
 */

class WheelViewAdapter extends RecyclerView.Adapter<WheelViewHolder> {

    /**
     * recyclerview 布局方向
     */
    final int orientation;
    /**
     * 每个item大小
     */
    final int itemSize;
    /**
     * wheelview 滑动时上和下空白总数量
     */
    private final int totalShowItemCount;

    WheelView.WheelAdapter adapter;

    WheelViewAdapter(int orientation, int itemSize, int showItemCount) {
        this.orientation = orientation;
        this.itemSize = itemSize;
        this.totalShowItemCount = showItemCount * 2;
    }

    @Override
    public void onBindViewHolder(WheelViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return totalShowItemCount + (adapter == null ? 0 : adapter.getItemCount());
    }

    @Override
    public WheelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new View(parent.getContext());
        view.setLayoutParams(WheelUtils.createLayoutParams(orientation, itemSize));
        return new WheelViewHolder(view);
    }

    String getItemString(int index) {
        return adapter == null ? "" : adapter.getItem(index);
    }

}
