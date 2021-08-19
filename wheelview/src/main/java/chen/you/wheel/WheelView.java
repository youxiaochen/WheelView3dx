package chen.you.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by you on 2019/9/25.
 * 作QQ:86207610 
 */

public class WheelView extends FrameLayout {
    /**
     * 无效的位置
     */
    public static final int IDLE_POSITION = -1;
    /**
     * 垂直与水平布局两种状态
     */
    public static final int WHEEL_VERTICAL = 1;
    public static final int WHEEL_HORIZONTAL = 2;
    /**
     * item color
     */
    private int textColor = Color.BLACK;
    /**
     * 中心item颜色
     */
    private int textColorCenter = Color.RED;
    /**
     * 分割线颜色
     */
    private int dividerColor = Color.BLACK;
    /**
     * 文本大小
     */
    private float textSize = 36.f;
    /**
     * item数量
     */
    private int itemCount = 3;
    /**
     * item大小
     */
    private int itemSize = 90;
    /**
     * 分割线之间距离
     */
    private int dividerSize = 90;
    /**
     * 布局方向
     */
    private int orientation = WHEEL_VERTICAL;
    /**
     * 对齐方式
     */
    private int gravity = WheelDecoration.GRAVITY_CENTER;

    /**
     * recyclerView
     */
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    /**
     * wheel 3d
     */
    private WheelDecoration wheelDecoration;
    private WheelViewAdapter wheelAdapter;
    /**
     * adapter
     */
    private WheelAdapter mAdapter;
    private WheelViewObserver observer;

    private WheelItemClickListener itemClickListener;

    private int lastSelectedPosition = IDLE_POSITION;
    private int selectedPosition = IDLE_POSITION;

    public WheelView(Context context) {
        super(context);
        init(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
            itemCount = a.getInt(R.styleable.WheelView_wheelItemCount, itemCount);
            textColor = a.getColor(R.styleable.WheelView_wheelTextColor, textColor);
            textColorCenter = a.getColor(R.styleable.WheelView_wheelTextColorCenter, textColorCenter);
            dividerColor = a.getColor(R.styleable.WheelView_wheelDividerColor, dividerColor);
            textSize = a.getDimension(R.styleable.WheelView_wheelTextSize, textSize);
            itemSize = a.getDimensionPixelOffset(R.styleable.WheelView_wheelItemSize, itemSize);
            dividerSize = a.getDimensionPixelOffset(R.styleable.WheelView_wheelDividerSize, dividerSize);
            orientation = a.getInt(R.styleable.WheelView_wheelOrientation, orientation);
            gravity = a.getInt(R.styleable.WheelView_wheelGravity, gravity);
            a.recycle();
        }
        initRecyclerView(context);
    }

    private void initRecyclerView(Context context) {
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(orientation == WHEEL_VERTICAL ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //让滑动结束时都能定到中心位置
        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);
        //通过计算,在item3个或者以上的时候,旋转后的WheelView高度会比实际高度小一个item的大小
        //计算方式可以详见博客中的原理图,计算出三角形的腰长即为半径
        //item在6个或者以上时,旋转后的高度会相差大于2, 因此3-5个时的效果最好,不会留过多的空白区域
        //此时防止WheelView旋转后的空白区域过多,可以适当修改大小, 适配器中头和尾添加的itemCount - 1
        boolean isGreaterThan = itemCount > 2;
        int showItemCount = isGreaterThan ? itemCount - 1 : itemCount;
        int totalItemSize = (showItemCount * 2 + 1) * itemSize;
        if (isGreaterThan) {
            //RecyclerView高度或者水平时的宽度添加2个像素是为了在减掉显示的一个item时,
            // 超出RecyclerView显示区域刚好可以再显示头部一个和尾部一个item
            totalItemSize += 2;
        }
        float itemDegree = 180.f / (itemCount * 2 + 1);
        super.addView(mRecyclerView, WheelUtils.createLayoutParams(orientation, totalItemSize));
        wheelAdapter = new WheelViewAdapter(orientation, itemSize, showItemCount);
        wheelDecoration = new SimpleWheelDecoration(wheelAdapter, showItemCount, itemDegree, gravity, textColor, textColorCenter, textSize, dividerColor, dividerSize);
        mRecyclerView.addItemDecoration(wheelDecoration);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (listener == null || wheelDecoration == null) return;
                if (wheelDecoration.centerItemPosition == IDLE_POSITION || newState != RecyclerView.SCROLL_STATE_IDLE) return;
                selectedPosition = wheelDecoration.centerItemPosition;
                if (selectedPosition != lastSelectedPosition) {
                    listener.onItemSelected(WheelView.this, selectedPosition);
                    lastSelectedPosition = selectedPosition;
                }
            }
        });
        mRecyclerView.setAdapter(wheelAdapter);
    }

    public void setAdapter(WheelAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.setWheelViewObserver(null);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            if (observer == null) {
                observer = new WheelViewObserver();
            }
            mAdapter.setWheelViewObserver(observer);
            this.selectedPosition = -1;
            this.lastSelectedPosition = -1;
            this.wheelAdapter.adapter = adapter;
            this.wheelAdapter.notifyDataSetChanged();
        }
    }

    public WheelAdapter getAdapter() {
        return mAdapter;
    }

    private void dataSetChanged() {
        this.wheelAdapter.notifyDataSetChanged();
    }

    public void setCurrentItem(int position) {
        layoutManager.scrollToPositionWithOffset(position, 0);
    }

    public int getCurrentItem() {
        int adapterCount = layoutManager.getItemCount();
        if (wheelDecoration.centerItemPosition >= adapterCount) return 0; //如果当前位置大于整个适配器大小,刷新时RecyclerView会回到第0个位置
        int wheelCount = adapterCount - itemCount * 2;
        if (wheelDecoration.centerItemPosition >= wheelCount) {
            return wheelCount -1;
        }
        return wheelDecoration.centerItemPosition;
    }

    private OnItemSelectedListener listener;
    private OnItemClickListener clickListener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * item selected
     */
    public interface OnItemSelectedListener {
        void onItemSelected(WheelView wheelView, int index);
    }

    /**
     * 设置点击
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (itemClickListener == null) {
            itemClickListener = new WheelItemClickListener(getContext()) {
                @Override
                void onItemClick(int position) {
                    int currentPosition = position - itemCount;
                    if (clickListener != null && currentPosition == getCurrentItem()) {
                        clickListener.onItemClick(WheelView.this, currentPosition);
                    }
                }
            };
            mRecyclerView.addOnItemTouchListener(itemClickListener);
        }
        this.clickListener = listener;
    }

    /**
     * item点击
     */
    public interface OnItemClickListener {
        void onItemClick(WheelView wheelView, int centerPosition);
    }

    private class WheelViewObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    /**
     * wheel adapter
     */
    public static abstract class WheelAdapter {

        private DataSetObserver wheelObserver;

        void setWheelViewObserver(DataSetObserver observer) {
            synchronized (this) {
                wheelObserver = observer;
            }
        }

        protected abstract int getItemCount();

        protected abstract String getItem(int index);

        public final void notifyDataSetChanged() {
            synchronized (this) {
                if (wheelObserver != null) {
                    wheelObserver.onChanged();
                }
            }
        }
    }

}
