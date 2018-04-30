package cn.app.yimirong.log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.utils.FileUtils;
import cn.app.yimirong.utils.StringUtils;

public class LogUtils {

	public static List<File> getCrashLogs() {
		List<File> list = null;
		String sdroot = FileUtils.getSDCard();
		if (sdroot == null) {
			return list;
		}

		File dir = new File(Constant.LOG_PATH);
		if (!dir.exists() || !dir.isDirectory()) {
			return list;
		}

		list = new ArrayList<File>();
		FileFilter filter = new LogFileFilter();
		File[] files = dir.listFiles(filter);
		for (File file : files) {
			if (file.exists() && file.isFile()) {
				list.add(file);
			}
		}

		return list;
	}

	/**
	 * 获取日志字符串
	 *
	 * @return
	 */
	public static List<String> getCrashLogsString() {
		List<String> list = null;
		String sdroot = FileUtils.getSDCard();
		if (sdroot == null) {
			return list;
		}

		File dir = new File(Constant.LOG_PATH);
		if (!dir.exists() || !dir.isDirectory()) {
			return list;
		}

		list = new ArrayList<String>();
		FileFilter filter = new LogFileFilter();
		File[] files = dir.listFiles(filter);
		for (File file : files) {
			if (file.exists() && file.isFile()) {
				String content = FileUtils.readFile(file);
				if (!StringUtils.isBlank(content)) {
					list.add(content);
				}
			}
		}

		return list;
	}


}
