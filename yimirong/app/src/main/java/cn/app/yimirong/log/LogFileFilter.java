package cn.app.yimirong.log;

import java.io.File;
import java.io.FileFilter;

public class LogFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname == null) {
			return false;
		}

		if (pathname.isDirectory()) {
			return false;
		}

		String name = pathname.getName();
		if (name.startsWith("crash") && name.endsWith(".log")) {
			return true;
		}

		return false;
	}

}
