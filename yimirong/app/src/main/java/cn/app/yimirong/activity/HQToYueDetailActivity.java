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
public class HQToYueDetailActivity extends BaseActivity {

	private TextView tvMoney;
	private TextView zhuanchuTime;
	private TextView daozhangTime;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_hqtoyue_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("转出详情");
		tvMoney = (TextView) findViewById(R.id.activity_hqtoyue_detail_money);
		zhuanchuTime = (TextView) findViewById(R.id.zhunchu_time);
		daozhangTime = (TextView) findViewById(R.id.daozhang_time);
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
			if (actionlog.pname.contains("活期")) {
				if (actionlog.pname.equals("活期")) {
				} else {
				}
				tvMoney.setText("-"+SystemUtils.getDoubleStr(actionlog.money));
				zhuanchuTime.setText(actionlog.getTimeStr1());
				daozhangTime.setText(actionlog.getSuccessTimeStr());
			}
			else {

			}
		}
	}

}
