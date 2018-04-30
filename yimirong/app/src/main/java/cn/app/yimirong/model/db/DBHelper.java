package cn.app.yimirong.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.log.Logger;

public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";

	public DBHelper(Context context, String name) {
		super(context, name, null, Constant.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "onCreate");
		//新安装
		//创建Cookie表
		createCookiesTable(db);

		//新建银行表
		createBanksTable(db);

		//新建限额表
		createQuotasTable(db);

		//新建消息表
		createMessagesTable(db);

		//新建七天收益表
		createOffLineTable(db);
	}

	private void createOffLineTable(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "createOffLineTable");
		String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME_OFFLINE + " "
				+ "("
				+ "date VARCHAR,"
				+ "profit VARCHAR"
				+ ")";
		db.execSQL(sql);
	}

	private void createMessagesTable(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "createMsgTable");
		String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME_MESSAGES + " "
				+ "("
				+ "nid VARCHAR,"
				+ "isread INTEGER,"
				+ "title VARCHAR,"
				+ "content VARCHAR,"
				+ "ctime VARCHAR,"
				+ "onlinetime VARCHAR,"
				+ "phonetype VARCHAR,"
				+ "status VARCHAR,"
				+ "type VARCHAR,"
				+ "yugaotime VARCHAR"
				+ ")";
		db.execSQL(sql);
	}

	private void createCookiesTable(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "createCookiesTable");
		String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME_COOKIES + " "
				+ "("
				+ "id INTEGER PRIMARY KEY,"
				+ "name VARCHAR,"
				+ "value VARCHAR,"
				+ "comment VARCHAR,"
				+ "domain VARCHAR,"
				+ "expiryDate LONG,"
				+ "path VARCHAR,"
				+ "version INTEGER,"
				+ "isSecure INTEGER"
				+ ")";
		db.execSQL(sql);
	}

	private void createBanksTable(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "createBanksTable");
		String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME_BANKS + " "
				+ "("
				+ "code VARCHAR,"
				+ "name VARCHAR,"
				+ "url VARCHAR,"
				+ "plat VARCHAR,"
				+ "single INTEGER,"
				+ "singleDay INTEGER,"
				+ "singleMonth INTEGER,"
				+ "isShow INTEGER"
				+ ")";
		db.execSQL(sql);
	}

	private void createQuotasTable(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "createQuotasTable");
		String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME_QUOTAS + " "
				+ "("
				+ "plat VARCHAR,"
				+ "bankCode VARCHAR,"
				+ "single INTEGER,"
				+ "singleDay INTEGER,"
				+ "singleMonth INTEGER"
				+ ")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.getInstance().d(TAG, "onUpgrade");
		// 数据库升级
		if (oldVersion == 1 && newVersion == 2) {
			//创建银行表
			createBanksTable(db);
			createOffLineTable(db);
		}

		if (oldVersion == 2 && newVersion == 3) {
			//新建限额表
			createQuotasTable(db);
			//更新银行表
			updateBanksTable(db);
		}

		if (oldVersion == 3 && newVersion == 4) {
			createMessagesTable(db);
		}
	}

	private void updateBanksTable(SQLiteDatabase db) {
		Logger.getInstance().d(TAG, "updateBanksTable");
		String sql = "ALTER TABLE " + Constant.TABLE_NAME_BANKS + " "
				+ "ADD COLUMN plat VARCHAR";
		db.execSQL(sql);
	}
}
