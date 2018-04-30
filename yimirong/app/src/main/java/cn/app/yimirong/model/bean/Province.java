package cn.app.yimirong.model.bean;

import java.util.Map;

public class Province {

	public String name;
	public String code;
	public Map<String, String> list;

	@Override
	public String toString() {
		return "Province [name=" + name + ", code=" + code + ", list=" + list
				+ "]";
	}

}
