package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.cookie.Cookie;

import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.JinJiEvent;
import cn.app.yimirong.event.custom.ToMainEvent;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.event.custom.UserInfoUpdateEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.ActivityTime;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.User;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;

public class SplashActivity extends BaseActivity {

	protected ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void loadView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_splash);
		isStatusBarTint = false;
		shouldVerify = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void initView() {
		// 不显示标题栏
		setTitleBar(false);
		imageView = (ImageView) findViewById(R.id.activity_splash_image);
	}

	@Override
	public void initData() {
		long time = 0;
		try {
			time = DataMgr.getInstance(context).restoreEvaluateTime();
		}catch (Exception e){
			e.printStackTrace();
		}
		String oldVer = DataMgr.getInstance(context).restoreisversion();
		String versionCode = SystemUtils.getVersionName(context);

		if (oldVer == null) {
			DataMgr.getInstance(context).saveisVersion(versionCode);
		}else {
			if (!oldVer.equals(versionCode)){
				DataMgr.getInstance(context).saveShow(false);
				DataMgr.getInstance(context).saveisVersion(versionCode);
				DataMgr.getInstance(context).saveEvaluateTime(System.currentTimeMillis() / 1000);
			}
		}

		if (time == 0) {
			DataMgr.getInstance(context).saveShow(false);
			DataMgr.getInstance(context).saveisAllow(false);
			DataMgr.getInstance(context).saveAllow(false);
			DataMgr.getInstance(context).saveEvaluateTime(System.currentTimeMillis() / 1000);
		}



		autoLogin();
		loadActivityTime();
		SystemUtils.checkSystem(this);
		toNext();
	}

	/**
	 * 自动登录
	 */
	private void autoLogin() {
		// 获取保存的用户名和密码
		final User user = DataMgr.getInstance(appContext).restoreUserData();
		if (user.username != null && user.password != null) {
			// 先从缓存登录
			loginFromCache(user);
			// 再从网络登录
			login(user);
		}
	}

	/**
	 * 从缓存登录
	 *
	 * @param user
	 */
	private void loginFromCache(User user) {
		List<Cookie> cookies = DataMgr.getInstance(appContext)
				.restoreCookies();
		if (cookies != null && cookies.size() > 0) {
			// 如果cookie存在
			LoginData loginData = DataMgr.getInstance(appContext)
					.restoreLoginData();
			if (loginData != null) {
				App.loginData = loginData;
				App.isLogin = true;
				App.account = user.username;
				App.code = loginData.code;
				logger.i(TAG, "uid: " + loginData.uid);
				SystemUtils.configBugly(appContext);
				CrashReport.setUserId(loginData.uid);

				//从缓存登录成功，加载用户数据
				EventBus.getDefault().post(new UserInfoEvent());
			} else {
				logout();
			}
		} else {
			logout();
		}
	}

	/**
	 * 从网络登录
	 *
	 * @param user
	 */
	private void login(final User user) {
		if (amodel != null) {
			amodel.login(user.username, user.password, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof LoginData) {
						// 登录成功
						LoginData loginData = (LoginData) t;
						// 保存登录数据
						DataMgr.getInstance(appContext).saveLoginData(
								loginData);
						if (!App.isLogin) {
							//从网络登录成功，加载用户数据
							App.isLogin = true;
							EventBus.getDefault().post(new UserInfoEvent());
						}
						App.loginData = loginData;
						App.account = user.username;
						App.code = loginData.code;
						SystemUtils.configBugly(appContext);
						CrashReport.setUserId(loginData.uid);
						// 绑定信鸽推送账号
//						MobclickAgent.onProfileSignIn(loginData.uid);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					if ("1021".equals(errorCode)) {
						// 密码错误
						logout();
					}
				}
			});
		}
	}

/*	*//**
	 * 登出
	 *//*
	private void logout() {
		App.isLogin = false;
		App.account = null;
		App.userinfo = null;
		App.code = null;

		MobclickAgent.onProfileSignOff();
		DataMgr.getInstance(appContext).clearCookies();
		Http.clearCookies();
	}*/

	/**
	 * 加载用户信息
	 */
	private void loadUserInfo() {
		if (App.isLogin) {
			App.userinfo = DataMgr.getInstance(appContext).restoreUserInfo();
			if (amodel != null) {
				amodel.getUserInfo(new ResponseHandler() {
					@Override
					public <T> void onSuccess(String response, T t) {
						super.onSuccess(response, t);
						if (t != null && t instanceof UserInfo) {
							UserInfo userinfo = (UserInfo) t;
							App.userinfo = userinfo;
							DataMgr.getInstance(appContext).saveUserInfo(userinfo);
							DataMgr.getInstance(appContext).saveBalance(
									userinfo.balance);
							calcDelta(userinfo.serverTime);
							MobclickAgent.onEvent(context, "loadUserInfo");
							EventBus.getDefault().post(new UserInfoUpdateEvent());
						}
					}
				});
			}
		}
	}

	private void loadActivityTime() {
		if (cmodel != null) {
			cmodel.getActivityTime(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof ActivityTime) {
						ActivityTime ate = (ActivityTime) t;
						DataMgr.getInstance(appContext).saveActivityTime(ate);
					}
				}
			});
		}
	}

	/**
	 * 界面跳转
	 */
	protected void toNext() {
		if (sp.getBoolean(Constant.SP_CONFIG_FIRST_IN, true)) {
			toNav(3000);
		} else {
			toMain(3000);
//			installMark();
		}
	}

	/**
	 * 首次进入，去引导页
	 *
	 * @param delay
	 */
	private void toNav(long delay) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(context, FirstNavActivity.class));
				overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
				finish();
			}
		}, delay);
	}

	/**
	 * 跳转到MainActivity
	 *
	 * @param delay
	 */
	private void toMain(int delay) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(context, MainActivity.class));
				overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
				finish();
			}
		}, delay);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 紧急事件，界面不在跳转
	 *
	 * @param event
	 */
	public void onEventMainThread(JinJiEvent event) {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}
	public void onEventMainThread(ToMainEvent event) {
		toNext();
	}

	/**
	 * 刷新用户数据
	 *
	 * @param event
	 */
	public void onEventMainThread(UserInfoEvent event) {
		loadUserInfo();
	}
}
