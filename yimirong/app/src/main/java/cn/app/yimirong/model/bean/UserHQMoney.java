package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class UserHQMoney implements Serializable {
	/**
	 * ID
	 */
	private static final long serialVersionUID = 8627661885724538662L;

	// 累计收益
	public String count_profit;
	// 昨日收益
	public String yesterday;
	// 总资产
	public String longmoney;
	// 一周收益
	public String day_7;
	// 一月收益
	public String day_30;
	// 万份收益
	public String wan;
}
