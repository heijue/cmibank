package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.BuySuccessEvent;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.model.bean.BuyResult;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

/**
 * 购买详情
 *
 * @author android
 */
public class BuyDetailActivity extends BaseActivity {

	private TextView tvName;
	private TextView tvMoney;
	private TextView tvPayTime;
	private TextView tvPayNum;
	protected TextView tvRemark;
	private int color;
	private String pname, money, paytype, trxid;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_buy_detail);
	}


	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("购买结果");

		tvName = (TextView) findViewById(R.id.activity_buy_detail_pname);
		tvMoney = (TextView) findViewById(R.id.activity_buy_detail_money);
		tvPayTime = (TextView) findViewById(R.id.activity_buy_detail_paytime);
		tvPayNum = (TextView) findViewById(R.id.activity_buy_detail_paynum);
//		tvRemark = (TextView) findViewById(R.id.activity_buy_detail_remark);

	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		color = intent.getIntExtra("color", -1);
		final BuyResult result = (BuyResult) bundle.getSerializable("result");
		// 产品名称
		pname = intent.getStringExtra("pname");
		// 购买金额
		money = intent.getStringExtra("money");
		// 付款方式
		paytype = intent.getStringExtra("paytype");
		// 交易号
		trxid = intent.getStringExtra("trxid");
		if (result != null) {
			updateView(result);
		}
		ActionLog actionlog = (ActionLog) bundle.getSerializable("actionlog");

		if (actionlog != null) {
			updateView(actionlog);
		}

		// 通知产品详情页和产品列表页更新数据
		EventBus.getDefault().post(new BuySuccessEvent());
		EventBus.getDefault().post(new UserInfoEvent());


	}

	/**
	 * 更新界面
	 */
	private void updateView(ActionLog actionlog) {
		if (actionlog != null) {
			tvName.setText(actionlog.pname);

			if (color != -1) {
				tvMoney.setTextColor(color);
			}
			tvMoney.setText(SystemUtils.getDoubleStr(actionlog.money)+"元");

			tvPayNum.setText(actionlog.orderid);

			if ("1".equals(actionlog.desc)) {
				tvPayTime.setText("账户余额支付");
			} else if ("2".equals(actionlog.desc)) {
				tvPayTime.setText("银行卡支付");
			}
		}
	}

	/**

	/**
	 * 更新界面
	 */
	private void updateView(BuyResult result) {
		if (result != null) {
			tvName.setText(pname);

			if (color != -1) {
				tvMoney.setTextColor(color);
			}
			tvMoney.setText(SystemUtils.getDoubleStr(money));

			tvPayNum.setText(trxid);


			tvPayTime.setText(paytype);

		}
	}
}
