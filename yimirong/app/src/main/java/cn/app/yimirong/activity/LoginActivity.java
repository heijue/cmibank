package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Locale;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
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

public class LoginActivity extends BaseActivity implements OnClickListener, EditText.OnFocusChangeListener {

	private String account;
	private EditText etAccount;
	private EditText etPassword;
	private String password;
	private ImageView edReset, mMReset;
	private TextView tvForget, ed_pass_visiblity, toRegister,format;
	private View zhanghao_line, mima_line;
	private Button btnOk;
	private boolean yingcang = false;
	private Button login_btn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void loadView() {
		setContentView(R.layout.activity_login);
	}

	@Override
	public void initView() {
		setTitleText("使用手机号登录");
		setTitleBack(true);

		etPassword = (EditText) findViewById(R.id.activity_login_pass_et);
		etAccount = (EditText) findViewById(R.id.activity_login_zhanghao);
		edReset = (ImageView) findViewById(R.id.login_num_qingchu);
		mMReset = (ImageView) findViewById(R.id.login_mima_qingchu);
		zhanghao_line = (View) findViewById(R.id.login_zhanghao_line);
		mima_line = (View) findViewById(R.id.login_mima_line);
		ed_pass_visiblity = (TextView) findViewById(R.id.login_mima_xianshi);
		toRegister = (TextView) findViewById(R.id.login_zhuce);
		format = (TextView) findViewById(R.id.fomat_account_card);
		login_btn = (Button) findViewById(R.id.activity_login_btnok);

		etAccount.setOnFocusChangeListener(this);
		etPassword.setOnFocusChangeListener(this);
		edReset.setOnClickListener(this);
		mMReset.setOnClickListener(this);
		ed_pass_visiblity.setOnClickListener(this);
		toRegister.setOnClickListener(this);

		etAccount.addTextChangedListener(new TextWatcher() {

			private int action = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				// before == 0 增
				// before == 1 删
				action = before;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() != 0) {
					edReset.setVisibility(View.VISIBLE);
					format.setVisibility(View.GONE);
				} else {
					format.setVisibility(View.GONE);
					edReset.setVisibility(View.GONE);
				}
				if (s.length() > 0) {
					format.setText(s.toString());
					if (action == 0) {
						// 增
						if (s.length() == 4 || s.length() == 9) {
							s.insert(s.length() - 1, " ");
						}
					} else {
						// 删
						if (s.length() == 4 || s.length() == 9) {
							s.delete(s.length() - 1, s.length());
						}
					}

				}
				if (s.length()==11 && s.toString().replace(" ","").length()==11){
					s.insert(7," ");
					s.insert(3," ");
				}
				if (s.length() == 13) {
					account = s.toString().trim();
					account = account.replace(" ", "");
				}
			}
		});
		etPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					btnOk.setClickable(false);
					mMReset.setVisibility(View.GONE);
					btnOk.setBackground(getResources().getDrawable(R.drawable.shape_invite_btn));
				} else {
					btnOk.setClickable(true);
					btnOk.setBackground(getResources().getDrawable(R.drawable.shape_login_press));
					mMReset.setVisibility(View.VISIBLE);
				}
			}
		});

		tvForget = (TextView) findViewById(R.id.activity_login_forget);
		assert tvForget != null;
		tvForget.setOnClickListener(this);

		btnOk = (Button) findViewById(R.id.activity_login_btnok);
		assert btnOk != null;
		btnOk.setOnClickListener(this);
		btnOk.setClickable(false);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			String accountText=intent.getStringExtra("account");
			etAccount.setText(accountText);
			if (accountText !=null && !accountText.equals("")){
				etPassword.requestFocus();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

			//显示密码
			case R.id.login_mima_xianshi:
				if (yingcang) {
					etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					ed_pass_visiblity.setBackgroundResource(R.drawable.yincang);
					yingcang = false;
				} else {
					etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					ed_pass_visiblity.setBackgroundResource(R.drawable.xianshi);
					yingcang = true;
				}
				break;

			case R.id.activity_login_forget:
				toSetLoginPass();
				break;

			case R.id.activity_login_btnok:
				if (!getInput()) {
					return;
				}
				verifyAccount();
				break;

			case R.id.login_num_qingchu:
				etAccount.setText("");
				break;

			case R.id.login_mima_qingchu:
				etPassword.setText("");
				break;

			case R.id.login_zhuce:
				toRegister();
				break;

			default:
				break;
		}
	}

	/**
	 * 去注册
	 */
	protected void toRegister() {
		// 跳转到输入验证码的界面
		Intent intent = new Intent(context, PhoneActivity.class);
		String accountText = etAccount.getText().toString();
		if (accountText.length() > 0) {
			intent.putExtra("accountText", accountText);
		}
		startActivity(intent);
	}

	public boolean isTrue() {
		if (etAccount.getText().toString().length() == 0) {
			PromptUtils.showDialog3(context, "请输入您的手机号码！", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return false;
		}

		if (etAccount.getText().toString().length() < 13) {
			PromptUtils.showDialog3(context, "手机号码格式有误，请重新输入！", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return false;
		}

		return true;
	}

	/**
	 * 校验手机号码是否已经注册
	 */
	protected void verifyAccount() {
		if (amodel != null) {
			showLoading("正在验证账号");
			amodel.verifyAccount(account, new ResponseHandler() {
				public void onSuccess(String response, Object obj) {
					super.onSuccess(response, obj);
					closeLoading();
					// 未注册，去注册
					PromptUtils.showDialog4(activity, context, "", "该手机号码还未注册，是否前去注册？", "取消", "注册", new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							dialog.dismiss();
						}

						@Override
						public void onRightClick(Dialog dialog) {
							dialog.dismiss();
							toRegister();
						}
					}, false);

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if ("1011".equals(errorCode)) {
						login();
					}
				}
			});
		}
	}


	/**
	 * 忘记密码，去设置新密码
	 */
	private void toSetLoginPass() {
		Intent intent = new Intent(context, ForgetPassActivity.class);
		intent.putExtra("account", account);
		intent.putExtra("accountText", etAccount.getText().toString());
		startActivity(intent);
		finish();
	}

	/**
	 * 登录
	 */
	private void login() {

		if (amodel != null) {
			String passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
			showLoading("正在登录");
			amodel.login(account, passmd5, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof LoginData) {
						// 登录成功
						LoginData loginData = (LoginData) t;
						loginSuccess(loginData);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
					App.isLogin = false;
					App.account = null;
					App.userinfo = null;
				}
			});
		}
	}

	/**
	 * 获取输入
	 *
	 * @return
	 */
	private boolean getInput() {
		password = etPassword.getText().toString().trim();
		if (StringUtils.isBlank(password)) {
			ToastUtils.show(context, "请输入密码");
			return false;
		}
		return true;
	}

	/**
	 * 登录成功
	 */
	private void loginSuccess(LoginData loginData) {
		App.isLogin = true;
		App.account = account;
		App.code = loginData.code;
		App.isVerified = true;
		App.loginData = loginData;

		SystemUtils.configBugly(appContext);
		CrashReport.setUserId(loginData.uid);
		//友盟登录成功
//		MobclickAgent.onProfileSignIn(loginData.uid);

		// 缓存LoginData
		DataMgr.getInstance(appContext).saveLoginData(loginData);

		String passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);

		// 保存用户名和密码的MD5
		User user = new User(account, passmd5);
		DataMgr.getInstance(appContext).saveUserData(user);

		// 登录成功后，获取用户信息
		EventBus.getDefault().post(new UserInfoEvent());

		// 登录状态改变，通知其他界面更新
		EventBus.getDefault().post(new LoginChangeEvent());

		btnOk.setClickable(false);

		setResult(233);

		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"登录成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		setResult(2);

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent data = getIntent();
				boolean isBuy = data.getBooleanExtra("isBuy", false);
				if (isBuy) {
					String origin = data.getStringExtra("origin");
					BaseEvent event = new BaseEvent();
					event.origin = origin;
					event.action = "login";
					EventBus.getDefault().post(event);
				}
				finish();
				EventBus.getDefault().post(new SetNewPassEvent());
			}
		}, 1200);
	}

	public void onEventMainThread(SetNewPassEvent event) {
		finish();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.activity_login_zhanghao:
				zhanghao_line.setBackgroundColor(Color.parseColor("#eb515e"));
				mima_line.setBackgroundColor(Color.parseColor("#cccccc"));
				if (etAccount.getText().toString().trim().length()>0){
					format.setVisibility(View.GONE);
				}else {
					format.setVisibility(View.GONE);
				}
				break;
			case R.id.activity_login_pass_et:
				zhanghao_line.setBackgroundColor(Color.parseColor("#cccccc"));
				mima_line.setBackgroundColor(Color.parseColor("#eb515e"));
				format.setVisibility(View.GONE);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==666){
			finish();
		}
	}
}
