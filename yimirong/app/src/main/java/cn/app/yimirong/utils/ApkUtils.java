package cn.app.yimirong.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileFilter;

import cn.app.yimirong.log.Logger;

/**
 * Created by android on 2015/11/2.
 */
public class ApkUtils {

	private static final String TAG = "ApkUtils";

	/**
	 * 安装apk
	 *
	 * @param context
	 * @param filename
	 */
	public static void installApk(Context context, String filename) {
		Logger.getInstance().d(TAG, filename);
		if (!filename.endsWith(".apk")) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri apk = Uri.fromFile(new File(filename));
		intent.setDataAndType(apk, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 删除指定目录下的apk文件
	 *
	 * @param path 目录
	 */
	public static void deleteApk(String path) {
		/**
		 * 遍历apk
		 */
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname == null || !pathname.exists()
						|| pathname.isDirectory()) {
					return false;
				}

				String name = pathname.getName();
				if (name != null && name.endsWith(".apk")) {
					return true;
				}
				return false;
			}
		});

		/**
		 * 删除所有的apk
		 */
		if (files != null && files.length > 0) {
			Logger.getInstance().d(TAG, "length:" + files.length);
			for (File file : files) {
				Logger.getInstance().d(TAG, "name:" + file.getName());
				file.delete();
			}
		}
	}

	/**
	 * 检查APK是否存在
	 *
	 * @param path apk路径
	 * @return
	 */
	public static boolean checkApk(String path, String md5) {
		if (StringUtils.isBlank(path)) {
			//路径为空
			return false;
		}

		File file = new File(path);
		if (!file.exists()) {
			//文件不存在
			return false;
		}

		if (file.isDirectory()) {
			//是目录
			return false;
		}
		if (!FileUtils.checkFileMD5(file, md5)) {
			//校验MD5
			Logger.getInstance().d(TAG, "校验失败");
			return false;
		}

		Logger.getInstance().d(TAG, "校验成功");

		return true;
	}
}
