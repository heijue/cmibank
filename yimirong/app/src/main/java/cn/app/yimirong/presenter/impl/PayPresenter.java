package cn.app.yimirong.presenter.impl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baofoo.sdk.vip.BaofooPayActivity;
import com.fuiou.mobile.FyPay;
import com.fuiou.mobile.FyPayCallBack;
import com.fuiou.mobile.bean.MchantMsgBean;
import com.fuiou.mobile.util.AppConfig;
import com.fuiou.mobile.util.EncryptUtils;
import com.fuiou.mobile.util.FyLogUtil;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.activity.BuyActivity;
import cn.app.yimirong.activity.SmsCodeActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.PassEvent;
import cn.app.yimirong.event.custom.PassEvent1;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.AModel;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.FuYouInfo;
import cn.app.yimirong.model.bean.PayResult;
import cn.app.yimirong.model.bean.VersionData;
import cn.app.yimirong.model.db.dao.BankDao;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.AbstractPresenter;
import cn.app.yimirong.presenter.IViewController;
import cn.app.yimirong.presenter.MyFyPayCallBack;
import cn.app.yimirong.presenter.pay.BaseHelper;
import cn.app.yimirong.presenter.pay.Constants;
import cn.app.yimirong.presenter.pay.FyHttpClient;
import cn.app.yimirong.presenter.pay.FyHttpInterface;
import cn.app.yimirong.presenter.pay.FyHttpResponse;
import cn.app.yimirong.presenter.pay.FyXmlNodeData;
import cn.app.yimirong.presenter.pay.MobileSecurePayer;
import cn.app.yimirong.utils.PhoneUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class PayPresenter extends AbstractPresenter {

	private static final String TAG = "PayPresenter";

	private static final int LLPAY_CALLBACK = 100;

	private static final int QUERY_YEEPAY_RESULT = 200;

	private static final int QUERY_LLPAY_RESULT = 201;

	private static final int QUERY_JYTPAY_RESULT = 202;

	public final static int BAOFOO_REQUEST_CODE = 300;

	public final static int QUERY_FUYOU_RESULT = 203;

	private int elapseTime = 60;

	private ScheduledExecutorService scheduledExecutor;

	private Logger logger;

	private AModel amodel;

	private PayHandler handler;

	private List<String> platList;

	private double money;

	private Dialog dialog;

	private Display display;

	private Context context;

	private TextView send;

	private String passmd5;

	private VersionData vdata;

	private String currentPay;

	private String tradeNo;

	String order;

	private int single;

	private Bank banked;

	private String cid;

	private String ptid;


	public String getCurrentPay() {


		return currentPay;
	}

	/**
	 * 支付回调
	 */
	private static class PayHandler extends Handler {
		private WeakReference<PayPresenter> weakReference;

		public PayHandler(PayPresenter presenter) {
			weakReference = new WeakReference<>(presenter);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			PayPresenter presenter = weakReference.get();
			switch (msg.what) {
				case LLPAY_CALLBACK: {
					// 连连支付回调
					if (presenter != null) {
						String result = (String) msg.obj;
						presenter.handleLLPayResult(result);
					}
					break;
				}
				case QUERY_YEEPAY_RESULT: {
					// 易宝支付结果查询
					if (presenter != null) {
						Bundle data = msg.getData();
						String orderid = data.getString("orderid");
						presenter.queryResult(orderid);
					}
					break;
				}
				case QUERY_LLPAY_RESULT: {
					// 连连支付结果查询
					if (presenter != null) {
						Bundle data = msg.getData();
						String dtOrder = data.getString("dt_order");
						String noOrder = data.getString("no_order");
						presenter.queryResult(dtOrder, noOrder);
					}
					break;
				}
				case QUERY_JYTPAY_RESULT: {
					//金运通支付查询结果
					if (presenter != null) {
						Bundle data = msg.getData();
						String orderid = data.getString("orderid");
						presenter.queryResult(orderid);
					}
					break;
				}
				case QUERY_FUYOU_RESULT: {
					//富友支付查询结果
					Log.i(TAG, "查询结果发送");
					if (presenter != null) {
						Bundle data = msg.getData();
						String orderid = data.getString("orderid");
						presenter.queryfResult(orderid);
						Log.i(TAG, "handleMessage: " + orderid + "  " + QUERY_FUYOU_RESULT);
					}
					break;
				}
				default:
					break;
			}
		}
	}

	@Override
	public void bind(IViewController view) {
		super.bind(view);
		if (App.userinfo != null
				&& App.userinfo.identity != null) {
			String bankid = App.userinfo.identity.bankid;
			platList = BankDao.getInstance(appContext).getPayList(bankid);
		}
		this.logger = Logger.getInstance();
		this.handler = new PayHandler(this);
		vdata = DataMgr.getInstance(appContext).restoreVersionData();
		amodel = AModel.getInstance(appContext);
	}


	public List<String> getPlatList() {

		return platList;
	}

	@Override
	public void unBind() {
		super.unBind();
		this.amodel = null;
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}

	private void showLoading() {
		if (view != null) {
			view.showLoading("正在处理...");
		}
	}

	private void closeLoading() {
		if (view != null) {
			view.closeLoading();
		}
	}

	public String getTradeNo() {
		return tradeNo;
	}


	public void pay(Display display){
		this.display = display;
	}


	/**
	 * 支付
	 *
	 * @param bank    支付行数据
	 * @param money   支付金额
	 * @param passmd5 支付密码MD5
	 */
	public void
	pay(Bank bank, double money, String passmd5, String cid, String ptid,MyFyPayCallBack myFyPayCallBack, Context context) {
		this.money = money;
		this.passmd5 = passmd5;
		this.context = context;
		this.banked = bank;
		this.ptid = ptid;
		this.cid = cid;
		if (bank != null) {
			//小额走易宝
//            if (vdata != null && money < vdata.yee_amount_limit) {
//                if (platList != null && platList.contains("yeepay")) {
//                    bank.plat = "yeepay";
//                }
//            }
			if (platList != null && platList.size() > 0 && !platList.contains(bank.plat)) {
				currentPay = platList.get(0);

//            single=BankDao.getInstance(context).getPlatSingle(presenter.getPlatList().get(0),bank.code);
			} else {
				currentPay = bank.plat;
			}

			pay1(currentPay, this.money, this.passmd5, this.cid, this.ptid,myFyPayCallBack);
		}
	}

	//支付失败后，重新选择渠道

	private void pay() {
//        String changePlat=platList.get(0);
//        single=BankDao.getInstance(context).getPlatSingle(changePlat,banked.code);
//        if (money>single){
//                Toast.makeText(context,"替换通道限额为"+single,Toast.LENGTH_SHORT).show();
//                if (platList != null) {
//                    if (platList.contains(changePlat)) {
//                        platList.remove(changePlat);
//                    }
//                }
//                showPayError("替换通道限额为"+single);
//            return;
//        }
		Log.i("xiao", "pay: " + platList.toString());
//        if (platList != null) {
		if (platList.size() > 0) {
			// pay1(platList.get(0), this.money, this.passmd5);
			Log.i("xiao", "去掉当前通道 ");
			nextplat(currentPay);
			if (platList.size() == 0) {
				Log.i("xiao", "获取通道 ");
				String bankid = App.userinfo.identity.bankid;
				platList = BankDao.getInstance(appContext).getPayList(bankid);
//                 nextplat(platList.get(0));
			}
		}
//        }
	}


	public void nextplat(String plat) {

		if (platList != null) {
			if (platList.contains(plat)) {
				platList.remove(plat);

			}
		}


	}

	/**
	 * 支付
	 *
	 * @param plat    支付通道
	 * @param money
	 * @param passmd5
	 */
	public void pay1(String plat, double money, String passmd5, String cid, String ptid,final MyFyPayCallBack myFyPayCallBack) {
//         去除当前使用的plat
//        if (platList != null) {
//            if (platList.contains(plat)) {
//                platList.remove(plat);
//            }
//        }
//        currentPay = plat;
/*		if ("llpay".equals(plat)) {
			// 连连支付
			jytPayPASS(money, passmd5, cid, ptid);
		} else if ("baofoo".equals(plat)) {
			// 宝付支付
			baofoopay(money, passmd5, cid, ptid);
		} else if ("yeepay".equals(plat)) {
			//易宝支付
			jytPayPASS(money, passmd5, cid, ptid);
		} else if ("fuiou".equals(plat)) {
			//富友支付
			fuyou(money, passmd5, cid, ptid);
		} else if ("jytpay".equals(plat)) {
			//金运通支付

		} else {
			//不支持的支付类型
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
		}*/
//		jytPayPASS(money, passmd5, cid, ptid);
		fuyou(money, passmd5, cid, ptid,myFyPayCallBack);
	}

	public void jytPayPASS(final double money, final String passmd5, final String cid, final String ptid){
		if (amodel != null){
			showLoading();
			amodel.verifyJYTpayPassword(passmd5, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					jytpayDialog(money, passmd5, cid, ptid);
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					PassEvent p = new PassEvent();
					p.msg = msg;
					EventBus.getDefault().post(p);
					closeLoading();
				}
			});
		}
	}

	public void jytpayDialog(final double money, final String passmd5, final String cid, final String ptid){
		dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.jyt_pay_diaog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);

		ImageButton btnClose = (ImageButton) dialog
				.findViewById(R.id.dialog_jyt_close);

		TextView jine = (TextView) dialog.findViewById(R.id.detail_pop_name);

		TextView bank = (TextView) dialog.findViewById(R.id.detail_pop_account);

		TextView bindPhone = (TextView) dialog.findViewById(R.id.detail_pop_paytype);

		final TextView payOK = (TextView) dialog.findViewById(R.id.jyt_buy_ok_btn);

		send = (TextView) dialog.findViewById(R.id.activity_bind_send);

		final EditText codeView = (EditText) dialog.findViewById(R.id.activity_bind_verify);

		payOK.setEnabled(false);

		codeView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length()==4){
					payOK.setBackgroundResource(R.drawable.jyt_ok_btn);
					payOK.setEnabled(true);
				}else {
					payOK.setBackgroundResource(R.drawable.jyt_ok_btn_false);
					payOK.setEnabled(false);
				}

			}
		});

		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeCalculator();
				showPayError("取消支付");
			}
		});

		jine.setText(String.valueOf(money));

		bank.setText(App.userinfo.identity.bankname + "(尾号" + App.userinfo.identity.cardno + ")");
		//暂时
		bindPhone.setText(StringUtils.getSecretAccount(App.account));

		payOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String code = codeView.getText().toString().trim();
				jytpay(money, passmd5, cid, ptid,code);
			}
		});

		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCode(String.valueOf(money));
			}
		});

		showCalculator();

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		dialog.getWindow().setAttributes(lp);

