package cn.app.yimirong.model.bean;

import java.io.Serializable;
import java.util.List;

public class Questions implements Serializable {

	private static final long serialVersionUID = -3194576743384574213L;
	public String nid;
	public String title;
	public String icon;
	public List<Answer> data;


	public class Answer implements Serializable {
		private static final long serialVersionUID = 4334492242288487689L;
		public String nid;
		public String title;
		public String content;
	}
}
