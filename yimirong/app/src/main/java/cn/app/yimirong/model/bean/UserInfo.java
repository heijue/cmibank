package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -5297151529230768425L;

	public String balance;

	public Identity identity;

	public String top_uid;

	public String top_pwd;

	public long serverTime;

	public double canBuyLong = 0d;

	public int qiandao;

	public class Identity implements Serializable {

		private static final long serialVersionUID = 4638627547205194334L;

		// 银行标志
		public String bankid;

		// 银行名称
		public String bankname;

		// 银行卡号前4位
		public String cardnoTop;

		// 银行卡后4位
		public String cardno;

		// 身份证号
		public String idCard;

		// 真实姓名
		public String realName;

		// 交易密码是否设置
		public boolean tpwd;

		// 银行名称加后四位尾号
		public String nameCard;

		//判断新老用户  1 新   0老
		public String isnew;

		@Override
		public String toString() {
			return "Identity{" +
					"bankid='" + bankid + '\'' +
					", bankname='" + bankname + '\'' +
					", cardnoTop='" + cardnoTop + '\'' +
					", cardno='" + cardno + '\'' +
					", idCard='" + idCard + '\'' +
					", realName='" + realName + '\'' +
					", tpwd=" + tpwd +
					", nameCard='" + nameCard + '\'' +
					", isnew='" + isnew + '\'' +
					'}';
		}
	}

}