//
	}


	/**
	 * 显示计算器
	 */
	public void showCalculator() {
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	/**
	 * 关闭计算器
	 */
	public void closeCalculator() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void sendCode(String money){
		if (amodel != null){
			showLoading();
			amodel.verifyJYTcode(money, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					// 验证码已发送
					ToastUtils.show(context, "验证码已发送");
					// 开启倒计时
					startTimer();
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg);
					closeLoading();
				}
			});
		}
	}

	/**
	 * 启动计时任务
	 */
	private void startTimer() {
		elapseTime = 59;
		send.setClickable(false);
		send.setTextColor(Color.parseColor("#303030"));
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (elapseTime == 0) {
							send.setTextColor(Color.parseColor("#0c82f0"));
							send.setText("发送验证码");
							send.setClickable(true);
							scheduledExecutor.shutdown();
						} else {
							send.setText("重新发送"+elapseTime + "秒");
						}
						elapseTime--;
					}
				});
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	/**
	 * 易宝支付
	 */
	private void yeePay(final double money, final String passmd5) {
		if (amodel != null) {
			showLoading();
			amodel.yeeBindCheck(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (isBinded) {
						if (t != null && t instanceof Boolean) {
							boolean isBind = (Boolean) t;
							if (isBind) {
								//已绑定易宝支付，直接支付
								yeePay2(money, passmd5);
							} else {
								//未绑定易宝支付
								//第一步，注册易宝支付
								yeeRegist();
							}
						} else {
							ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						ToastUtils.show(context, msg);
					}
				}
			});
		}
	}

	/**
	 * 易宝注册第一步
	 */
	private void yeeRegist() {
		if (amodel != null) {
			showLoading();
			amodel.yeeRegist(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (isBinded) {
						//易宝第二步，短信验证码确认
						if (t != null && t instanceof String) {
							String requestid = (String) t;
							//跳转到输入验证码的界面
							toYeeVerify(requestid);
						} else {
							showPayError(BaseModel.SYSTEM_ERROR);
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						ToastUtils.show(context, msg);
						showPayError(msg);
					}
				}
			});
		}
	}

	/**
	 * 易宝注册第二步
	 *
	 * @param requestid
	 */
	private void toYeeVerify(String requestid) {
		if (activity != null) {
			Intent intent = new Intent(context, SmsCodeActivity.class);
			intent.putExtra("title", "易宝支付");
			intent.putExtra("action", SmsCodeActivity.ACTION_REGIST_YEE_CHECK);
			intent.putExtra("requestid", requestid);
			activity.startActivityForResult(intent, SmsCodeActivity.ACTION_REGIST_YEE_CHECK);
		}
	}

	/**
	 * 连连支付
	 *
	 * @param money
	 * @param passmd5
	 */
	private void llpay(final double money, final String passmd5) {
		if (amodel != null) {
			showLoading();
			amodel.llpayGetPaySign(money, passmd5, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (isBinded) {
						if (t != null && t instanceof String) {
							// 保存正确的密码
							String payorder = (String) t;
							PayPresenter.this.money = money;
							PayPresenter.this.passmd5 = passmd5;
							MobileSecurePayer payer = new MobileSecurePayer();
							payer.pay(payorder, handler, LLPAY_CALLBACK, activity, false);
						} else {
							ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						ToastUtils.show(context, msg);
					}
				}
			});
		}
	}

	/**
	 * 宝付支付
	 *
	 * @param money
	 * @param passmd5
	 */
	private void baofoopay(final double money, final String passmd5, final String cid, final String ptid) {
		if (amodel != null) {
			showLoading();
			amodel.baofooPayReady(money, passmd5, cid, ptid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (isBinded) {
						if (t != null && t instanceof JSONObject) {
							JSONObject jsonData = (JSONObject) t;
							String retCode = jsonData.optString("retCode", null);
							String retMsg = jsonData.optString("retMsg", null);
							if (!"0000".equals(retCode)) {
								ToastUtils.show(context, retMsg);
								return;
							}
							String tradeNo = jsonData.optString("tradeNo", null);
							if (tradeNo != null) {
								// 保存正确的密码
								PayPresenter.this.tradeNo = tradeNo;
								PayPresenter.this.money = money;
								PayPresenter.this.passmd5 = passmd5;
								if (activity != null) {
									Intent intent = new Intent(context,
											BaofooPayActivity.class);
									intent.putExtra(BaofooPayActivity.PAY_TOKEN,
											tradeNo);
									intent.putExtra(BaofooPayActivity.PAY_BUSINESS,
											true);
									activity.startActivityForResult(intent, BAOFOO_REQUEST_CODE);
								}
							}
						} else {
							ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						if ("4021".equals(errorCode)) {
							// 密码错误
							PassEvent p = new PassEvent();
							p.msg = msg;
							EventBus.getDefault().post(p);
						} else {
							PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
								@Override
								public void onClick(Dialog dialog) {
									dialog.dismiss();
								}
							});
						}
					}
				}
			});
		}
	}

	/**
	 * 富友支付
	 *
	 * @param money
	 * @param passmd5
	 */
	private String mPassmd5;
	private double mMoney;
	private String mCid;
	private String mPtid;
	private void fuyou(final double money, final String passmd5, final String cid, final String ptid, final MyFyPayCallBack myFyPayCallBack) {
		this.mPassmd5 =passmd5;
		this.mMoney = money;
		this.mCid = cid;
		this.mPtid = ptid;
		final FuYouInfo info = new FuYouInfo();
		if (amodel != null) {
			showLoading();
			double s = money * 100;
			final int payMoney = (int) s;
			amodel.fuyou(money, passmd5, cid, ptid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(final String response, final T t) {
					super.onSuccess(response, t);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							parseJsonAndFuiouPay(payMoney, response, info,myFyPayCallBack);
						}
					}, 500);

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						if ("4021".equals(errorCode)) {
							// 密码错误
							if (myFyPayCallBack instanceof BuyActivity) {
								PassEvent1 p1 = new PassEvent1();
								p1.msg = msg;
								EventBus.getDefault().post(p1);
							} else {
								PassEvent p = new PassEvent();
								p.msg = msg;
								EventBus.getDefault().post(p);
							}

						} else {
							PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
								@Override
								public void onClick(Dialog dialog) {
									dialog.dismiss();
								}
							});
						}
					}
				}
			});
		}

	}

	/**
	 * 金运通支付
	 *
	 * @param money
	 * @param passmd5
	 */
	private void jytpay(final double money, final String passmd5, final String cid, final String ptid,final String code) {
		if (amodel != null) {
			showLoading();
			amodel.jytPay(money, passmd5, cid, ptid,code, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeCalculator();
					if (isBinded) {
						if (t != null && t instanceof String) {
							String orderid = (String) t;
							PayPresenter.this.money = money;
							PayPresenter.this.passmd5 = passmd5;
							Bundle data = new Bundle();
							data.putString("orderid", orderid);
							sendQueryMsg(QUERY_JYTPAY_RESULT, data, 2000);
						} else {
							ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
							closeLoading();
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						if ("4021".equals(errorCode)) {
							// 密码错误
							PassEvent p = new PassEvent();
							p.msg = msg;
							EventBus.getDefault().post(p);
						} else if ("4040".equals(errorCode) || "111111".equals(errorCode)) {
							PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
								@Override
								public void onClick(Dialog dialog) {
									dialog.dismiss();
								}
							});
						} else if("10021".equals(errorCode)){
							ToastUtils.show(context,"验证码错误！");
						}else {
							closeCalculator();
							showPayError(msg);
						}
					}
				}
			});
		}
	}

	/**
	 * 易宝支付
	 *
	 * @param money
	 * @param passmd5
	 */
	private void yeePay2(final double money, final String passmd5) {
		if (amodel != null) {
			showLoading();
			amodel.yeePay(money, passmd5, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (isBinded) {
						if (t != null && t instanceof String) {
							// 保存正确的密码
							String orderid = (String) t;
							PayPresenter.this.money = money;
							PayPresenter.this.passmd5 = passmd5;
							Bundle data = new Bundle();
							data.putString("orderid", orderid);
							sendQueryMsg(QUERY_YEEPAY_RESULT, data, 2000);
						} else {
							closeLoading();
							ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if (isBinded) {
						ToastUtils.show(context, msg);
						if (!"4021".equals(errorCode)) {
							//不是交易密码错误
							showPayError(msg);
						}
					}
				}
			});
		}
	}

	/**
	 * 连连支付结果处理
	 *
	 * @param result
	 */
	private void handleLLPayResult(String result) {
		JSONObject jsonObj = BaseHelper.string2JSON(result);
		String orderid = jsonObj.optString("no_order");
		String retCode = jsonObj.optString("ret_code");
		String retMsg = jsonObj.optString("ret_msg");
		if (Constants.RET_CODE_SUCCESS.equals(retCode)) {
			// 支付成功
			String resultPay = jsonObj.optString("result_pay", null);
			if (Constants.RESULT_PAY_SUCCESS.equalsIgnoreCase(resultPay)) {
				// 充值成功，查询充值结果
				llpaySuccess(jsonObj);
			} else {
				// 充值失败
				ToastUtils.show(context, retMsg);
				showPayError(retMsg);
				postPayError(orderid, retCode, retMsg);
				Map<String, String> map = new HashMap<>();
				map.put("uid", App.loginData.uid);
				map.put("pay", "llpay");
				map.put("retCode", retCode);
				map.put("retMsg", retMsg);
				MobclickAgent.onEvent(context, "payError", map);
			}

		} else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
			// 支付处理中
			String resultPay = jsonObj.optString("result_pay", null);
			if (Constants.RESULT_PAY_PROCESSING.equalsIgnoreCase(resultPay)) {
				logger.d(TAG, "支付消息：" + retMsg + " 交易状态码：" + retCode);
				ToastUtils.show(context, retMsg);
				// 充值处理中，查询充值结果
				llpaySuccess(jsonObj);
			} else {
				// 充值失败
				logger.d(TAG, "支付消息：" + retMsg + " 交易状态码：" + retCode);
				ToastUtils.show(context, retMsg);
				showPayError(retMsg);
				postPayError(orderid, retCode, retMsg);
				Map<String, String> map = new HashMap<>();
				map.put("uid", App.loginData.uid);
				map.put("pay", "llpay");
				map.put("retCode", retCode);
				map.put("retMsg", retMsg);
				MobclickAgent.onEvent(context, "payError", map);
			}
		} else {
			// 充值失败
			logger.d(TAG, "支付消息：" + retMsg + " 交易状态码：" + retCode);
			ToastUtils.show(context, retMsg);
			showPayError(retMsg);
			postPayError(orderid, retCode, retMsg);
			Map<String, String> map = new HashMap<>();
			map.put("uid", App.loginData.uid);
			map.put("pay", "llpay");
			map.put("retCode", retCode);
			map.put("retMsg", retMsg);
			MobclickAgent.onEvent(context, "payError", map);
		}
	}

	/**
	 * 连连支付-成功或支付处理中
	 *
	 * @param jsonObj
	 */
	private void llpaySuccess(JSONObject jsonObj) {
		showLoading();
		Bundle data = new Bundle();
		String dtOrder = jsonObj.optString("dt_order", null);
		String noOrder = jsonObj.optString("no_order", null);
		data.putString("dt_order", dtOrder);
		data.putString("no_order", noOrder);
		sendQueryMsg(QUERY_LLPAY_RESULT, data, 2000);
	}

	/**
	 * 发送查询消息
	 *
	 * @param what
	 * @param data
	 * @param delay
	 */
	private void sendQueryMsg(int what, Bundle data, long delay) {
		Message msg = new Message();
		msg.what = what;
		msg.setData(data);
		handler.sendMessageDelayed(msg, delay);
	}

	/**
	 * 查询充值结果-易宝支付
	 *
	 * @param orderid
	 */
	private void queryResult(final String orderid) {
		if (amodel != null) {
			showLoading();
			amodel.queryYeePayResult(orderid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (isBinded) {
						if (t != null && t instanceof PayResult) {
							PayResult result = (PayResult) t;
							int status = result.status;
							if (status == 0) {
								// 充值失败

								closeLoading();
								ToastUtils.show(context, result.errormsg);
								showPayError(result.errormsg);
								Map<String, String> map = new HashMap<>();
								map.put("uid", App.loginData.uid);
								map.put("pay", currentPay);
								map.put("retCode", result.status + "");
								map.put("retMsg", result.errormsg);
								MobclickAgent.onEvent(context, "payError", map);
							} else if (status == 1) {
								// 充值成功
								closeLoading();
								if (view != null) {
									view.updateData(null);
								}
								Map<String, String> map = new HashMap<>();
								map.put("pay", currentPay);
								MobclickAgent.onEvent(context, "paySuccess", map);

							} else if (status == 2 || status == 3) {
								// 未处理|处理中
								Bundle data = new Bundle();
								data.putString("orderid", orderid);
								int code;
								if ("jytpay".equals(currentPay)) {
									code = QUERY_JYTPAY_RESULT;
								} else {
									code = QUERY_YEEPAY_RESULT;
								}
								sendQueryMsg(code, data, 2000);

							} else {
								closeLoading();
								ToastUtils.show(context, result.errormsg);
							}
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					if (isBinded) {
						if ("-1".equals(errorCode)) {
							closeLoading();
							ToastUtils.show(context, msg);
						} else {
							Bundle data = new Bundle();
							data.putString("orderid", orderid);
							int code;
							if ("jytpay".equals(currentPay)) {
								code = QUERY_JYTPAY_RESULT;
							} else {
								code = QUERY_YEEPAY_RESULT;
							}
							sendQueryMsg(code, data, 2000);
						}
					}
				}
			});
		}
	}

	/**
	 * 查询充值结果-富友支付
	 *
	 * @param orderid
	 */
	public void queryfResult(final String orderid) {
		if (amodel != null) {
			showLoading();
			amodel.queryFuiouPayResult(orderid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);

					if (isBinded) {
						if (t != null && t instanceof PayResult) {
							PayResult result = (PayResult) t;
							int status = result.status;
							if (status == 0) {
								// 充值失败
								closeLoading();
								ToastUtils.show(context, result.errormsg);
								showPayError(result.errormsg);
								Map<String, String> map = new HashMap<>();
								map.put("uid", App.loginData.uid);
								map.put("pay", currentPay);
								map.put("retCode", result.status + "");
								map.put("retMsg", result.errormsg);
								MobclickAgent.onEvent(context, "payError", map);
							} else if (status == 1) {
								// 充值成功
								closeLoading();
								if (view != null) {
									view.updateData(null);
								}
								Map<String, String> map = new HashMap<>();
								map.put("pay", currentPay);
								MobclickAgent.onEvent(context, "paySuccess", map);

							} else if (status == 2 || status == 3) {
								// 未处理|处理中
								Bundle data = new Bundle();
								data.putString("orderid", orderid);
								int code;
								if ("fuiou".equals(currentPay)) {
									code = QUERY_FUYOU_RESULT;
								} else {
									code = QUERY_YEEPAY_RESULT;
								}
								sendQueryMsg(code, data, 2000);

							} else {
								closeLoading();
								showPayError(result.errormsg);
								ToastUtils.show(context, result.errormsg);
							}
						}
					}

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);

					if (isBinded) {
						if ("-1".equals(errorCode)) {
							closeLoading();
							ToastUtils.show(context, msg);
						} else {
							Bundle data = new Bundle();
							data.putString("orderid", orderid);
							int code;
							if ("fuiou".equals(currentPay)) {
								code = QUERY_FUYOU_RESULT;
							} else {
								code = QUERY_YEEPAY_RESULT;
							}
							sendQueryMsg(code, data, 2000);
						}
					}

				}
			});
		}
	}


	/**
	 * 查询充值结果-连连支付
	 */
	private void queryResult(final String dtOrder, final String noOrder) {
		if (amodel != null) {
			showLoading();
			amodel.queryLLPayResult(dtOrder, noOrder, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (isBinded) {
						if (t != null && t instanceof PayResult) {
							PayResult result = (PayResult) t;
							int status = result.status;
							if (status == 0) {
								// 充值失败
								closeLoading();
								ToastUtils.show(context, result.errormsg);
								showPayError(result.errormsg);
								Map<String, String> map = new HashMap<>();
								map.put("uid", App.loginData.uid);
								map.put("pay", currentPay);
								map.put("retCode", result.status + "");
								map.put("retMsg", result.errormsg);
								MobclickAgent.onEvent(context, "payError", map);
							} else if (status == 1) {
								// 充值成功
								closeLoading();
								if (view != null) {
									view.updateData(null);
								}
								Map<String, String> map = new HashMap<>();
								map.put("pay", currentPay);
								MobclickAgent.onEvent(context, "paySuccess", map);

							} else if (status == 2 || status == 3) {
								// 未处理 | 处理中
								Bundle data = new Bundle();
								data.putString("dt_order", dtOrder);
								data.putString("no_order", noOrder);
								sendQueryMsg(QUERY_LLPAY_RESULT, data, 2000);
							} else {
								closeLoading();
								ToastUtils.show(context, result.errormsg);
							}
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					if (isBinded) {
						if ("-1".equals(errorCode)) {
							closeLoading();
							ToastUtils.show(context, msg);
						} else {
							Bundle data = new Bundle();
							data.putString("dt_order", dtOrder);
							data.putString("no_order", noOrder);
							sendQueryMsg(QUERY_LLPAY_RESULT, data, 2000);
						}
					}
				}
			});
		}
	}

	/**
	 * 显示充值失败
	 *
	 * @param error
	 */
	public void showPayError(String error) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_pay_error);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		TextView tvError = (TextView) dialog
				.findViewById(R.id.dialog_pay_error_text);
		LinearLayout llkefu = (LinearLayout) dialog
				.findViewById(R.id.dialog_pay_error_kefu);
		TextView tvOther = (TextView) dialog
				.findViewById(R.id.dialog_pay_error_other);
		TextView tvDismiss = (TextView) dialog
				.findViewById(R.id.dialog_pay_error_dismiss);
		tvError.setText(error);
		llkefu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (appContext != null && activity != null) {
					String tel = appContext.getResources().getString(
							R.string.service_phone);
					PhoneUtils.call(context, tel);
				}
			}
		});
		tvDismiss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		tvOther.setVisibility(View.VISIBLE);
		tvOther.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				pay();
				final Dialog dalog = new Dialog(context, R.style.DialogStyle);
				dalog.setContentView(R.layout.dialog_success);
				TextView tv = (TextView) dalog.findViewById(R.id.dialog_success_msg);
				String str = "<font size='28px' color='#ffffff'>通道切换成功</font>" + "<br><font size='28px' color='#ffffff'>请重新提交</font>";
				tv.setText(Html.fromHtml(str));
				if (activity != null && !activity.isFinishing()) {
					// Activity没有被销毁
					dalog.show();
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						dalog.dismiss();
					}
				}, 2000);
			}
		});
		if (activity != null
				&& !activity.isFinishing()
				&& !dialog.isShowing()
				&& platList != null
				&& platList.size() > 0) {
			dialog.show();
		}
	}

	public void showPayError(String error,final Context context) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_pay_error);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		TextView tvError = (TextView) dialog
				.findViewById(R.id.dialog_pay_error_text);
		LinearLayout llkefu = (LinearLayout) dialog
				.findViewById(R.id.dialog_pay_error_kefu);
		TextView tvOther = (TextView) dialog
				.findViewById(R.id.dialog_pay_error_other);
		TextView tvDismiss = (TextView) dialog
				.findViewById(R.id.dialog_pay_error_dismiss);
		tvError.setText(error);
		llkefu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (appContext != null && activity != null) {
					String tel = appContext.getResources().getString(
							R.string.service_phone);
					PhoneUtils.call(context, tel);
				}
			}
		});
		tvDismiss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		tvOther.setVisibility(View.VISIBLE);
		tvOther.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				pay();
				final Dialog dalog = new Dialog(context, R.style.DialogStyle);
				dalog.setContentView(R.layout.dialog_success);
				TextView tv = (TextView) dalog.findViewById(R.id.dialog_success_msg);
				String str = "<font size='28px' color='#ffffff'>通道切换成功</font>" + "<br><font size='28px' color='#ffffff'>请重新提交</font>";
				tv.setText(Html.fromHtml(str));
				if (activity != null && !activity.isFinishing()) {
					// Activity没有被销毁
					dalog.show();
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						dalog.dismiss();
					}
				}, 2000);
			}
		});
		if (activity != null
				&& !activity.isFinishing()
				&& !dialog.isShowing()
				&& platList != null
				&& platList.size() > 0) {
			dialog.show();
		}
	}


	/**
	 * 上报支付错误
	 *
	 * @param retCode
	 * @param retMsg
	 */
	public void postPayError(String orderid, String retCode, String retMsg) {
		if (amodel != null) {
			amodel.postPayError(orderid, retCode, retMsg, null);
		}
	}
	private String mchnt_key = "";
