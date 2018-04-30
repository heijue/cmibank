package cn.app.yimirong.model.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.db.DBHelper;

@SuppressWarnings("deprecation")
public class CookieDao {

	private SQLiteOpenHelper helper;

	private static CookieDao dao;

	public synchronized static CookieDao getInstance(Context context) {
		if (dao == null) {
			dao = new CookieDao(context);
		}
		return dao;
	}

	private CookieDao(Context context) {
		helper = new DBHelper(context, Constant.DB_NAME);
	}

	/**
	 * 增
	 *
	 * @param cookies
	 */
	public boolean insert(List<Cookie> cookies) {
		if (cookies == null || cookies.isEmpty()) {
			return false;
		}

		SQLiteDatabase db = helper.getWritableDatabase();
		if (db != null && db.isOpen()) {
			int i = 0;
			for (Cookie cookie : cookies) {
				ContentValues values = new ContentValues();
				values.put("name", cookie.getName());
				values.put("value", cookie.getValue());
				values.put("comment", cookie.getComment());
				values.put("domain", cookie.getDomain());
				long time = 0;
				if (cookie.getExpiryDate() != null) {
					time = cookie.getExpiryDate().getTime();
				}
				values.put("expiryDate", time);
				values.put("path", cookie.getPath());
				values.put("version", cookie.getVersion());
				values.put("isSecure", cookie.isSecure() ? 1 : 0);
				db.insert(Constant.TABLE_NAME_COOKIES, null,
						values);
				i++;
			}
			db.close();
			if (i == cookies.size()) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	/**
	 * 删除所有
	 */
	public void deleteAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db != null && db.isOpen()) {
			String sql = "DELETE FROM " + Constant.TABLE_NAME_COOKIES;
			db.execSQL(sql);
			db.close();
		}
	}

	/**
	 * 查
	 */
	public List<Cookie> queryAll() {
		List<Cookie> cookies = null;
		SQLiteDatabase db = null;
		try {
			db = helper.getReadableDatabase();
		} catch (SQLiteDiskIOException e) {
			db = null;
		}
		if (db != null && db.isOpen()) {
			String[] columns = new String[]{"name", "value", "comment",
					"domain", "expiryDate", "path", "version", "isSecure"};
			Cursor cursor = db.query(Constant.TABLE_NAME_COOKIES, columns,
					null, null, null, null, null, null);
			cookies = new ArrayList<>();
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				String value = cursor.getString(1);
				BasicClientCookie cookie = new BasicClientCookie(name, value);
				cookie.setComment(cursor.getString(2));
				cookie.setDomain(cursor.getString(3));
				long time = cursor.getLong(4);
				if (time != 0) {
					cookie.setExpiryDate(new Date(time));
				}
				cookie.setPath(cursor.getString(5));
				cookie.setVersion(cursor.getInt(6));
				cookie.setSecure(cursor.getInt(7) == 1 ? true : false);
				cookies.add(cookie);
			}
			cursor.close();
			db.close();
		}
		return cookies;
	}
}
