package cn.app.yimirong.model.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.Quota;
import cn.app.yimirong.model.db.DBHelper;
import cn.app.yimirong.utils.StringUtils;

/**
 * Created by android on 2015/10/24.
 */
public class BankDao {

	private static final String TAG = "BankDao";

	private SQLiteOpenHelper helper;

	private static BankDao dao;

	public synchronized static BankDao getInstance(Context context) {
		if (dao == null) {
			dao = new BankDao(context);
		}
		return dao;
	}

	private BankDao(Context context) {
		helper = new DBHelper(context, Constant.DB_NAME);
	}

	/**
	 * 插入数据
	 *
	 * @param banks
	 * @return
	 */
	public boolean insert(List<Bank> banks) {
		if (banks == null || banks.isEmpty()) {
			return false;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null || !db.isOpen() || db.isReadOnly()) {
			return false;
		}
		int i = 0;
		for (Bank bank : banks) {
			ContentValues values = new ContentValues();
			values.put("code", bank.code);
			values.put("name", bank.name);
			values.put("url", bank.url);
			values.put("plat", bank.plat);
			values.put("single", bank.single);
			values.put("singleDay", bank.singleDay);
			values.put("singleMonth", bank.singelMonth);
			values.put("isShow", bank.isShow ? 1 : 0);
			if (bank.quotas != null) {
				for (Quota quota : bank.quotas) {
					ContentValues values2 = new ContentValues();
					values2.put("bankCode", quota.bankCode);
					values2.put("plat", quota.plat);
					values2.put("single", quota.single);
					values2.put("singleDay", quota.singleDay);
					values2.put("singleMonth", quota.singleMonth);
					db.insert(Constant.TABLE_NAME_QUOTAS, null, values2);
				}
			}
			long insert = db.insert(Constant.TABLE_NAME_BANKS, null, values);
			if (insert == 1) {
				i++;
			}
		}
		db.close();
		if (i != banks.size()) {
			return false;
		}
		return true;
	}

	/**
	 * 清空banks表和quotas表
	 */
	public void clear() {
		Logger.getInstance().d(TAG, "clear");
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
		} catch (Exception e) {
			return;
		}
		if (db == null || !db.isOpen() || db.isReadOnly()) {
			return;
		}
		String sql = "DELETE FROM " + Constant.TABLE_NAME_BANKS;
		String sql2 = "DELETE FROM " + Constant.TABLE_NAME_QUOTAS;
		db.beginTransaction();
		try {
			db.execSQL(sql);
			db.execSQL(sql2);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * 按bank code查询
	 *
	 * @param code
	 * @return
	 */
	public Bank query(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}
		Bank bank = null;
		String[] columns = new String[]{"code", "name", "url", "plat",
				"isShow"};
		String[] selectionArgs = new String[]{code};
		Cursor cursor = db.query(Constant.TABLE_NAME_BANKS, columns, "code=?",
				selectionArgs, null, null, null);
		if (cursor.moveToNext()) {
			bank = new Bank();
			bank.code = cursor.getString(0);
			bank.name = cursor.getString(1);
			bank.url = cursor.getString(2);
			bank.plat = cursor.getString(3);
			bank.isShow = cursor.getInt(4) == 0 ? false : true;
		}
		cursor.close();

		if (bank != null) {
			String[] columns2 = new String[]{"single", "singleDay", "singleMonth"};
			String[] selectionArgs2 = new String[]{bank.code, bank.plat};
			Cursor cursor2 = db.query(Constant.TABLE_NAME_QUOTAS, columns2,
					"bankCode=? and plat=?", selectionArgs2, null, null, null);
			if (cursor2.moveToFirst()) {
				bank.single = cursor2.getInt(0);
				bank.singleDay = cursor2.getInt(1);
				bank.singelMonth = cursor2.getInt(2);
			}
		}
		db.close();
		return bank;
	}

	/**
	 * 多表查
	 *
	 * @param code
	 * @return
	 */
	public Bank unionQuery(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}
		Bank bank = null;
		// 多表查
		String sql = " SELECT A.[code],A.[name],A.[url],A.[plat],B.[single],B.[singleDay],B.[singleMonth] "
				+ " FROM "
				+ Constant.TABLE_NAME_BANKS
				+ " A, "
				+ Constant.TABLE_NAME_QUOTAS
				+ " B "
				+ " WHERE "
				+ " A.[code]=? and "
				+ " A.[code]=B.[bankCode] and "
				+ " A.[plat]=B.[plat] ";
		Log.i(TAG, "unionQuery: "+sql);
		Cursor cursor = db.rawQuery(sql, new String[]{code});
		if (cursor.moveToFirst()) {
			bank = new Bank();
			bank.code = cursor.getString(0);
			bank.name = cursor.getString(1);
			bank.url = cursor.getString(2);
			bank.plat = cursor.getString(1);
			bank.single = cursor.getInt(4);
			bank.singleDay = cursor.getInt(5);
			bank.singelMonth = cursor.getInt(6);
		}
		cursor.close();
		db.close();
		return bank;
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	public List<Bank> queryAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}
		List<Bank> banks = new ArrayList<Bank>();
		String[] columns = new String[]{"code", "name", "url", "single",
				"singleDay", "singleMonth"};
		Cursor cursor = db.query(Constant.TABLE_NAME_BANKS, columns, null,
				null, null, null, null);
		while (cursor.moveToNext()) {
			Bank bank = new Bank();
			bank.code = cursor.getString(0);
			bank.name = cursor.getString(1);
			bank.url = cursor.getString(2);
			bank.single = cursor.getInt(3);
			bank.singleDay = cursor.getInt(4);
			bank.singelMonth = cursor.getInt(5);
			banks.add(bank);
		}
		cursor.close();
		db.close();
		return banks;
	}

	/**
	 * 支付渠道列表
	 *
	 * @param bankid
	 * @return
	 */
	public List<String> getPayList(String bankid) {
		if (StringUtils.isBlank(bankid)) {
			return null;
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return null;
		}
		List<String> list = new ArrayList<>();
		String[] columns = new String[]{"plat"};
		String selection = "bankCode=?";
		String[] selectionArgs = new String[]{bankid};
		Cursor cursor = db.query(Constant.TABLE_NAME_QUOTAS, columns,
				selection, selectionArgs, null, null, null);
		while (cursor.moveToNext()) {
			String plat = cursor.getString(0);
			list.add(plat);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 查询限额
	 *
	 * @param bankCode
	 * @param plat
	 * @return
	 */
	public int getPlatSingle(String plat, String bankCode) {
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db == null || !db.isOpen()) {
			return 0;
		}
		int single = 0;
		Cursor c = db.rawQuery("select * from quotas where plat=? and bankCode=?", new String[]{plat, bankCode});
		while (c.moveToNext()) {
			single = c.getInt(2);
		}
		return single;
	}

}
