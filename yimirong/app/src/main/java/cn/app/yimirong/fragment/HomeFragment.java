package cn.app.yimirong.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.OnClick;
import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.activity.ActionListActivity;
import cn.app.yimirong.activity.ActivityQZC;
import cn.app.yimirong.activity.AssetNewActivity;
import cn.app.yimirong.activity.BindActivity;
import cn.app.yimirong.activity.CashActivity;
import cn.app.yimirong.activity.DQListActivity;
import cn.app.yimirong.activity.ExpNewActivity;
import cn.app.yimirong.activity.InvestHistoryActivity;
import cn.app.yimirong.activity.LoginActivity;
import cn.app.yimirong.activity.MainActivity;
import cn.app.yimirong.activity.MessageActivity;
import cn.app.yimirong.activity.PayActivity;
import cn.app.yimirong.activity.PhoneActivity;
import cn.app.yimirong.activity.ProfitListActivity;
import cn.app.yimirong.activity.SetPayPassActivity;
import cn.app.yimirong.activity.YesterdayProfitActivity;
import cn.app.yimirong.base.BaseFragment;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.BuySuccessEvent;
import cn.app.yimirong.event.custom.CashEvent;
import cn.app.yimirong.event.custom.LoginChangeEvent;
import cn.app.yimirong.event.custom.PayEvent;
import cn.app.yimirong.event.custom.ProfitEvent;
import cn.app.yimirong.event.custom.ZChuEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.LuckyBag;
import cn.app.yimirong.model.bean.NewExp;
import cn.app.yimirong.model.bean.Notice;
import cn.app.yimirong.model.bean.OffLineInfo;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.UserDQMoney;
import cn.app.yimirong.model.bean.UserHQMoney;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.MarketUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	private static final int POINTS = 7;
	public static final int AccAssetActivityTag = 99;
	private static final String MTEMP_OFF_INFO = "mtempoffinfo";

	private RelativeLayout noticeLay,noticeNum,RelateTYJ,RelateHQBwrapper,RelateKYJ,RelateDQZC;

	private LinearLayout Relateleiji,RelateLJSY;

	private TextView new_notice,BaoZhang,expNumber,leijishouyi,tvDqNum,
			wode_touzi,tvZzcNum,tvHqbNum,tvKyjeNum,tvCountProfit,btnRight,btnLeft,tvTitle;

	private View wdzc_line;

	private SwipeRefreshLayout refresher;

	private ImageView eyes_btn;

	private DataMgr offdata;

	private double yesterdayAll;

	private UserProfit mProfit;

	public OffLineInfo offinfo;

	private List<NewExp> exp;

	private UserHQMoney mHQMoney;

	private UserDQMoney mDQMoney;

	private boolean isEyes = false;

	private int loadTimes = 0;

	private int loadProg = 0;

	private double allmoney = 0;

	private double balance = 0d;

	private double dqmoney = 0d;

	private double hqmoney = 0d;

	private double expprofit = 0d;

	private MyHandler myHandler;

	private List<LuckyBag> lList;

	private List<Notice> nlist;

	private View rootView;

	private static class MyHandler extends Handler {

		private HomeFragment mWeak;

		public MyHandler(HomeFragment fragment) {
			this.mWeak = fragment;
		}

		@Override
		public void handleMessage(Message msg) {
			HomeFragment jingxuan = mWeak;
			int what = msg.what;
			if (what == 0 && jingxuan != null) {
				jingxuan.loadTimes = 0;
				jingxuan.allmoney = jingxuan.balance + jingxuan.hqmoney
						+ jingxuan.dqmoney + jingxuan.expprofit;
				String str = null;
				if (jingxuan.mDQMoney!=null && jingxuan.mDQMoney.countprofit>0) {
					str = SystemUtils.getDoubleStr(jingxuan.allmoney + jingxuan.mDQMoney.countprofit);
				}else {
					str = SystemUtils.getDoubleStr(jingxuan.allmoney);
				}
				if(jingxuan.tvZzcNum != null){
					if (jingxuan.isEyes){
						jingxuan.tvZzcNum.setText("****");
					}else {
						jingxuan.tvZzcNum.setText(str != null ? str : "0.00");
					}
				}

				//总资产
				jingxuan.offinfo.allmoney = str;
			}
		}
	}

	private void init(View rootView){
		eyes_btn = (ImageView) rootView.findViewById(R.id.visible_eye);
		refresher = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_refresher);

		new_notice = (TextView) rootView.findViewById(R.id.new_notice);
		BaoZhang = (TextView) rootView.findViewById(R.id.baozhang_text);
		expNumber = (TextView) rootView.findViewById(R.id.fragment_home_tyj_num);
		leijishouyi = (TextView) rootView.findViewById(R.id.leijishouyi_num);
		wode_touzi = (TextView) rootView.findViewById(R.id.wode_touzi);
		tvZzcNum = (TextView) rootView.findViewById(R.id.fragment_home_zzc_num);
		tvHqbNum = (TextView) rootView.findViewById(R.id.fragment_home_hqb_num);
		tvDqNum = (TextView) rootView.findViewById(R.id.fragment_home_dqzc_num);
		tvKyjeNum = (TextView) rootView.findViewById(R.id.fragment_home_kyje_num);
		tvCountProfit = (TextView) rootView.findViewById(R.id.fragment_home_count_profit);
		btnRight = (TextView) rootView.findViewById(R.id.fragment_home_rightbtn);
		btnLeft = (TextView) rootView.findViewById(R.id.fragment_home_leftbtn);
		tvTitle = (TextView) rootView.findViewById(R.id.main_title_bar_title_text);
		wdzc_line = (View) rootView.findViewById(R.id.wdzc_line);

		noticeLay = (RelativeLayout) rootView.findViewById(R.id.notice_layout);
		noticeNum = (RelativeLayout) rootView.findViewById(R.id.notice_number);
		RelateTYJ = (RelativeLayout) rootView.findViewById(R.id.fragment_home_tyj_wrapper);
		RelateDQZC = (RelativeLayout) rootView.findViewById(R.id.fragment_home_dqzc_wrapper);
		RelateHQBwrapper = (RelativeLayout) rootView.findViewById(R.id.fragment_home_hqb_wrapper);
		RelateKYJ = (RelativeLayout) rootView.findViewById(R.id.fragment_home_kyje_wrapper);

		Relateleiji = (LinearLayout) rootView.findViewById(R.id.fragment_empty);
		RelateLJSY = (LinearLayout) rootView.findViewById(R.id.fragment_home_ljsy_wrapper);
	}


	@Override
	public void onResume() {
		super.onResume();
		checkLogin();
		MobclickAgent.onResume(context);
		loadBalance();
		loadHQMoney();
		loadProfit();
		loadDQMoney();
		loadNEWExpMoney();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	public View loadView(LayoutInflater inflater, ViewGroup container) {
		if (rootView == null){
			rootView = inflater.inflate(R.layout.fragment_home, container, false);
			init(rootView);
		}
		ViewGroup par = (ViewGroup)rootView.getParent();
		if (par != null){
			par.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void initView() {

		if (tvTitle != null) {
			tvTitle.setText("资产");
		}

		offinfo = new OffLineInfo();
		myHandler = new MyHandler(this);
		int[] colors = getResources().getIntArray(R.array.google_colors);
		refresher.setColorSchemeColors(colors);
		refresher.setOnRefreshListener(this);

	}

	@Override
	public void initData() {

		isEyes = offdata.getInstance(context).restorEyes();
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite != null && shareInvite.Third_party_payment != null && !shareInvite.Third_party_payment.equals("")){
			BaoZhang.setText(shareInvite.Third_party_payment);
		}
		getoffInfo();
		loadBalance();
		loadHQMoney();
		loadProfit();
		loadDQMoney();
		loadNEWExpMoney();
	}


	public void getoffInfo() {
		if (App.isLogin) {
			OffLineInfo mtempoffinfo = offdata.getInstance(getContext()).restoreOffLineInfo();
			if (mtempoffinfo != null) {
				if (isEyes){
					tvKyjeNum.setText("****");
					tvCountProfit.setText("****");
					tvDqNum.setText("****");
					tvHqbNum.setText("****");
					expNumber.setText("****");
					leijishouyi.setText("****");
					tvZzcNum.setText("****");
					eyes_btn.setImageResource(R.drawable.biyan);
				}else {
					tvKyjeNum.setText(mtempoffinfo.kymoney);
					tvCountProfit.setText(mtempoffinfo.yesterday_profit);
					tvHqbNum.setText(mtempoffinfo.longmoney);
					leijishouyi.setText(mtempoffinfo.allprofit);
					tvDqNum.setText(mtempoffinfo.dqmoney);
					tvZzcNum.setText(mtempoffinfo.allmoney);
					eyes_btn.setImageResource(R.drawable.yanjing);
				}

			}
		}
	}

	@Override
	public void onVisible() {
		super.onVisible();
		checkLogin();

		if (App.isLogin) {

			getNotice();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					long etime = DataMgr.getInstance(context).restoreEvaluateTime();
					if (etime != 0) {
						boolean show = DataMgr.getInstance(context).restorShow();
						boolean isAllow = DataMgr.getInstance(context).saveisAllow();
						long server = TimeUtils.getServerTime();
						int days = (int) (server - etime) / 86400;
						if (etime > 0 && !isAllow && !show && days > 2 && mProfit.buyNum > 1) {
							showPL();
						}
					}
				}
			},1000);
		}

				if (!activity.bottomNav.isShown()){
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							activity.bottomNav.setVisibility(View.VISIBLE);
						}
					},500);
				}

	}

	public void checkLogin() {
		if (App.isLogin) {
			noticeLay.setVisibility(View.VISIBLE);
			itemClickenable(true);
			btnLeft.setText("提现");
			btnRight.setText("充值");
			initData();
		} else {
			noticeLay.setVisibility(View.GONE);
			noticeNum.setVisibility(View.GONE);
			itemClickenable(false);
			btnLeft.setText("登录");
			btnRight.setText("注册");
			clearData();
		}
	}

	private void clearData() {
		tvCountProfit.setText("0.00");
		tvKyjeNum.setText("0.00");
		tvHqbNum.setText("0.00");
		tvZzcNum.setText("0.00");
		tvDqNum.setText("0.00");
		leijishouyi.setText("0.00");
		expNumber.setText("0.00");
		wode_touzi.setText("");
		wdzc_line.setVisibility(View.GONE);
		eyes_btn.setImageResource(R.drawable.yanjing);
	}


	private void itemClickenable(boolean enable) {

		RelateKYJ.setEnabled(enable);
		RelateHQBwrapper.setEnabled(enable);
		RelateDQZC.setEnabled(enable);
		tvZzcNum.setEnabled(enable);
		RelateTYJ.setEnabled(enable);
		Relateleiji.setEnabled(enable);
		RelateLJSY.setEnabled(enable);
		leijishouyi.setEnabled(enable);
		eyes_btn.setEnabled(enable);

	}


	@OnClick(R.id.fragment_home_leftbtn)
	public void leftBtnClicked() {
		if (App.isLogin) {
			toCash();
		} else {
			toLogin();
		}
	}

	@OnClick(R.id.fragment_home_rightbtn)
	public void rightBtnClicked() {
		if (App.isLogin) {
			toPay();
		} else {
			toPhone();
		}
	}

	/**
	 * 去登录
	 */
	private void toLogin() {
		Intent intent = new Intent(context, LoginActivity.class);
		startActivity(intent);
	}

	/**
	 * 注册
	 */
	private void toPhone() {
		Intent intent = new Intent(context, PhoneActivity.class);
		startActivity(intent);
	}

	/**
	 * 去提现
	 */
	private void toCash() {
		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity == null) {
				//未绑卡
				showAddCard();
			} else {
				//已绑卡
				if (!userinfo.identity.tpwd) {
					//未设置交易密码，去设置支付密码
					showSetTpwd();
				} else {
					//已设置交易密码，去提现
					Intent intent = new Intent(context, CashActivity.class);
					startActivity(intent);
				}
			}
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			activity.finish();
		}
	}

	/**
	 * 去充值
	 */
	private void toPay() {
		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity == null) {
				//未绑卡，去绑卡
				showAddCard();
			} else {
				//已绑卡
				if (!userinfo.identity.tpwd) {
					//未设置交易密码，去设置交易密码
					showSetTpwd();
				} else {
					//已设置交易密码，去充值
					Intent intent = new Intent(context, PayActivity.class);
					startActivity(intent);
				}
			}
		} else {
			//用户数据未获取到，退出APP
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			activity.finish();
		}
	}

	@OnClick({
			R.id.fragment_home_jymx_wrapper,
			R.id.fragment_home_wdzc_wrapper,
			R.id.fragment_home_tyj_wrapper,
			R.id.fragment_home_zzc_num,
			R.id.fragment_home_hqb_wrapper,
			R.id.fragment_home_kyje_wrapper,
			R.id.fragment_empty,
			R.id.notice_layout,
			R.id.visible_eye,
			R.id.fragment_home_dqzc_wrapper,
			R.id.fragment_home_ljsy_wrapper
	})
	public void onClick(View view) {
		if (!App.isLogin) {
			ToastUtils.show(context, "请先登录");
			toLogin();
			return;
		}
		switch (view.getId()) {
			case R.id.fragment_home_jymx_wrapper:
				toDQList();
				break;
			case R.id.visible_eye:
				DataMgr.getInstance(context).saveEys(!isEyes);
				OffLineInfo mtempoffinfo = offdata.getInstance(getContext()).restoreOffLineInfo();
				if (mtempoffinfo != null) {
					isEyes = DataMgr.getInstance(context).restorEyes();
					if (isEyes){
						tvKyjeNum.setText("****");
						tvCountProfit.setText("****");
						tvDqNum.setText("****");
						tvHqbNum.setText("****");
						leijishouyi.setText("****");
						expNumber.setText("****");
						tvZzcNum.setText("****");
						eyes_btn.setImageResource(R.drawable.biyan);
					}else {
						tvKyjeNum.setText(mtempoffinfo.kymoney);
						tvCountProfit.setText(mtempoffinfo.yesterday_profit);
						tvHqbNum.setText(mtempoffinfo.longmoney);
						tvDqNum.setText(mtempoffinfo.dqmoney);
						expNumber.setText(mtempoffinfo.tyjin);
						leijishouyi.setText(mtempoffinfo.allprofit);
						tvZzcNum.setText(mtempoffinfo.allmoney);
						eyes_btn.setImageResource(R.drawable.yanjing);
					}

				}
				break;
			case R.id.notice_layout:
				DataMgr.getInstance(context).saveNtime(TimeUtils.getServerTime());
				noticeNum.setVisibility(View.GONE);
				toMessage();
				break;
			case R.id.fragment_home_wdzc_wrapper:
				toInvestHistory();
				break;
			case R.id.fragment_home_tyj_wrapper:
				toExp();
				break;
			case R.id.fragment_home_dqzc_wrapper:
				toDqzcHistory();
				break;
			case R.id.fragment_home_zzc_num:
				toAssetDetail();
				break;
			case R.id.fragment_home_hqb_wrapper:
				toHQMain();
				break;
			case R.id.fragment_home_kyje_wrapper:
				toActionList();
				break;
			case R.id.fragment_empty:
				toProfitList();
				break;
			case R.id.fragment_home_ljsy_wrapper:
				toYesterdayList();
				break;

			default:
				break;
		}
	}

	/**
	 * 去查看定期投资记录
	 */
	private void toDqzcHistory() {
		if (mProfit != null) {
			Intent intent = new Intent(context, ActivityQZC.class);
			intent.putExtra("dqmoney",String.valueOf(mDQMoney.dqmoney));
			startActivity(intent);
		}
	}

	private void toDQList() {
		Intent intent = new Intent(context, DQListActivity.class);
		startActivity(intent);
	}

	/**
	 * 去查看公告
	 */
	private void toMessage() {
		Intent intent = new Intent(context, MessageActivity.class);
		if(nlist != null){
			intent.putExtra("notice",false);
			intent.putExtra("nlist",(Serializable) nlist);
		}
		startActivity(intent);
	}

	/**
	 * 去查看累计收益
	 */
	private void toProfitList() {
		if (mProfit != null && mDQMoney != null) {
			Intent intent = new Intent(context, ProfitListActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("userprofit", mProfit);
			intent.putExtras(data);
			intent.putExtra("selected", 0);
			intent.putExtra("countprofit", mDQMoney.countprofit);
			startActivity(intent);
		}
	}

	/**
	 * 去查看昨日收益
	 */
	private void toYesterdayList() {
		if (mProfit != null && mDQMoney != null) {
			Intent intent = new Intent(context, YesterdayProfitActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("userprofit", mProfit);
			intent.putExtras(data);
			intent.putExtra("selected", 0);
			intent.putExtra("countprofit", yesterdayAll);
			startActivity(intent);
		}
	}

	/**
	 * 去快活宝
	 */
	private void toHQMain() {
		if (activity != null) {
			IPageSwitcher switcher = activity;
			switcher.switchToPage(MainActivity.PAGE_HUOQIBAO);
		}
	}

	/**
	 * 去查看投资记录
	 */
	private void toInvestHistory() {
		if (mProfit != null) {
			Intent intent = new Intent(context, InvestHistoryActivity.class);
			intent.putExtra("buynum", mProfit.buyNum);
			startActivity(intent);
		}
	}


	/**
	 * 我的体验金
	 */
	private void toExp() {
		Intent intent = new Intent(context, ExpNewActivity.class);
		if (mProfit!=null) {
			intent.putExtra("countprofit",mProfit.expmoneyCountprofit);
			if (exp != null && exp.size() > 0) {
				intent.putExtra("explist", (Serializable) exp);
				intent.putExtra("yestoday_exp", mProfit.expmoneyYesterdayprofit);
			}
		}
		startActivity(intent);
	}

	/**
	 * 去资产总额
	 */
	private void toAssetDetail() {
		Intent intent = new Intent(context, AssetNewActivity.class);
		if (mProfit != null && mDQMoney != null) {
			intent.putExtra("allmoney", allmoney);
			intent.putExtra("khbasset", mProfit.longmoney);
			intent.putExtra("klbasset", mProfit.klmoney);
			intent.putExtra("countmoney", mDQMoney.countmoney);
			intent.putExtra("countprofit", mDQMoney.countprofit);
			intent.putExtra("expmoneycurrentprofit",
					mProfit.expmoneyCurrentProfit);
			Bundle bundle = new Bundle();
			bundle.putSerializable("userprofit", mProfit);
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, AccAssetActivityTag);
	}

	/**
	 * 去收支明细
	 */
	private void toActionList() {
		Intent intent = new Intent(context, ActionListActivity.class);
		startActivity(intent);
	}


	public void showPL(){

		DataMgr.getInstance(context).saveShow(true);
		MobclickAgent.onEvent(context,"tk");
		String content = null;
		if (DataMgr.getInstance(context).saveAllow()){
			content  = "不再提醒";
		}else {
			content = "残忍拒绝";
		}

		PromptUtils.showEvaluateDialog(activity, context,content, new PromptUtils.OnDialogClickListener2() {
			@Override
			public void onLeftClick(Dialog dialog) {
				if (DataMgr.getInstance(context).saveAllow()){
					DataMgr.getInstance(context).saveisAllow(true);
					MobclickAgent.onEvent(context,"bztx");
				}else {
					DataMgr.getInstance(context).saveAllow(true);
					MobclickAgent.onEvent(context,"crjj");
				}
			}

			@Override
			public void onRightClick(Dialog dialog) {
				MobclickAgent.onEvent(context,"gghp");
				MarketUtils.launchAppDetail("cn.app.yimirong", context);
			}
		});

	}



	/**
	 * 提示添加银行卡
	 */
	private void showAddCard() {
		String message = "<font color='#000000'>您未绑定银行卡，请先绑定银行卡</font>";
		PromptUtils.showDialog4(activity, context, "", message, "取消",
				"去绑卡", new PromptUtils.OnDialogClickListener2() {

					@Override
					public void onRightClick(Dialog dialog) {
						Intent intent = new Intent(context, BindActivity.class);
						startActivity(intent);
					}

					@Override
					public void onLeftClick(Dialog dialog) {
					}
				}, false);
	}

	/**
	 * 显示设置交易密码
	 */
	private void showSetTpwd() {
		PromptUtils.showDialog1(activity,
				context,
				"提示",
				"未设置支付密码，请设置支付密码",
				"确定",
				new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						Intent intent = new Intent(context, SetPayPassActivity.class);
						startActivity(intent);
					}
				}, true);
	}

	@Override
	public void onRefresh() {
		if (App.isLogin) {
			loadTimes = 0;
			loadProg = 0;
			loadBalance();
			loadHQMoney();
			loadProfit();
			loadDQMoney();
			loadNEWExpMoney();
		} else {
			refresher.setRefreshing(false);
		}
	}

	private void updateLoadProg() {
		loadProg++;
		if (loadProg >= 4) {
			loadProg = 0;
			if (refresher != null) {
				refresher.setRefreshing(false);
			}
		}
	}

	public void getNotice(){
		if (amodel != null){
			amodel.getUserNotice(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof List){
						nlist = (List<Notice>) t;
						if (!nlist.isEmpty()){
							setNoticeNum();
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
				}
			});
		}
	}

	public void setNoticeNum(){

		long ntime = DataMgr.getInstance(context).restorNtime();

		assert nlist != null;
		if (ntime == 0) {
			if (noticeNum != null) {
				if (nlist.size() > 0) {
					noticeNum.setVisibility(View.VISIBLE);
					int len = nlist.size();
					if (len > 9) {
						len = 9;
					}
					new_notice.setText(String.valueOf(len));
				} else {
					noticeNum.setVisibility(View.GONE);
				}
			}
		}else {
			int num = 0;
			for (int i = 0;i < nlist.size();i++){
				if (Long.parseLong(nlist.get(i).ctime) > ntime){
					num += 1;
				}
			}
			if (noticeNum != null) {
				if (num > 0) {
					noticeNum.setVisibility(View.VISIBLE);
					if (num > 9) {
						num = 9;
					}
					new_notice.setText(String.valueOf(num));
				} else {
					noticeNum.setVisibility(View.GONE);
				}
			}
		}
	}

	/**
	 * 获取余额
	 */
	private void loadBalance() {
		activity.showLoading("玩命加载中");
		if (amodel != null) {
			amodel.getUserBalance(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof String) {
						String str = (String) t;
						String bstr="0.00";
						double b=0.00;
						if (!TextUtils.isEmpty(str)) {
							b = SystemUtils.getDouble(str);
							bstr = SystemUtils.getDoubleStr(b);
						}
						if (App.userinfo != null && bstr!=null && !bstr.equals("")) {
							App.userinfo.balance = bstr;
						}
						balance = b;

						if (isEyes){
							tvKyjeNum.setText("****");
						}else {
							tvKyjeNum.setText(bstr);
						}

						//可用余额
						offinfo.kymoney = bstr;
						updateLoadProg();
						myHandler.sendEmptyMessage(loadTimes);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					updateLoadProg();
				}
			});
		}
	}


	/**
	 * 加载快活宝数据
	 */
	private void loadHQMoney() {
		if (amodel != null) {
			amodel.getUserHQMoney(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (refresher != null) {
						refresher.setRefreshing(false);
					}
					if (t != null && t instanceof UserHQMoney) {
						mHQMoney = (UserHQMoney) t;
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					if (refresher != null) {
						refresher.setRefreshing(false);
					}
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 获取用户收益数据
	 */
	private void loadProfit() {
		if (amodel != null) {
			amodel.getUserProfitInfo(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof UserProfit) {
						mProfit = (UserProfit) t;
						DataMgr.getInstance(context).saveUserProfitInfo(mProfit);
						EventBus.getDefault().post(new ProfitEvent());
						hqmoney = mProfit.longmoney;
						expprofit = mProfit.expmoneyCurrentProfit;
						double countProfit = mProfit.countProfit
								+ mProfit.longmoneyCountprofit
								+ mProfit.expmoneyCountprofit;
						double hq = 0;
						if (mHQMoney != null) {
							hq = Double.parseDouble(mHQMoney.yesterday);
						}
						yesterdayAll = mProfit.yesterdayProfit + hq + mProfit.expmoneyYesterdayprofit;

						if (isEyes){
							tvHqbNum.setText("****");
							leijishouyi.setText("****");
							tvCountProfit.setText("****");
						}else {
							tvCountProfit.setText(SystemUtils.getDoubleStr(yesterdayAll));
							tvHqbNum.setText(SystemUtils.getDoubleStr(mProfit.longmoney) == null ? "0.00" : SystemUtils.getDoubleStr(mProfit.longmoney));
							leijishouyi.setText(SystemUtils.getDoubleStr(countProfit) == null ? "0.00" : SystemUtils.getDoubleStr(countProfit));
						}
						//累计收益
						offinfo.allprofit = SystemUtils.getDoubleStr(countProfit);
						//活期资产
						offinfo.longmoney = SystemUtils.getDoubleStr(mProfit.longmoney);
						//昨日总收益
						offinfo.yesterday_profit = SystemUtils.getDoubleStr(yesterdayAll);
						//投资笔数
						offinfo.buynum = mProfit.buyNum + "";
						offinfo.invite = mProfit.invite + "";

						if (mProfit.buyNum > 0) {
							wdzc_line.setVisibility(View.VISIBLE);
							wode_touzi.setText(String.valueOf(mProfit.buyNum));
						} else {
							wdzc_line.setVisibility(View.GONE);
							wode_touzi.setText("");
						}
						updateLoadProg();
						myHandler.sendEmptyMessage(loadTimes);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					updateLoadProg();
				}
			});
		}
	}

	/**
	 * 获取用户定期总资产
	 */
	private void loadDQMoney() {
		if (amodel != null) {
			amodel.getUserPeridic(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, final T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof UserDQMoney) {
						mDQMoney = (UserDQMoney) t;
						dqmoney = mDQMoney.dqmoney;
						if (isEyes) {
							tvDqNum.setText("****");
						}else {
							tvDqNum.setText(SystemUtils.getDoubleStr(dqmoney));
						}
						//定期资产
						offinfo.dqmoney = SystemUtils.getDoubleStr(dqmoney);
						updateLoadProg();
						myHandler.sendEmptyMessage(loadTimes);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					updateLoadProg();
				}
			});
		}
	}

	/**
	 * 获取新体验金余额
	 */
	private void loadNEWExpMoney() {
		if (amodel != null) {
			amodel.newExpMoney(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					double expmoney=0.0;
					int num = 0;
					if (t != null && t instanceof List) {
						exp = (List<NewExp>)t;
						if (!exp.isEmpty()) {
							for (int i = 0; i < exp.size(); i++) {
								if (exp.get(i).status.equals("0")) {
									expmoney += Double.parseDouble(exp.get(i).money);
									num++;
								}
							}
							if (isEyes){
								expNumber.setText("****");
							}else {
								if (expmoney > 0) {
									DecimalFormat df = new DecimalFormat("#.00");
									String format = df.format(expmoney);
									expNumber.setText(format);
								}
							}
						}
					}
					//体验金
					offinfo.tyjin = SystemUtils.getDoubleStr(expmoney);
					offdata.getInstance(getContext()).savOffLineInfo(offinfo);
					activity.closeLoading();
				}

				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					updateLoadProg();
					activity.closeLoading();
				}
			});
		}
	}

	/**
	 * 充值事件
	 *
	 * @param event
	 */
	public void onEventMainThread(PayEvent event) {
		if (isViewInited && App.isLogin) {
			loadBalance();
		}
	}

	/**
	 * 提现事件
	 *
	 * @param event
	 */
	public void onEventMainThread(CashEvent event) {
		if (isViewInited && App.isLogin) {
			loadBalance();
		}
	}

	/**
	 * 登录或注销事件
	 *
	 * @param event
	 */
	public void onEventMainThread(LoginChangeEvent event) {
		if (isViewInited) {
			if (App.isLogin) {
				loadTimes = 0;
				initData();
			} else {
				clearData();
			}
		}
	}

	/**
	 * 转出事件
	 *
	 * @param event
	 */
	public void onEventMainThread(ZChuEvent event) {
		if (isViewInited && App.isLogin) {
			loadBalance();
			loadHQMoney();
			loadProfit();
		}
	}

	public void onEventMainThread(BuySuccessEvent event) {
		if (isViewInited && App.isLogin) {
			loadBalance();
			loadHQMoney();
			loadProfit();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(null);
	}
}
