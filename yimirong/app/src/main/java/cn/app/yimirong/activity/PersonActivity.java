package cn.app.yimirong.activity;

import android.widget.TextView;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.utils.StringUtils;

public class PersonActivity extends BaseActivity {

	private TextView tvAccount;

	private TextView tvName;

	private TextView tvIDNo;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_person);

		tvAccount = (TextView) findViewById(R.id.activity_person_account);

		tvName = (TextView) findViewById(R.id.activity_person_name);

		tvIDNo = (TextView) findViewById(R.id.activity_person_idno);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("个人信息");
	}

	@Override
	public void initData() {
		updateView();
	}

	/**
	 * 更新界面
	 */
	private void updateView() {
		if (App.account != null) {
			tvAccount.setText(StringUtils.getSecretAccount(App.account));
		}

		if (App.userinfo != null && App.userinfo.identity != null) {
			tvName.setText(App.userinfo.identity.realName);
			tvIDNo.setText(App.userinfo.identity.idCard);
		}
	}

}
