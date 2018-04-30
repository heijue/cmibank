package cn.app.yimirong.utils;

import android.os.Handler;

/**
 * Created by hetwen on 15/10/29.
 */
public class ClickCounter {

	private int allTimes;
	private long[] timesArray;
	private int currentTime = 0;
	private long deltaTime;
	private OnCountListener listener;
	private Handler mHandler;

	public ClickCounter(int allTimes, long deltaTime) {
		if (allTimes <= 1) {
			this.allTimes = 1;
		} else {
			this.allTimes = allTimes;
		}
		if (deltaTime <= 500) {
			this.deltaTime = 500;
		} else {
			this.deltaTime = deltaTime;
		}
		timesArray = new long[allTimes];
		mHandler = new Handler();
	}

	public void setListener(OnCountListener listener) {
		this.listener = listener;
	}

	public void countClick() {
		if (allTimes == 1) {
			if (listener != null) {
				listener.onCountDone();
			}
			return;
		}
		mHandler.removeCallbacksAndMessages(null);
		long ms = System.currentTimeMillis();
		timesArray[currentTime] = ms;
		if (currentTime > 0 && currentTime < allTimes) {
			//本次点击和上次点击的时间差
			long delta = timesArray[currentTime] - timesArray[currentTime - 1];
			if (delta > deltaTime) {
				//两次点击超过设定时间，中断操作
				currentTime = 0;
				return;
			}

			if (currentTime == allTimes - 1) {
				// 连续点击完成
				if (listener != null) {
					listener.onCountDone();
				}
				currentTime = 0;
				return;
			}
		}
		currentTime++;
		if (listener != null) {
			listener.onCount(currentTime);
		}
		//如果2秒后没有再次点击，则复原
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				currentTime = 0;
			}
		}, deltaTime);
	}

	/**
	 * 连续点击的回调接口
	 */
	public interface OnCountListener {
		public void onCount(int currentTime);

		public void onCountDone();
	}
}
