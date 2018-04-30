package cn.app.yimirong.presenter;

import android.support.annotation.NonNull;

/**
 * Created by android on 2015/12/31 0031.
 */
public interface IPresenter {

	public void bind(@NonNull IViewController view);

	public void unBind();

}
