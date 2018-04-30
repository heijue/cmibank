package cn.app.yimirong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by xiaor on 2016/6/16.
 */
public class StationaryGridview extends GridView {
	public StationaryGridview(Context context) {
		super(context);
	}

	public StationaryGridview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StationaryGridview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//重写dispatchTouchEvent方法禁止GridView滑动
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}