package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.UserProfit;

public class MyRewardActivity extends BaseActivity implements View.OnClickListener {

	private TextView Allreward, ivMonry, payMoney;

	private LinearLayout ivFriend, friendPay;

	private UserProfit mUserProfit;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_my_reward);
	}

	@Override
	public void initView() {
		setTitleText("我的奖励");
		setTitleBack(true);

		Allreward = (TextView) findViewById(R.id.layout_profit_list_sum);
		ivMonry = (TextView) findViewById(R.id.yaoqing_jiangli_money);
		payMoney = (TextView) findViewById(R.id.jiaoyi_jiangli_money);

		ivFriend = (LinearLayout) findViewById(R.id.yaoqing_jiangli);
		ivFriend.setOnClickListener(this);
		friendPay = (LinearLayout) findViewById(R.id.jiaoyi_jiangli);
		friendPay.setOnClickListener(this);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			mUserProfit = (UserProfit) data.getSerializable("userprofit");
		}
		DecimalFormat df = new DecimalFormat("######0.00");
		if (mUserProfit != null) {
			Allreward.setText(df.format(mUserProfit.invite + mUserProfit.transaction) + "");
			ivMonry.setText(df.format(mUserProfit.invite) + "");
			payMoney.setText(df.format(mUserProfit.transaction) + "");
		} else {
			Allreward.setText("0.00");
			ivMonry.setText("0.00");
			payMoney.setText("0.00");
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.yaoqing_jiangli:
				toReward();
				break;
			case R.id.jiaoyi_jiangli:
				toBuyReard();
				break;
		}

	}

	private void toReward() {
		Intent intent = new Intent(context, InviteRewardActivity.class);
		if(mUserProfit != null) {
			intent.putExtra("money", mUserProfit.invite);
		}
		startActivity(intent);
	}

	private void toBuyReard() {
		Intent intent = new Intent(context, YJAwardActivity.class);
		if(mUserProfit != null) {
			intent.putExtra("money", mUserProfit.transaction);
		}
		startActivity(intent);
	}

}
