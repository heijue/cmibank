package cn.app.yimirong.base;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuiou.mobile.FyPay;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;

import cn.app.yimirong.ACache;
import cn.app.yimirong.App;
import cn.app.yimirong.AppMgr;
import cn.app.yimirong.R;
import cn.app.yimirong.activity.UnLockActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.JinJiEvent;
import cn.app.yimirong.event.custom.LoginChangeEvent;
import cn.app.yimirong.event.custom.ToMainEvent;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.AModel;
import cn.app.yimirong.model.CModel;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.PModel;
import cn.app.yimirong.model.bean.JinJiNotice;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.SystemInfo;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.bean.VersionData;
import cn.app.yimirong.model.db.dao.OfflineDao;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.IViewController;
import cn.app.yimirong.utils.ApkUtils;
import cn.app.yimirong.utils.Downloader;
import cn.app.yimirong.utils.FileUtils;
import cn.app.yimirong.utils.PermissionsChecker;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.PromptUtils.OnDialogClickListener1;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.SystemBarTintManager;
import qiu.niorgai.StatusBarCompat;


public abstract class BaseActivity extends AppCompatActivity implements IViewController {

	private static final String SAVETIME = "saveTime";
	protected String TAG;
	protected Activity activity;
	protected Context appContext;
	protected Context context;
	protected App app;
	protected Logger logger;
	protected SharedPreferences sp;
	protected Handler mHandler;
	protected RelativeLayout titleBar;
	protected TextView titleBack;
	protected TextView titleText;
	protected TextView titleAccount;
	protected ImageView titleArrow;
	protected ImageButton titleMsg;
	protected TextView titleRight;

	private OnRightClickListener rightClickListener;

	private OnTitleArrowChangeListener titleClickListener;

	public Dialog mLoading;

	protected boolean isStatusBarTint = true;

	protected boolean isArrowDown = true;

	protected Animation rotateUp;

	protected Animation rotateDown;

	protected Animation mRotateAnim;

	protected ACache mCache;

	public boolean shouldVerify = true;

	public boolean isCreated = false;
	private TextView mLoadingMsg;
	private ImageView mLoadingIcon;

	protected CModel cmodel;

	protected AModel amodel;

	protected PModel pmodel;

	protected String cacheDir;
	protected boolean epxIsUse;
	private boolean isShowing;

	protected int tintColor = R.color.control_text_pressed;

	private long currentTime;

	// 所需的全部权限
	static final String[] PERMISSIONS = new String[]{
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.CALL_PHONE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
	};

	private boolean isRequireCheck; // 是否需要系统权限检测, 防止和系统提示框重叠

	private PermissionsChecker mChecker; // 权限检测器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TAG = this.getClass().getSimpleName();
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			restoreInstance(savedInstanceState);
			Http.init(getApplicationContext());
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appContext = getApplicationContext();
		currentTime = TimeUtils.getCurrentTimeInLong();
		context = this;
		activity = this;
		app = (App) getApplication();
		logger = Logger.getInstance();
		sp = getSharedPreferences(Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		mHandler = new Handler();
		mCache = ACache.get(appContext);
		cacheDir = getCacheDir().getPath();
		mChecker = new PermissionsChecker(appContext);
		isRequireCheck = true;
		amodel = AModel.getInstance(appContext);
		cmodel = CModel.getInstance(appContext);
		pmodel = PModel.getInstance(appContext);
		initLoading();
		loadView();
		initTint();
		initTitle();
		initView();
		initData();
		isCreated = true;
		AppMgr.add(this);
	}

	public void saveInstance(Bundle outState) {
		Log.d(TAG, "saveInstance");
		outState.putBoolean("isLogin", App.isLogin);
		outState.putString("account", App.account);
		outState.putBoolean("isVerified", App.isVerified);
		outState.putLong("lastVerifyTime", App.lastVerifyTime);
		outState.putSerializable("loginData", App.loginData);
		outState.putSerializable("userinfo", App.userinfo);
		outState.putString("code", App.code);
		outState.putBoolean("isPayLocked", App.isPayLocked);
		outState.putBoolean("isInBack", App.isInBack);
		outState.putLong("delta", App.delta);
		outState.putBoolean("isDebug", App.isDebug);
		outState.putInt("debug_level", App.DEBUG_LEVEL);
	}

