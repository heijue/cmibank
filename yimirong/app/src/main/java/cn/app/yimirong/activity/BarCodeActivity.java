package cn.app.yimirong.activity;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;

/**
 * 查看二维码
 *
 * @author android
 */
public class BarCodeActivity extends BaseActivity {

	@Override
	public void loadView() {
		setContentView(R.layout.activity_barcode);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("公众号二维码");
	}

	@Override
	public void initData() {
	}

}
