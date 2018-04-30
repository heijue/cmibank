package cn.app.yimirong.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by android on 2016/3/14 0014.
 */
public class PhoneUtils {

	public static void call(Context context, Uri uri) {
		if (context != null) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(uri);
			context.startActivity(intent);
		}
	}

	public static void call(Context context, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		call(context, uri);
	}

}
