package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.socialize.utils.Log;

import java.util.Locale;
import java.util.Map;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.BaseEvent;
import cn.app.yimirong.event.custom.ZChuEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.UserCD;
import cn.app.yimirong.model.bean.UserHQMoney;
import cn.app.yimirong.model.bean.VersionData;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.BitmapUtils;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.DisplayUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.OnPasswordInputFinish;
import cn.app.yimirong.view.PasswordView;
import cn.app.yimirong.view.PayView;

/**
 * 快活宝转出
 *
 * @author android
 */
public class HQZCActivity extends BaseActivity implements OnClickListener {

	public static final int TO_YUE = 1;
	public static final int TO_BANK = 2;

	public static final int PAYTYPE_ACCOUNT = 1;

	public static final int PAYTYPE_BANK = 2;

	private TextView tvToYue;

	private TextView tvToBank;

	private LinearLayout rlBank;

//    private ImageView ivBankIcon;

	private TextView allZC;

	private TextView tvBankName;

	private EditText etMoney;

	private TextView tvZChuCD;

	private TextView tvZChuDetail;

	private TextView tvZChuMax;

	private Button btnSubmit;

	private double money;

	private boolean isToYue = true;

	private UserHQMoney mHQMoney;

	private double allasset = 0d;

	private PayView payView;

	private int freecd = 0;

	private VersionData vdata;

	private PopupWindow window;

	private String password;

	private long time;

	private UserCD usercd;
    private ImageView ivBank_ic;


    @Override
	protected void onStop() {
		closePopupWindow();
		super.onStop();
	}

