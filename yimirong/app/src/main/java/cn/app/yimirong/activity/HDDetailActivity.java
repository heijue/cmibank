package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

/**
 * 回款详情
 *
 * @author android
 */
public class HDDetailActivity extends BaseActivity {

	private TextView tvName;
	private TextView tvMoney;
	private TextView tvPayTime;
	private TextView tvPayNum;
//	protected TextView tvRemark;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_hd_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("活动");

		tvName = (TextView) findViewById(R.id.activity_payback_detail_pname);
		tvMoney = (TextView) findViewById(R.id.activity_payback_detail_money);
		tvPayTime = (TextView) findViewById(R.id.activity_payback_detail_backway);
		tvPayNum = (TextView) findViewById(R.id.activity_payback_detail_paynum);
//		tvRemark = (TextView) findViewById(R.id.activity_payback_detail_remark);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		ActionLog actionlog = (ActionLog) bundle.getSerializable("actionlog");
		updateView(actionlog);
	}

	/**
	 * 更新界面
	 */
	private void updateView(ActionLog actionlog) {
		if (actionlog != null) {
			tvName.setText(actionlog.pname);

			tvMoney.setText(SystemUtils.getDoubleStr(actionlog.money));

			String time = TimeUtils.getTimeFromSeconds(actionlog.ctime,
					TimeUtils.DATE_FORMAT_DAY_MINUTE);

			tvPayTime.setText(time);

			tvPayNum.setText(actionlog.orderid);
		}
	}

}