package cn.app.yimirong.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.List;

/**
 * AppUtils
 * <ul>
 * <li>{@link AppUtils#isNamedProcess(Context, String)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-5-07
 */
public class AppUtils {

	private AppUtils() {
		throw new AssertionError();
	}

	/**
	 * whether this process is named with processName
	 *
	 * @param context
	 * @param processName
	 * @return <ul>
	 * return whether this process is named with processName
	 * <li>if context is null, return false</li>
	 * <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
	 * return false</li>
	 * <li>if one process of
	 * {@link ActivityManager#getRunningAppProcesses()} is equal to
	 * processName, return true, otherwise return false</li>
	 * </ul>
	 */
	public static boolean isNamedProcess(Context context, String processName) {
		if (context == null) {
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = manager
				.getRunningAppProcesses();
		if (ListUtils.isEmpty(processInfoList)) {
			return false;
		}

		for (RunningAppProcessInfo processInfo : processInfoList) {
			if (processInfo != null
					&& processInfo.pid == pid
					&& ObjectUtils.isEquals(processName,
					processInfo.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * whether application is in background
	 * <ul>
	 * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
	 * </ul>
	 *
	 * @param context
	 * @return if application is in background return true, otherwise return
	 * false
	 */
	public static boolean isApplicationInBackground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null
					&& !topActivity.getPackageName().equals(
					context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取application中指定的meta-data
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		if (ctx == null || TextUtils.isEmpty(key)) {
			return null;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString(key);
					}
				}

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}

}
