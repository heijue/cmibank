package cn.app.yimirong.activity;

import android.content.Intent;
import android.widget.TextView;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

/**
 * 提现结果
 *
 * @author android
 */
public class CashResultActivity extends BaseActivity {

	private TextView tvTime1;
	private TextView tvTime2;
	private TextView tvTime3;
	private TextView time1,time2;
	private String money = "0.00";

	@Override
	public void loadView() {
		setContentView(R.layout.activity_cash_result);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("结果详情");
		tvTime1 = (TextView) findViewById(R.id.activity_cash_result_bank);
		tvTime2 = (TextView) findViewById(R.id.activity_cash_result_money);
		tvTime3 = (TextView) findViewById(R.id.activity_cash_result_tv2);
		time1 = (TextView) findViewById(R.id.activity_cash_result_time1);
		time2 = (TextView) findViewById(R.id.activity_cash_result_time2);
	}

	@Override
	public void initData() {
		updateView();
	}

	/**
	 * 更新界面
	 */
	protected void updateView() {
		Intent intent = getIntent();
		money = intent.getStringExtra("money");
		tvTime1.setText(App.userinfo.identity.nameCard);
		tvTime2.setText(SystemUtils.getDoubleStr(new Double(money))+ "元");
		String str = TimeUtils.getTime(System.currentTimeMillis(),TimeUtils.DATE_FORMAT_DAY_MINUTE);
		time1.setText(str);
		time2.setText(str);

		int h = TimeUtils.getServerHour();
		String time2 = StringUtils.getDaoZhangTime(h);
		tvTime3.setText(time2);
	}
}
