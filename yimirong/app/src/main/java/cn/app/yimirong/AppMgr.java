package cn.app.yimirong;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppMgr {

	private static final String TAG = "AppMgr";

	public static Map<String, WeakReference<Activity>> map;

	static {
		map = new HashMap<>();
	}

	public static void add(Activity activity) {
		if (activity != null) {
			String name = activity.getClass().getSimpleName();
			if (!map.containsKey(name)) {
				Log.d(TAG, "add activity:" + name);
				WeakReference<Activity> weakRef = new WeakReference<>(activity);
				map.put(name, weakRef);
			}
		}
	}

	public static void remove(Activity activity) {
		if (activity != null) {
			String name = activity.getClass().getSimpleName();
			if (map.containsKey(name)) {
				Log.d(TAG, "remove activity:" + name);
				map.remove(name);
			}
		}
	}

	public static void finishAll() {
		Set<Map.Entry<String, WeakReference<Activity>>> entries = map.entrySet();
		for (Map.Entry<String, WeakReference<Activity>> entry : entries) {
			WeakReference<Activity> value = entry.getValue();
			Activity activity = value.get();
			if (activity != null) {
				Log.d(TAG, "finishing " + activity.getClass().getSimpleName());
				activity.finish();
			}
		}
	}

}
