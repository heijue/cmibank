package cn.app.yimirong.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.OnClick;
import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.activity.BindActivity;
import cn.app.yimirong.activity.BuyActivity;
import cn.app.yimirong.activity.BuyRecordActivity;
import cn.app.yimirong.activity.HQLogsActivity;
import cn.app.yimirong.activity.HQProfitActivity;
import cn.app.yimirong.activity.HQZCActivity;
import cn.app.yimirong.activity.LoginActivity;
import cn.app.yimirong.activity.ProductDetailActivity;
import cn.app.yimirong.activity.SetPayPassActivity;
import cn.app.yimirong.base.BaseFragment;
import cn.app.yimirong.event.custom.BaseEvent;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.ProductMore;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.UserHQMoney;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;


public class HQBFragment extends BaseFragment {

	public static final int HQActivityTag = 98;

	private int days = 30;


	private void itemClickenable(boolean enable) {
		zzc.setEnabled(enable);
		zrsy.setEnabled(enable);
		yysy.setEnabled(enable);
		yzsy.setEnabled(enable);
		ljsy.setEnabled(enable);
	}


	private double income = 0.00;

	boolean gh = true;
	private UserHQMoney mHQMoney;
	private BaseProduct product;

	private TextView BaoZhang,tvAQProfit,tvYysyNum,tvYzsyNum
			,tvLjsyNum,tvZzcNum,tvZrsyNum,tvYqnhNum,tvTitle;

	private LinearLayout zrsy;

	private RelativeLayout  ljsy,yzsy,yysy,zzc;

	private SeekBar sbProfit;

	private View rootView;

	private void init(View rootView){

		sbProfit = (SeekBar) rootView.findViewById(R.id.fragment_anquan_seekbar);
		BaoZhang = (TextView) rootView.findViewById(R.id.baozhang_text);
		tvAQProfit = (TextView) rootView.findViewById(R.id.layout_anquan_profit);
		tvYysyNum = (TextView) rootView.findViewById(R.id.fragment_huoqibao_yysy_num);
		tvYzsyNum = (TextView) rootView.findViewById(R.id.fragment_huoqibao_yzsy_num);
		tvLjsyNum = (TextView) rootView.findViewById(R.id.fragment_huoqibao_ljsy_num);
		tvZzcNum = (TextView) rootView.findViewById(R.id.fragment_huoqibao_zzc_num);
		tvZrsyNum = (TextView) rootView.findViewById(R.id.fragment_huoqibao_zrsy_num);
		tvYqnhNum = (TextView) rootView.findViewById(R.id.fragment_huoqibao_yqnh_num);
		tvTitle = (TextView) rootView.findViewById(R.id.main_title_bar_title_text);

		ljsy = (RelativeLayout) rootView.findViewById(R.id.fragment_huoqibao_ljsy_wrapper);
		yzsy = (RelativeLayout) rootView.findViewById(R.id.fragment_huoqibao_yzsy_wrapper);
		yysy = (RelativeLayout) rootView.findViewById(R.id.fragment_huoqibao_yysy_wrapper);
		zrsy = (LinearLayout) rootView.findViewById(R.id.fragment_huoqibao_zrsy_wrapper);
		zzc = (RelativeLayout) rootView.findViewById(R.id.fragment_huoqibao_zzc_wrapper);
	}

	@Override
	public View loadView(LayoutInflater inflater, ViewGroup container) {
		if (rootView == null){
			rootView = inflater.inflate(R.layout.fragment_huoqibao, container, false);
			init(rootView);
		}
		ViewGroup par = (ViewGroup)rootView.getParent();
		if (par != null){
			par.removeView(rootView);
		}
		return rootView;
	}

	private void clearData() {
		tvYzsyNum.setText("0.00");
		tvYysyNum.setText("0.00");
		tvZrsyNum.setText("0.00");
		tvLjsyNum.setText("0.00");
		tvZzcNum.setText("0.00");
	}


