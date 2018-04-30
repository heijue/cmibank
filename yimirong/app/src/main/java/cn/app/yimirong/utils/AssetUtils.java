package cn.app.yimirong.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetUtils {

	public static String getStringFromAsset(Context context, String filename) {
		String content = null;
		try {
			InputStream is = context.getAssets().open(filename);
			content = FileUtils.readFile(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

}
