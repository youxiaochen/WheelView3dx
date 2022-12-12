package chen.you.wheelviewtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * author: you : 2022/12/12
 */
public final class TestView extends View {

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams params = getLayoutParams();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                Log.d("youxiaochen", "EXACTLY... " + width + "  " + params.width);
                break;
            case MeasureSpec.AT_MOST:
                Log.d("youxiaochen", "AT_MOST... " + width + "  " + params.width);
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.d("youxiaochen", "UNSPECIFIED... " + width + "  " + params.width);
                break;
        }




        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
