package cn.app.yimirong.model.bean;

import java.io.Serializable;

/**
 * Created by xiaor on 2016/11/14.
 */

public class AcceptedLuck implements Serializable{

	public String id;
	public String money;
	public String uid;
	public String uuid;
	public String uuaccount;
	public String pid;
	public String ctime;
	public String stime;
	public String etime;
	public String noticed;
	public String status;
	public String utime;
	//使用条件：1:指定金额，2：金额倍数
	public String usetype;
	//购买金额
	public String goumaimoney;
	//购买倍数
	public String goumaibeishu;
	//可用于购买队列的名称
	public String pnames;
	//老用户激活说明
	public String olddesc;
	public String newdesc;

}
