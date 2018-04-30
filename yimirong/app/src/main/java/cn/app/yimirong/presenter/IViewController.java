package cn.app.yimirong.presenter;

import android.os.Bundle;

/**
 * Created by android on 2016/2/26 0026.
 */
public interface IViewController {

	public void loadView();

	public void initView();

	public void initData();

	public void showLoading(String msg);

	public void closeLoading();

	public void updateView(Bundle data);

	public void updateData(Bundle data);

}
