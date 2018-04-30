package cn.app.yimirong.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.fuiou.mobile.util.DialogUtils;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class FeedBackActivity extends BaseActivity {

	private EditText etPhone;

	private EditText etContent;

	private TextView btnSend;

	private String phone;

	private String content;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_feedback);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("意见反馈");
		etPhone = (EditText) findViewById(R.id.activity_feedback_phone);
		etContent = (EditText) findViewById(R.id.activity_feedback_content);
		if (App.isLogin && !StringUtils.isBlank(App.account)) {
			etPhone.setText(StringUtils.getSecretAccount(App.account));
			etContent.setFocusable(true);
		}
		btnSend = (TextView) findViewById(R.id.activity_feedback_send);
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
		btnSend.setClickable(false);
		etContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					btnSend.setTextColor(Color.parseColor("#ff9898"));
					btnSend.setClickable(false);
				} else {
					btnSend.setClickable(true);
					btnSend.setTextColor(Color.parseColor("#ffffff"));
				}
			}
		});

		findViewById(R.id.title_bar_back_text).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (etContent.getText().toString().length() <= 0) {
					exit();
				}
				PromptUtils.showDialog4(FeedBackActivity.this,context,"","您确定放弃所编辑内容吗？", "继续编辑","放弃",new PromptUtils.OnDialogClickListener2(){
					@Override
					public void onLeftClick(Dialog dialog) {
						dialog.dismiss();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						exit();
					}
				},true);
			}
		});

	}

	@Override
	public void initData() {
	}

	/**
	 * 发送
	 */
	protected void send() {
		if (!getInput()) {
			return;
		}
		if (amodel != null) {
			showLoading("正在提交");
			cmodel.sendFeedBack(phone, content, new ResponseHandler() {

				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					ToastUtils.show(context, "提交成功");
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 500);
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
		if (etPhone.getText().toString().trim().contains("*")) {
			phone = App.account;
		}else {
			phone = etPhone.getText().toString().trim();
		}
		content = etContent.getText().toString().trim();

		if (StringUtils.isBlank(phone)) {
			ToastUtils.show(context, "请输入手机号");
			return false;
		} else {
			if (phone.length() != 11) {
				ToastUtils.show(context, "手机号码格式不正确");
				return false;
			}
		}

		if (StringUtils.isBlank(content)) {
			ToastUtils.show(context, "请输入内容");
			return false;
		}

		return true;
	}

}
