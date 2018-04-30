package cn.app.yimirong.model.base;

import android.content.Context;

import cn.app.yimirong.log.Logger;

/**
 * Created by android on 2015/10/28.
 */
public abstract class BaseModel {

	public static final String NETWORK_ERROR = "网络异常,请稍后再试";

	public static final String SYSTEM_ERROR = "系统异常,请稍后再试";

	protected Logger logger;

	protected Context context;

	protected BaseModel(Context context) {
		this.context = context;
		this.logger = Logger.getInstance();
	}
}
