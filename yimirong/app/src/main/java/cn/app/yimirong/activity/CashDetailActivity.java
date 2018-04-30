package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.IqueryWithDrawArriveCallBack;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

/**
 * 提现详情
 *
 * @author android
 */
public class CashDetailActivity extends BaseActivity {

	private TextView tvResult1;
	private TextView tvTime1;

	private TextView tvTime2;
	private TextView bankName;

	private TextView tvResult3;
	private TextView tvTime3;
	private TextView zhuanchu_info;
	private int chuli = 2;
	private int type = 0;


	private ImageView iv3;
	private ImageView chuliState;
	private String orderid;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_cash_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("提现详情");
		setTitleRight(false, null);

		tvResult1 = (TextView) findViewById(R.id.zhuanchu_info_jine);
		tvTime1 = (TextView) findViewById(R.id.left_time);
		zhuanchu_info = (TextView) findViewById(R.id.zhuanchu_info);
		tvTime2 = (TextView) findViewById(R.id.center_time);

		tvResult3 = (TextView) findViewById(R.id.right_text);
		tvTime3 = (TextView) findViewById(R.id.right_time);
		bankName = (TextView) findViewById(R.id.zhuanchudao_bankcard);
		chuliState = (ImageView)findViewById(R.id.chuli_state);

		iv3 = (ImageView) findViewById(R.id.right_img);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		ActionLog actionLog = (ActionLog) bundle.getSerializable("actionlog");
		orderid = actionLog.orderid;
		type=intent.getIntExtra("type",0);
		if (type != 0){
			setTitleText(" ");
		}
		chuli=intent.getIntExtra("chuli",2);
		updateView(actionLog);
		setBankInfo();
	}

	/**
	 * 设置银行卡信息
	 */
	protected void setBankInfo() {
		UserInfo userinfo = null;
		userinfo = App.userinfo;
		if (userinfo != null && userinfo.identity != null) {
			bankName.setText(userinfo.identity.nameCard);
			String url = API.BANK_ICON_BASE + userinfo.identity.bankid + ".jpg";
			BitmapUtils bitmapUtils = new BitmapUtils(context);
			bitmapUtils.configDefaultLoadFailedImage(R.drawable.yinhangbeijing);
		}
	}

	/**
	 * 更新界面
	 *
	 * @param log
	 */
	private void updateView(final ActionLog log) {
		bankName.setText(App.userinfo.identity.bankname);
		if (amodel!=null) {
			if (!orderid.isEmpty()) {
				String startdt = orderid.substring(2, 10);
				String enddt = TimeUtils.getCurrentTimeInString(TimeUtils.DATE_FORMAT_DATE).replace("-","");
				showLoading("加载中...");
				amodel.fuyou(orderid,startdt,enddt,new IqueryWithDrawArriveCallBack() {
					@Override
					public void requestCode(String code,String reason,String data) {
						closeLoading();
						tvResult1.setText(SystemUtils.getDoubleStr(Double.valueOf(log.money))+"元");
						String time1 = TimeUtils.getTimeFromSeconds(log.ctime,
								TimeUtils.DATE_FORMAT_DAY_MINUTE);
						tvTime1.setText(time1);

						String time2 = TimeUtils.getTimeFromSeconds(log.ctime,
								TimeUtils.DATE_FORMAT_DAY_MINUTE);
						tvTime2.setText(time2);

						String time3 = TimeUtils.getTimeFromSeconds(log.ctime, TimeUtils.DATE_FORMAT_DAY_MINUTE);
						tvTime3.setText(time3);

						if (code.equals("4444")) {
							zhuanchu_info.setText("交易失败");
							iv3.setImageResource(R.drawable.tixianshibai);
							chuliState.setImageResource(R.drawable.jiaoyihshibai);
							tvResult3.setText("提现失败");
							tvResult3.setTextColor(Color.parseColor("#333333"));
							tvTime3.setText("");
						} else if (code.equals("0000")) {
							zhuanchu_info.setText("交易成功");
							iv3.setImageResource(R.drawable.daozhangchenggong);
							chuliState.setImageResource(R.drawable.jiaoyichenggong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
							tvResult3.setText("到账成功");
							tvTime3.setText("");
						} else if (code.equals("1111")) {
							int h = TimeUtils.getServerHour(log.paytime);
							String time = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time);
							zhuanchu_info.setText("处理中");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
						} else if (code.equals("-1")) {
							int h = TimeUtils.getServerHour(log.paytime);
							String time = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time);
							zhuanchu_info.setText("处理中");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
						} else if(code.equals("1001")){
							int h = TimeUtils.getServerHour(log.paytime);
							String time = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time);
							zhuanchu_info.setText("稍后再试");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
							tvTime3.setText(data);
//							tvResult3.setText("稍后再试");
						} else if(code.equals("1004")){
							int h = TimeUtils.getServerHour(log.paytime);
							String time = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time);
							zhuanchu_info.setText("处理中");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
						} else if(code.equals("1005")){
							int h = TimeUtils.getServerHour(log.paytime);
							String time = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time);
							zhuanchu_info.setText("处理中");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
						}
						else if(code.equals("3333")){
							if (reason != null && !reason.isEmpty() && reason.equals("交易已废弃")) {
								zhuanchu_info.setText("交易失败");
								iv3.setImageResource(R.drawable.tixianshibai);
								chuliState.setImageResource(R.drawable.jiaoyihshibai);
								tvResult3.setText("提现失败");
								tvResult3.setTextColor(Color.parseColor("#333333"));
								tvTime3.setText("");
								return;
							}
							int h = TimeUtils.getServerHour(log.paytime);
							String time = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time);
							zhuanchu_info.setText("处理中");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
						}
					}
				});
			}
		}
