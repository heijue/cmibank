package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.utils.SystemUtils;

/**
 * 快活宝转出详情
 *
 * @author android
 */
public class HQZCDetailActivity extends BaseActivity {

	private TextView tvTitle;

	private TextView tvTime;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_hqb_zc_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("结果详情");

		tvTitle = (TextView) findViewById(R.id.wancheng_num);

		tvTime = (TextView) findViewById(R.id.look_result);

	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		final String money = intent.getStringExtra("money");
		tvTitle.setText("成功转出"+SystemUtils.getDoubleStr(new Double(money))+"元至账户余额");
		tvTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HQToYueDetailActivity.class);
				ActionLog log = new ActionLog();
				log.pname = "易米宝";
				long serverDate = System.currentTimeMillis();
				log.paytime = serverDate/1000;
				log.ctime = serverDate/1000;
				log.money = money;
				Bundle bundle = new Bundle();
				bundle.putSerializable("actionlog", log);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}


}