	public void restoreInstance(Bundle savedState) {
		Log.d(TAG, "restoreInstance");
		App.isLogin = savedState.getBoolean("isLogin");
		App.account = savedState.getString("account");
		App.isVerified = savedState.getBoolean("isVerified");
		App.lastVerifyTime = savedState.getLong("lastVerifyTime");
		App.loginData = (LoginData) savedState.getSerializable("loginData");
		App.userinfo = (UserInfo) savedState.getSerializable("userinfo");
		App.code = savedState.getString("code");
		App.isPayLocked = savedState.getBoolean("isPayLocked");
		App.isInBack = savedState.getBoolean("isInBack");
		App.delta = savedState.getLong("delta");
		App.isDebug = savedState.getBoolean("isDebug");
		App.DEBUG_LEVEL = savedState.getInt("debug_level");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState");
		saveInstance(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		mHandler.removeCallbacksAndMessages(null);
		closeLoading();
		isCreated = false;
		AppMgr.remove(this);
	}

	/**
	 * 初始化进度条
	 */
	protected void initLoading() {
		mLoading = new Dialog(this, R.style.DialogStyle);
		mLoading.setContentView(R.layout.dialog_loading);
		mLoadingMsg = (TextView) mLoading
				.findViewById(R.id.dialog_loading_text);
		mLoadingIcon = (ImageView) mLoading
				.findViewById(R.id.dialog_loading_bar);
		mRotateAnim = AnimationUtils.loadAnimation(context, R.anim.progress_rotate);
		mRotateAnim.setInterpolator(new LinearInterpolator());
		mLoading.setCancelable(true);
		mLoading.setCanceledOnTouchOutside(false);
	}

	@Override
	public void updateView(Bundle data) {
	}

	@Override
	public void updateData(Bundle data) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		fyPayChangeEvent();
		// 缺少权限时
			if (isRequireCheck) {
				if (mChecker.lacksPermissions(PERMISSIONS)) {
					requestPermissions(PERMISSIONS); // 请求权限
				} else {
					// 全部权限都已获取
				}
			} else {
				isRequireCheck = true;

		}
		app = (App) getApplication();
		appContext = getApplicationContext();
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		MobclickAgent.onResume(this);
		if (App.isInBack) {
			// app从后台进入
			App.isInBack = false;
			getNotice();
			// 获取服务器时间
			getServerTime();
			getVersion();
			long saveTime = sp.getLong(SAVETIME, 0);
			long TimeDifference  = currentTime-saveTime;
			Log.i(TAG, "onResume: "+TimeDifference);
			if (App.isDestroy == 0) {
				getSystemInfo();
			}
			App.isDestroy = 1;
		}
	}