/*		if (log != null) {
			tvResult1.setText(Double.valueOf(log.money).toString()+"元");
			String time1 = TimeUtils.getTimeFromSeconds(log.ctime,
					TimeUtils.DATE_FORMAT_DAY_MINUTE);
			tvTime1.setText(time1);

			String time2 = TimeUtils.getTimeFromSeconds(log.ctime,
					TimeUtils.DATE_FORMAT_DAY_MINUTE);
			tvTime2.setText(time2);
			long nowTime = TimeUtils.getServerTime();
			if (nowTime < Constant.AUGST) {
				String time3 = TimeUtils.getTimeFromSeconds(log.ctime, TimeUtils.DATE_FORMAT_DAY_MINUTE);
				tvTime3.setText(time3);
				if (log.action == 20 || log.action == 21) {
					zhuanchu_info.setText("交易失败");
					iv3.setImageResource(R.drawable.tixianshibai);
					chuliState.setImageResource(R.drawable.jiaoyihshibai);
					tvResult3.setText("提现失败");
					tvResult3.setTextColor(Color.parseColor("#333333"));
				} else {
					zhuanchu_info.setText("交易成功");
					iv3.setImageResource(R.drawable.daozhangchenggong);
					chuliState.setImageResource(R.drawable.jiaoyichenggong);
					tvResult3.setTextColor(Color.parseColor("#333333"));
					tvResult3.setText("到账成功");
				}
			} else {
				if (log.paytime == 0) {
					if (log.action == 20 || log.action == 21) {
						tvTime3.setText(log.pname);
						zhuanchu_info.setText("交易失败");
						iv3.setImageResource(R.drawable.tixianshibai);
						chuliState.setImageResource(R.drawable.jiaoyihshibai);
						tvResult3.setText("提现失败");
						tvResult3.setTextColor(Color.parseColor("#333333"));
					}else {
						int h = TimeUtils.getServerHour();
						String time3 = StringUtils.getDaoZhangTime(h);
						tvTime3.setText(time3);
						zhuanchu_info.setText("处理中");
						iv3.setImageResource(R.drawable.daozhangshijian);
						chuliState.setImageResource(R.drawable.chulizhong);
						tvResult3.setTextColor(Color.parseColor("#333333"));
					}
				}else {
					if (log.action == 20 || log.action == 21) {
						tvTime3.setText(log.pname);
						zhuanchu_info.setText("交易失败");
						iv3.setImageResource(R.drawable.tixianshibai);
						tvResult3.setText("提现失败");
						tvResult3.setTextColor(Color.parseColor("#333333"));
						chuliState.setImageResource(R.drawable.jiaoyihshibai);
					} else {
						if (nowTime - log.paytime > 7200) {
							String time3 = TimeUtils.getTimeFromSeconds(log.paytime + 7200, TimeUtils.DATE_FORMAT_DAY_MINUTE);
							tvTime3.setText(time3);
							zhuanchu_info.setText("交易成功");
							iv3.setImageResource(R.drawable.daozhangchenggong);
							chuliState.setImageResource(R.drawable.jiaoyichenggong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
							tvResult3.setText("到账成功");
						} else {
							int h = TimeUtils.getServerHour(log.paytime);
							String time3 = StringUtils.getDaoZhangTime(h);
							tvTime3.setText(time3);
							zhuanchu_info.setText("处理中");
							iv3.setImageResource(R.drawable.daozhangshijian);
							chuliState.setImageResource(R.drawable.chulizhong);
							tvResult3.setTextColor(Color.parseColor("#333333"));
						}
					}
				}
			}

		}*/
	}

}
