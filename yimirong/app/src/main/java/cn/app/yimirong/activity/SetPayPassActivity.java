package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.view.KeyBoard;
import cn.app.yimirong.view.KeyBoard.InputListener;

public class SetPayPassActivity extends BaseActivity {

	/**
	 * 密码图片
	 */
	private static int PASS_IMAGE = R.drawable.shape_dot_black;

	private ImageView ivPass1;
	private ImageView ivPass2;
	private ImageView ivPass3;
	private ImageView ivPass4;
	private ImageView ivPass5;
	private ImageView ivPass6;

	private KeyBoard keyboard;

	// 密码
	private String password;

	private int i = 0;

	private char[] buffer = new char[6];
	private int len = 0;

	private TextView tvMsg, tvNotice;

	private boolean isResetPayPass;

	private String verifyCode;

	private String passmd5 = null;

	private boolean isOK = false;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_setpass);
	}


	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("设置支付密码");
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
		tvMsg = (TextView) findViewById(R.id.activity_setpass_msg);
		tvNotice = (TextView) findViewById(R.id.activity_setpass_info);
		ivPass1 = (ImageView) findViewById(R.id.activity_setpass_password_1);
		ivPass2 = (ImageView) findViewById(R.id.activity_setpass_password_2);
		ivPass3 = (ImageView) findViewById(R.id.activity_setpass_password_3);
		ivPass4 = (ImageView) findViewById(R.id.activity_setpass_password_4);
		ivPass5 = (ImageView) findViewById(R.id.activity_setpass_password_5);
		ivPass6 = (ImageView) findViewById(R.id.activity_setpass_password_6);

		keyboard = KeyBoard.getInstance(activity);


		keyboard.setInputListener(new InputListener() {
			@Override
			public void onInput(char ch) {
				handleInput(ch);
			}

			@Override
			public void onDelete(boolean isDeleteAll) {
				handleDelete(isDeleteAll);
			}
		});
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		isResetPayPass = intent.getBooleanExtra("isResetPayPass", false);
		if (isResetPayPass) {
			verifyCode = intent.getStringExtra("verifyCode");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isOK) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
//					keyboard.show(getWindow().getDecorView());
					keyboard.createKeyboard().showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, WindowManager.LayoutParams.WRAP_CONTENT);
				}
			}, 500);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		keyboard.close();
	}

	/**
	 * 显示密码
	 *
	 * @param position
	 */
	private void showPassword(int position) {
		switch (position) {
			case 1:
				ivPass1.setImageResource(PASS_IMAGE);
				break;

			case 2:
				ivPass2.setImageResource(PASS_IMAGE);
				break;

			case 3:
				ivPass3.setImageResource(PASS_IMAGE);
				break;

			case 4:
				ivPass4.setImageResource(PASS_IMAGE);
				break;

			case 5:
				ivPass5.setImageResource(PASS_IMAGE);
				break;

			case 6:
				ivPass6.setImageResource(PASS_IMAGE);
				break;

			default:
				break;
		}
	}

	/**
	 * 删除密码
	 */
	private void deletePassword(int position) {
		switch (position) {
			case 1:
				ivPass1.setImageDrawable(null);
				break;

			case 2:
				ivPass2.setImageDrawable(null);
				break;

			case 3:
				ivPass3.setImageDrawable(null);
				break;

			case 4:
				ivPass4.setImageDrawable(null);
				break;

			case 5:
				ivPass5.setImageDrawable(null);
				break;

			case 6:
				ivPass6.setImageDrawable(null);
				break;

			default:
				break;
		}
	}

	/**
	 * 删除所有密码
	 */
	public void deleteAllPassword() {
		ivPass1.setImageDrawable(null);
		ivPass2.setImageDrawable(null);
		ivPass3.setImageDrawable(null);
		ivPass4.setImageDrawable(null);
		ivPass5.setImageDrawable(null);
		ivPass6.setImageDrawable(null);
	}

	protected void handleInput(char ch) {
		if (len < 6) {
			buffer[len] = ch;
			len++;
			showPassword(len);
			if (len == 6) {
				// 输入完毕
				if (i == 0) {
					// 第一次输入密码
					password = new String(buffer);
					i++;
					len = 0;
					tvMsg.setText("请确认支付密码");
					tvNotice.setVisibility(View.INVISIBLE);
					deleteAllPassword();
				} else {
					String confirmPassword = new String(buffer);
					if (confirmPassword.equals(password)) {
						// 两次密码一致
						// 调用后台接口提交密码
						passmd5 = DigestUtils.md5(password).toUpperCase(
								Locale.CHINA);
						if (isResetPayPass) {
							resetPayPass(passmd5);
						} else {
							setPayPass(passmd5);
						}

					} else {
						// 两次密码不一致
						i = 0;
						len = 0;
						deleteAllPassword();
						tvMsg.setText("");
						tvNotice.setVisibility(View.VISIBLE);
						PromptUtils.showDialog1(activity, context, "", "两次密码不一致，请重新设置", "确定", new PromptUtils.OnDialogClickListener1() {
							@Override
							public void onClick(Dialog dialog) {
								dialog.dismiss();
							}
						});
					}
				}
			}
		}
	}

	/**
	 * 处理Backspace
	 *
	 * @param isDeleteAll
	 */
	protected void handleDelete(boolean isDeleteAll) {
		if (isDeleteAll) {
			len = 0;
			deleteAllPassword();
		} else {
			if (len > 0) {
				deletePassword(len);
				len--;
			}
		}
	}

	/**
	 * 重置支付密码
	 *
	 * @param passmd5
	 */
	private void resetPayPass(String passmd5) {
		if (amodel != null) {
			showLoading("正在处理");
			amodel.resetPayPassStep2(verifyCode, passmd5,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							closeLoading();
							showSuccess();
						}

						@Override
						public void onFailure(String errorCode, String msg) {
							super.onFailure(errorCode, msg);
							closeLoading();
							i = 0;
							len = 0;
							tvMsg.setText("请设置支付密码");
							deleteAllPassword();
							PromptUtils.showDialog1(activity, context, "", msg, "确定", new PromptUtils.OnDialogClickListener1() {
								@Override
								public void onClick(Dialog dialog) {
									dialog.dismiss();
								}
							});

						}
					});
		}
	}

	/**
	 * 设置支付密码
	 *
	 * @param passmd5
	 */
	protected void setPayPass(String passmd5) {
		if (amodel != null) {
			showLoading("正在处理");
			amodel.setPayPassword(passmd5, new ResponseHandler() {
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					isOK = true;
					showSuccess();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					isOK = false;
					i = 0;
					len = 0;
					tvMsg.setText("请设置支付密码");
					deleteAllPassword();
					PromptUtils.showDialog1(activity, context, "", msg, "确定", new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});

				}
			});
		}
	}

	/**
	 * 显示成功消息
	 */
	protected void showSuccess() {
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context, "设置成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
//                keyboard.close();
				close();
			}
		}, 1000);
	}

	/**
	 * 退出
	 */
	private void close() {
		// 用户已经设置了支付密码，重新获取用户数据
		EventBus.getDefault().post(new UserInfoEvent());
		// 关闭键盘
		keyboard.close();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 200);
	}

}
