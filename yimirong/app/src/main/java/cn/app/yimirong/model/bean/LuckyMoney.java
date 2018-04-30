package cn.app.yimirong.model.bean;

import java.io.Serializable;

import cn.app.yimirong.utils.TimeUtils;

/**
 * Created by android on 2016/1/22 0022.
 */
public class LuckyMoney implements Serializable {

	public int status;

	public String id;

	public String name;

	public String target;

	public String startTime;

	public String yugaoTime;

	public String text;

	public LuckyMoney() {
		this.status = 0;
		this.startTime = "0";
	}

	public long getCountSeconds() {
		long startTime = Long.parseLong(this.startTime);
		long currentTime = TimeUtils.getServerTime();
		if (currentTime < startTime) {
			return startTime - currentTime;
		} else {
			return 0;
		}
	}
}
