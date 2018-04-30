package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.CancelEvent;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.ToastUtils;

public class PhoneActivity extends BaseActivity implements OnClickListener {

	private static final int REQUEST_CODE_REGISTER = 200;
	private static final int REQUEST_CODE_LOGIN = 201;

	private String account;

	private Button btnNext;

	private ImageView reset;

	private EditText etNumber;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_phone);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("请填写手机号");

		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new CancelEvent());
				exit();
			}
		});
		TextView tvLink = (TextView) findViewById(R.id.activity_register_link);

		tvLink.setOnClickListener(this);
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
		String accountText = intent.getStringExtra("accountText");
		if (accountText != null) {
			etNumber.setText(accountText);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_phone_next:
				verifyAccount();
				break;

			case R.id.zhuce_phone_qingchu:
				etNumber.setText("");
				break;

			case R.id.activity_register_link:
				toSeeProtocol();
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
					String msg = "+86 " + etNumber.getText().toString();
					PromptUtils.showIfVerify(activity, context, msg, "取消", "确定", new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							dialog.dismiss();
						}

						@Override
						public void onRightClick(Dialog dialog) {
							dialog.dismiss();
							MobclickAgent.onEvent(context,"registtbtn");
							toRegister();
						}
					}, false);

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if ("1011".equals(errorCode)) {
						// 账号存在
						// 去登录
						String m = "该账号已经注册，是否直接登录易米融？";
						PromptUtils.showDialog4(activity, context, "", m, "取消", "确定", new PromptUtils.OnDialogClickListener2() {
							@Override
							public void onLeftClick(Dialog dialog) {
								dialog.dismiss();
							}

							@Override
							public void onRightClick(Dialog dialog) {
								dialog.dismiss();
								toLogin();
							}
						}, false);

					} else {
						// 其他错误
						ToastUtils.show(context, msg);
					}
				}
			});
		}
	}

	/**
	 * 去查看用户注册协议
	 */
	private void toSeeProtocol() {
		Intent intent = new Intent(context, WebViewActivity.class);
		String title = "用户协议";
		String url = API.USER_AGREEMENT;
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		startActivity(intent);
	}

	/**
	 * 去注册
	 */
	protected void toRegister() {
		// 跳转到输入验证码的界面
		Intent data = getIntent();
		boolean isBuy = data.getBooleanExtra("isBuy", false);
		String origin = data.getStringExtra("origin");
		Intent intent = new Intent(context, PhoneTestActivity.class);
		intent.putExtra("account", account);
		intent.putExtra("accountText", etNumber.getText().toString());
		intent.putExtra("isBuy", isBuy);
		intent.putExtra("origin", origin);
		startActivityForResult(intent, REQUEST_CODE_REGISTER);
	}

	/**
	 * 去登录
	 */
	protected void toLogin() {
		// 跳转到输入密码的界面
		Intent data = getIntent();
		boolean isBuy = data.getBooleanExtra("isBuy", false);
		String origin = data.getStringExtra("origin");
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("account", etNumber.getText().toString());
		intent.putExtra("isBuy", isBuy);
		intent.putExtra("origin", origin);
		startActivityForResult(intent, REQUEST_CODE_LOGIN);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_REGISTER
				|| requestCode == REQUEST_CODE_LOGIN) {
			finish();
		}
	}
}
