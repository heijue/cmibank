package cn.app.yimirong.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.app.yimirong.R;

/**
 * Created by android on 2015/11/2.
 */
public class Downloader {

	private Activity activity;

	private Context context;

	private String url;

	private String location = "Download";

	private String filename = "download";

	private String title = "下载";

	private String description;

	private String mimeType;

	private boolean downloading = false;

	private DownloadListener listener;

	private DownloadManager dm;

	private Handler mHandler;

	private String path;

	public Downloader(Activity activity, String url) {
		this.activity = activity;
		this.url = url;
		context = activity;
		dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
		mHandler = new Handler(Looper.getMainLooper());
	}

	public Downloader setLocation(String location) {
		this.location = location;
		return this;
	}

	public Downloader setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public Downloader setPath(String path) {
		this.path = path;
		return this;
	}

	public Downloader setTitle(String title) {
		this.title = title;
		return this;
	}

	public Downloader setDescription(String description) {
		this.description = description;
		return this;
	}

	public Downloader setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public Downloader setListener(DownloadListener listener) {
		this.listener = listener;
		return this;
	}

	/**
	 * @param showProgress
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void download(boolean showProgress) {
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		//下载文件存放位置
		request.setDestinationInExternalPublicDir(location, filename);
		if (title != null) {
			request.setTitle(title);
		}
		if (description != null) {
			request.setDescription(description);
		}
		if (mimeType != null) {
			request.setMimeType(mimeType);
		}
		request.allowScanningByMediaScanner();
		request.setVisibleInDownloadsUi(true);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		long id = dm.enqueue(request);
		if (showProgress) {
			showDownloadProgress(id);
		}
	}

	public void download() {
		download(false);
	}

	/**
	 * 显示下载进度
	 *
	 * @param id
	 */
	private void showDownloadProgress(final long id) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_download);
		final TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_download_title);
		final TextView tvCurrentSize = (TextView) dialog.findViewById(R.id.dialog_download_currentsize);
		final TextView tvTotalSize = (TextView) dialog.findViewById(R.id.dialog_download_totalsize);
		final TextView tvPercentage = (TextView) dialog.findViewById(R.id.dialog_download_percentage);
		final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.dialog_download_progressbar);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		if (activity != null && !activity.isFinishing()) {
			dialog.show();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				do {
					downloading = true;
					DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(id);
					Cursor cursor = dm.query(query);
					if (cursor.moveToFirst()) {
						final int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
						final int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
						final long currentSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
						final long totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
						final long percentage = (currentSize * 100) / totalSize;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (status == DownloadManager.STATUS_RUNNING) {
									//正在下载
									downloading = true;
									progressBar.setIndeterminate(false);
									tvCurrentSize.setText(String.format("%.2fMB", (currentSize * 1.0) / 1024 / 1024));
									tvTotalSize.setText(String.format("%.2fMB", (totalSize * 1.0) / 1024 / 1024));
									tvPercentage.setText((int) percentage + "%");
									progressBar.setProgress((int) percentage);
								} else if (status == DownloadManager.STATUS_SUCCESSFUL) {
									//下载成功
									downloading = false;
									if (listener != null) {
										listener.onSuccess(dialog, path);
									}
								} else if (status == DownloadManager.STATUS_FAILED) {
									//下载失败
									downloading = false;
									if (listener != null) {
										listener.onFailure(dialog, reason);
									}
								} else {
									//下载暂停或重试
									downloading = true;
									progressBar.setIndeterminate(true);
									tvCurrentSize.setText("0.00MB");
									tvTotalSize.setText("0.00MB");
									tvPercentage.setText("00%");
								}
							}
						});
					} else {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								downloading = false;
								if (listener != null) {
									listener.onFailure(dialog, -1);
								}
							}
						});
					}
					cursor.close();
				} while (downloading);
			}
		}).start();
	}

	public interface DownloadListener {
		public void onSuccess(Dialog dialog, String path);

		public void onFailure(Dialog dialog, int reason);
	}

}
