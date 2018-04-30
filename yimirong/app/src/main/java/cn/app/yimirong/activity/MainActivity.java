package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.AppMgr;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.base.BaseFragment;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.HQevnt;
import cn.app.yimirong.event.custom.LoadBalanceEvent;
import cn.app.yimirong.event.custom.ShareInviteEvent1;
import cn.app.yimirong.event.custom.ShareInviteEvent2;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.event.custom.UserInfoUpdateEvent;
import cn.app.yimirong.fragment.HQBFragment;
import cn.app.yimirong.fragment.HomeFragment;
import cn.app.yimirong.fragment.InvestFragment;
import cn.app.yimirong.fragment.MineFragment;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.SpecialDays;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.db.dao.BankDao;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.model.inter.CheckInstallMarkCallBack;
import cn.app.yimirong.utils.AssetUtils;
import cn.app.yimirong.utils.ClickCounter;
import cn.app.yimirong.utils.FileUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import qiu.niorgai.StatusBarCompat;

public final class MainActivity extends BaseActivity implements
		RadioGroup.OnCheckedChangeListener, BaseFragment.IPageSwitcher {

	private static final int[] NAVIDS = {
			R.id.activity_main_nav_jingxuan,
			R.id.activity_main_nav_huoqibao,
			R.id.activity_main_nav_invest,
			R.id.activity_main_nav_mine
	};

	// 屏幕宽度
	public static int screenWidth;
	// 屏幕高度
	public static int screenHeight;

	public static final int PAGE_JINGXUAN = 0;
	public static final int PAGE_HUOQIBAO = 1;
	public static final int PAGE_INVEST = 2;
	public static final int PAGE_MINE = 3;
	public int currentPage = PAGE_JINGXUAN;

	private ConnectionChangeReceiver myReceiver;

	private ViewPager viewpager;
	public RadioGroup bottomNav;
	private LinearLayout no_network;

	private ClickCounter clickCounter;

	private Animation show;

	private Animation hidden;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		AppMgr.add(this);
		show = AnimationUtils.loadAnimation(context, R.anim.popshow_anim);
		hidden = AnimationUtils.loadAnimation(context, R.anim.pophidden_anim);
		registerReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompat.translucentStatusBar(this, true);
        }

		/*try {
			PackageManager packageManager = getApplicationContext().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
			//应用装时间
			long firstInstallTime = packageInfo.firstInstallTime;
			//应用最后一次更新时间
			long lastUpdateTime = packageInfo.lastUpdateTime;
			int sum = sp.getInt("sum",1);
			sum = sum++;
			sp.edit().putLong("sum",sum).commit();

			Log.i(TAG, "onCreate: "+"lastUpdateTime:"+lastUpdateTime);

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}*/
//		EventBus.getDefault().register(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sp.edit().putBoolean(Constant.SP_CONFIG_FIRST_IN, false).apply();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		AppMgr.finishAll();
		unregisterReceiver();
