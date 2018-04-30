package cn.app.yimirong.model.bean;

/**
 * Created by xiaor on 2016/5/17.
 */
public class FuYouInfo {

	public String card_no;
	public String user_id;
	public String bank_name;
	//    public String merchant_key;
	public String merchant_id;
	public String acct_name;
	public String dt_order;
	public String id_no;
	public String money;
	public String order;
	public String notify_url;
	public String valid_order;
	//    public String orgString;
	public String sign;

    @Override
    public String toString() {
        return "FuYouInfo{" +
                "card_no='" + card_no + '\'' +
                ", user_id='" + user_id + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", merchant_id='" + merchant_id + '\'' +
                ", acct_name='" + acct_name + '\'' +
                ", dt_order='" + dt_order + '\'' +
                ", id_no='" + id_no + '\'' +
                ", money='" + money + '\'' +
                ", order='" + order + '\'' +
                ", notify_url='" + notify_url + '\'' +
                ", valid_order='" + valid_order + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
