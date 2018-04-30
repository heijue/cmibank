package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.ToastUtils;

public class ForgetPassActivity extends BaseActivity implements View.OnClickListener {

	private String account;

	private Button btnNext;

	private ImageView reset;

	private EditText etNumber;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_forget_pass);
	}

	@Override
	public void initView() {
		setTitleBack(true);

		setTitleText("忘记密码");
		reset = (ImageView) findViewById(R.id.zhuce_phone_qingchu);
		etNumber = (EditText) findViewById(R.id.activity_phone_numer_et);
		btnNext = (Button) findViewById(R.id.activity_phone_next);
		assert btnNext != null;
		btnNext.setOnClickListener(this);
		reset.setOnClickListener(this);

		btnNext.setClickable(false);

		assert etNumber != null;
		etNumber.addTextChangedListener(new TextWatcher() {

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

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					reset.setVisibility(View.VISIBLE);
					btnNext.setBackground(getResources().getDrawable(R.drawable.shape_login_press));
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

				} else {
					reset.setVisibility(View.GONE);
					btnNext.setBackground(getResources().getDrawable(R.drawable.shape_invite_btn));
				}

				if (s.length() == 13) {
					btnNext.setClickable(true);
					account = s.toString().trim();
					account = account.replace(" ", "");
				} else {
					btnNext.setClickable(false);
				}
			}
		});
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		String etA = intent.getStringExtra("accountText");
		String teA = intent.getStringExtra("account");
		if (etA != null && !etA.equals("")) {
			etNumber.setText(etA);
		}
		if (teA != null && !teA.equals("")) {
			account = teA;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_phone_next:
				/*PromptUtils.showDialog2(activity, context, "您是否想通过该账号发送短信验证码？", "取消", "确定", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						dialog.dismiss();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						dialog.dismiss();
						verifyAccount();
					}
				}, false);*/
				final Dialog dialog = new Dialog(context, R.style.DialogStyle);
				PromptUtils.showDialog3(dialog,"您是否想通过该账号发送短信验证码？", new PromptUtils.OnDialogClickListener3() {

					@Override
					public void onLeftClick() {
						dialog.dismiss();
					}

					@Override
					public void onRightClick() {
						dialog.dismiss();
						verifyAccount();
					}
				},"取消","确定");
				break;

			case R.id.zhuce_phone_qingchu:
				etNumber.setText("");
				break;
			default:
				break;
		}
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
			/*		PromptUtils.showDialog4(activity, context, "", "该手机号码还未注册，是否前去注册？", "取消", "注册", new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							dialog.dismiss();
						}

						@Override
						public void onRightClick(Dialog dialog) {
							dialog.dismiss();
							toRegister();
						}
					}, false);*/
					final Dialog dialog = new Dialog(context, R.style.DialogStyle);
					PromptUtils.showDialog3(dialog,"该手机号码还未注册，是否前去注册？", new PromptUtils.OnDialogClickListener3() {

						@Override
						public void onLeftClick() {
							dialog.dismiss();
						}

						@Override
						public void onRightClick() {
							dialog.dismiss();
							toRegister();
						}
					},"取消","注册");

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if ("1011".equals(errorCode)) {
						sendResetVerifyCode();
					}
				}
			});
		}
	}

	/**
	 * 去注册
	 */
	protected void toRegister() {
		// 跳转到输入验证码的界面
		Intent intent = new Intent(context, PhoneActivity.class);
		intent.putExtra("account", account);
		intent.putExtra("accountText", etNumber.getText().toString());
		startActivity(intent);
		finish();
	}

	/**
	 * 去注册
	 */
	protected void toForget() {
		// 跳转到输入验证码的界面
		Intent intent = new Intent(context, ForgetPassTestActivity.class);
		intent.putExtra("account", account);
		intent.putExtra("accountText", etNumber.getText().toString());
		startActivity(intent);
		finish();
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
						toForget();
						ToastUtils.show(context, message);
					}
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
					closeLoading();
				}

			});
		}
	}
}