//		EventBus.getDefault().unregister(this);
	}

	@Override
	public void loadView() {
		tintColor = R.color.trans;
		setContentView(R.layout.activity_main);
		screenWidth = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth(); // 获取屏幕的宽度
		screenHeight = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();// 获取屏幕的高度
		if (!sp.getBoolean(Constant.SP_CONFIG_FIRST_IN, true)) {
			if (!sp.getString(Constant.PLAT, "").equals(AnalyticsConfig.getChannel(this))) {
				installMark();
				sp.edit().putString(Constant.PLAT,AnalyticsConfig.getChannel(this)).commit();
			}
		}else {
			loadRegistEXP();
			installMark();
			sp.edit().putString(Constant.PLAT,AnalyticsConfig.getChannel(this)).commit();
		}
	}
	private void installMark() {
		if (cmodel!=null) {
			String channel = AnalyticsConfig.getChannel(context);
			String uid = Config.UID;
			cmodel.checkInstallMark(uid,"android",channel, new CheckInstallMarkCallBack() {
				@Override
				public void onSucceed(String qudao) {

					sp.edit().putBoolean(Constant.CHECK_INSTANLL_MARK_FIRSTIN,false).commit();

					sp.edit().putString(Constant.QUDAO,qudao).commit();
				}

				@Override
				public void onFailure() {
					sp.edit().putBoolean(Constant.CHECK_INSTANLL_MARK_FIRSTIN,true).commit();
				}
			});
		}
	}
	private void  loadRegistEXP(){
		if (cmodel != null) {
			cmodel.getRegistEXP(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof String) {
						String amount = (String)t;
						if (!amount.equals("") && !amount.equals("0")) {
							MobclickAgent.onEvent(context, "expalert");
							PromptUtils.showDialog5(context, amount + "元", new PromptUtils.OnDialogClickListener1() {
								@Override
								public void onClick(Dialog dialog) {
									dialog.dismiss();
									MobclickAgent.onEvent(context,"expRegist");
									Intent intent = new Intent(context, PhoneActivity.class);
									startActivity(intent);
								}
							});
						}
					}
				}
			});
		}
	}


	@Override
	public void initView() {
		no_network = (LinearLayout) findViewById(R.id.no_network);
		viewpager = (ViewPager) findViewById(R.id.activity_main_viewpager);
		bottomNav = (RadioGroup) findViewById(R.id.activity_main_bottom_nav);
		bottomNav.setOnCheckedChangeListener(this);
		Fragment home = new InvestFragment();
		Fragment huoqibao = new HQBFragment();
		Fragment invest = new HomeFragment();
		Fragment mine = new MineFragment();

		List<Fragment> fragments = new ArrayList<>();
		fragments.add(0, home);
		fragments.add(1, huoqibao);
		fragments.add(2, invest);
		fragments.add(3, mine);

		FragmentManager fm = getSupportFragmentManager();
		PagerAdapter adapter = new MainPagerAdapter(fm, fragments);
		viewpager.setAdapter(adapter);
		viewpager.requestDisallowInterceptTouchEvent(true);
		viewpager.setOffscreenPageLimit(3);
		viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int page) {
				bottomNav.clearCheck();
				bottomNav.check(NAVIDS[page]);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		clickCounter = new ClickCounter(2, 2000);
		clickCounter.setListener(new ClickCounter.OnCountListener() {
			@Override
			public void onCount(int currentTime) {
				if (currentTime == 1) {
					ToastUtils.show(context, "再按一次退出应用", Toast.LENGTH_SHORT);
				}
			}

			@Override
			public void onCountDone() {
				finish();
			}
		});
	}

	@Override
	public void initData() {
		loadShareInvite();
		loadBankList();
		loadSpecialDays();
	}


	/**
	 * 获取specialdays.json
	 */
	private void loadSpecialDays() {
		if (cmodel != null) {
			cmodel.getSpecialDays(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					if (t != null && t instanceof SpecialDays) {
						//存入内存
						DataMgr.sd = (SpecialDays) t;
						//写入文件
						FileUtils.writeFile(context, Constant.SPECIAL_DAYS, response, false);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					//获取失败时从文件加载
					String json = AssetUtils.getStringFromAsset(context, Constant.SPECIAL_DAYS);
					//存入内存
					DataMgr.sd = TimeUtils.parseSpecialDays(json);
				}
			});
		}
	}

	public class ConnectionChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mobNetInfo != null && wifiNetInfo != null) {
				if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
					no_network.setVisibility(View.VISIBLE);
					//改变背景或者 处理网络的全局变量
				} else {
					//改变背景或者 处理网络的全局变量
					no_network.setVisibility(View.GONE);
				}
			}

		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		this.registerReceiver(myReceiver, filter);
	}

	private void unregisterReceiver() {
		this.unregisterReceiver(myReceiver);
	}

	/**
	 * 加载银行列表
	 */
	private void loadBankList() {
		if (cmodel != null) {
			cmodel.getBankList(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					if (t != null && t instanceof List) {
						List<Bank> list = (List<Bank>) t;
						//先清空
						BankDao.getInstance(context).clear();
						//再插入
						BankDao.getInstance(context).insert(list);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
				}
			});
		}
	}

	/**
	 * 获取邀请数据
	 */
	private void loadShareInvite() {
		if (cmodel != null) {
			cmodel.getShareInviteDesc(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof ShareInvite) {
						ShareInvite shareInvite = (ShareInvite) t;
						mCache.put("share_invite", shareInvite);
						EventBus.getDefault().post(new ShareInviteEvent2());
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
				}
			});
		}
	}

	/**
	 * 从网络加载数据
	 */
	private void loadUserBalance() {
		if (App.isLogin) {
			if (App.userinfo != null) {
				App.userinfo.balance = DataMgr.getInstance(appContext)
						.restoreBalance();
			}
			if (amodel != null) {
				amodel.getUserBalance(new ResponseHandler() {
					@Override
					public <T> void onSuccess(String response, T t) {
						super.onSuccess(response, t);
						if (t != null && t instanceof String) {
							String balance = (String) t;
							if (App.userinfo != null) {
								App.userinfo.balance = balance;
							}
							DataMgr.getInstance(appContext).saveBalance(balance);
						}
					}

					@Override
					public void onFailure(String errorCode, String msg) {
						super.onFailure(errorCode, msg);
					}
				});
			}
		}
	}

	/**
	 * 加载用户信息
	 */
	private void loadUserInfo() {
		if (App.isLogin) {
			App.userinfo = DataMgr.getInstance(appContext).restoreUserInfo();
			if (amodel != null) {
				amodel.getUserInfo(new ResponseHandler() {
					@Override
					public <T> void onSuccess(String response, T t) {
						super.onSuccess(response, t);
						if (t != null && t instanceof UserInfo) {

							UserInfo userinfo = (UserInfo) t;
							App.userinfo = userinfo;
							DataMgr.getInstance(appContext).saveUserInfo(userinfo);
							DataMgr.getInstance(appContext).saveBalance(
									userinfo.balance);
							calcDelta(userinfo.serverTime);
							MobclickAgent.onEvent(context, "loadUserInfo");
							EventBus.getDefault().post(new UserInfoUpdateEvent());
						}
					}
				});
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int i) {
		int page = currentPage;
		switch (i) {
			case R.id.activity_main_nav_jingxuan:
				page = PAGE_JINGXUAN;
				break;
			case R.id.activity_main_nav_huoqibao:
				page = PAGE_HUOQIBAO;
				break;
			case R.id.activity_main_nav_invest:
				page = PAGE_INVEST;
				break;
			case R.id.activity_main_nav_mine:
				page = PAGE_MINE;
				break;
			default:
				break;
		}

		if (page != currentPage) {
			currentPage = page;
			viewpager.setCurrentItem(currentPage, false);
		}
	}

	@Override
	public void switchToPage(int page) {
		if (viewpager != null && bottomNav != null) {
			if (page != currentPage) {
				bottomNav.clearCheck();
				bottomNav.check(NAVIDS[page]);
			}
		}
	}


	private class MainPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;

		public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int page) {
			return fragments.get(page);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			clickCounter.countClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获取邀请数据
	 *
	 * @param event
	 */
	public void onEventMainThread(ShareInviteEvent1 event) {
		loadShareInvite();
	}

	/**
	 * 刷新用户数据
	 *
	 * @param event
	 */
	public void onEventMainThread(UserInfoEvent event) {
		loadUserInfo();
	}

	/**
	 * 获取用户账户余额
	 *
	 * @param event
	 */
	public void onEventMainThread(LoadBalanceEvent event) {
		loadUserBalance();
	}

	public void onEventMainThread(HQevnt event) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				bottomNav.clearCheck();
				bottomNav.check(NAVIDS[PAGE_HUOQIBAO]);
				viewpager.setCurrentItem(PAGE_HUOQIBAO, false);
				currentPage = PAGE_HUOQIBAO;
				//logger.i("gdc", "currentPage"+currentPage);
			}
		}, 50);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == HomeFragment.AccAssetActivityTag) {

			//延迟两秒跳转
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					bottomNav.clearCheck();
					bottomNav.check(NAVIDS[PAGE_HUOQIBAO]);
					viewpager.setCurrentItem(PAGE_HUOQIBAO, false);
					currentPage = PAGE_HUOQIBAO;
					//logger.i("gdc", "currentPage"+currentPage);
				}
			}, 50);

//            new Handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, 500);


		}
		if (resultCode == MineFragment.InvestFlag) {
		//延迟两秒跳转
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					bottomNav.clearCheck();
					bottomNav.check(NAVIDS[PAGE_JINGXUAN]);
					viewpager.setCurrentItem(PAGE_JINGXUAN, false);
					currentPage = PAGE_JINGXUAN;
				}
			}, 50);

		}
	}



	public void HideRadioButton(boolean ishide) {
		if (ishide) {
			if (bottomNav.isShown()) {
				bottomNav.startAnimation(hidden);
				bottomNav.setVisibility(View.GONE);
			}
		} else {
			if (!bottomNav.isShown()){
				bottomNav.startAnimation(show);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						bottomNav.setVisibility(View.VISIBLE);
					}
				},500);
			}

		}
	}

}
