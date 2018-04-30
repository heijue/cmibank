package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.utils.SystemUtils;

/**
 * 回款详情
 *
 * @author android
 */
public class DQPayBackDetailActivity extends BaseActivity {

	private TextView tvName;
	private TextView tvMoney;
	private TextView tvBackWay;
	private TextView tvPayNum;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_dqpayback_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("回款详情");

		tvName = (TextView) findViewById(R.id.activity_payback_detail_pname);
		tvMoney = (TextView) findViewById(R.id.activity_payback_detail_money);
		tvBackWay = (TextView) findViewById(R.id.activity_payback_detail_backway);
		tvPayNum = (TextView) findViewById(R.id.activity_payback_detail_paynum);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		ActionLog actionlog = null;
		if (bundle != null) {
			actionlog = (ActionLog) bundle.getSerializable("actionlog");
		}
		updateView(actionlog);
	}

	/**
	 * 更新界面
	 */
	private void updateView(ActionLog actionlog) {
		if (actionlog != null) {
			tvName.setText(actionlog.pname);

			tvMoney.setText(SystemUtils.getDoubleStr(actionlog.money));

			tvBackWay.setText("账户余额");

			tvPayNum.setText(actionlog.orderid);
		}
	}

}
