package cn.app.yimirong.activity;

import android.content.Intent;
import android.provider.Settings;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

/**
 * 充值结果
 *
 * @author android
 */
public class PayResultActivity extends BaseActivity {


	private TextView tv1;

	private TextView time1;

	private TextView time2;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_pay_result);
	}

	@Override
	public void initView() {
		setTitleBack(false);
		setTitleText("充值详情");
		setTitleRight(true, new OnRightClickListener() {

			@Override
			public void onClick() {
				exit();
			}
		});
		setRightText("完成");

		tv1 = (TextView) findViewById(R.id.activity_pay_result_tv1);
		time1 = (TextView) findViewById(R.id.activity_pay_result_time);
        time2 = (TextView) findViewById(R.id.activity_pay_result_start);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		double money = intent.getDoubleExtra("money", 0.00f);
		tv1.setText("转入" + money + "元");
		String currentTimeInString = TimeUtils.getCurrentTimeInString(TimeUtils.DEFAULT_DATE_FORMAT);
		time1.setText(currentTimeInString);
        time2.setText(currentTimeInString);
	}

}
