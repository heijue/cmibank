package cn.app.yimirong.log;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.utils.FileUtils;
import cn.app.yimirong.utils.TimeUtils;

public class Logger {
	protected boolean isLogEnabled = false;
	protected boolean isLog2FileEnabled = false;
	private static Logger logger;

	public static Logger getInstance() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	private Logger() {
	}

	/**
	 * 是否开启日志
	 *
	 * @param isEnabled
	 */
	public void setLogEnabled(boolean isEnabled) {
		this.isLogEnabled = isEnabled;
	}

	/**
	 * 日志是否记录到文件
	 *
	 * @param isEnabled
	 */
	public void setLog2FileEnabled(boolean isEnabled) {
		this.isLog2FileEnabled = isEnabled;
	}


	/**
	 * 创建日志文件
	 */
	private File createLogFile() {
		File dir = new File(Constant.LOG_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, "log.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public void i(String tag, String msg) {
		if (isLogEnabled) {
			Log.i(tag, msg);
		}

		if (isLog2FileEnabled) {
			File file = createLogFile();
			String log = TimeUtils.getCurrentTimeInString() + " INFO/" + tag + ": " + msg;
			FileUtils.writeFile(file, log, true);
		}
	}

	public void v(String tag, String msg) {
		if (isLogEnabled) {
			Log.v(tag, msg);
		}

		if (isLog2FileEnabled) {
			File file = createLogFile();
			String log = TimeUtils.getCurrentTimeInString() + " VERBOSE/" + tag + ": " + msg;
			FileUtils.writeFile(file, log, true);
		}
	}

	public void d(String tag, String msg) {
		if (isLogEnabled) {
			Log.d(tag, msg);
		}

		if (isLog2FileEnabled) {
			File file = createLogFile();
			String log = TimeUtils.getCurrentTimeInString() + " DEBUG/" + tag + ": " + msg;
			FileUtils.writeFile(file, log, true);
		}
	}

	public void w(String tag, String msg) {
		if (isLogEnabled) {
			Log.w(tag, msg);
		}

		if (isLog2FileEnabled) {
			File file = createLogFile();
			String log = TimeUtils.getCurrentTimeInString() + " WARN/" + tag + ": " + msg;
			FileUtils.writeFile(file, log, true);
		}
	}

	public void e(String tag, String msg) {
		if (isLogEnabled) {
			Log.e(tag, msg);
		}

		if (isLog2FileEnabled) {
			File file = createLogFile();
			String log = TimeUtils.getCurrentTimeInString() + " ERROR/" + tag + ": " + msg;
			FileUtils.writeFile(file, log, true);
		}
	}

}
