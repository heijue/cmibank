package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class UserCD implements Serializable{

	//快活宝转出次数
	public int longmoneyToBalance;

	// 取现次数
	public int withDraw;

	//免手续费取现次数
	public int free_withDraw;

	// 充值次数
	public int pay;

	public int withDrawVersion;

	public String t;

}
