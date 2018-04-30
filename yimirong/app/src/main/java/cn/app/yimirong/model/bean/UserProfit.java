package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class UserProfit implements Serializable {

	private static final long serialVersionUID = -8000090313587258465L;

	// 投资笔数
	public int buyNum;

	// 定期收益
	public double countProfit;

	// 昨日收益
	public double yesterdayProfit;

	// 快活宝余额
	public double longmoney;

	// 活期收益
	public double longmoneyCountprofit;

	// 快乐宝
	public double klmoney;

	// 快乐宝收益
	public double klmoneyCountProfit;

	// 邀请奖励
	public double invite;

	// 好友交易奖励
	public double transaction;

	public double expmoney;

	public double expmoneyCountprofit;

	public double expmoneyYesterdayprofit;

	public double expmoneyCurrentProfit;

	@Override
	public String toString() {
		return "UserProfit{" +
				"buyNum=" + buyNum +
				", countProfit=" + countProfit +
				", yesterdayProfit=" + yesterdayProfit +
				", longmoney=" + longmoney +
				", longmoneyCountprofit=" + longmoneyCountprofit +
				", klmoney=" + klmoney +
				", klmoneyCountProfit=" + klmoneyCountProfit +
				", invite=" + invite +
				", transaction=" + transaction +
				", expmoney=" + expmoney +
				", expmoneyCountprofit=" + expmoneyCountprofit +
				", expmoneyYesterdayprofit=" + expmoneyYesterdayprofit +
				", expmoneyCurrentProfit=" + expmoneyCurrentProfit +
				'}';
	}
}
