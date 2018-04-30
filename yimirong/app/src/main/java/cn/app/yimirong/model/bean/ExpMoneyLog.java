package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class ExpMoneyLog implements Serializable {

	private static final long serialVersionUID = -137308323156645436L;

	public String uid;

	public String date;

	public String ctime;

	public String log_desc;

	public String money;

	public String balance;

	public String action;

	public String id;

	public String getSymbol() {
		int action = 2;
		try {
			action = Integer.parseInt(this.action);
		} catch (Exception e) {
			action = 2;
		}
		String symbol = "";
		switch (action) {
			case 0:
				symbol = "-";
				break;
			case 2:
				symbol = "";
				break;
			case 1:
				symbol = "+";
				break;
			default:
				break;
		}
		return symbol;
	}

	public String getColor() {
		int action = 2;
		try {
			action = Integer.parseInt(this.action);
		} catch (Exception e) {
			action = 2;
		}
		String color = "#000000";
		switch (action) {
			case 0:
				color = "#ff6600";
				break;

			case 1:
				color = "#449900";
				break;

			case 2:
				color = "#666666";
				break;

			default:
				break;
		}
		return color;
	}

}
