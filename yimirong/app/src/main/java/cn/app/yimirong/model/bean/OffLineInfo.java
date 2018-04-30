package cn.app.yimirong.model.bean;

import java.io.Serializable;

/**
 * Created by xiaor on 2016/6/7.
 */
public class OffLineInfo implements Serializable {

	public String yesterday_profit;
	public String kymoney;
	public String buynum;
	public String longmoney;
	public String dqmoney;
	public String allmoney;
	public String allprofit;
	public String tyjin;
	public String invite;

	@Override
	public String toString() {
		return "OffLineInfo{" +
				"yesterday_profit='" + yesterday_profit + '\'' +
				", kymoney='" + kymoney + '\'' +
				", buynum='" + buynum + '\'' +
				", longmoney='" + longmoney + '\'' +
				", dqmoney='" + dqmoney + '\'' +
				", allmoney='" + allmoney + '\'' +
				", allprofit='" + allprofit + '\'' +
				", tyjin='" + tyjin + '\'' +
				", invite='" + invite + '\'' +
				'}';
	}
}
