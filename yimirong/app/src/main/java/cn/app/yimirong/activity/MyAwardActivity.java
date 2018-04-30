package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.utils.SystemUtils;

public class MyAwardActivity extends BaseActivity implements OnClickListener {

	private TextView tvSum;

	private RelativeLayout rlYaoqingWrapper;

	private TextView tvYaoqingSum;

	private RelativeLayout rlYongjinWrapper;

	private TextView tvYongjinSum;

	private UserProfit mUserProfit;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_myaward);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("邀请奖励");
		setTitleRight(true, new OnRightClickListener() {
			@Override
			public void onClick() {
				toInviteFriends();
			}
		});
		setRightText("邀请好友");

		tvSum = (TextView) findViewById(R.id.activity_myaward_sum);

		rlYaoqingWrapper = (RelativeLayout) findViewById(R.id.activity_myaward_yaoqing_wrapper);
		rlYaoqingWrapper.setOnClickListener(this);
		tvYaoqingSum = (TextView) findViewById(R.id.activity_myaward_yaoqing_sum);

		rlYongjinWrapper = (RelativeLayout) findViewById(R.id.activity_myaward_yongjin_wrapper);
		rlYongjinWrapper.setOnClickListener(this);
		tvYongjinSum = (TextView) findViewById(R.id.activity_myaward_yongjin_sum);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mUserProfit = (UserProfit) bundle.getSerializable("UserProfit");
		updateView(mUserProfit);
	}

	private void updateView(UserProfit mUserProfit) {
		if (mUserProfit != null) {
			double all = mUserProfit.invite + mUserProfit.transaction;
			tvSum.setText(SystemUtils.getDoubleStr(all));
			tvYaoqingSum
					.setText(SystemUtils.getDoubleStr(mUserProfit.invite));
			tvYongjinSum.setText(SystemUtils
					.getDoubleStr(mUserProfit.transaction));
		}
	}

	protected void toInviteFriends() {
		Intent intent = new Intent(context, InviteActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_myaward_yaoqing_wrapper:
				toYaoqingAward();
				break;

			case R.id.activity_myaward_yongjin_wrapper:
				toYongjinAward();
				break;

			default:
				break;
		}
	}

	/**
	 * 佣金奖励
	 */
	private void toYongjinAward() {
		if (mUserProfit != null) {
			Intent intent = new Intent(context, YJAwardActivity.class);
			intent.putExtra("transaction", mUserProfit.transaction);
			startActivity(intent);
		}
	}

	/**
	 * 邀请奖励
	 */
	private void toYaoqingAward() {
		if (mUserProfit != null) {
			Intent intent = new Intent(context, YQAwardActivity.class);
			intent.putExtra("invite", mUserProfit.invite);
			startActivity(intent);
		}
	}

}
