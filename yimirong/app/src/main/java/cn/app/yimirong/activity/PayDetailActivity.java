package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.utils.TimeUtils;

/**
 * 提现详情
 *
 * @author android
 */
public class PayDetailActivity extends BaseActivity {

	private ImageView ivImg3;

	protected TextView tvResult2;
	private TextView tvTransferMoney;
	private TextView tvTransferTime;
	private TextView tvAccountState;
	private TextView tvAccountTime;
	private ImageView image2;
	private ImageView image1;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_pay_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("充值详情");
		setTitleRight(false, null);

		tvResult2 = (TextView) findViewById(R.id.wancheng_status);
		ivImg3 = (ImageView) findViewById(R.id.wanchen_img);
		tvTransferMoney = (TextView) findViewById(R.id.tv_transfer_money);
		tvTransferTime = (TextView) findViewById(R.id.tv_transfer_time);
		tvAccountState = (TextView) findViewById(R.id.tv_account_state);
		tvAccountTime = (TextView) findViewById(R.id.tv_account_time);
		image2 = (ImageView) findViewById(R.id.image2);
		image1 = (ImageView) findViewById(R.id.image1);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		ActionLog actionLog = (ActionLog) bundle.getSerializable("actionlog");
		updateView(actionLog);
	}

	/**
	 * 更新界面
	 */
	private void updateView(ActionLog log) {
		if (log != null) {
			if (App.userinfo.identity != null) {
				DecimalFormat df = new DecimalFormat("#.00");
				String formatMoney = df.format(new Double(log.money));
				tvTransferMoney.setText("转入"+formatMoney+"元");
				tvTransferTime.setText(TimeUtils.getTimeFromSeconds(log.paytime,
						TimeUtils.DATE_FORMAT_DAY_MINUTE));
				tvAccountTime.setText(TimeUtils.getTimeFromSeconds(log.ctime,
						TimeUtils.DATE_FORMAT_DAY_MINUTE));
			}
			if (log.action == 0) {
				tvResult2.setText("充值成功");
				tvAccountState.setText("充值成功");
				ivImg3.setImageResource(R.drawable.zhifuchenggong);
				image2.setImageResource(R.drawable.daozhangchenggong);
			} else if (log.action == 10) {
				tvResult2.setText("充值失败");
				ivImg3.setImageResource(R.drawable.zhifushibai);
				image2.setImageResource(R.drawable.shibai);
			}
		}
	}
}
