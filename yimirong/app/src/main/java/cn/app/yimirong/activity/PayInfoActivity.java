package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.DQProduct;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

public class PayInfoActivity extends BaseActivity {

	private TextView tvName;
	private TextView tvStart;
	private TextView tvEnd;
	private TextView tvDays;

	private BaseProduct product;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_payinfo);
	}

	@Override
	public void initView() {
		setTitleText("还款信息");
		setTitleBack(true);

		tvName = (TextView) findViewById(R.id.activity_payinfo_name);
		tvStart = (TextView) findViewById(R.id.activity_payinfo_start);
		tvEnd = (TextView) findViewById(R.id.activity_payinfo_end);
		tvDays = (TextView) findViewById(R.id.activity_payinfo_days);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		product = (BaseProduct) bundle.getSerializable("product");
		updateView();
	}

	/**
	 * 更新界面
	 */
	private void updateView() {
		if (product != null && product instanceof DQProduct) {
			DQProduct p = (DQProduct) product;
			tvName.setText(p.pname);
			tvStart.setText(p.uistime);
			String endDate = TimeUtils.addDay(1, p.uietime);
			tvEnd.setText(endDate);
			int days = SystemUtils.getDays(p.uistime, p.uietime);
			tvDays.setText(days + "天");
		}
	}

}
