package cn.app.yimirong.utils;

import android.content.Context;

/**
 * Created by android on 2016/1/13 0013.
 */
public class DisplayUtils {

	/**
	 * sp转px
	 *
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, int spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
