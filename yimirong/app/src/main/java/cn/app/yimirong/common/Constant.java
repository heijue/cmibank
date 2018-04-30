package cn.app.yimirong.common;

import java.io.File;

import cn.app.yimirong.utils.FileUtils;

public class Constant {

	public static final String SP_CONFIG_NAME = "config";

	public static final String SP_CONFIG_FIRST_IN = "firstIn";

	public static final String CHECK_INSTANLL_MARK_FIRSTIN = "check_firstIn";

	public static final int FIRST_IN_REFRESH_DELAY = 0;

	public static final String QUDAO = "qudao";
	public static final String PLAT = "plat";

	public static final String OS = "android";

	public static final long AUGST = 1470931200;


	// 统计渠道名
	public static final String CHANNEL_NAME = "UMENG_CHANNEL";

	public static final String SPECIAL_DAYS = "specialdays.json";

	public static final String LOGIN_DATA = "login_data";

	public static final String USER_INFO = "user_info";

	public static final String USER_PROFIT_INFO = "mProfit";

	public static final String OFFLINE_INFO = "offline_info";

	// 数据库版本
	public static final int DB_VERSION = 2;

	// 数据库名称
	public static final String DB_NAME = "yimirong.db";

	//限额表
	public static final String TABLE_NAME_QUOTAS = "quotas";

	// 数据库表名-银行
	public static final String TABLE_NAME_BANKS = "banks";

	// 数据库表名-Cookie
	public static final String TABLE_NAME_COOKIES = "cookies";

	//数据库表名-公告
	public static final String TABLE_NAME_MESSAGES = "messages";

	//数据库表名-离线信息
	public static final String TABLE_NAME_OFFLINE = "offline";

	public static final String HETWEN_PATH =
			FileUtils.getSDCard()
					+ File.separator
					+ "yimirong";

	//崩溃日志存放目录
	public static final String CRASH_PATH =
			FileUtils.getSDCard()
					+ File.separator
					+ "yimirong"
					+ File.separator
					+ "crash";

	// 日志存放目录
	public static final String LOG_PATH =
			FileUtils.getSDCard()
					+ File.separator
					+ "yimirong"
					+ File.separator
					+ "log";

	// 缓存目录
	public static final String CACHE_PATH =
			FileUtils.getSDCard()
					+ File.separator
					+ "yimirong"
					+ File.separator
					+ "cache";

	// 下载目录
	public static final String DOWNLOAD_PATH =
			FileUtils.getSDCard()
					+ File.separator
					+ "yimirong"
					+ File.separator
					+ "download";

	// Android api版本
	public static final int API_LEVEL = android.os.Build.VERSION.SDK_INT;

	public static final String BUGLY_APPID = "100000001";

	// 进入debug模式的密码
	public static final String DEBUG_PASSWORD = "yimirong0821";

	// 进入调试模式的点击次数
	public static final int DEBUG_CLICK_TIMES = 4;
}
