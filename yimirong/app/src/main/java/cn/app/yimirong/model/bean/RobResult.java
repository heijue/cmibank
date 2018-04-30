package cn.app.yimirong.model.bean;

import java.io.Serializable;

/**
 * Created by android on 2016/1/24 0024.
 */
public class RobResult implements Serializable {

	public String id;

	public int status;
	public double money;
	public String msg;

	public RobResult() {
		this.status = 2;
		this.money = 0d;
		this.msg = "运气不好，再接再厉";
	}
}
