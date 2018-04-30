package cn.app.yimirong.model.bean;

import java.io.Serializable;
import java.util.List;

public class UserProduct implements Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 5157321584827625468L;

	public String pname;

	public double money;

	public double profit;

	public long buytime;

	public List<SingleProduct> product_list;

	public class SingleProduct implements Serializable {

		/**
		 * ID
		 */
		private static final long serialVersionUID = -6957782536625836511L;

		public String bankid;

		public String f;

		public String paytype;

		public String buytime;

		public int days;

		public String from;

		public String id;
		public String income;

		public double money;

		public String pid;

		public String pname;

		public double profit;

		public String status;

		public String uid;

		public String uietime;

	}
}
