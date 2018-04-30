package cn.app.yimirong.model.bean;

import android.graphics.Color;

import java.io.Serializable;

import cn.app.yimirong.App;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.TimeUtils;

/**
 * 产品基类
 *
 * @author android
 */
public class BaseProduct implements Serializable {

	private static final String TAG = "BaseProduct";

	public static final int STATE_SELLING = 1;

	public static final int STATE_COMPLETE = 2;

	public static final int STATE_SELLOUT = 3;

	public static final int TYPE_NONE = -1;

	/**
	 * 活期产品
	 */
	public static final int TYPE_KH = 1;

	/**
	 * 定期产品
	 */
	public static final int TYPE_DQ = 2;

	/**
	 * 快乐宝
	 */
	public static final int TYPE_KL = 3;

	// 产品类型
	public int ptype;

	public int buyUserNumer;
	public String capital_overview;
	public String canbuyuser;
	public String cancm;
	public String cid;
	public String ctime;
	public String downtime;
	public String income;
	public String money;
	public String money_limit;
	public String money_max;
	public String object_overview;
	public String odate;
	public String online_time;
	public String pid;
	public String pname;
	public String ptid;
	public String sellmoney;
	public String sellouttime;
	public String standard_text;
	public String standard_tag;
	public String operation_tag;
	public String standard_icon;
	public String startmoney;
	public String status;
	public String repayment_status;
	public String stopttime;
	public String uptime;
	public int state;
	private boolean isYuGao = false;

	/**
	 * 判断是否是预告
	 *
	 * @return
	 */
	public boolean isYuGao() {
		if (StringUtils.isBlank(online_time)) {
			return false;
		} else {
			long serverTime = System.currentTimeMillis() / 1000 - App.delta;
			long onlineTime = TimeUtils.getTimeInSecondsFromString(online_time,
					TimeUtils.DEFAULT_DATE_MINUTE);
			Logger.getInstance().i(TAG, "servertime:" + serverTime);
			Logger.getInstance().i(TAG, "onlinetime:" + onlineTime);
			if (onlineTime < serverTime) {
				return false;
			}
		}
		isYuGao = true;
		return true;
	}

	/**
	 * 获取剩余时间
	 *
	 * @return
	 */
	public long getLeftSeconds() {
		if (!isYuGao) {
			return -1;
		} else {
			long serverTime = System.currentTimeMillis() / 1000 - App.delta;
			long onlineTime = TimeUtils.getTimeInSecondsFromString(online_time,
					TimeUtils.DEFAULT_DATE_MINUTE);
			if (onlineTime <= serverTime) {
				return -1;
			} else {
				return onlineTime - serverTime;
			}
		}
	}

	public class ProgState {
		public int progBarColor;

		public int progTextColor;

		public String progText;

		public int progress;

		public ProgState(int groupPosition) {
			if (groupPosition == 0) {
				progBarColor = Color.parseColor("#f14b3b");
				progTextColor = Color.parseColor("#e93928");

				double allMoney = Double.parseDouble(money);
				double sellMoney = Double.parseDouble(sellmoney);
				double progFloat = (sellMoney / allMoney) * 360f;
				double prog = progFloat * 100f / 360f;

				int progText = 0;
				if (prog == 0) {
					progText = 0;
				} else if (prog < 1) {
					progText = 1;
				} else if (prog > 99) {
					progText = 99;
				} else {
					progText = (int) Math.ceil(prog);
				}

				this.progText = progText + "%";

				this.progress = (int) Math.ceil(progFloat);

			} else {
				this.progBarColor = Color.parseColor("#d9d9d9");
				this.progTextColor = Color.parseColor("#9a9a9a");
				if (groupPosition == 2) {
					if ("2".equals(repayment_status)) {
						this.progText = "完成";
					} else {
						this.progText = "还款中";
					}
				} else {
					this.progText = 100 + "%";
				}
				this.progress = 360;
			}
		}
	}
}
