package cn.app.yimirong.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.app.yimirong.log.Logger;

public class SmsUtils {

	private static final String TAG = "SmsUtils";

	/**
	 * 获取指定号码的短信验证码
	 *
	 * @param context
	 * @param address 发送号码
	 * @return
	 */
	public static final String getSmsVerifyCode(Context context, String address) {
		Uri uri = Uri.parse("content://sms/");
		ContentResolver resolver = context.getContentResolver();
		String[] projection = new String[]{"body"};
		String where = "address = " + address + " AND date > "
				+ (System.currentTimeMillis() - 10 * 60 * 1000);

		Cursor cursor = resolver.query(uri, projection, where, null,
				"date desc");
		if (cursor != null) {
			if (cursor.moveToNext()) {
				String body = cursor.getString(cursor.getColumnIndex("body"));
				Logger.getInstance().i(TAG, "短信内容：" + body);
				// 解析body

			}
		}

		return null;
	}

}
