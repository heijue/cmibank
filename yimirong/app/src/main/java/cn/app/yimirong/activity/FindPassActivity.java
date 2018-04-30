package cn.app.yimirong.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.utils.StringUtils;

public class FindPassActivity extends BaseActivity {

	private TextView tvNumber;

	private Button btnNext;

	private String account;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_findpass);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("找回登录密码");

		tvNumber = (TextView) findViewById(R.id.activity_findpass_number);
		btnNext = (Button) findViewById(R.id.activity_findpass_next);
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toIdentify();
			}
		});
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		account = intent.getStringExtra("account");
		tvNumber.setText(StringUtils.getSecretAccount(account));
	}

	/**
	 * 去身份验证
	 */
	protected void toIdentify() {
		Intent intent = new Intent(context, IdentifyActivity.class);
		startActivity(intent);
	}

}
