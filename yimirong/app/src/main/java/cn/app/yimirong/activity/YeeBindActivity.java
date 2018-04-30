package cn.app.yimirong.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.SetPayPassEvent;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class YeeBindActivity extends BaseActivity implements OnClickListener {

	private EditText etName;
	private String name;

	private EditText etIDNum;
	private String id;

	private LinearLayout llBank;

	private TextView tvBank;
	private String bankCode;
	private ImageView ivBank;

	private EditText etCard;
	private String bankCard;

	private EditText etPhone;
	private String bankPhone;

	private RelativeLayout rlCard;

	private TextView tvCard;

	private Button btnNext;

	private BitmapUtils bitmapUtils;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_yee_bind);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("银行卡管理");

		etName = (EditText) findViewById(R.id.activity_yee_bind_name);
		etIDNum = (EditText) findViewById(R.id.activity_yee_bind_idnum);

		llBank = (LinearLayout) findViewById(R.id.activity_yee_bind_bank_wrapper);
		tvBank = (TextView) findViewById(R.id.activity_yee_bind_bank);
		ivBank = (ImageView) findViewById(R.id.activity_yee_bind_bank_icon);
		llBank.setOnClickListener(this);

		etCard = (EditText) findViewById(R.id.activity_yee_bind_card);
		rlCard = (RelativeLayout) findViewById(R.id.activity_yee_bind_card_wrapper);
		tvCard = (TextView) findViewById(R.id.activity_yee_bind_card_number);

		etPhone = (EditText) findViewById(R.id.activity_yee_bind_phone);
		if (App.account != null) {
			etPhone.setText(App.account);
		}

		btnNext = (Button) findViewById(R.id.activity_yee_bind_next);
		btnNext.setOnClickListener(this);

		etCard.addTextChangedListener(new TextWatcher() {

			private int action = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				action = before;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					rlCard.setVisibility(View.GONE);
				} else {
					rlCard.setVisibility(View.VISIBLE);
					if (action == 0) {
						// 增
						if (s.length() == 5 || s.length() == 10
								|| s.length() == 15 || s.length() == 20) {
							s.insert(s.length() - 1, " ");
						}

					} else {
						// 删
						if (s.length() == 5 || s.length() == 10
								|| s.length() == 15 || s.length() == 20) {
							s.delete(s.length() - 1, s.length());
						}
					}
					tvCard.setText(s);
				}
			}
		});
	}

	@Override
	public void initData() {
		bitmapUtils = new BitmapUtils(context);
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_yee_bind_bank_wrapper:
				toChooseBank();
				break;

			case R.id.activity_yee_bind_next:
				submit();
				break;

			default:
				break;
		}
	}

	/**
	 * 提交
	 */
	private void submit() {
		hideInputMethod();
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在处理");
			amodel.yeeRegist(bankCode, bankCard, id, name, bankPhone,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							closeLoading();
							if (t != null && t instanceof String) {
								String requestid = (String) t;
								toNext(requestid);
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
	 * 绑卡第二步，输入短信验证码
	 */
	protected void toNext(String requestid) {
		Intent intent = new Intent(context, SmsCodeActivity.class);
		intent.putExtra("requestid", requestid);
		intent.putExtra("title", "银行卡管理");
		intent.putExtra("action", SmsCodeActivity.ACTION_YEE_REGIST_CHECK);
		intent.putExtra("bankPhone", bankPhone);
		intent.putExtra("bankCode", bankCode);
		startActivity(intent);
	}

	/**
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		name = etName.getText().toString().trim();
		if (StringUtils.isBlank(name)) {
			ToastUtils.show(context, "请输入姓名");
			return false;
		}

		id = etIDNum.getText().toString().trim();
		if (StringUtils.isBlank(id)) {
			ToastUtils.show(context, "请输入身份证号");
			return false;
		} else {
			if (!(id.length() == 18 || id.length() == 15)) {
				ToastUtils.show(context, "身份证号格式不正确");
				return false;
			}
		}

		if (StringUtils.isBlank(bankCode)) {
			ToastUtils.show(context, "请选择开户银行");
			return false;
		}

		bankCard = etCard.getText().toString().trim();
		bankCard = bankCard.replace(" ", "");
		if (StringUtils.isBlank(bankCard)) {
			ToastUtils.show(context, "请输入银行卡号");
			return false;
		} else {
			if (!(bankCard.length() <= 19 && bankCard.length() >= 16)) {
				ToastUtils.show(context, "银行卡号格式不正确");
				return false;
			}
		}

		bankPhone = etPhone.getText().toString().trim();
		if (StringUtils.isBlank(bankPhone)) {
			ToastUtils.show(context, "请输入银行预留手机号");
			return false;
		} else {
			if (bankPhone.length() != 11) {
				ToastUtils.show(context, "手机号格式不正确");
				return false;
			}
		}
		return true;
	}

	/**
	 * 选择银行
	 */
	private void toChooseBank() {
		Intent intent = new Intent(context, BankActivity.class);
		startActivity(intent);
	}

	/**
	 * 显示选择的银行
	 *
	 * @param bank
	 */
	public void onEventMainThread(Bank bank) {
		if (bank != null) {
			bankCode = bank.code;
			tvBank.setText(bank.name);
			ivBank.setVisibility(View.VISIBLE);
			bitmapUtils.display(ivBank, bank.url);
		}
	}

	public void onEventMainThread(SetPayPassEvent event) {
		finish();
	}

}
