package cn.app.yimirong.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import cn.app.yimirong.ACache;
import cn.app.yimirong.App;
import cn.app.yimirong.activity.MainActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.AModel;
import cn.app.yimirong.model.CModel;
import cn.app.yimirong.model.PModel;
import cn.app.yimirong.presenter.IViewController;

public abstract class BaseFragment extends Fragment implements IViewController {

	protected Context appContext;

	protected Activity context;

	protected MainActivity activity;

	protected App app;

	protected Handler mHandler;

	protected Logger logger;

	protected String TAG;

	protected View view;

	protected SharedPreferences sp;

	protected Dialog mLoadingDialog;
	private TextView tvLoadingMessage;
	protected Animation mLoadingRotate;

	protected boolean isViewInited = false;

	protected ACache mCache;

	protected AModel amodel;
	protected CModel cmodel;
	protected PModel pmodel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		logger = Logger.getInstance();
		logger.i(TAG, "onCreate");
		mHandler = new Handler();
		context = getActivity();
		activity = (MainActivity) getActivity();
		appContext = activity.getApplicationContext();
		app = (App) activity.getApplication();
		sp = context.getSharedPreferences(Constant.SP_CONFIG_NAME,
				Context.MODE_PRIVATE);
		mCache = ACache.get(appContext);
		amodel = AModel.getInstance(appContext);
		cmodel = CModel.getInstance(appContext);
		pmodel = PModel.getInstance(appContext);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		logger.i(TAG, "onCreateView");
		EventBus.getDefault().register(this);
		loadView();
		view = loadView(inflater, container);
		ButterKnife.bind(this, view);
		initView();
		isViewInited = true;
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		return view;
	}


	@Override
	public void loadView() {
		logger.i(TAG, "loadView");
	}

	public abstract View loadView(LayoutInflater inflater, ViewGroup container);

	@Override
	public void updateData(Bundle data) {
	}

	@Override
	public void updateView(Bundle data) {
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isViewInited) {
			if (isVisibleToUser) {
				onVisible();
			} else {
				onInVisible();
			}
		}
	}

	public void onVisible() {
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		logger.i(TAG, "onVisible");
	}

	public void onInVisible() {
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		logger.i(TAG, "onInVisible");
	}


	@Override
	public void onDestroyView() {
		logger.i(TAG, "onDestroyView");
		EventBus.getDefault().unregister(this);
		isViewInited = false;
		ButterKnife.unbind(this);
		super.onDestroyView();
	}

	/**
	 * 显示加载进度条
	 *
	 * @param msg
	 */
	public void showLoading(String msg) {
		if (activity != null
				&& !activity.isFinishing()
				&& !mLoadingDialog.isShowing()) {
			activity.showLoading(msg);
		}
	}

	/**
	 * 关闭加载进度
	 */
	public void closeLoading() {
		if (activity != null
				&& !activity.isFinishing()
				&& !mLoadingDialog.isShowing()) {
			activity.closeLoading();
		}
	}

	public interface IPageSwitcher {
		void switchToPage(int page);
	}

}