	/**
	 * 用户权限处理,
	 * 如果全部获取, 则直接过.
	 * 如果权限缺失, 则提示Dialog.
	 *
	 * @param requestCode  请求码
	 * @param permissions  权限
	 * @param grantResults 结果
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 0 && hasAllPermissionsGranted(grantResults)) {
			isRequireCheck = true;
		} else {
			isRequireCheck = false;
			showMissingPermissionDialog();
		}
	}

	// 含有全部的权限
	private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

	// 显示缺失权限提示
	private void showMissingPermissionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("帮助");
		builder.setMessage("当前应用缺少必要权限。请点击设置-权限-打开所需权限。最后点击两次后退按钮，即可返回。");

		// 拒绝, 退出应用
		builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				startAppSettings();
			}
		});

		builder.setCancelable(false);

		builder.show();
	}

	// 启动应用的设置
	private void startAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

	// 请求权限兼容低版本
	private void requestPermissions(String... permissions) {
		ActivityCompat.requestPermissions(this, permissions, 0);
	}

	/**
	 * 获取版本信息
	 */
	private void getSystemInfo() {
		final String version = SystemUtils.getVersionName(context);
		String plat = SystemUtils.getMetaData(context, Constant.CHANNEL_NAME);
		if (cmodel != null) {
			cmodel.getSystemInfo(version, plat, Constant.OS, 0,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							if (t != null && t instanceof SystemInfo) {
								// 清除消息，界面不再跳转
								EventBus.getDefault().post(new JinJiEvent());
								SystemInfo systemInfo = (SystemInfo) t;
								Log.i(TAG, "onSuccess: "+systemInfo.toString());
								showUpdate(systemInfo);
							}
						}
					});
		}
	}

	/**
	 * 提示更新
	 *
	 * @param systemInfo
	 */
	private void showUpdate(final SystemInfo systemInfo) {
		long currentTimeMillis = System.currentTimeMillis();
		if (systemInfo.type.equals("0") && currentTimeMillis < new Double(systemInfo.force_use_time)) {
			PromptUtils.showDialog2(activity, context, systemInfo.title,
					systemInfo.content, systemInfo.button_name,
					new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							dialog.dismiss();
							EventBus.getDefault().post(new ToMainEvent());
						}

						@Override
						public void onRightClick(Dialog dialog) {
							String localPath = Constant.DOWNLOAD_PATH
									+ File.separator + "yimirong.apk";
							boolean isCheck = ApkUtils.checkApk(localPath,
									systemInfo.md5);
							if (isCheck) {
								// 文件通过校验则安装
								ApkUtils.installApk(context, localPath);
								finish();
							} else {
								// 删除文件重新下载
								FileUtils.deleteFile(localPath);
								download(systemInfo, localPath);
							}
						}
					/*@Override
					public void onClick(Dialog dialog) {

					}*/
					}, false);
		} else {
			PromptUtils.showDialog1(activity, context, systemInfo.title,
					systemInfo.content, systemInfo.button_name,
					new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							String localPath = Constant.DOWNLOAD_PATH
									+ File.separator + "yimirong.apk";
							boolean isCheck = ApkUtils.checkApk(localPath,
									systemInfo.md5);
							if (isCheck) {
								// 文件通过校验则安装
								ApkUtils.installApk(context, localPath);
								finish();
							} else {
								// 删除文件重新下载
								FileUtils.deleteFile(localPath);
								download(systemInfo, localPath);
							}
						}
					});
		}

	}

	/**
	 * 下载
	 *
	 * @param systemInfo
	 * @param path
	 */
	private void download(final SystemInfo systemInfo, String path) {
		ApkUtils.deleteApk(Constant.DOWNLOAD_PATH);
		Downloader downloader = new Downloader(activity, systemInfo.url);
		downloader.setTitle("易米融").setLocation("yimirong/download").setPath(path)
				.setFilename("yimirong.apk").setDescription("正在下载更新")
				.setMimeType("application/vnd.android.package-archive")
				.setListener(new Downloader.DownloadListener() {
					@Override
					public void onSuccess(Dialog dialog, String path) {
						logger.d(TAG, "Download Success");
						dialog.dismiss();
						/*// 下载完成先校验文件
						if (ApkUtils.checkApk(path, systemInfo.md5)) {
							// 校验成功则提示安装
							ApkUtils.installApk(context, path);
						} else {
							// 校验失败
							ToastUtils.show(context, "下载失败");
						}*/
						ApkUtils.installApk(context, path);
						finish();
					}

					@Override
					public void onFailure(Dialog dialog, int reason) {
						dialog.dismiss();
						ToastUtils.show(context, "下载失败");
						finish();
					}
				});
		downloader.download(true);
	}

	/**
	 * 获取支付渠道
	 */
	private void getVersion() {
		if (cmodel != null) {
			cmodel.getVersion(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof VersionData) {
						VersionData data = (VersionData) t;
						DataMgr.getInstance(appContext).saveVersionData(
								data);
//						Log.i(TAG, "onSuccess: "+data.toString());

					}
				}
			});
		}
	}

	/**
	 * 获取紧急消息
	 */
	private void getNotice() {
		if (cmodel != null) {
			cmodel.getJinJiNotice(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof JinJiNotice) {
						JinJiNotice jinjiNotice = (JinJiNotice) t;
						if (jinjiNotice.action == 1) {
							EventBus.getDefault().post(new JinJiEvent());
							showExit(jinjiNotice);
						}
					}
				}
			});
		}
	}

	/**
	 * 显示紧急信息
	 *
	 * @param jinjiNotice
	 */
	protected void showExit(JinJiNotice jinjiNotice) {
		if (isShowing) {
			return;
		}
		PromptUtils.showDialog1(activity, context, jinjiNotice.title,
				jinjiNotice.content, jinjiNotice.button_name,
				new OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						isShowing = false;
						exit();
					}
				});
		isShowing = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		closeLoading();
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		checkLock();
	}

	private void checkLock() {
		String lockPass = sp.getString("lockPass", null);
		boolean islockSet = lockPass != null ? true : false;
		//long currentTime = TimeUtils.getCurrentTimeInLong();
		//long delta = ((currentTime - App.lastVerifyTime) / 1000) / 3600;
		if (shouldVerify && islockSet && App.isLogin && !App.isVerified) {
			// 手势密码启用
			Intent intent = new Intent(context, UnLockActivity.class);
			intent.putExtra("action", UnLockActivity.ACTION_VERIFY_LOCK);
			startActivity(intent);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		checkAppState();
	}

	/**
	 * 检查app是进入后台还是切换界面
	 */
	protected void checkAppState() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		if (processes != null) {
			for (RunningAppProcessInfo p : processes) {
				if (p.processName.equals(context.getPackageName())) {
					if (p.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						// 处于后台
						logger.i(TAG, "App在后台运行！");
						App.isVerified = false;
						App.isInBack = true;
					} else {
						// 处于前台
						logger.i(TAG, "App在前台运行！");
						App.isInBack = false;
					}
				}
			}
		}
	}

	private void initTint() {
		if (isStatusBarTint) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				SystemBarTintManager tintManager = new SystemBarTintManager(
						this);
				tintManager.setStatusBarTintEnabled(true);
				tintManager.setStatusBarTintResource(tintColor);
			}
		}
	}

	/**
	 * 初始化标题栏
	 */
	protected void initTitle() {
		rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
		rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_down);

		titleBar = (RelativeLayout) findViewById(R.id.title_bar_wrapper);
		titleBack = (TextView) findViewById(R.id.title_bar_back_text);
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exit();
			}
		});

		titleText = (TextView) findViewById(R.id.title_bar_title_text);
		titleText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isArrowDown) {
					// 正常状态
//					titleArrow.startAnimation(rotateUp);
					if (titleClickListener != null) {
						titleClickListener.onArrowUp();
					}
					isArrowDown = false;
				} else {
					// 展开状态
//					titleArrow.startAnimation(rotateDown);
					if (titleClickListener != null) {
						titleClickListener.onArrowDown();
					}
					isArrowDown = true;
				}
			}
		});
		titleAccount = (TextView) findViewById(R.id.title_bar_title_account);
		titleArrow = (ImageView) findViewById(R.id.title_bar_title_indicator);
		titleArrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isArrowDown) {
					// 正常状态
//					titleArrow.startAnimation(rotateUp);
					if (titleClickListener != null) {
						titleClickListener.onArrowUp();
					}
					isArrowDown = false;
				} else {
					// 展开状态
//					titleArrow.startAnimation(rotateDown);
					if (titleClickListener != null) {
						titleClickListener.onArrowDown();
					}
					isArrowDown = true;
				}
			}
		});
		titleMsg = (ImageButton) findViewById(R.id.title_bar_title_msg);

		titleRight = (TextView) findViewById(R.id.title_bar_title_right);
		titleRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (rightClickListener != null) {
					rightClickListener.onClick();
				}
			}
		});
		setTitleBack(false);
		setTitleArrow(false, null);
		setTitleMsg(false, false);
	}

	/**
	 * 设置是否显示标题栏
	 *
	 * @param isShow
	 */
	protected void setTitleBar(boolean isShow) {
		if (isShow) {
			titleBar.setVisibility(View.VISIBLE);
		} else {
			titleBar.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置是否显示返回
	 *
	 * @param isShow
	 */
	protected void setTitleBack(boolean isShow) {
		if (isShow) {
			titleBack.setVisibility(View.VISIBLE);
		} else {
			titleBack.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置是否显示标题栏下拉箭头
	 */
	protected void setTitleArrow(boolean isShow,
								 OnTitleArrowChangeListener listener) {
		if (isShow) {
			titleArrow.setVisibility(View.VISIBLE);
			titleText.setClickable(true);
			this.titleClickListener = listener;
		} else {
			titleArrow.setVisibility(View.GONE);
			titleText.setClickable(false);
		}
	}

	/**
	 * 设置标题文本
	 *
	 * @param title
	 */
	protected void setTitleText(String title) {
		titleText.setText(title);
	}

	/**
	 * titleBar消息
	 *
	 * @param isShow
	 * @param hasMsg
	 */
	protected void setTitleMsg(boolean isShow, boolean hasMsg) {
		if (isShow) {
			titleRight.setVisibility(View.GONE);
			titleMsg.setVisibility(View.VISIBLE);
			if (hasMsg) {
				titleMsg.setBackgroundResource(R.drawable.xiaoxi_you);
			} else {
				titleMsg.setBackgroundResource(R.drawable.xiaoxi_wu);
			}
		} else {
			titleMsg.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置标题栏右边的文字是否显示
	 *
	 * @param isShow
	 * @param listener
	 */
	protected void setTitleRight(boolean isShow, OnRightClickListener listener) {
		if (isShow) {
			this.rightClickListener = listener;
			titleMsg.setVisibility(View.GONE);
			titleRight.setVisibility(View.VISIBLE);
		} else {
			titleRight.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置右侧文本内容
	 *
	 * @param right
	 */
	protected void setRightText(String right) {
		if (titleRight.getVisibility() == View.VISIBLE) {
			titleRight.setText(right);
		}
	}

	/**
	 * 隐藏输入法
	 */
	protected void hideInputMethod() {
		View view = getCurrentFocus();
		if (view != null) {
			view.clearFocus();
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 显示加载进度
	 *
	 * @param msg
	 */
	public void showLoading(String msg) {
		if (this != null && !this.isFinishing()) {
			if (mLoading != null && !mLoading.isShowing()) {
				mLoading.show();
				if (mLoadingMsg != null) {
					mLoadingMsg.setText(msg);
				}
				if (mLoadingIcon != null && mRotateAnim != null) {
					mLoadingIcon.startAnimation(mRotateAnim);
				}
			}
		}
	}

	/**
	 * 关闭加载进度
	 */
	public void closeLoading() {
		if (this != null) {
			if (mLoading != null && mLoading.isShowing()) {
				mLoading.dismiss();
				if (mLoadingIcon != null) {
					mLoadingIcon.clearAnimation();
				}
			}
		}
	}

	/**
	 * 获取服务器时间
	 */
	protected void getServerTime() {
		String delta = mCache.getAsString("delta");
		if (!StringUtils.isBlank(delta)) {
			try {
				App.delta = Long.parseLong(delta);
			} catch (Exception e) {
				App.delta = 0;
			}
		}
		cmodel.getServerTime(new ResponseHandler() {
			@Override
			public <T> void onSuccess(String response, T t) {
				super.onSuccess(response, t);
				if (t != null && t instanceof Long) {
					long serverTime = (Long) t;
					calcDelta(serverTime);
				}
			}

			@Override
			public void onFailure(String errorCode, String msg) {
				super.onFailure(errorCode, msg);
			}
		});
	}

	protected void calcDelta(long serverTime) {
		long localTime = System.currentTimeMillis() / 1000;
		App.delta = localTime - serverTime;
		mCache.put("delta", App.delta + "");
	}

	/**
	 * 退出
	 */
	protected void exit() {
		hideInputMethod();
		finish();
	}

	public interface OnRightClickListener {
		public void onClick();
	}

	public interface OnTitleArrowChangeListener {
		public void onArrowDown();

		public void onArrowUp();
	}
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config=new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config,res.getDisplayMetrics() );
		return res;
	}
	/**
	 * 退出登录
	 */
	protected void logout() {

//		loginOut_Sample();

		// 清除用户名和密码
		DataMgr.getInstance(appContext).clearUserData();
		// 清除cookies
		DataMgr.getInstance(appContext).clearCookies();
		// 清除登录数据
		DataMgr.getInstance(appContext).clearLoginData();
		// 清除用户数据
		DataMgr.getInstance(appContext).clearUserInfo();
		// 清除我的资产中数据
		DataMgr.getInstance(appContext).clearOffLineInfo();
		// 清除用户收益
		DataMgr.getInstance(appContext).clearUserProfitInfo();

		//清空七天收益表
		OfflineDao.getInstance(this).clear();

		// 退出登录
		App.userinfo = null;
		App.isLogin = false;
		App.account = null;
		App.code = null;
		App.loginData = null;

		//清除Cookies
		Http.clearCookies();

		// 信鸽推送取消绑定账号

		//友盟退出登录
		MobclickAgent.onProfileSignOff();

		sp.edit().remove("lockPass").commit();

		EventBus.getDefault().post(new LoginChangeEvent());
		fyPayChangeEvent();
		finish();
	}
	protected void fyPayChangeEvent(){
		if (App.DEBUG_LEVEL == App.DEBUG_LEVEL_TEST) {
			FyPay.setDev(false);
		} else {
			FyPay.setDev(true);
		}
		FyPay.init(this);
	}
}
