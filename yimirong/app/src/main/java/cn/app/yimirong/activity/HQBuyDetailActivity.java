package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

public class HQBuyDetailActivity extends BaseActivity {

	private TextView tvName;
	private TextView tvTime;
	private TextView tvMoney;

	private TextView tvTime1;
	private TextView tvDesc1;

	private TextView tvTime2;
	protected TextView tvDesc2;

	private TextView tvTime3;
	protected TextView tvDesc3;

	protected ImageView iv1;
	private ImageView iv2;
	private ImageView iv3;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_hqb_buy_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("购买详情");

		tvName = (TextView) findViewById(R.id.zhuanchudao_bankcard);

		tvTime = (TextView) findViewById(R.id.daozhang_time_info);

		tvMoney = (TextView) findViewById(R.id.activity_khb_buy_detail_money);

		tvTime1 = (TextView) findViewById(R.id.left_time);

		tvDesc1 = (TextView) findViewById(R.id.left_text);

		tvTime2 = (TextView) findViewById(R.id.center_time);

		tvDesc2 = (TextView) findViewById(R.id.center_text);

		tvTime3 = (TextView) findViewById(R.id.right_time);

		tvDesc3 = (TextView) findViewById(R.id.right_text);

		iv1 = (ImageView) findViewById(R.id.left_img);

		iv2 = (ImageView) findViewById(R.id.center_img);

		iv3 = (ImageView) findViewById(R.id.right_img);

	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		ActionLog log = (ActionLog) bundle.getSerializable("log");
		updateView(log);
	}

	/**
	 * 更新View
	 *
	 * @param log
	 */
	private void updateView(ActionLog log) {
		if (log != null) {
			tvName.setText(log.pname+"期");
			tvTime.setText(TimeUtils.getTimeFromSeconds(log.ctime,
					TimeUtils.DEFAULT_DATE_MINUTE));
			tvMoney.setText("+"+SystemUtils.getDoubleStr(log.money));

			String time1 = TimeUtils.getTimeFromSeconds(log.ctime,
					TimeUtils.DATE_FORMAT_MONTH_DAY);
			tvTime1.setText(time1 + "转入");

			tvDesc1.setText("账户余额");

			String week2 = TimeUtils.getDayOfWeek(log.ctime, 0);
			tvTime2.setText(time1 + week2);

			String time3 = TimeUtils.addDay(1, log.ctime,
					TimeUtils.DATE_FORMAT_MONTH_DAY);
			String week3 = TimeUtils.getDayOfWeek(log.ctime, 1);
			tvTime3.setText(time3 + week3);

			String today = TimeUtils
					.getCurrentTimeInString(TimeUtils.DATE_FORMAT_MONTH_DAY);

			MyDay todayMyDay = new MyDay(today);
			MyDay myDay2 = new MyDay(time1);
			MyDay myDay3 = new MyDay(time3);

			if (todayMyDay.month > myDay2.month) {
				setStep2();
			} else if (todayMyDay.month == myDay2.month) {
				if (todayMyDay.day >= myDay2.day) {
					setStep2();
				}
			}

			if (todayMyDay.month > myDay3.month) {
				setStep3();
			} else if (todayMyDay.month == myDay3.month) {
				if (todayMyDay.day >= myDay3.day) {
					setStep3();
				}
			}

		}
	}

	protected void setStep2() {
		iv2.setImageResource(R.drawable.zhuanru);
	}

	protected void setStep3() {
		iv3.setImageResource(R.drawable.daozhangchenggong);
	}

	private class MyDay {
		private int month;
		private int day;

		public MyDay(String time) {
			String[] split = time.split("-");
			if (split.length == 2) {
				month = Integer.parseInt(split[0]);
				day = Integer.parseInt(split[1]);
			}
		}
	}
}
