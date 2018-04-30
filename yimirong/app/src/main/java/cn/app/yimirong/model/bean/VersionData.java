package cn.app.yimirong.model.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2016/1/22 0022.
 */
public class VersionData implements Serializable {
	public String version;

	public String paytype;
	public String pay_qudao;
	public String withdraw_txt;
	public int withdraw_sxf;
	public List<String> pay_list;
	public boolean withdraw_tips;
	public double yee_amount_limit;

	public VersionData() {
		this.withdraw_sxf = 2;
		this.withdraw_tips = false;
		this.withdraw_txt = "day";
		this.pay_list = new ArrayList<String>();
		this.pay_list.add("llpay");
		this.pay_list.add("baofoo");
	}

}