//    private String mchnt_key = "5f0bx65hzecaduuib7ckfidy17ck4fa2";
	private String mchnt_id = "";
//	private String mchnt_id = "0002900F0504406";
	private String ordernumber,temp;

	public void parseJsonAndFuiouPay(final int payMoney, final String response, final FuYouInfo info , final MyFyPayCallBack myFyPayCallBack) {
		try {
			if (App.DEBUG_LEVEL == App.DEBUG_LEVEL_TEST) {
				mchnt_key = "fx45crzkmo8akn24plwvrv8ywd10zjuy";
				mchnt_id = "0002900F0280321";
			} else {
				mchnt_key = "5f0bx65hzecaduuib7ckfidy17ck4fa2";
				mchnt_id = "0002900F0504406";
			}
			JSONObject result = new JSONObject(response);
			JSONObject data = result.getJSONObject("data");
//            info.orgString=data.getString("orgString");
			info.sign = data.getString("sign");
			info.acct_name = data.getString("acct_name");
			info.bank_name = data.getString("bank_name");
			info.card_no = data.getString("card_no");
			info.dt_order = data.getString("dt_order");
			info.id_no = data.getString("id_no");
			info.merchant_id = data.getString("merchant_id");
//            info.merchant_key=data.getString("merchant_key");
			info.money = data.getString("money");
			info.notify_url = data.getString("notify_url");
			info.order = data.getString("order");
			order = info.order;
			info.user_id = data.getString("user_id");
			info.valid_order = data.getString("valid_order");
		} catch (JSONException e) {
			Toast.makeText(context, "获取数据失败...", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	/*	RequestOrder requestOrder = new RequestOrder(context);
		Log.i(TAG, "FuYouInfo: "+response);
		String Sing = info.sign;
		AppConfig.setData(context, AppConfig.MCHNT_CD, info.merchant_id);
		AppConfig.setData(context, AppConfig.MCHNT_AMT, payMoney + "");
		AppConfig.setData(context, AppConfig.MCHNT_BACK_URL,
				info.notify_url);
		AppConfig.setData(context,
				AppConfig.MCHNT_BANK_NUMBER, info.card_no);
		AppConfig.setData(context, AppConfig.MCHNT_ORDER_ID,
				info.order);
		AppConfig.setData(context,
				AppConfig.MCHNT_USER_IDCARD_TYPE, "0");
		AppConfig.setData(context, AppConfig.MCHNT_USER_ID,
				info.user_id);
		AppConfig.setData(context, AppConfig.MCHNT_USER_IDNU,
				info.id_no);
		AppConfig.setData(context, AppConfig.MCHNT_USER_NAME,
				info.acct_name);
		AppConfig.setData(context, AppConfig.MCHNT_SING_KEY,
				Sing);
		AppConfig.setData(context, AppConfig.MCHNT_SDK_SIGNTP,
				"MD5");
		AppConfig.setData(context, AppConfig.MCHNT_SDK_TYPE,
				"02");
		AppConfig.setData(context, AppConfig.MCHNT_SDK_VERSION,
				"2.0");
		requestOrder.Request();*/
		temp = EncryptUtils.md5Encrypt(
				mchnt_id + "|"
						+ payMoney+"" + "|"
						+ mchnt_key).toLowerCase();

		HashMap<String, String> mhashMap = new HashMap<String, String>();
		mhashMap.put("Rmk1", "");
		mhashMap.put("Rmk2", "");
		mhashMap.put("Rmk3", "");
		mhashMap.put("Sign", temp);
		mhashMap.put("MchntCd", mchnt_id);
		mhashMap.put("Amt", payMoney+"");

		FyHttpClient.getXMLWithPostUrl("createOrder.pay", mhashMap,
				new FyHttpInterface() {
					@Override
					public void requestSuccess(FyHttpResponse resData) {
						FyXmlNodeData data = resData.getXml();
						closeLoading();
						Log.i("wyl", "订单请求成功返回的响应数据：" + data.toString());
						ordernumber = (String) data.get("OrderId");
					/*	Bundle bundle = new Bundle();
						bundle.putString(FyPay.KEY_ORDER_NO, ordernumber);
//						bundle.putString(FyPay.KEY_MOBILE_NO, "");
						bundle.putString(FyPay.KEY_MAC, getMac());*/

						/**
						 * 将富友的订单号传给服务器
						 */
						sendOrdernumber(ordernumber);

						MchantMsgBean bean = new MchantMsgBean();
						bean.setOrderId(ordernumber);
						bean.setKey(mchnt_key);
						bean.setMchntCd(mchnt_id);//设置商户号
						bean.setAmt(payMoney+"");
						bean.setUserId(info.user_id);
						bean.setCardNo(info.card_no);
						bean.setIDcardType("0");
						bean.setIDNo(info.id_no);
						bean.setUserName(info.acct_name);
						bean.setBackUrl(info.notify_url);
						bean.setPayType("mobilePay");

						bean.setBackUrl(info.notify_url);
						bean.setPayType("mobilePay");
						int i = FyPay.pay(context, bean,
								new FyPayCallBack() {

									@Override
									public void onPayComplete(String arg0,
															  String arg1, Bundle arg2) {

										myFyPayCallBack.requestFailed("取消支付");
									}

									@Override
									public void onPayBackMessage(
											String paramString) {
										myFyPayCallBack.requestSuccess(paramString);
										Log.i(TAG, "onPayBackMessage: "+paramString);
									}
								});
					}

					@Override
					public void requestFailed(FyHttpResponse resData) {
						// TODO Auto-generated method stub
						Log.i(TAG, "executeFailed: "+resData);
						closeLoading();
						myFyPayCallBack.requestFailed("请求失败:"+resData.toString());
					}

					@Override
					public void executeFailed(FyHttpResponse resData) {
						// TODO Auto-generated method stub
						Log.i(TAG, "executeFailed: "+resData);
						closeLoading();
						myFyPayCallBack.requestFailed("请求失败:"+resData.toString());
					}

				});
	}

	private void sendOrdernumber(String ordernumber) {
		if (amodel!=null) {
			amodel.fuyou(mMoney, mPassmd5, mCid, mPtid, ordernumber, new ResponseHandler() {
				@Override
				public <T> void onSuccess(final String response, final T t) {

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);

				}
			});
		}
	}
}
