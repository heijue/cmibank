package cn.app.yimirong.model.bean;

import java.util.List;

public class Bank {

	public String code;
	public String name;
	public String url;
	public int single;
	public int singleDay;
	public int singelMonth;
	public boolean isShow = true;
	public String plat;
	public List<Quota> quotas;

	@Override
	public String toString() {
		return "Bank{" +
				"code='" + code + '\'' +
				", name='" + name + '\'' +
				", url='" + url + '\'' +
				", single=" + single +
				", singleDay=" + singleDay +
				", singelMonth=" + singelMonth +
				", plat='" + plat + '\'' +
				'}';
	}
}
