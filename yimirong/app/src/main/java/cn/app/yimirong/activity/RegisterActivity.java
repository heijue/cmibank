package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.BaseEvent;
import cn.app.yimirong.event.custom.LoginChangeEvent;
import cn.app.yimirong.event.custom.SetNewPassEvent;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.User;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;

public class RegisterActivity extends BaseActivity implements OnClickListener {

	private String account;

	private EditText etVerifyCode;
	private String verifyCode;

	private TextView tvTimer, visible;
	private int elapseTime = 60;

	private EditText etPassword;

	private String password;

	private Button btnOk;

	private boolean yingcang = false;

	private boolean isSetNewPass = false;

	private ScheduledExecutorService scheduledExecutor;
	private String passmd5;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_register);
	}

	@Override
	public void initView() {
		Intent intent = getIntent();
		isSetNewPass = intent.getBooleanExtra("isSetNewPass", false);
		verifyCode = intent.getStringExtra("yanZ");
		account = intent.getStringExtra("account");
//        TextView tvNumber = (TextView) findViewById(R.id.activity_register_numer);

		setTitleBack(true);
		if (isSetNewPass) {
			setTitleText("重置登录密码");
//            tvNumber.setText("已发送短信验证码到您的手机");
		} else {
			setTitleText("设置密码");
//            String str = "已发送短信验证码到号码\n" + "<font color='#fe5720'>"+account+"</fonr>";
//            tvNumber.setText(Html.fromHtml(str));
		}

//        etVerifyCode = (EditText) findViewById(R.id.activity_register_numer_et);

//        tvTimer = (TextView) findViewById(R.id.activity_register_timer);
//        tvTimer.setText("重发");
//        tvTimer.setTextColor(Color.parseColor("#303030"));
//        tvTimer.setClickable(true);
//        tvTimer.setOnClickListener(this);
		btnOk = (Button) findViewById(R.id.activity_register_btnok);
		btnOk.setOnClickListener(this);

		visible = (TextView) findViewById(R.id.zhuce_setmm_xianshi);
		visible.setOnClickListener(this);

		etPassword = (EditText) findViewById(R.id.activity_register_pass_et);
		etPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					btnOk.setClickable(false);
					btnOk.setTextColor(Color.parseColor("#ff9898"));
				} else {
					btnOk.setClickable(true);
					btnOk.setTextColor(Color.parseColor("#ffffff"));
				}
			}
		});


//        CheckBox cbAgree = (CheckBox) findViewById(R.id.activity_register_check);
//
//        if (cbAgree.isChecked()) {
//            btnOk.setClickable(true);
//            btnOk.setBackgroundResource(R.drawable.selector_bg_red_btn);
//        } else {
//            btnOk.setClickable(false);
//            btnOk.setBackgroundResource(R.drawable.shape_bg_dark_gray_normal);
//        }
//
//        cbAgree.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                if (isChecked) {
//                    btnOk.setClickable(true);
//                    btnOk.setBackgroundResource(R.drawable.selector_bg_red_btn);
//                } else {
//                    btnOk.setClickable(false);
//                    btnOk.setBackgroundResource(R.drawable.shape_bg_dark_gray_normal);
//                }
//            }
//        });
//
//        TextView tvLink = (TextView) findViewById(R.id.activity_register_link);
//
//        tvLink.setOnClickListener(this);
	}

	@Override
	public void initData() {
//        if (isSetNewPass) {
//            // 重置登录密码
//            sendResetVerifyCode();
//        } else {
//            // 注册
//            sendRegisterVerifyCode();
//        }
	}

	/**
	 * 发送重置密码的验证码
	 */
	private void sendResetVerifyCode() {
		if (amodel != null) {
			showLoading("正在获取验证码");
			amodel.resetLoginPassStep1(account, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof String) {
						String message = (String) t;
						ToastUtils.show(context, message);
						startTimer();
					}
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg);
					closeLoading();
				}

			});
		}
	}

	/**
	 * 发送注册验证码
	 */
	protected void sendRegisterVerifyCode() {
		if (amodel != null) {
			showLoading("正在获取验证码");
			amodel.sendVerifyCode(account, new ResponseHandler() {
				@Override
				public void onSuccess(String response, Object obj) {
					super.onSuccess(response, obj);
					// 验证码已发送
					ToastUtils.show(context, "验证码已发送");
					// 开启倒计时
					startTimer();
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg);
					closeLoading();
				}
			});
		}
	}

	/**
	 * 启动计时任务
	 */
	private void startTimer() {
		elapseTime = 59;
		tvTimer.setClickable(false);
		tvTimer.setTextColor(Color.parseColor("#303030"));
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (elapseTime == 0) {
							tvTimer.setTextColor(Color.parseColor("#303030"));
							tvTimer.setText("重发");
							tvTimer.setClickable(true);
							scheduledExecutor.shutdown();
						} else {
							tvTimer.setText(elapseTime + "秒");
						}
						elapseTime--;
					}
				});
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_register_btnok:
				handleBtnOK();
				break;

			case R.id.zhuce_setmm_xianshi:
				if (yingcang) {
					etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					visible.setBackgroundResource(R.drawable.yincang);
					yingcang = false;
				} else {
					etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					visible.setBackgroundResource(R.drawable.xianshi);
					yingcang = true;
				}
				break;

