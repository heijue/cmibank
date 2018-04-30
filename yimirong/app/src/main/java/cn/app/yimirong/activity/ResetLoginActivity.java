package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class ResetLoginActivity extends BaseActivity implements View.OnClickListener, EditText.OnFocusChangeListener {

	private String account;

	private TextView phoneNum, again;

	private EditText yanzNum;

	private String yanZ;

	private Button submit;

	private View mM_line;

	private boolean isBuy = false;

	private String origin;

	private int recLen = 60;

	private ScheduledExecutorService scheduledExecutor;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_reset_login);
	}

	@Override
	public void initView() {
		setTitleText("重置登录密码");
		setTitleBack(true);
		titleBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*PromptUtils.showDialog4(activity, context, "", "验证码短信可能略有延迟，确定返回并重新开始？", "等待", "返回", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						dialog.dismiss();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						dialog.dismiss();
						exit();
					}
				}, false);*/
				 final Dialog dialog = new Dialog(activity,R.style.DialogStyle);
				PromptUtils.showDialog3(dialog, "验证码短信可能略有延迟，确定返回并重新开始？", new PromptUtils.OnDialogClickListener3() {
					@Override
					public void onLeftClick() {
						dialog.dismiss();
					}

					@Override
					public void onRightClick() {
						dialog.dismiss();
						exit();
					}
				},"等待","返回");
			}
		});

		mM_line = (View) this.findViewById(R.id.test_mima_line);
		phoneNum = (TextView) findViewById(R.id.activity_resetyanzheng_zhanghao);
		again = (TextView) findViewById(R.id.reset_reset);
		yanzNum = (EditText) findViewById(R.id.activity_resetyanzheng_ma);
		submit = (Button) findViewById(R.id.reset_text_btn);
		yanzNum.setOnFocusChangeListener(this);
		again.setOnClickListener(this);
		submit.setOnClickListener(this);
		submit.setClickable(false);
		again.setClickable(false);

		yanzNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					submit.setClickable(false);
					submit.setTextColor(Color.parseColor("#ff9898"));
				} else {
					submit.setClickable(true);
					submit.setTextColor(Color.parseColor("#ffffff"));
				}
			}
		});

	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		String number = intent.getStringExtra("accountText");
		account = intent.getStringExtra("account");
		phoneNum.setText(number);
		sendResetVerifyCode();

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


	private void resetPassTest() {
		// 获取验证码和密码
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在处理");
			amodel.resetrTest(account, yanZ, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null) {
						Intent data = getIntent();
						boolean isBuy = data.getBooleanExtra("isBuy", false);
						String origin = data.getStringExtra("origin");
						Intent intent = new Intent(context, RegisterActivity.class);
						intent.putExtra("isSetNewPass", true);
						intent.putExtra("account", account);
						intent.putExtra("yanZ", yanZ);
						intent.putExtra("isBuy", isBuy);
						intent.putExtra("origin", origin);
						startActivity(intent);
						finish();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.reset_reset:
				sendResetVerifyCode();
				break;
			case R.id.reset_text_btn:
				resetPassTest();
				break;
		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.test_mima_line:
				if (hasFocus && yanzNum.getText().toString().length() > 0) {
					mM_line.setBackgroundResource(R.color.control_text_pressed);
				} else {
					mM_line.setBackgroundColor(Color.parseColor("#cccccc"));
				}
				break;
		}
	}


	/**
	 * 启动计时任务
	 */
	private void startTimer() {
		again.setClickable(false);
		again.setTextColor(Color.parseColor("#808080"));
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (recLen == 0) {
							again.setText("发送验证码");
							again.setTextColor(Color.parseColor("#ff4747"));
							again.setClickable(true);
							scheduledExecutor.shutdown();
						} else {
							again.setText(recLen + "秒");
							again.setTextColor(Color.parseColor("#808080"));
						}
						recLen--;
					}
				});
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	/**
	 * 获取输入
	 */
	private boolean getInput() {
		yanZ = yanzNum.getText().toString().trim();

		if (StringUtils.isBlank(yanZ)) {
			ToastUtils.show(context, "请输入验证码");
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdownNow();
		}
		super.onDestroy();
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
						recLen = 60;
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

}
