package cn.app.yimirong.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ouyangsiping on 16/5/8.
 */
public class MyViewpage extends ViewPager {
	private View ignoreView;

	public MyViewpage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewpage(Context context) {
		super(context);
	}

	public View getIgnoreView() {
		return ignoreView;
	}

	public void setIgnoreView(View ignoreView) {
		this.ignoreView = ignoreView;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ignoreView != null) {
			Rect rect = new Rect();
			ignoreView.getHitRect(rect);
//		    	Log.i("gdc", "rect.top"+rect.top);
//		    	Log.i("gdc", "rect.bottom"+rect.bottom);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) return false;

		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (ignoreView != null) {
			Rect rect = new Rect();
			ignoreView.getHitRect(rect);
//		    	Log.i("gdc", "rect.top"+rect.top);
//		    	Log.i("gdc", "rect.bottom"+rect.bottom);
			if (rect.contains((int) event.getX(), (int) event.getY())) return true;

		}
		return super.onTouchEvent(event);


	}


//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                if (this.getParent() != null && this.getParent() instanceof ViewPager) {
//                    this.getParent().requestDisallowInterceptTouchEvent(true);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                if (this.getParent() != null && this.getParent() instanceof ViewPager) {
//                    this.getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                break;
//        }
//
//
//    }

}







