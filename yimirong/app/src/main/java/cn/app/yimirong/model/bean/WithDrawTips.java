package cn.app.yimirong.model.bean;

import java.io.Serializable;

/**
 * Created by android on 2016/1/22 0022.
 */
public class WithDrawTips implements Serializable {
	public String text;
	public String cancel_btn;
	public String true_btn;

	public WithDrawTips() {
		this.text = "非工作取现顺延到下一个工作日到账";
		this.cancel_btn = "取消";
		this.true_btn = "确定";
	}
}
