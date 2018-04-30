package cn.app.yimirong.model.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.model.db.DBHelper;

/**
 * Created by android on 2016/4/5 0005.
 */
public class MsgDao {

	private SQLiteOpenHelper helper;

	private static MsgDao instance;

	public synchronized static MsgDao getInstance(Context context) {
		if (instance == null) {
			instance = new MsgDao(context);
		}
		return instance;
	}

	private MsgDao(Context context) {
		helper = new DBHelper(context, Constant.DB_NAME);
	}

	public boolean isExists(Message msg) {
		boolean exist = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			String[] columns = new String[]{"nid"};
			Cursor cursor = db.query(Constant.TABLE_NAME_MESSAGES, columns, "nid=?", new String[]{msg.nid}, null, null, null, null);
			if (cursor.moveToNext()) {
				exist = true;
			}
			cursor.close();
			db.close();
		}
		return exist;
	}

	public int insert(List<Message> list) {
		int i = 0;
		SQLiteDatabase db = null;
		for (Message msg : list) {
			if (!isExists(msg)) {
				db = helper.getWritableDatabase();
				if (db.isOpen() && !db.isReadOnly()) {
					ContentValues values = new ContentValues();
					values.put("nid", msg.nid);
					values.put("isread", msg.isread ? 1 : 0);
					values.put("title", msg.title);
					values.put("content", msg.content);
					values.put("ctime", msg.ctime);
					values.put("phonetype", msg.phonetype);
					values.put("onlinetime", msg.onlinetime);
					values.put("status", msg.status);
					values.put("type", msg.type);
					values.put("yugaotime", msg.yugaotime);
					long insert = db.insert(Constant.TABLE_NAME_MESSAGES, null, values);
					if (insert == 1) {
						i++;
					}
				}
			}
		}
		if (db != null) {
			db.close();
		}
		return i;
	}

	public List<Message> queryAll() {
		List<Message> list = new ArrayList<>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			String[] columns = new String[]{
					"nid",
					"isread",
					"title",
					"content",
					"ctime",
					"phonetype",
					"onlinetime",
					"status",
					"type",
					"yugaotime"
			};
			Cursor cursor = db.query(Constant.TABLE_NAME_MESSAGES, columns, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Message msg = new Message();
				msg.nid = cursor.getString(0);
				msg.isread = cursor.getInt(1) == 1 ? true : false;
				msg.title = cursor.getString(2);
				msg.content = cursor.getString(3);
				msg.ctime = cursor.getString(4);
				msg.phonetype = cursor.getString(5);
				msg.onlinetime = cursor.getString(6);
				msg.status = cursor.getString(7);
				msg.type = cursor.getString(8);
				msg.yugaotime = cursor.getString(9);
				list.add(msg);
			}
			cursor.close();
			db.close();
		}
		return list;
	}

	public boolean update(Message msg) {
		boolean result = false;
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen() && !db.isReadOnly()) {
			ContentValues values = new ContentValues();
			values.put("nid", msg.nid);
			values.put("isread", msg.isread ? 1 : 0);
			values.put("title", msg.title);
			values.put("content", msg.content);
			values.put("ctime", msg.ctime);
			values.put("phonetype", msg.phonetype);
			values.put("onlinetime", msg.onlinetime);
			values.put("status", msg.status);
			values.put("type", msg.type);
			values.put("yugaotime", msg.yugaotime);
			int update = db.update(Constant.TABLE_NAME_MESSAGES, values, "nid=?", new String[]{msg.nid});
			if (update == 1) {
				result = true;
			}
			db.close();
		}
		return result;
	}
}