//            case R.id.activity_register_timer:
//                resendVerify();
//                break;

			default:
				break;
		}
	}

	private void handleBtnOK() {
		if (isSetNewPass) {
			setNewPass();
		} else {
			register();
		}
	}

	/**
	 * 设置新密码
	 */
	private void setNewPass() {
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在处理");
			String passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
			amodel.resetLoginPassStep2(account, verifyCode, passmd5,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							if (t != null && t instanceof String) {
								showSuccess();
							}
							closeLoading();
						}

						@Override
						public void onFailure(String errorCode, String msg) {
							super.onFailure(errorCode, msg);
							ToastUtils.show(context, msg);
							closeLoading();
						}

					});
		}
	}

	/**
	 * 清除登录状态
	 */
	protected void clearLoginState() {
		App.isLogin = false;
		App.account = null;
		App.userinfo = null;
		App.code = null;

		// 清除密码
		DataMgr.getInstance(appContext).clearUserData();
		DataMgr.getInstance(appContext).clearCookies();
		DataMgr.getInstance(appContext).clearUserInfo();
		DataMgr.getInstance(appContext).clearLoginData();

		// 取消绑定账号
		EventBus.getDefault().post(new SetNewPassEvent());
		EventBus.getDefault().post(new LoginChangeEvent());
	}

	/**
	 * 显示成功的消息
	 */
	protected void showSuccess() {
		btnOk.setClickable(false);
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"密码重置成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 1200);
	}

	/**
	 * 重置完密码，去登录
	 */
	protected void toLogin() {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("account", account);
		startActivity(intent);
		finish();
	}

	/**
	 * 重发验证码
	 */
	private void resendVerify() {
		// 调用后台接口发送短信验证码
		if (isSetNewPass) {
			sendResetVerifyCode();
		} else {
			sendRegisterVerifyCode();
		}
	}

	/**
	 * 注册
	 */
	private void register() {
		// 获取验证码和密码
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在处理");
			passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
			String channel = SystemUtils
					.getMetaData(context, Constant.CHANNEL_NAME);
			String plat = sp.getString(Constant.QUDAO, "");
			if (plat.isEmpty()) {
				plat = AnalyticsConfig.getChannel(context);
			}
			amodel.register(plat, account, passmd5, passmd5, verifyCode, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof LoginData) {
						// 注册成功
						LoginData loginData = (LoginData) t;
						registSucess(loginData);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 注册成功
	 */
	protected void registSucess(LoginData loginData) {
		MobclickAgent.onEvent(context, "registSucess");

		if (scheduledExecutor != null) {
			scheduledExecutor.shutdown();
		}

		App.isLogin = true;
		App.account = account;
		App.code = loginData.code;
		App.isVerified = true;
		App.loginData = loginData;

		SystemUtils.configBugly(appContext);
		CrashReport.setUserId(loginData.uid);
		//友盟登录成功
		MobclickAgent.onProfileSignIn(loginData.uid);

		// 保存LoginData
		DataMgr.getInstance(appContext).saveLoginData(loginData);

		// 保存用户名和密码
		User user = new User(account, passmd5);
		DataMgr.getInstance(appContext).saveUserData(user);

		// 加载用户数据
		EventBus.getDefault().post(new UserInfoEvent());

		// 通知其他界面，用户已登录
		EventBus.getDefault().post(new LoginChangeEvent());

		EventBus.getDefault().post(new SetNewPassEvent());

		btnOk.setClickable(false);

		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"注册成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
//				setResult(1);
				Intent data = getIntent();
				boolean isBuy = data.getBooleanExtra("isBuy", false);
				if (isBuy) {
					String origin = data.getStringExtra("origin");
					BaseEvent event = new BaseEvent();
					event.origin = origin;
					event.action = "register";
					EventBus.getDefault().post(event);
				}
				finish();
			}
		}, 1200);
	}

	/**
	 * 获取输入
	 */
	private boolean getInput() {
//        verifyCode = etVerifyCode.getText().toString().trim();
		password = etPassword.getText().toString().trim();

		if (StringUtils.isBlank(verifyCode)) {
			ToastUtils.show(context, "请输入验证码");
			return false;
		}

		if (StringUtils.isBlank(verifyCode)) {
			ToastUtils.show(context, "请设置登录密码");
			return false;
		} else {
			if (password.length() < 6) {
				ToastUtils.show(context, "登录密码长度过小");
				return false;
			}
		}

		return true;
	}


}
