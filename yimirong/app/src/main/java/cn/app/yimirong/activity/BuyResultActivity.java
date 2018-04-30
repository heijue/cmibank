package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.BuySuccessEvent;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.model.bean.BuyResult;
import cn.app.yimirong.utils.SystemUtils;

public class BuyResultActivity extends BaseActivity {

	private TextView tvName,title;
	private TextView tvMoney;
	private TextView tvPayType;
	private TextView tvPayNum;
	private Dialog dialog;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_buy_result);
	}

	@Override
	protected void onDestroy() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void initView() {
		setTitleText("购买结果");
		setTitleBack(true);

		title = (TextView) findViewById(R.id.activity_buy_result_success);
		tvName = (TextView) findViewById(R.id.activity_buy_result_pname);
		tvMoney = (TextView) findViewById(R.id.activity_buy_result_money);
		tvPayType = (TextView) findViewById(R.id.activity_buy_result_paytype);
		tvPayNum = (TextView) findViewById(R.id.activity_buy_result_paynum);

	}

	@Override
	public void initData() {
		Intent intent = getIntent();

		Bundle bundle = intent.getExtras();

		final BuyResult result = (BuyResult) bundle.getSerializable("result");

		// 产品名称
		String pname = intent.getStringExtra("pname");
		// 购买金额
		String money = intent.getStringExtra("money");
		// 付款方式
		String paytype = intent.getStringExtra("paytype");
		// 交易号
		String trxid = intent.getStringExtra("trxid");

		tvName.setText(pname);
		tvMoney.setText(SystemUtils.getDoubleStr(money)+"元");
		tvPayType.setText(paytype);
		tvPayNum.setText(trxid);

		// 通知产品详情页和产品列表页更新数据
		EventBus.getDefault().post(new BuySuccessEvent());
		EventBus.getDefault().post(new UserInfoEvent());

	}


}
