package cn.app.yimirong.model.bean;

/**
 * Created by hetwen on 16/4/23.
 */
public class MenuItem {

	public boolean enabled = true;
	public String text;

	public MenuItem(String text, boolean enabled) {
		this.text = text;
		this.enabled = enabled;
	}


}
