package cn.app.yimirong.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by android on 2016/1/24 0024.
 */
public class RobList implements Serializable {

	public int overTime;

	public List<RobItem> ranks;

	public static class RobItem implements Serializable {
		public String account;
		public String name;
		public String score;
	}

}
