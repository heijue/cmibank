package cn.app.yimirong.model.bean;

import java.io.Serializable;

import cn.app.yimirong.utils.TimeUtils;

public class ActionLog implements Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -4462594240178169093L;

	public String id;

	public int action;

	public String uid;

	public String pid;

	public String pname;

	public long paytime;

	public String orderid;

	public String desc;

	public long ctime;

	public String money;

	public String balance;

	@Override
	public String toString() {
		return "ActionLog{" +
				"id='" + id + '\'' +
				", action=" + action +
				", uid='" + uid + '\'' +
				", pid='" + pid + '\'' +
				", pname='" + pname + '\'' +
				", paytime=" + paytime +
				", orderid='" + orderid + '\'' +
				", desc='" + desc + '\'' +
				", ctime=" + ctime +
				", money='" + money + '\'' +
				", balance='" + balance + '\'' +
				'}';
	}

	/**
	 * 获取时间
	 *
	 * @return
	 */
	public String getTimeStr() {
		return TimeUtils.getTimeFromSeconds(ctime,
				TimeUtils.DATE_FORMAT_CHINA_MONTH);
	}
	public String getTimeStr1() {
		return TimeUtils.getTimeFromSeconds(ctime,
				TimeUtils.DATE_FORMAT_DAY_MINUTE);
	}
	public String getSuccessTimeStr() {

		return TimeUtils.getTimeFromSeconds(ctime-paytime,TimeUtils.DATE_FORMAT_DAY_MINUTE);
	}

	public String getActionStr() {
		String str;
		switch (action) {
			case 0:
				str = "充值";
				break;

			case 1:
				str = "购买 - " + pname;
				break;

			case 2:
				str = "提现";
				break;

			case 4:
				str = "产品还款";
				break;

			case 5:
			case 6:
				str = "活动赠送 - " + pname;
				break;

			case 7:
				str = "体验金收益发放";
				break;

			case 10:
				str = "充值失败";
				break;

			case 11:
				str ="可用余额转入易米宝";
				break;

			case 13:
                if ("2".equals(desc)) {
                    str = "易米宝转出到可用余额";
                } else if ("1".equals(desc)) {
                    str = "易米宝转出到可用余额";
                } else {
				str = "易米宝转出";
                }
				break;

			case 14:
				str = "易米宝还款";
				break;

			case 20:
				str = "提现失败";
				break;

			case 21:
				str = "提现退回";
				break;

			case 31:
				str = "购买 - " + pname;
				break;

			case 33:
				str = pname;
				break;

			default:
				str = pname;
				break;
		}
		return str;
	}

	public int getActionType() {
		int type = 2;
		switch (action) {
			case 0:
			case 4:
			case 5:
			case 6:
			case 7:
			case 13:
			case 21:
			case 33:
				type = 0;
				break;

			case 1:
			case 2:
			case 11:
			case 14:
			case 31:
				type = 1;
				break;

			case 10:
			case 20:
				type = 2;
				break;

			default:
				type = 2;
				break;
		}
		return type;
	}
}
