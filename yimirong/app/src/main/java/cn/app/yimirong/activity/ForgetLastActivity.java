package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.ForgetEvent;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.ToastUtils;

public class ForgetLastActivity extends BaseActivity implements EditText.OnFocusChangeListener, View.OnClickListener {

	private TextView etAccount;
	private EditText first, second;
	private ImageView fReset, sReset;
	private String verifyCode;
	private String account;
	private View fLine, sLine;
	private String password;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_forget_last);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("设置密码");
		setTitleRight(true, new OnRightClickListener() {
			@Override
			public void onClick() {
				submit();
			}
		});
		setRightText("完成");

		etAccount = (TextView) findViewById(R.id.account_number);
		first = (EditText) findViewById(R.id.first_number);
		second = (EditText) findViewById(R.id.second_number);
		fReset = (ImageView) findViewById(R.id.first_qingchu);
		sReset = (ImageView) findViewById(R.id.second_qingchu);
		fLine = (View) findViewById(R.id.first_line);
		sLine = (View) findViewById(R.id.second_line);

		first.setOnFocusChangeListener(this);
		second.setOnFocusChangeListener(this);
		fReset.setOnClickListener(this);
		sReset.setOnClickListener(this);

		assert first != null;
		first.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() != 0) {
					fReset.setVisibility(View.VISIBLE);
				} else {
					fReset.setVisibility(View.GONE);
				}
			}
		});

		assert second != null;
		second.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() != 0) {
					sReset.setVisibility(View.VISIBLE);
				} else {
					sReset.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		verifyCode = intent.getStringExtra("yanZ");
		account = intent.getStringExtra("account");
		String number = intent.getStringExtra("accountText");
		etAccount.setText(number);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.first_number:
				if (hasFocus) {
					fLine.setBackgroundResource(R.color.control_text_pressed);
				} else {
					fLine.setBackgroundColor(Color.parseColor("#cccccc"));
				}
				break;
			case R.id.second_number:
				if (hasFocus) {
					sLine.setBackgroundResource(R.color.control_text_pressed);
				} else {
					sLine.setBackgroundColor(Color.parseColor("#cccccc"));
				}
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.first_qingchu:
				first.setText("");
				break;

			case R.id.second_qingchu:
				second.setText("");
				break;
		}
	}

	private void submit() {
		if (!getInput()) {
			return;
		}

		password = first.getText().toString().trim();

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
	 * 显示成功的消息
	 */
	protected void showSuccess() {
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"已设置密码");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toLogin();
			}
		}, 1200);
	}

	/**
	 * 重置完密码，去登录
	 */
	protected void toLogin() {
		EventBus.getDefault().post(new ForgetEvent());
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("account", etAccount.getText().toString());
		startActivity(intent);
		finish();
	}


	/**
	 * 获取输入
	 */
	private boolean getInput() {
		String one = first.getText().toString().trim();
		String twe = second.getText().toString().trim();
		if (one.equals("") && one == null) {
			PromptUtils.showDialog1(activity, context, "", "请设置密码", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return false;
		}

		if (twe.equals("") && twe == null) {
			PromptUtils.showDialog1(activity, context, "", "请确认密码", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return false;
		}

		if (!one.equals(twe)) {
			PromptUtils.showDialog1(activity, context, "", "两次密码输入不一致！", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return false;
		}

		if (one.length() < 6 && twe.length() < 6) {
			PromptUtils.showDialog1(activity, context, "", "密码长度过小！", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return false;
		}


		return true;
	}
}
