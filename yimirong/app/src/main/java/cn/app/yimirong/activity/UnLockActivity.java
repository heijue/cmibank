package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.LoginChangeEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.db.dao.OfflineDao;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.yimirong.view.lock.LockView;

public class UnLockActivity extends BaseActivity implements OnClickListener {

	// 验证手势密码
	public static final int ACTION_VERIFY_LOCK = 0;

	// 修改手势密码
	public static final int ACTION_ALTER_LOCK = 1;

	// 关闭手势密码
	public static final int ACTION_CLOSE_LOCK = 2;

	private int action = ACTION_VERIFY_LOCK;

	private int action_login = 0;

	private RelativeLayout rlIcon;

	private TextView tvName;

	private TextView tvHead;

	private Animation mShakeAnim;

	private String lockPass;

	private int errorTimes = 5;

	@Override
	public void loadView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_unlock);
		action = getIntent().getIntExtra("action", ACTION_VERIFY_LOCK);
		isStatusBarTint = action == ACTION_VERIFY_LOCK ? false : true;
		shouldVerify = false;
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void initView() {
		if (action != ACTION_VERIFY_LOCK) {
			setTitleBar(true);
			setTitleBack(true);
			setTitleText("验证手势密码");
		} else {
			setTitleBar(false);
		}

		LockView lockView = (LockView) findViewById(R.id.activity_unlock_lockview);
		lockPass = sp.getString("lockPass", null);
		lockView.setPassword(false, lockPass);
		lockView.setCallBack(new LockView.CallBack() {
			@Override
			public void onStart() {
			}

			@Override
			public void onFinish(String password) {
				if (lockPass.equals(password)) {
					if (action == ACTION_ALTER_LOCK) {
						// 修改手势密码
						startActivity(new Intent(context, SetLockActivity.class));
						finish();
					} else if (action == ACTION_CLOSE_LOCK) {
						// 关闭手势密码
						// 清除手势密码
						sp.edit().remove("lockPass").commit();
						finish();

					} else {
						// 验证手势密码
						App.isVerified = true;
						App.lastVerifyTime = TimeUtils.getCurrentTimeInLong();
						finish();
					}
				} else {
					//密码错误
					showLockPassError();
				}
			}
		});

		rlIcon = (RelativeLayout) findViewById(R.id.activity_unlock_icon_wrapper);

		tvName = (TextView) findViewById(R.id.activity_unlock_name);

		tvHead = (TextView) findViewById(R.id.activity_unlock_head_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

		LinearLayout bottomWrapper = (LinearLayout) findViewById(R.id.activity_unlock_bottom_wrapper);
		if (action != ACTION_VERIFY_LOCK) {
			bottomWrapper.setVisibility(View.INVISIBLE);
		} else {
			bottomWrapper.setVisibility(View.VISIBLE);
		}

		TextView tvLogin = (TextView) findViewById(R.id.activity_unlock_login_other);
		tvLogin.setOnClickListener(this);

		TextView tvForget = (TextView) findViewById(R.id.activity_unlock_forget_pass);
		tvForget.setOnClickListener(this);
	}

	@Override
	public void initData() {
		setUserInfo();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setUserInfo() {
		if (App.account != null) {
			String str = StringUtils.getSecretAccount(App.account);
			tvHead.setText(str);
		}

		if (App.userinfo != null && App.userinfo.identity != null
				&& App.userinfo.identity.realName != null) {
			setAccountVerified(true);
		} else {
			setAccountVerified(false);
		}
	}

	private void setAccountVerified(boolean isverified) {
		if (isverified) {
			rlIcon.setBackgroundResource(R.drawable.shape_circle_dark);
			tvName.setVisibility(View.VISIBLE);
			tvName.setText(App.userinfo.identity.realName);
		} else {
			rlIcon.setBackgroundResource(R.drawable.shape_circle_light);
			tvName.setVisibility(View.GONE);
		}
	}

	private void showLockPassError() {
		errorTimes--;
		if (errorTimes <= 0) {
			if (action == ACTION_ALTER_LOCK || action == ACTION_CLOSE_LOCK) {
				ToastUtils.show(context, "验证失败");
				finish();
			} else {
//				App.exitApp();
				toLogin();
			}
		} else {
			tvHead.setText("输入错误，您还有" + errorTimes + "次机会");
			tvHead.setTextColor(Color.RED);
			tvHead.startAnimation(mShakeAnim);
		}
	}

	/**
	 * 去登录
	 */
	protected void toLogin() {
		// 跳转到输入密码的界面
		Intent intent = new Intent(context, LoginActivity.class);
		if (!App.account.isEmpty()) {
			String one = App.account.substring(0, 3);
			String twe = App.account.substring(3, 7);
			String three = App.account.substring(7, 11);
			String accountText = one + " " + twe + " " + three;
			intent.putExtra("account", accountText);
		}
			logout();
			startActivity(intent);
			finish();

	}

/*	*//**
	 * 退出登录
	 *//*
	private void logout() {
		// 清除用户名和密码
		DataMgr.getInstance(appContext).clearUserData();
		// 清除cookies
		DataMgr.getInstance(appContext).clearCookies();
		// 清除登录数据
		DataMgr.getInstance(appContext).clearLoginData();
		// 清除用户数据
		DataMgr.getInstance(appContext).clearUserInfo();
		// 清除我的资产中数据
		DataMgr.getInstance(appContext).clearOffLineInfo();
		// 清除用户收益
		DataMgr.getInstance(appContext).clearUserProfitInfo();

		//清空七天收益表
		OfflineDao.getInstance(this).clear();

		// 退出登录
		App.userinfo = null;
		App.isLogin = false;
		App.account = null;
		App.code = null;
		App.loginData = null;

		//清除Cookies
		Http.clearCookies();
		//友盟退出登录
		MobclickAgent.onProfileSignOff();

		sp.edit().remove("lockPass").commit();

		EventBus.getDefault().post(new LoginChangeEvent());
	}*/

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_unlock_login_other:
				action_login = 1;
				App.isVerified = true;
				loginOtherAccount();
				break;

			case R.id.activity_unlock_forget_pass:
				action_login = 1;
				App.isVerified = true;
				toInputLoginPass();
				break;

			default:
				break;
		}
	}

	/**
	 * 登录其他账户
	 */
	private void loginOtherAccount() {
		Intent intent = new Intent(context, LoginActivity.class);
		startActivity(intent);
	}

	/**
	 * 去输入登录密码
	 */
	private void toInputLoginPass() {
		Intent intent = new Intent(context, LoginActivity.class);
		String one = App.account.substring(0, 3);
		String twe = App.account.substring(3, 7);
		String three = App.account.substring(7, 11);
		String accountText = one + " " + twe + " " + three;
		intent.putExtra("account", accountText);
		startActivity(intent);
	}

	public void onEventMainThread(LoginChangeEvent event) {
		if (App.isLogin) {
			if (action_login == 1) {
				// 忘记手势密码，清除手势密码
				sp.edit().remove("lockPass").commit();
			}
			finish();
		}
	}

}
