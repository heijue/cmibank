package cn.app.yimirong.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.model.http.ResponseHandler;

public class DownloadUtils {

	/**
	 * 下载APK
	 *
	 * @param activity
	 * @param url
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static long downloadApk(Activity activity, String url) {
		if (StringUtils.isBlank(url)) {
			return -1;
		}
		ApkUtils.deleteApk(Constant.DOWNLOAD_PATH);
		DownloadManager dm = (DownloadManager) activity
				.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		//下载文件存放位置
		request.setDestinationInExternalPublicDir("yimirong/download", "yimirong.apk");
		request.setTitle("易米融");
		request.setDescription("正在下载更新");
		request.setMimeType("application/vnd.android.package-archive");
		request.allowScanningByMediaScanner();
		request.setVisibleInDownloadsUi(true);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		return dm.enqueue(request);
	}

	/**
	 * 下载文件
	 *
	 * @param url
	 * @param target
	 * @param handler
	 */
	public static void download(String url, final String target, final ResponseHandler handler) {
		Http.httpUtils.download(url, target, new RequestCallBack<File>() {
			@Override
			public void onSuccess(ResponseInfo<File> response) {
				File file = response.result;
				if (handler != null) {
					handler.onSuccess(target, response.result);
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", error);
				}
			}
		});
	}

}
