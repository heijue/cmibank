package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;



import org.json.JSONException;
import org.json.JSONObject;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.TimeUtils;

/**
 * com.kdlc.activity.PushMsgActivity
 */
public class PushMsgActivity extends BaseActivity {

	@Override
	public void loadView() {
		setContentView(R.layout.activity_push_msg);
		isStatusBarTint = false;
	}

	@Override
	public void initView() {
		setTitleBar(false);
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
/*		XGPushClickedResult click = XGPushManager.onActivityStarted(activity);
		if (click != null) {
			logger.d(TAG, "通知被点击: " + click.getContent());
			String customContent = click.getCustomContent();
			if (!StringUtils.isBlank(customContent)) {
				logger.d(TAG, "Custom Content: " + customContent);
				try {
					JSONObject jsonObj = new JSONObject(customContent);
					String type = jsonObj.optString("type", null);
					String title = jsonObj.optString("title", "易米融");
					String time = jsonObj.optString("time", null);
					String content = jsonObj.optString("content", null);

					if (type == null) {
						finish();
					} else {
						if ("message".equals(type)) {
							//推送消息
							Message msg = new Message();
							msg.title = title;
							msg.content = content;
							msg.onlinetime = TimeUtils.getTimeInSecondsFromString(time, TimeUtils.DEFAULT_DATE_FORMAT) + "";
							Bundle bundle = new Bundle();
							bundle.putSerializable("message", msg);
							bundle.putBoolean("isPush", true);
							Intent intent = new Intent(context, MsgDetailActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						} else if ("banner".equals(type)) {
							//推送Banner
							Intent intent = new Intent(context, WebViewActivity.class);
							intent.putExtra("title", title);
							intent.putExtra("url", content);
							intent.putExtra("isPush", true);
							startActivity(intent);
						} else if ("notice".equals(type)) {
							//推送公告
							Intent intent = new Intent(context, MessageActivity.class);
							intent.putExtra("isPush", true);
							startActivity(intent);
						} else {
						}
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					finish();
				}
			}
		} else {
			finish();
		}*/
	}
}
