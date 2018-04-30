package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.LoginChangeEvent;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;


public class PhoneTestActivity extends BaseActivity implements View.OnClickListener, EditText.OnFocusChangeListener {

	private ImageView qingchu;

	private String account;

	private TextView phoneNum, again;

	private EditText yanzNum;

	private String yanZ;

	private Button submit;

	private View mM_line;

	private PopupWindow window;

	private boolean isBuy = false;

	private String origin;

	private int recLen = 60;

	private ScheduledExecutorService scheduledExecutor;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_phone_test);
		EventBus.getDefault().register(this);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("");
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

		mM_line = (View) findViewById(R.id.test_mima_line);
		qingchu = (ImageView) findViewById(R.id.yanzheng_num_qingchu);
		phoneNum = (TextView) findViewById(R.id.activity_yanzheng_zhanghao);
		again = (TextView) findViewById(R.id.reset_test_sms);
		yanzNum = (EditText) findViewById(R.id.activity_yanzheng_ma);
		submit = (Button) findViewById(R.id.zhuce_text_btn);
		yanzNum.setOnFocusChangeListener(this);
		again.setOnClickListener(this);
		qingchu.setOnClickListener(this);
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
				if (s.length() > 0) {
					submit.setClickable(true);
					submit.setTextColor(Color.parseColor("#ffffff"));
					qingchu.setVisibility(View.VISIBLE);
				} else {
					submit.setClickable(false);
					submit.setTextColor(Color.parseColor("#ff9898"));
					qingchu.setVisibility(View.GONE);
				}
			}
		});
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
	public void initData() {
		Intent intent = getIntent();
		String number = intent.getStringExtra("accountText");
		account = intent.getStringExtra("account");
		phoneNum.setText(number);
		sendRegisterVerifyCode();
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
							again.setText("收不到验证码？");
							again.setTextColor(Color.parseColor("#000000"));
							again.setClickable(true);
							scheduledExecutor.shutdown();
						} else {
							again.setText("接收短信大约需要" + recLen + "秒");
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


	/**
	 * 显示popupWindow
	 */
	private void showPopwindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.reset_sms_pop, null);

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

		window = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);

		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);


		// 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        window.setBackgroundDrawable(dw);


		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.alpha = 0.7f;
		this.getWindow().setAttributes(params);
		// 在底部显示
		window.showAtLocation(PhoneTestActivity.this.findViewById(R.id.reset_test_sms),
				Gravity.BOTTOM, 0, 0);

		// 这里检验popWindow里的button是否可以点击
		Button reset = (Button) view.findViewById(R.id.pop_reset_yanzheng);
		reset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				recLen = 60;
				sendRegisterVerifyCode();
				closeLoading();
				closePopupWindow();
			}
		});
		Button quxiao = (Button) view.findViewById(R.id.pop_reset_cancel);
		quxiao.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopupWindow();
			}
		});

		//popWindow消失监听方法
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				System.out.println("popWindow消失");
			}
		});

	}

	/**
	 * 关闭窗口
	 */
	private void closePopupWindow() {
		if (window != null && window.isShowing()) {
			window.dismiss();
			window = null;
			WindowManager.LayoutParams params = this.getWindow().getAttributes();
			params.alpha = 1f;
			this.getWindow().setAttributes(params);
		}
	}

	@Override
	protected void onDestroy() {
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdownNow();
		}
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.reset_test_sms:
				showPopwindow();
				break;
			case R.id.yanzheng_num_qingchu:
				yanzNum.setText("");
				break;
			case R.id.zhuce_text_btn:
				registerTest();
				break;

		}

	}

	private void registerTest() {
		// 获取验证码和密码
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在处理");
			amodel.registerTest(account, yanZ, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null) {
						Intent data = getIntent();
						boolean isBuy = data.getBooleanExtra("isBuy", false);
						String origin = data.getStringExtra("origin");
						Intent intent = new Intent(context, RegisterActivity.class);
						intent.putExtra("account", account);
						intent.putExtra("yanZ", yanZ);
						intent.putExtra("isBuy", isBuy);
						intent.putExtra("origin", origin);
						startActivity(intent);
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

	public void onEventMainThread(LoginChangeEvent event) {
		exit();
	}

}
