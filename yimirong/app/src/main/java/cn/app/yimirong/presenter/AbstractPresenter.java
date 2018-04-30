package cn.app.yimirong.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by android on 2016/3/21 0021.
 */
public abstract class AbstractPresenter implements IPresenter {

	protected IViewController view;
	protected Context appContext;
	protected Activity activity;
	protected Context context;
	protected boolean isBinded = false;

	@Override
	public void bind(IViewController view) {
		this.view = view;
		if (view instanceof Activity) {
			this.activity = (Activity) view;
			this.context = this.activity;
			this.appContext = this.activity.getApplicationContext();
		} else if (view instanceof Fragment) {
			this.activity = ((Fragment) view).getActivity();
			this.context = this.activity;
			this.appContext = this.activity.getApplicationContext();
		} else {
			throw new RuntimeException("unsupported view type");
		}
		isBinded = true;
	}

	@Override
	public void unBind() {
		this.view = null;
		this.activity = null;
		this.appContext = null;
		isBinded = false;
	}
}
