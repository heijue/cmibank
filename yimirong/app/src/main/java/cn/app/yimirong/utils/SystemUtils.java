package cn.app.yimirong.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.app.yimirong.App;
import cn.app.yimirong.common.Constant;

public class SystemUtils {

	public static void configBugly(Context appContext) {
		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(appContext);
		String channel = getMetaData(appContext,
				Constant.CHANNEL_NAME);
		strategy.setAppChannel(channel);
		CrashReport.initCrashReport(appContext,
				Constant.BUGLY_APPID, App.isDebug, strategy);
	}

	/**
	 * 获取WIFI代理IP地址,若无代理则为null
	 *
	 * @return
	 */
	public static String getWiFiProxy() {
		String proxy = System.getProperty("http.proxyHost");
		if (StringUtils.isBlank(proxy)) {
			proxy = null;
		}
		return proxy;
	}

	/**
	 * 是否有NavigationBar
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasNavBar(Context context) {
		boolean hasNavBar = false;
		Resources res = context.getResources();
		int id = res.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavBar = res.getBoolean(id);
		}

		try {
			Class<?> sysprop = Class.forName("android.os.SystemProperties");
			Method m = sysprop.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(sysprop, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavBar = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasNavBar;
	}

	/**
	 * 获取NavigationBar的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getNavBarHeight(Context context) {
		int navBarHeight = 0;
		Resources res = context.getResources();
		int id = res.getIdentifier("navigation_bar_height", "dimen", "android");
		if (id > 0 && hasNavBar(context)) {
			navBarHeight = res.getDimensionPixelOffset(id);
		}
		return navBarHeight;
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @param activity
	 * @return
	 */
	public static final int getScreenWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @param activity
	 * @return
	 */
	public static final int getScreenHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 计算时间差
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getDays(String start, String end) {
		if (start == null || end == null) {
			return 0;
		}

		int days = 0;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",
					Locale.CHINA);
			Date startDate = df.parse(start);
			Date endDate = df.parse(end);
			long ms = endDate.getTime() - startDate.getTime();
			days = (int) (ms / (1000 * 60 * 60 * 24));
		} catch (ParseException e) {
			return 0;
		}
		return days + 1;
	}

	/**
	 * 检查网络连接
	 *
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		String networkTypeName = NetWorkUtils.getNetworkTypeName(context);
		if (NetWorkUtils.NETWORK_TYPE_UNKNOWN.equals(networkTypeName)
				|| NetWorkUtils.NETWORK_TYPE_DISCONNECT.equals(networkTypeName)) {
			return false;
		}
		return true;
	}

	/**
	 * float保留两位小数
	 *
	 * @param d
	 * @return
	 */
	public static String getDoubleStr(double d) {
		DecimalFormat format = new DecimalFormat("0.00");
		format.setRoundingMode(RoundingMode.HALF_UP);
		String str = format.format(d);
		return str;
	}

	public static double getDouble(double d) {
		DecimalFormat format = new DecimalFormat("0.00");
		format.setRoundingMode(RoundingMode.HALF_UP);
		double num = 0.00d;
		num = Double.parseDouble(format.format(d));
		return num;
	}

	public static double getDouble(String fstr) {
		DecimalFormat format = new DecimalFormat("0.00");
		format.setRoundingMode(RoundingMode.HALF_UP);
		double num = 0.00d;
		try {
			double d = Double.parseDouble(fstr);
			String str = format.format(d);
			num = Double.parseDouble(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	/**
	 * 格式化钱数表示，每三位数以一个逗号分隔
	 *
	 * @param money
	 * @return
	 */
	public static String formatMoneyNum(double money) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(money);
	}

	/**
	 * float保留两位小数
	 *
	 * @param d
	 * @return
	 */
	public static String getDoubleStr(String d) {
		double num = 0d;
		if (d != null && !"".equals(d.trim())) {
			try {
				num = Double.parseDouble(d);
			} catch (Exception e) {
				num = 0d;
			}
		}
		return getDoubleStr(num);
	}

	/**
	 * 格式化钱数 元/万元
	 *
	 * @param money
	 * @return
	 */
	public static String formatMoney(double money) {
		String str;
		if (money >= 100000) {
			str = getDoubleStr(money / 10000f) + "万元";
		} else {
			str = formatMoneyNum(money) + "元";
		}
		return str;
	}

	/**
	 * 格式化钱数 元/万元
	 *
	 * @param moneyStr
	 * @return
	 */
	public static String formatMoney(String moneyStr) {
		double money = getDouble(moneyStr);
		return formatMoney(money);
	}

	public static String formatIntMoney(double money) {
		String str;
		if (money >= 100000) {
			str = (int) (money / 10000) + "万元";
		} else {
			str = (int) money + "元";
		}
		return str;
	}

	public static String formatIntMoney(String moneyStr) {
		double money = getDouble(moneyStr);
		return formatIntMoney(money);
	}

	/**
	 * 读取清单文件中的配置
	 *
	 * @return
	 */
	public static String getMetaData(Context context, String key) {
		String value = null;
		try {
			ApplicationInfo appinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			value = appinfo.metaData.getString(key);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 获取版本名称
	 *
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionCode = "1.0.0";
		try {
			PackageInfo pkginfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionCode = pkginfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获取版本号
	 *
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 1;
		try {
			PackageInfo pkginfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionCode = pkginfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 系统检查
	 */
	public static void checkSystem(Activity activity) {
		Context appContext = activity.getApplicationContext();
		String channel = SystemUtils.getMetaData(appContext, Constant.CHANNEL_NAME);
		String versionName = getVersionName(appContext);
		int versionCode = getVersionCode(appContext);
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		float density = metrics.density;
		int dpi = metrics.densityDpi;
		JSONObject json = new JSONObject();
		try {
			json.putOpt("model", Build.MODEL);
			json.putOpt("width", width);
			json.putOpt("height", height);
			json.putOpt("density", density);
			json.putOpt("dpi", dpi);
			json.putOpt("mode", (App.isDebug ? "debug" : "release"));
			json.putOpt("debug_level", App.DEBUG_LEVEL);
			json.putOpt("channel", channel);
			json.putOpt("api", Build.VERSION.SDK_INT);
			json.putOpt("android", Build.VERSION.RELEASE);
			json.putOpt("version_name", versionName);
			json.putOpt("version_code", versionCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String info = json.toString();
		System.out.println("System Info:" + info);
	}

	public static String getProcessName(Context appContext) {
		String pname = null;
		int pid = android.os.Process.myPid();
		ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo p : processes) {
			if (p.pid == pid) {
				pname = p.processName;
			}
		}
		return pname;
	}

}