	@Override
	public void loadView() {
		setContentView(R.layout.activity_zhuanchu);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("转出");
		setTitleRight(true, new OnRightClickListener() {
			@Override
			public void onClick() {
				toSeeZCDesc();
			}
		});
		setRightText("转出说明");

		tvToYue = (TextView) findViewById(R.id.activity_zhuanchu_toyue);
		tvToYue.setOnClickListener(this);
		tvZChuDetail = (TextView) findViewById(R.id.hq_zc_detail);
		tvZChuDetail.setOnClickListener(this);

		tvToBank = (TextView) findViewById(R.id.activity_zhuanchu_tobank);
		tvToBank.setOnClickListener(this);

		allZC = (TextView) findViewById(R.id.all_hq_zc);
		allZC.setOnClickListener(this);

		rlBank = (LinearLayout) findViewById(R.id.activity_zhuanchu_bank_wrapper);


		tvBankName = (TextView) findViewById(R.id.activity_zhuanchu_bankname);
        ivBank_ic = (ImageView) findViewById(R.id.im_bank_ic);

        etMoney = (EditText) findViewById(R.id.activity_zhuanchu_money);
		etMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int cd = 0;
				if (usercd != null) {
					if (isToYue){
						cd = usercd.longmoneyToBalance;

					}else {
						cd = Math.min(usercd.longmoneyToBalance, usercd.withDraw);

					}

				}
				if (cd <= 0) {
					btnSubmit.setClickable(false);
					btnSubmit.setBackgroundResource(R.drawable.grayanniu);
					btnSubmit.setTextColor(Color.parseColor("#999999"));
					return;
				}
				String tmpStr = s.toString().trim();
				double allasset = SystemUtils.getDouble(mHQMoney.longmoney);
				double ss = SystemUtils.getDouble(tmpStr);

				if (tmpStr.length() > 0) {
					btnSubmit.setTextColor(Color.WHITE);
					btnSubmit.setBackgroundResource(R.drawable.redanniu);
				} else {
					btnSubmit.setTextColor(Color.parseColor("#999999"));
					btnSubmit.setBackgroundResource(R.drawable.grayanniu);
				}
				if (ss > allasset) {
					if (allasset >= 20000.0) {
						etMoney.setText(20000 + "");
						etMoney.setSelection(String.valueOf(20000).length());
					} else {
						etMoney.setText(allasset + "");
						etMoney.setSelection(String.valueOf(allasset).length());
					}
				}
				int dotPos = tmpStr.indexOf(".");
				if (dotPos <= 0) {
					return;
				}
				if (tmpStr.length() - dotPos - 1 > 2) {
					//小数点后位数大于2，删除多余位数
					s.delete(dotPos + 3, dotPos + 4);
				}
			}
		});
		tvZChuCD = (TextView) findViewById(R.id.activity_khb_zchu_cd);
		tvZChuMax = (TextView) findViewById(R.id.hqzc_jine_tishi);
		btnSubmit = (Button) findViewById(R.id.activity_zhuanchu_submit);
		btnSubmit.setOnClickListener(this);
		btnSubmit.setClickable(false);
		if (isToYue) {
			setToYue();
		} else {
			setToBank();
		}

	}

	/**
	 * 去购买转出列表
	 */
	private void toBuyList() {
		Intent intent = new Intent(context, HQLogsActivity.class);
		intent.putExtra("currentSelected", 1);
		startActivity(intent);
	}


	/**
	 * 转出说明
	 */
	protected void toSeeZCDesc() {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", "转出说明");
		intent.putExtra("url", API.ZCHU_DESC);
		startActivity(intent);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			mHQMoney = (UserHQMoney) data.getSerializable("khbmoney");
		}
		vdata = DataMgr.getInstance(appContext).restoreVersionData();
		loadUserCD();
		updateView();
	}

	private void loadUserCD() {
		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getUserCD(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof UserCD) {
						usercd = (UserCD) t;
						updateView(usercd);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	protected void updateView(UserCD usercd) {
		if (usercd != null) {

			freecd = usercd.free_withDraw;
			if (isToYue){

				int cd = usercd.longmoneyToBalance;

				String cdinfo = "今日还可以转出<font color='#ff4747'>" + cd + "</font>次，单笔转出最大金额20000元";
				tvZChuCD.setText(Html.fromHtml(cdinfo));

				if (cd <= 0) {
					btnSubmit.setClickable(false);
					btnSubmit.setBackgroundResource(R.drawable.grayanniu);
					btnSubmit.setTextColor(Color.parseColor("#999999"));
				} else {
					btnSubmit.setClickable(true);
				}
			}else {
				int cd = Math.min(usercd.longmoneyToBalance, usercd.withDraw);

				String cdinfo = "今日还可以转出<font color='#ff4747'>" + cd + "</font>次，单笔转出最大金额20000元";
				if (usercd.withDrawVersion > 0){
					tvZChuCD.setText(Html.fromHtml(cdinfo));
				}else {
					tvZChuCD.setText(Html.fromHtml(cdinfo));
				}

				if (cd <= 0) {
					btnSubmit.setClickable(false);
					btnSubmit.setBackgroundResource(R.drawable.grayanniu);
					btnSubmit.setTextColor(Color.parseColor("#999999"));
				} else {
					btnSubmit.setClickable(true);
				}
			}

		}
	}

	/**
	 * 更新界面
	 */
	protected void updateView() {
		if (mHQMoney != null) {
			allasset = SystemUtils.getDouble(mHQMoney.longmoney);
			if (allasset >= 20000.0) {
				etMoney.setHint("本次最多转出￥" + 20000 + "元");
			} else {
				etMoney.setHint("本次最多转出￥" + SystemUtils.getDoubleStr(allasset) + "元");
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_zhuanchu_toyue:
				setToYue();
				updateView(usercd);
				break;

			case R.id.activity_zhuanchu_tobank:
				setToBank();
				updateView(usercd);
				break;

			case R.id.activity_zhuanchu_submit:
				if (!isToYue && new Double(etMoney.getText().toString().trim()) < 2) {
					PromptUtils.showDialog3(context, "转出到银行卡不能低于2元!", new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
					return;
				} else {
					submit();
				}

				break;

			case R.id.hq_zc_detail:
				toSeeZCDesc();
				break;

			case R.id.all_hq_zc:
				if (allasset >= 20000.0) {
					etMoney.setText(20000 + "");
					etMoney.setSelection(String.valueOf(20000).length());
				} else {
					etMoney.setText(allasset + "");
					etMoney.setSelection(String.valueOf(allasset).length());
				}

				break;

			default:
				break;
		}
	}

	/**
	 * 转出到余额
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void setToYue() {
		isToYue = true;
		tvToBank.setBackgroundDrawable(null);
		tvToBank.setTextColor(Color.BLACK);
		tvToBank.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
		tvToYue.setCompoundDrawablesWithIntrinsicBounds(null,null,null,getResources().getDrawable(R.drawable.shape_red_line));
		tvToYue.setTextColor(Color.RED);
		rlBank.setVisibility(View.GONE);
	}

	/**
	 * 转出到银行卡
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void setToBank() {
		isToYue = false;
		tvToYue.setBackgroundDrawable(null);
		tvToYue.setTextColor(Color.BLACK);
		tvToBank.setTextColor(Color.RED);
		tvToYue.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
		tvToBank.setCompoundDrawablesWithIntrinsicBounds(null,null,null,getResources().getDrawable(R.drawable.shape_red_line));
		rlBank.setVisibility(View.VISIBLE);

		if (App.userinfo != null) {
			setBankInfo();
		}

	}

	/**
	 * 设置银行卡信息
	 */
	protected void setBankInfo() {
		if (App.userinfo != null && App.userinfo.identity != null) {
			String bankInfo = App.userinfo.identity.bankname
					+ "(尾号" + App.userinfo.identity.cardno + ")";
			tvBankName.setText(bankInfo);
            String url = API.BANK_ICON_BASE + App.userinfo.identity.bankid + ".jpg";
            Glide.with(this).load(url).error(R.drawable.yinhangbeijing).into(ivBank_ic);
        }
	}

	/**
	 * 提交
	 */
	private void submit() {
		hideInputMethod();

		if (!getInput()) {
			return;
		}
		if (isToYue) {
			showPopwindow();
		} else {
			if (freecd > 0) {

				String msg = "亲，每笔提现第三方支付公司需扣除￥2.00手续费，本次由易米融为您支付！";
				PromptUtils.showCashDialog(activity, context, money, money, 0, msg, "取消", "确定", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						dialog.dismiss();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						dialog.dismiss();
						showPopwindow();
					}
				});
			} else {
				//不可免费提现
				showCashInfo();
			}
		}
	}


	/**
	 * 显示提现手续费信息
	 */
	private void showCashInfo() {
		double sx = (double) vdata.withdraw_sxf;
		String message = "亲，因每笔提现第三方支付公司需要扣取￥" + sx + "手续费，我们建议您多赚一点再提现！";
		String leftBtn = "取消";
		String rightBtn = "继续提现";
		PromptUtils.showCashDialog(activity, context, money, money - vdata.withdraw_sxf, vdata.withdraw_sxf, message, leftBtn, rightBtn, new PromptUtils.OnDialogClickListener2() {
			@Override
			public void onLeftClick(Dialog dialog) {
				dialog.dismiss();
			}

			@Override
			public void onRightClick(Dialog dialog) {
				dialog.dismiss();
				showPopwindow();
			}
		});
	}

	/**
	 * 转出到余额
	 */
	private void toYue(String password) {
		if (amodel != null) {
			showLoading("正在处理");
			String passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
			amodel.hqToBalance(TO_YUE, money, passmd5,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							closeLoading();
							showSuccess("转出成功");
							EventBus.getDefault().post(new ZChuEvent());
						}

						@Override
						public void onFailure(String errorCode, String msg) {
							super.onFailure(errorCode, msg);
							closeLoading();
							if ("4021".equals(errorCode)) {
								// 密码错误
								showPassError(msg);
							}else {
								PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
									@Override
									public void onClick(Dialog dialog) {
										dialog.dismiss();
									}
								});
							}
							EventBus.getDefault().post(new BaseEvent());
						}

					});
		}
	}

	/**
	 * 转出到银行
	 */
	private void toBank(String password) {

		if (amodel != null) {
			showLoading("正在处理");
			String passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
			amodel.hqToBank(TO_BANK, money, passmd5, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					Map<String, String> map = (Map<String, String>) t;
					time = Long.parseLong(map.get("time"));
					showSuccess("转出申请已提交");
					EventBus.getDefault().post(new ZChuEvent());
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if ("-2".equals(errorCode)) {
						// 转出到银行卡失
						EventBus.getDefault().post(new ZChuEvent());
					}

					if ("4021".equals(errorCode)) {
						// 密码错误
						showPassError(msg);
					}else if ("-1".equals(errorCode)){
						time = TimeUtils.getServerTime();
						showSuccess("转出申请已提交");
						EventBus.getDefault().post(new ZChuEvent());
					} else {
						PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
							@Override
							public void onClick(Dialog dialog) {
								dialog.dismiss();
							}
						});
						EventBus.getDefault().post(new BaseEvent());
						return;
					}
				}
			});
		}
	}

	private void isBuy() {

	}

	/**
	 * 显示成功的消息
	 *
	 * @param msg
	 */
	private void showSuccess(String msg) {
		btnSubmit.setClickable(false);
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context, msg);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		// 1200ms后跳转到结果详情
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				EventBus.getDefault().post(new BaseEvent());
				toResult();
			}
		}, 1200);
	}

	/**
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		String moneyStr = etMoney.getText().toString().trim();
		if (StringUtils.isBlank(moneyStr)) {
			ToastUtils.show(context, "请输入转出金额");
			return false;
		} else {
			try {
				money = SystemUtils.getDouble(moneyStr);
				if (money <= 0) {
					ToastUtils.show(context, "输入金额有误");
					etMoney.setText("");
					return false;
				} else {
					if (allasset <= 0) {
						// 活期总资产未获取到
						ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
						finish();
						return false;
					} else {
						if (money > allasset) {
							// 如果转出金额大于活期总资产
							ToastUtils.show(context, "输入金额超出余额");
							etMoney.setText("");
							return false;
						}
						if (!isToYue) {
							if (money < 1){
								ToastUtils.show(context, "最低取现一元");
								etMoney.setText("");
								return false;
							}

							if (freecd <= 0 && money <= vdata.withdraw_sxf) {
								ToastUtils.show(context,
										"提现手续费" + vdata.withdraw_sxf + "元");
								etMoney.setText("");
								return false;
							}
						}
					}
				}
			} catch (NumberFormatException e) {
				ToastUtils.show(context, "输入金额有误");
				etMoney.setText("");
				return false;
			}
		}
		return true;
	}

	/**
	 * 去转出结果详情
	 */
	private void toResult() {
		Intent intent = null;
		if (isToYue) {
			intent = new Intent(context, HQZCDetailActivity.class);
		} else {
			intent = new Intent(context, CashResultActivity.class);
		}
		intent.putExtra("money", money + "");
		intent.putExtra("type", 1);
		intent.putExtra("time", time);
		startActivity(intent);
		finish();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (window != null && window.isShowing()) {
				window.dismiss();
				window = null;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 显示popupWindow
	 */
	private void showPopwindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.payview_pop, null);

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

		window = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);

		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);


		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.alpha = 0.7f;
		this.getWindow().setAttributes(params);
		// 在底部显示
		window.showAtLocation(activity.findViewById(R.id.activity_zhuanchu_submit),
				Gravity.BOTTOM, 0, 0);
		final PasswordView pass = (PasswordView) view.findViewById(R.id.payview);
		pass.setOnFinishInput(new OnPasswordInputFinish() {
			@Override
			public void inputFinish() {
//				closePopupWindow();
				password = pass.getStrPassword();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// 输入完成
						if (isToYue) {
							toYue(password);
							closePopupWindow();
						} else {
							toBank(password);
							closePopupWindow();
						}
					}
				}, 200);
			}
		});

		pass.getCancelImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopupWindow();
			}
		});
		//popWindow消失监听方法
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.alpha = 1f;
				getWindow().setAttributes(params);
			}
		});

	}

	/**
	 * 关闭窗口
	 */
	private void closePopupWindow() {
		if (window != null && window.isShowing()) {
			window.dismiss();
			window = null;
		}
	}

	/**
	 * 去找回密码
	 */
	private void toFindPass() {
		Intent intent = new Intent(context, IdentifyActivity.class);
		intent.putExtra("isResetPayPass", true);
		startActivity(intent);
	}

	/**
	 * 处理密码错误
	 */
	public final void showPassError(String msg) {

		PromptUtils.showDialog4(activity, context, "", msg, "重新输入",
				"找回密码", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						submit();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						toFindPass();
					}
				}, false);

	}

}
