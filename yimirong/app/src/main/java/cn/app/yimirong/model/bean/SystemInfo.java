package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class SystemInfo implements Serializable {

	public String title;
	public String content;
	public String button_name;
	public String qj_version;
	public String url;
	public String md5;
	public String new_version;
	public String type;
	public String force_use_time;

	@Override
	public String toString() {
		return "SystemInfo{" +
				"title='" + title + '\'' +
				", content='" + content + '\'' +
				", button_name='" + button_name + '\'' +
				", qj_version='" + qj_version + '\'' +
				", url='" + url + '\'' +
				", md5='" + md5 + '\'' +
				", new_version='" + new_version + '\'' +
				", type='" + type + '\'' +
				", force_use_time='" + force_use_time + '\'' +
				'}';
	}
}
