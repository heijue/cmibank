package cn.app.yimirong.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 2016/3/28 0028.
 */
public class HQProfit implements Serializable {
	public String date;
	public String profit;
	public List<ProfitInfo> profitInfos;

    @Override
    public String toString() {
        return "HQProfit{" +
                "date='" + date + '\'' +
                ", profit='" + profit + '\'' +
                ", profitInfos=" + profitInfos +
                '}';
    }
}
