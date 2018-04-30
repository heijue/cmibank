package cn.app.yimirong.model.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.bean.SevenProfit;
import cn.app.yimirong.model.db.DBHelper;

/**
 * Created by xiaor on 2016/6/7.
 */
public class OfflineDao {

	private SQLiteOpenHelper helper;


	private static OfflineDao instance;

	private OfflineDao(Context context) {
		helper = new DBHelper(context, Constant.DB_NAME);
	}

	public synchronized static OfflineDao getInstance(Context context) {
		if (instance == null) {
			instance = new OfflineDao(context);
		}
		return instance;
	}

	public void insertOfflineInfo(List<SevenProfit> list) {

		if (selectOffLineInfo().size() > 0) {
			clear();
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		for (int i = 0; i < list.size(); i++) {
			SevenProfit sevenProfit = list.get(i);

			ContentValues values = new ContentValues();
			values.put("date", sevenProfit.date);
			values.put("profit", sevenProfit.profit);
			db.insert(Constant.TABLE_NAME_OFFLINE, null, values);

			// db.execSQL("insert into "+Constant.TABLE_NAME_OFFLINE+"(income) values(?)", new String[]{sevenProfit.income});
		}
		db.close();
	}

	public List<SevenProfit> selectOffLineInfo() {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<SevenProfit> list = new ArrayList<>();
		Cursor c = db.rawQuery("select * from " + Constant.TABLE_NAME_OFFLINE, null);
		SevenProfit sevenProfit = null;
		while (c.moveToNext()) {
			sevenProfit = new SevenProfit();
			sevenProfit.date = c.getString(0);
			sevenProfit.profit = c.getString(1);
			list.add(sevenProfit);
		}
		c.close();
		db.close();
		return list;
	}

	public void clear() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from " + Constant.TABLE_NAME_OFFLINE);
		db.close();
	}

}
