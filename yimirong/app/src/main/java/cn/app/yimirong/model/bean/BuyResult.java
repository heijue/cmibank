package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class BuyResult implements Serializable {

	private static final long serialVersionUID = -2148307555232911236L;

	public boolean activity;

	public int couponcounts;

	public String uid;

	public String ctime;

	public String log_desc;

	public String money;

	public String action;

	public boolean add_exp;

	public String exp_add;

	public String exp_balance;

	// 账户余额
	public String balance;

	// 交易流水号
	public String trxid;

	// 支付金额
	public String cost;

}
