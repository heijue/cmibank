package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.utils.TimeUtils;

public class MsgDetailActivity extends BaseActivity {

	private TextView tvTitle;
	private TextView tvTime;
	private TextView tvContent;

	private boolean isPush;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_msg_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		titleBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPush) {
					toMain();
				} else {
					finish();
				}
			}
		});

		tvTitle = (TextView) findViewById(R.id.activity_msg_detail_title);
		tvTime = (TextView) findViewById(R.id.activity_msg_detail_time);
		tvContent = (TextView) findViewById(R.id.activity_msg_detail_content);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		Message msg = (Message) bundle.getSerializable("message");
		isPush = bundle.getBoolean("isPush", false);
		if (isPush) {
			logger.d(TAG, "is push message");
			setTitleText("推送消息");
		} else {
			logger.d(TAG, "is not push message");
			setTitleText("公告");
		}
		updateView(msg);
	}

	private void updateView(Message msg) {
		if (msg != null) {
			tvTitle.setText(msg.title);
			tvTime.setText(TimeUtils.getTimeFromSeconds(msg.onlinetime,
					TimeUtils.DATE_FORMAT_DATE));
			tvContent.setText(msg.content);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPush) {
				toMain();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void toMain() {
		Intent intent = new Intent(context, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.alpha_in_200, R.anim.alpha_out_200);
		finish();
	}
}
