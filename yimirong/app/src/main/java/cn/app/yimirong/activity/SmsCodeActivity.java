package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.SetPayPassEvent;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class SmsCodeActivity extends BaseActivity implements OnClickListener {

	public static final int ACTION_NONE = 0;

	public static final int ACTION_RESET_PAY_PASS = 1;

	public static final int ACTION_YEE_REGIST_CHECK = 2;

	public static final int ACTION_REGIST_YEE_CHECK = 3;

	private TextView tvNumber;

	private EditText etVeriCode;

	private TextView tvTimer;

	private Button btnNext;

	private TextView tvServicePhone;

	private int action;

	private String verifyCode;

	private String requestid;

	private String bankCode;

	private String bankPhone;

	private String message;

	private String idcard;

	private String realname;

	private ScheduledExecutorService scheduledExecutor;

	private int elapseTime;

	@Override
	public void loadView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_sms_code);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			PromptUtils.showDialog4(activity, context, "", "验证码短信可能略有延迟，确定返回并重新开始？", "等待", "返回", new PromptUtils.OnDialogClickListener2() {
				@Override
				public void onLeftClick(Dialog dialog) {
					dialog.dismiss();
				}

				@Override
				public void onRightClick(Dialog dialog) {
					dialog.dismiss();
					exit();
				}
			}, false);

		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void initView() {
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		setTitleBack(true);
		setTitleText(title);
		titleBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PromptUtils.showDialog4(activity, context, "", "验证码短信可能略有延迟，确定返回并重新开始？", "等待", "返回", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						dialog.dismiss();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						dialog.dismiss();
						exit();
					}
				}, false);
			}
		});


		action = intent.getIntExtra("action", ACTION_NONE);

		if (action == ACTION_RESET_PAY_PASS) {
			message = intent.getStringExtra("message");
			idcard = intent.getStringExtra("idcard");
			realname = intent.getStringExtra("realname");
		} else if (action == ACTION_YEE_REGIST_CHECK) {
			requestid = intent.getStringExtra("requestid");
			bankCode = intent.getStringExtra("bankCode");
			bankPhone = intent.getStringExtra("bankPhone");
		} else if (action == ACTION_REGIST_YEE_CHECK) {
			requestid = intent.getStringExtra("requestid");
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			finish();
		}

		tvNumber = (TextView) findViewById(R.id.activity_sms_code_numer);
		if (action == ACTION_YEE_REGIST_CHECK) {
			if (bankPhone != null) {
				String secretPhone = StringUtils.getSecretAccount(bankPhone);
				tvNumber.setText("已发送短信验证码到号码\n" + secretPhone);
			}
		} else {
			tvNumber.setText("已发送短信验证码到\n您的银行预留手机号");
		}

		etVeriCode = (EditText) findViewById(R.id.activity_sms_code_et);
		tvTimer = (TextView) findViewById(R.id.activity_sms_code_timer);
		if (action == ACTION_YEE_REGIST_CHECK) {
			tvTimer.setVisibility(View.GONE);
		} else {
			tvTimer.setVisibility(View.VISIBLE);
			tvTimer.setClickable(true);
			tvTimer.setOnClickListener(this);
		}

		btnNext = (Button) findViewById(R.id.activity_sms_code_btnok);
		btnNext.setOnClickListener(this);

		tvServicePhone = (TextView) findViewById(R.id.activity_sms_code_service_phone);
		tvServicePhone.setOnClickListener(this);

		assert etVeriCode != null;
		etVeriCode.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					btnNext.setClickable(false);
					btnNext.setTextColor(Color.parseColor("#ff9898"));
				} else {
					btnNext.setClickable(true);
					btnNext.setTextColor(Color.parseColor("#ffffff"));
				}
			}
		});
	}

	@Override
	public void initData() {
		if (action == ACTION_RESET_PAY_PASS) {
			ToastUtils.show(context, message != null ? message : "验证码已发送");
			startTimer();
		} else {
			ToastUtils.show(context, "验证码已发送");
			if (action == ACTION_REGIST_YEE_CHECK) {
				startTimer();
			}
		}
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_sms_code_timer:
				resendVerifyCode();
				break;
			case R.id.activity_sms_code_btnok:
				if (action == ACTION_RESET_PAY_PASS) {
					payPassTest();
				} else if (action == ACTION_YEE_REGIST_CHECK) {
					bindCardCheck();
				} else if (action == ACTION_REGIST_YEE_CHECK) {
					registYeeCheck();
				} else {
				}
				break;

			case R.id.activity_sms_code_service_phone:
				callService();
				break;

			default:
				break;
		}
	}

	/**
	 * 拨打客服电话
	 */
	private void callService() {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		Uri data = Uri.parse("tel:"
				+ getResources().getString(R.string.service_phone));
		intent.setData(data);
		startActivity(intent);
	}

	/**
	 * 验证重置支付密码验证码
	 */
	private void payPassTest() {
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在验证");
			amodel.resetPayTest(verifyCode, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					String message = (String) t;
					ToastUtils.show(context, message);
					toResetPayPass();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					PromptUtils.showDialog1(activity, context, "", msg, "确定", new PromptUtils.OnDialogClickListener1() {

						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					}, false);
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
		tvTimer.setTextColor(Color.parseColor("#000000"));
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (elapseTime == 0) {
							tvTimer.setTextColor(Color.parseColor("#ff4747"));
							tvTimer.setText("发送验证码");
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

	/**
	 * 重发验证码
	 */
	private void resendVerifyCode() {
		etVeriCode.setText("");
		if (amodel != null) {
			showLoading("正在获取验证码");
			if (action == ACTION_RESET_PAY_PASS) {
				amodel.resetPayPassStep1(idcard, realname, new ResponseHandler() {
					@Override
					public <T> void onSuccess(String response, T t) {
						super.onSuccess(response, t);
						closeLoading();
						String message = (String) t;
						ToastUtils.show(context, message);
						startTimer();
					}

					@Override
					public void onFailure(String errorCode, String msg) {
						super.onFailure(errorCode, msg);
						closeLoading();
						ToastUtils.show(context, msg);
					}
				});
			} else {
				amodel.yeeRegist(new ResponseHandler() {
					@Override
					public <T> void onSuccess(String response, T t) {
						super.onSuccess(response, t);
						closeLoading();
						ToastUtils.show(context, "验证码已发送");
						startTimer();
					}

					@Override
					public void onFailure(String errorCode, String msg) {
						super.onFailure(errorCode, msg);
						ToastUtils.show(context, msg);
					}
				});
			}
		}
	}

	/**
	 * 易宝注册确认
	 */
	public void registYeeCheck() {
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在验证");
			amodel.yeeRegistCheck(requestid, verifyCode, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					//注册成功
					Intent data = new Intent();
					data.putExtra("isSuccess", true);
					setResult(ACTION_REGIST_YEE_CHECK, data);
					finish();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
					etVeriCode.setText("");
				}
			});
		}
	}

	/**
	 * 绑卡校验短信验证码
	 */
	private void bindCardCheck() {
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在验证");
			amodel.yeeRegistCheck(requestid, verifyCode, bankCode,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							closeLoading();
							ToastUtils.show(context, "银行卡添加成功");
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									toSetPassword();
								}
							}, 1000);
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
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		verifyCode = etVeriCode.getText().toString().trim();
		if (StringUtils.isBlank(verifyCode)) {
			ToastUtils.show(context, "请输入验证码");
			return false;
		} else {
			if (!(verifyCode.length() == 6 || verifyCode.length() == 4)) {
				ToastUtils.show(context, "验证码错误");
				etVeriCode.setText("");
				return false;
			}
		}
		return true;
	}

	/**
	 * 去重新设置交易密码
	 */
	private void toResetPayPass() {
		if (!getInput()) {
			return;
		}
		Intent intent = new Intent(context, SetPayPassActivity.class);
		intent.putExtra("verifyCode", verifyCode);
		intent.putExtra("isResetPayPass", true);
		startActivity(intent);
		finish();
	}

	/**
	 * 去设置密码
	 */
	private void toSetPassword() {
		Intent intent = new Intent(context, SetPayPassActivity.class);
		startActivity(intent);
		finish();
	}

	public void onEventMainThread(SetPayPassEvent event) {
		finish();
	}


}
