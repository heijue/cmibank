package cn.app.yimirong.model.bean;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -5742915077030670948L;

	/**
	 * 用户名
	 */
	public String username;

	/**
	 * 密码MD5
	 */
	public String password;

	public User() {

	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
