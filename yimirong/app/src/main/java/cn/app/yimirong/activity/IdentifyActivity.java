package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.SetPayPassEvent;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PhoneUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class IdentifyActivity extends BaseActivity implements OnClickListener, EditText.OnFocusChangeListener {

	// 姓名
	private EditText etName;
	private String name;

	// 身份证号
	private EditText etIDCard;
	private String idcard;


	private ImageView reset;
	// 下一步
	private Button btnNext;

	private TextView tvServicePhone;

	private boolean isResetPayPass;

	@Override
	public void loadView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_identify);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("身份验证");

		etName = (EditText) findViewById(R.id.activity_identify_name);

		etIDCard = (EditText) findViewById(R.id.activity_identify_idcard);
		etIDCard.setOnFocusChangeListener(this);


		reset = (ImageView) findViewById(R.id.set_paypass_qingchu);
		reset.setOnClickListener(this);


		btnNext = (Button) findViewById(R.id.activity_identify_btn_next);

		btnNext.setOnClickListener(this);

		// 监听身份证号码输入
		assert etIDCard != null;
		etIDCard.addTextChangedListener(new TextWatcher() {

			private int action = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				action = before;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() == 0) {
					reset.setVisibility(View.GONE);
					btnNext.setClickable(false);
					btnNext.setTextColor(Color.parseColor("#ff9898"));
				} else {
					btnNext.setClickable(true);
					btnNext.setTextColor(Color.parseColor("#ffffff"));
					reset.setVisibility(View.VISIBLE);
					if (action == 0) {
						// 增
						if (s.length() == 7 || s.length() == 12 || s.length() == 17) {
							s.insert(s.length() - 1, " ");
						}
					} else {
						// 删
						if (s.length() == 7 || s.length() == 12 || s.length() == 17) {
							s.delete(s.length() - 1, s.length());
						}
					}
				}
			}
		});


	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		isResetPayPass = intent.getBooleanExtra("isResetPayPass", false);
	}

	public void onEventMainThread(SetPayPassEvent event) {
		finish();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_identify_btn_next:
				if (isResetPayPass) {
					resetPayPassStep1();
				}
				break;

			case R.id.set_paypass_qingchu:
				etIDCard.setText("");
				break;
			default:
				break;
		}
	}

	/**
	 * 拨打客服电话
	 */
	private void callService() {
		String number = getResources().getString(R.string.service_phone);
		PhoneUtils.call(context, number);
	}

	/**
	 * 重置支付密码第一步
	 */
	protected void resetPayPassStep1() {
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在校验身份信息");
			amodel.resetPayPassStep1(idcard, name, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					String message = (String) t;
					if (message == null) {
						message = "验证码已发送！";
					}
					toSmsVerifyCode(message);
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
					});
				}
			});
		}
	}

	/**
	 * 去填写短信验证码
	 */
	protected void toSmsVerifyCode(String message) {
		Intent intent = new Intent(context, SmsCodeActivity.class);
		intent.putExtra("title", "重置支付密码");
		intent.putExtra("action", SmsCodeActivity.ACTION_RESET_PAY_PASS);
		intent.putExtra("message", message);
		intent.putExtra("idcard", idcard);
		intent.putExtra("realname", name);
		startActivity(intent);
		finish();
	}

	/**
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		name = etName.getText().toString().trim();
		if (StringUtils.isBlank(name)) {
			ToastUtils.show(context, "请输入您的真实姓名");
			return false;
		}

		idcard = etIDCard.getText().toString().trim();
		idcard = idcard.replace(" ", "");
		if (StringUtils.isBlank(idcard)) {
			ToastUtils.show(context, "请输入您的身份证号码");
			return false;
		} else {
			if (idcard.length() != 15 && idcard.length() != 18) {
				ToastUtils.show(context, "身份证号码格式不正确");
				return false;
			}
		}

		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.activity_identify_idcard:
				if (hasFocus && etIDCard.getText().toString().trim().length() > 0) {
				} else {
				}
				break;
		}
	}
}
