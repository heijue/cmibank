package cn.app.yimirong;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.util.Log;


import com.alibaba.mobileim.aop.AdviceBinder;
import com.alibaba.mobileim.aop.PointCutEnum;
import com.alibaba.wxlib.util.SysUtil;
import com.fuiou.mobile.FyPay;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.utils.FileUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.view.ChattingUICustomSample;


public class App extends Application {

	private static final String TAG = "App";

	public static final int DEBUG_LEVEL_TEST = 0;

	public static final int DEBUG_LEVEL_HTTP = 1;

	public static final int DEBUG_LEVEL_HTTPS = 2;

	public static String account;

	public static long lastVerifyTime;

	public static LoginData loginData;

	public static UserInfo userinfo;

	public static String code;

	public static int isDestroy = 0;

	public static long delta = 0;

	public static boolean isVerified = false;

	public static boolean isLogin = false;

	public static boolean isPayLocked = false;

	public static boolean isInBack = true;

	//调试模式
	public static boolean isDebug = BuildConfig.isDebug;

	// 调试级别
	public static int DEBUG_LEVEL = DEBUG_LEVEL_HTTPS;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate:" + this.toString());
		String processName = SystemUtils.getProcessName(this);
		String packageName = getPackageName();
		Log.d(TAG, "package:" + packageName);
		Log.d(TAG, "process:" + processName);

		if (DEBUG_LEVEL == DEBUG_LEVEL_TEST) {
			FyPay.setDev(false);
		} else {
			FyPay.setDev(true);
		}
		FyPay.init(this);
		if (packageName.equals(processName)) {
			mkKdlcDir();
			readConfig();
			configLogger();
			configHttp();
			configUmeng();
			configXGPush();
			configLeakCanary();
			configBugly();
			UMShareAPI.get(this);
		}
		SysUtil.setApplication(this);
		if(SysUtil.isTCMSServiceProcess(this)){
			return;
		}
		if(SysUtil.isMainProcess()){
//			YWAPI.init(this,"");
			AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_UI_POINTCUT,ChattingUICustomSample.class);
		}

	}

	{
		PlatformConfig.setWeixin("wx9ce9555245d87c9b", "b89700897943496616dfbbab013d466d");
		PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
		Config.DEBUG = true;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(base);
	}

	/**
	 * 创建kdlc目录
	 */
	private void mkKdlcDir() {
		Log.d(TAG, "mkKdlcDir");
		FileUtils.makeDirs(Constant.HETWEN_PATH);
	}

	/**
	 * 读取配置
	 */
	private void readConfig() {
		Log.d(TAG, "readConfig");
		//读取调试模式配置
		SharedPreferences sp = getSharedPreferences(Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		int debugLevel = sp.getInt("debugLevel", DEBUG_LEVEL);
		setDebugLevel(debugLevel);
	}

	/**
	 * 设置日志打印
	 */
	private void configLogger() {
		Log.d(TAG, "configLogger");
		Logger.getInstance().setLogEnabled(isDebug);
		Logger.getInstance().setLog2FileEnabled(isDebug);
	}

	/**
	 * 配置Http
	 */
	private void configHttp() {
		Log.d(TAG, "configHttp");
		// Http初始化
		Http.init(this);
	}

	/**
	 * 友盟配置
	 */
	private void configUmeng() {
		Log.d(TAG, "configUmeng");
		// 友盟统计
		MobclickAgent.setDebugMode(isDebug);
		//关闭默认时长统计
		MobclickAgent.openActivityDurationTrack(false);
		// 关闭错误统计
		MobclickAgent.setCatchUncaughtExceptions(false);
		AnalyticsConfig.enableEncrypt(true);
	}

	/**
	 * 配置信鸽
	 */
	private void configXGPush() {
		//开启信鸽日志输出
		XGPushConfig.enableDebug(this, true);
		//信鸽注册代码

		XGPushManager.registerPush(this, new XGIOperateCallback() {

			@Override

			public void onSuccess(Object data, int flag) {

				Log.d("TPush", "注册成功，设备token为：" + data);

			}

			@Override

			public void onFail(Object data, int errCode, String msg) {

				Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);

			}
		});
	}

	/**
	 * 配置LeakCanary
	 */
	private void configLeakCanary() {
		Log.d(TAG, "configLeakCanary");
		if (isDebug) {
			LeakCanary.install(this);
		}
	}

	/**
	 * 设置bugly
	 */
	private void configBugly() {
		Log.d(TAG, "configBugly");
		SystemUtils.configBugly(this);
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.d(TAG, "onTrimMemory");
	}

	public static void addPatch(String path) {
		if (isDebug) {
			/*if (patchManager != null) {
				try {
                    patchManager.addPatch(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
		}
	}

	/**
	 * 设置调试级别
	 *
	 * @param level
	 */
	public static void setDebugLevel(int level) {
		switch (level) {
			case DEBUG_LEVEL_TEST:
				DEBUG_LEVEL = DEBUG_LEVEL_TEST;
				break;

			case DEBUG_LEVEL_HTTP:
				DEBUG_LEVEL = DEBUG_LEVEL_HTTP;
				break;

			case DEBUG_LEVEL_HTTPS:
				DEBUG_LEVEL = DEBUG_LEVEL_HTTPS;
				break;

			default:
				DEBUG_LEVEL = DEBUG_LEVEL_HTTPS;
		}
	}

	/**
	 * 退出
	 */
	public static void exitApp() {
		//结束所有Activity
		AppMgr.finishAll();
	}

}