	@Override
	public void initView() {
		tvTitle.setText("易米宝");
		setSeekBar();
	}

	private void setSeekBar() {
		int progress = 18;
		sbProfit.setMax(progress);
		sbProfit.setProgress(0);
		updateProfitInfo(0);
		sbProfit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				int progress = i;
				days = 30;
				sbProfit.setProgress(progress);
				updateProfitInfo(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	@Override
	public void onResume() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				updateView();
				updateProfitInfo(0);
			}
		}, 300);
		super.onResume();
	}


	private void updateProfitInfo(int progress) {

		double p = 0;

		double m = 5000.00;

		double pr = (double) progress;

		for (int i = 0; i < days; i++) {

			p += income * (10000 + pr * m + p) * 1 / 365.0f / 100.0f;


		}

		String profit = SystemUtils.getDoubleStr(p);
		String str = "投资<font color='#ff4747'>" + (10000 + pr * m) + "</font>，"
				+ "30天"
				+ "最多可赚取"
				+ "<font color='#ff4747'>" + profit + "元";
		tvAQProfit.setText(Html.fromHtml(str));
	}

	@Override
	public void initData() {
		if (!activity.bottomNav.isShown()){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					activity.bottomNav.setVisibility(View.VISIBLE);
				}
			},500);
		}
		loadHQProducts();
		if (App.isLogin) {
			loadHQMoney();
			itemClickenable(true);
		} else {
			itemClickenable(false);
			clearData();
		}
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite!=null && shareInvite.Third_party_payment!=null && !shareInvite.Third_party_payment.equals("")){
			BaoZhang.setText(shareInvite.Third_party_payment);
		}
	}

	@Override
	public void onVisible() {
		super.onVisible();
		initData();
	}

	@Override
	public void onInVisible() {
		super.onInVisible();
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
					if (t != null && t instanceof UserHQMoney) {
						mHQMoney = (UserHQMoney) t;
						mCache.put("khbmoney", mHQMoney);
						updateView();
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 加载快活宝列表
	 */
	private void loadHQProducts() {
		if (pmodel != null) {
			pmodel.getKHProducts(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof ProductMore) {
						ProductMore pm = (ProductMore) t;
						income = SystemUtils.getDouble(pm.defaultIncome);
						if (pm.product != null && !pm.product.isEmpty()) {
							product = pm.product.get(0);
							income = SystemUtils.getDouble(product.income);

						}else {
							product=null;
						}
						tvYqnhNum.setText(SystemUtils.getDoubleStr(income)+"%");
						updateProfitInfo(0);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg, Toast.LENGTH_SHORT);
				}
			});
		}
	}

	/**
	 * 更新界面
	 */
	private void updateView() {
		if (App.isLogin) {
			if (mHQMoney != null) {
				tvZrsyNum.setText(SystemUtils
						.getDoubleStr(mHQMoney.yesterday));
				tvZzcNum.setText(SystemUtils
						.getDoubleStr(mHQMoney.longmoney));
				tvLjsyNum.setText(SystemUtils
						.getDoubleStr(mHQMoney.count_profit));
				tvYzsyNum
						.setText(SystemUtils.getDoubleStr(mHQMoney.day_7));
				tvYysyNum.setText(SystemUtils
						.getDoubleStr(mHQMoney.day_30));
			}
		}
	}


	@OnClick({R.id.zhuanchu_text,
			R.id.zhuanru_text,
			R.id.fragment_huoqibao_zrsy_wrapper,
			R.id.fragment_huoqibao_zzc_wrapper,
			R.id.fragment_huoqibao_ljsy_wrapper,
			R.id.fragment_huoqibao_yzsy_wrapper,
			R.id.fragment_huoqibao_yysy_wrapper})
	public void onClick(View view) {
		if (!App.isLogin) {
			ToastUtils.show(context, "请先登录");
			Intent intent = new Intent(context, LoginActivity.class);
			startActivity(intent);
			return;
		}
		switch (view.getId()) {
			case R.id.zhuanchu_text:
				toZChu();
				break;
			case R.id.zhuanru_text:
				toBuy();
				break;
			case R.id.fragment_huoqibao_zrsy_wrapper:
				toProfitList(0);
				break;
			case R.id.fragment_huoqibao_zzc_wrapper:
				toBuyList();
				break;
			case R.id.fragment_huoqibao_ljsy_wrapper:
				toProfitList(0);
				break;
			case R.id.fragment_huoqibao_yzsy_wrapper:
				toProfitList(2);
				break;
			case R.id.fragment_huoqibao_yysy_wrapper:
				toProfitList(3);
				break;
		}
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
	 * 转出
	 */
	private void toZChu() {
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
					if (mHQMoney != null) {
						double asset = SystemUtils.getDouble(mHQMoney.longmoney);
						if (asset > 0d) {
							Intent intent = new Intent(context, HQZCActivity.class);
							Bundle data = new Bundle();
							data.putSerializable("khbmoney", mHQMoney);
							intent.putExtras(data);
							startActivity(intent);
						} else {
							ToastUtils.show(context, "易米宝资产为0");
						}
					}

				}
			}
		}
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

	/**
	 * 购买
	 */
	private void toBuy() {
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
					// 判断是否有活期产品
					if (product != null) {
						if (product.isYuGao()) {
							// 去预告
							Intent intent = new Intent(context, ProductDetailActivity.class);
							Bundle data = new Bundle();
							data.putSerializable("product", product);
							data.putInt("productType", product.ptype);
							data.putInt("productState", BaseProduct.STATE_SELLING);
							data.putString("title", product.pname);
							data.putString("pid", product.pid);
							data.putBoolean("canbuy", true);
							data.putBoolean("issellout", false);
							data.putBoolean("isYuGao", product.isYuGao());
							intent.putExtras(data);
							startActivity(intent);


						} else {
							// 去购买
							if (product != null) {
							/*	boolean canBuy = true;
								boolean isSellOut = product.state != BaseProduct.STATE_SELLING;
								if (isSellOut) {
									canBuy = false;
								}*/
								/*Intent intent = new Intent(activity, ProductDetailActivity.class);
								Bundle data = new Bundle();
								data.putSerializable("product", product);
								data.putInt("productType", product.ptype);
								data.putInt("productState", product.state);
								data.putString("title", product.pname);
								data.putString("pid", product.pid);
								data.putBoolean("canbuy", canBuy);
								data.putBoolean("issellout", isSellOut);
								data.putBoolean("isyugao", product.isYuGao());
								intent.putExtras(data);
								startActivity(intent);*/
								Intent intent = new Intent(context, BuyActivity.class);
								Bundle data = new Bundle();
								data.putSerializable("product", product);
								data.putString("pid", product.pid);
								data.putInt("productType", product.ptype);
								intent.putExtras(data);
								startActivity(intent);
							}
						}
					} else {
						ToastUtils.show(context, "暂无可购产品");
					}

				}
			}
		}


	}

	/**
	 * 去购买转出列表
	 */
	private void toBuyList() {
		Intent intent = new Intent(context, HQLogsActivity.class);
		intent.putExtra("longmoney",mHQMoney.longmoney);
		startActivity(intent);
	}

	/**
	 * 去快活宝累计收益
	 */
	private void toProfitList(int current) {
		if (mHQMoney != null) {
			Intent intent = new Intent(context, HQProfitActivity.class);
			Bundle data = new Bundle();
			data.putInt("currentSelected", current);
			data.putSerializable("khbmoney", mHQMoney);
			intent.putExtras(data);
			startActivity(intent);
		}
	}

	public void onEventMainThread(BaseEvent event) {
		initData();
		updateView();
	}

}
