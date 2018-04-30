package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.model.CModel;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.Province;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class BindActivity extends BaseActivity implements View.OnClickListener, EditText.OnFocusChangeListener {

	private EditText etName;
	private String name;
	private EditText etIDCard;
	private String id;
	private String TAG = "BindActivity";
	private TextView tvBank,format_card, chooseBank;
	private String bankCode;
	private ImageView ivBank, idReset, cardReset, phoneReset;
	private LinkedHashMap<String,String> cityInfo = new LinkedHashMap<>();
	private EditText etCard;
	private String bankCard;

	private EditText etPhone;
	private String bankPhone;

	private EditText etVerify;
	private String verifyCode;

	private TextView tvSend;

	private Button btnNext;

	private BitmapUtils bitmapUtils;

	private ScheduledExecutorService scheduledExecutor;

	private ArrayList<String> options1Items = new ArrayList<>();

	private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();

	private int elapseTime;
	private TextView bankOfDeposit;
	private String cityid;

	@Override
	public void loadView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_bind);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("绑定银行卡");

		etName = (EditText) findViewById(R.id.activity_bind_name);
		etIDCard = (EditText) findViewById(R.id.activity_bind_idnum);
		etIDCard.setOnFocusChangeListener(this);
		findViewById(R.id.bank_of_deposit).setOnClickListener(this);
		RelativeLayout rlBank = (RelativeLayout) findViewById(R.id.activity_bind_bank_wrapper);
		bankOfDeposit = (TextView) findViewById(R.id.bank_of_deposit_info);
		assert rlBank != null;
		rlBank.setOnClickListener(this);
		tvBank = (TextView) findViewById(R.id.activity_bind_bank);
		ivBank = (ImageView) findViewById(R.id.activity_bind_bank_icon);
		//身份证格式
//		format_id = (TextView) findViewById(R.id.fomat_id_card);
		//银行卡格式
		format_card = (TextView) findViewById(R.id.fomat_bank_card);
		//清除身份证
		idReset = (ImageView) findViewById(R.id.id_card_num_qingchu);
		idReset.setOnClickListener(this);
		//清除银行卡
		cardReset = (ImageView) findViewById(R.id.bank_card_num_qingchu);
		cardReset.setOnClickListener(this);
		//清除手机号
		phoneReset = (ImageView) findViewById(R.id.bank_card_phone_qingchu);
		phoneReset.setOnClickListener(this);

		chooseBank = (TextView) findViewById(R.id.activity_bind_bank_choose);

		etCard = (EditText) findViewById(R.id.activity_bind_card);
		etCard.setOnFocusChangeListener(this);

		etPhone = (EditText) findViewById(R.id.activity_bind_phone);
		if (App.account != null) {
			assert etPhone != null;
			String ss = App.account + " ";
			String start = ss.substring(0, 3);
			String middel = ss.substring(3, 7);
			String end = ss.substring(7, 11);
			String phoneNum = start + " " + middel + " " + end;
			etPhone.setText(phoneNum);
		}

		etVerify = (EditText) findViewById(R.id.activity_bind_verify);
		tvSend = (TextView) findViewById(R.id.activity_bind_send);
		assert tvSend != null;
		tvSend.setOnClickListener(this);

		etVerify.setOnFocusChangeListener(this);

		btnNext = (Button) findViewById(R.id.activity_bind_next);
		assert btnNext != null;
		btnNext.setOnClickListener(this);

		etCard.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
		idReset.setVisibility(View.GONE);
		cardReset.setVisibility(View.GONE);
		forMat();
	}

	@Override
	public void initData() {
		bitmapUtils = new BitmapUtils(context);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdownNow();
		}
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onClick(View v) {
		hideInputMethod();
		int id = v.getId();
		switch (id) {
			case R.id.activity_bind_bank_wrapper:
				toChooseBank();
				break;

			case R.id.activity_bind_next:
				submit();
				break;

			case R.id.activity_bind_send:
				sendVerifyCode();
				break;
			case R.id.id_card_num_qingchu:
				etIDCard.setText("");
				etIDCard.requestFocus();
				break;
			case R.id.bank_card_num_qingchu:
				etCard.setText("");
				etCard.requestFocus();
				break;
			case R.id.bank_card_phone_qingchu:
				etPhone.setText("");
				etPhone.requestFocus();
				break;
			case R.id.bank_of_deposit:
				if (options1Items.size() > 0 && options2Items.size() > 0) {
					showPvOptions();
				} else {
					getBankOfDeposit();
				}
				break;
			default:
				break;
		}
	}

	private void getBankOfDeposit() {
		showLoading("加载中...");
		CModel.getBankOfDeposit(new ResponseHandler() {
			@Override
			public <T> void onSuccess(String response, T t) {
				super.onSuccess(response, t);
				closeLoading();
				if (t != null) {
					final List<Province> list = (List<Province>) t;
					initList(list);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showPvOptions();
						}
					});
				}
			}

			@Override
			public void onFailure(String errorCode, String msg) {
				super.onFailure(errorCode, msg);
				ToastUtils.show(context,"errorCode："+msg);
				closeLoading();
			}
		});
	}

	private void showPvOptions() {
		OptionsPickerView pvOptions = new OptionsPickerView.Builder(BindActivity.this, new OptionsPickerView.OnOptionsSelectListener(){
			@Override
			public void onOptionsSelect(int options1, int options2, int options3, View v) {
				String tx = options1Items.get(options1)+" "+
						options2Items.get(options1).get(options2);
				bankOfDeposit.setText(tx);
			}
		})
				.setTitleText("城市选择")
				.setDividerColor(Color.BLACK)
				.setTextColorCenter(Color.BLACK) //设置选中项文字颜色
				.setContentTextSize(20)
				.build();
		pvOptions.setPicker(options1Items,options2Items);
		pvOptions.show();
	}

	private void initList(List<Province> list) {
		for (int i = 0; i < list.size(); i++) {
			Province province = list.get(i);
			Map<String, String> map = province.list;
			Set<String> set = map.keySet();
			ArrayList<String> citys = new ArrayList<>();
			for (String key : set) {
				citys.add(key);
				cityInfo.put(key,map.get(key));
			}
			options1Items.add(province.name);
			options2Items.add(citys);
		}
	}

	/**
	 * 发送短信验证码
	 */
	private void sendVerifyCode() {
/*		String city = bankOfDeposit.getText().toString();
		if (city == "") {
			ToastUtils.show(context, "请选择开户地址！");
			return;
		}
		String[] split = city.split("\\s+");
		if (split.length > 0) {
			cityid = cityInfo.get(split[1]);
		} else {
			ToastUtils.show(context, "城市错误！"+split.length);
			return;
		}*/





		name = etName.getText().toString().trim();
		if (StringUtils.isBlank(name)) {
			PromptUtils.showDialog1(activity, context, "", "请输入姓名", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return;
		}

		id = etIDCard.getText().toString().trim();
		id = id.replace(" ", "");
		if (StringUtils.isBlank(id)) {
			PromptUtils.showDialog1(activity, context, "", "请输入身份证件号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return;
		}

		if (bankCode == null) {
			PromptUtils.showDialog1(activity, context, "", "请选择银行", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return;
		}

		bankCard = etCard.getText().toString().trim();
		bankCard = bankCard.replace(" ", "");
		if (StringUtils.isBlank(bankCard)) {
			PromptUtils.showDialog1(activity, context, "", "请输入银行卡号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return;
		} else {
			if (!(bankCard.length() <= 19 && bankCard.length() >= 16)) {
				PromptUtils.showDialog1(activity, context, "", "银行卡号格式不正确", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});

				return;
			}
		}

		bankPhone = etPhone.getText().toString().trim();
		bankPhone = bankPhone.replace(" ", "");
		if (StringUtils.isBlank(bankPhone)) {
			PromptUtils.showDialog1(activity, context, "", "请输入银行预留手机号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return;
		} else {
			if (bankPhone.length() != 11) {
				PromptUtils.showDialog1(activity, context, "", "手机号格式不正确", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});

				return;
			}
		}

		bankPhone = etPhone.getText().toString().trim();
		if (StringUtils.isBlank(bankPhone)) {
			PromptUtils.showDialog1(activity, context, "", "请输入银行预留手机号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			return;
		} else {
			if (bankPhone.length() != 13) {
				PromptUtils.showDialog1(activity, context, "", "手机号格式不正确", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});

				return;
			}
		}
		bankPhone = bankPhone.replace(" ", "");
		if (amodel != null) {
			tvSend.setClickable(false);
			showLoading("获取验证码");
			amodel.getMsgCode(bankCode, bankPhone, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					String message;
					if (t == null) {
						message = "验证码已发送";
					} else {
						message = (String) t;
					}
					PromptUtils.showDialog1(activity, context, "", message, "确定", new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});

					startTimer();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					PromptUtils.showDialog1(activity, context, "", msg, "确定", new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
//					tvSend.setClickable(true);
				}
			});
		}
	}

	/**
	 * 启动计时
	 */
	private void startTimer() {
		elapseTime = 59;
		tvSend.setTextColor(Color.parseColor("#808080"));
		tvSend.setText(elapseTime + "秒");
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				elapseTime--;
				if (elapseTime <= 0) {
					resetTimer();
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvSend.setText(elapseTime + "秒");
						}
					});
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	/**
	 * 重置计时器
	 */
	private void resetTimer() {
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdownNow();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvSend.setTextColor(Color.parseColor("#ff4747"));
					tvSend.setText("发送验证码");
					tvSend.setClickable(true);
				}
			});
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
		if (amodel != null) {
			showLoading("正在处理");
			String plat = sp.getString(Constant.QUDAO, "");
			if (plat.isEmpty()) {
				plat = AnalyticsConfig.getChannel(context);
			}
			amodel.bindCard(bankCode, bankCard, "01", id, name, bankPhone, verifyCode,plat,new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					bindSuccess();
					MobclickAgent.onEvent(context, "bindSuccess");
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
					Map<String, String> map = new HashMap<String, String>();
					map.put("uid", App.loginData.uid);
					map.put("reason", msg);
					MobclickAgent.onEvent(context, "bindFailure", map);
					etVerify.setText("");
				}
			});
		}
	}

	/**
	 * 绑卡成功
	 */
	private void bindSuccess() {
		btnNext.setClickable(false);
		EventBus.getDefault().post(new UserInfoEvent());
		final Dialog dialog = PromptUtils.showSuccessDialog(activity, context,
				"绑卡成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toNext();
			}
		}, 1200);
	}

	/**
	 * 绑卡成功，去设置支付密码
	 */
	protected void toNext() {
		Intent intent = new Intent(context, SetPayPassActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		name = etName.getText().toString().trim();
		if (StringUtils.isBlank(name)) {
			PromptUtils.showDialog1(activity, context, "", "请输入姓名", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return false;
		}

		id = etIDCard.getText().toString().trim();
		id = id.replace(" ", "");
		if (StringUtils.isBlank(id)) {
			PromptUtils.showDialog1(activity, context, "", "请输入身份证件号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return false;
		}

		if (StringUtils.isBlank(bankCode)) {
			PromptUtils.showDialog1(activity, context, "", "请选择开户银行", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return false;
		}

		bankCard = etCard.getText().toString().trim();
		bankCard = bankCard.replace(" ", "");
		if (StringUtils.isBlank(bankCard)) {
			PromptUtils.showDialog1(activity, context, "", "请输入银行卡号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return false;
		} else {
			if (!(bankCard.length() <= 19 && bankCard.length() >= 16)) {
				PromptUtils.showDialog1(activity, context, "", "银行卡号格式不正确", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});

				return false;
			}
		}

		bankPhone = etPhone.getText().toString().trim();
		bankPhone = bankPhone.replace(" ", "");
		if (StringUtils.isBlank(bankPhone)) {
			PromptUtils.showDialog1(activity, context, "", "请输入银行预留手机号", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return false;
		} else {
			if (bankPhone.length() != 11) {
				PromptUtils.showDialog1(activity, context, "", "手机号格式不正确", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});

				return false;
			}
		}

		verifyCode = etVerify.getText().toString().trim();
		if (StringUtils.isBlank(verifyCode)) {
			PromptUtils.showDialog1(activity, context, "", "请输入短信验证码", "确定", new PromptUtils.OnDialogClickListener1() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
				}
			});

			return false;
		} else {
			int length = verifyCode.length();
			if (!(length == 4 || length == 6)) {
				PromptUtils.showDialog1(activity, context, "", "短信验证码格式不正确", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});
				etVerify.setText("");
				return false;
			}
		}
		return true;
	}

	/**
	 * 选择银行
	 */
	private void toChooseBank() {
		Intent intent = new Intent(context, BankActivity.class);
		startActivity(intent);
	}

	/**
	 * 显示选择的银行
	 *
	 * @param bank
	 */
	public void onEventMainThread(Bank bank) {
		if (bank != null) {
			bankCode = bank.code;
			tvBank.setText(bank.name);
			tvBank.setVisibility(View.VISIBLE);
			ivBank.setVisibility(View.VISIBLE);
			chooseBank.setVisibility(View.GONE);
			bitmapUtils.display(ivBank, bank.url);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.activity_bind_idnum:
				if (hasFocus && etIDCard.getText().toString().trim().length() > 0) {

					idReset.setVisibility(View.VISIBLE);
				} else {
					idReset.setVisibility(View.GONE);

				}
				break;
			case R.id.activity_bind_card:
				if (hasFocus && etCard.getText().toString().trim().length() > 0) {
					format_card.setVisibility(View.VISIBLE);
					cardReset.setVisibility(View.VISIBLE);
				} else {
					format_card.setVisibility(View.GONE);
					cardReset.setVisibility(View.GONE);
				}
				break;
		}
	}

	/**
	 * 各种格式调整
	 */
	private void forMat() {
		//调整手机号格式
		assert etPhone != null;
		etPhone.addTextChangedListener(new TextWatcher() {
			private int action = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				// before == 0 增
				// before == 1 删
				action = before;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					phoneReset.setVisibility(View.VISIBLE);
					if (action == 0) {
						// 增
						if (s.length() == 4 || s.length() == 9) {
							s.insert(s.length() - 1, " ");
						}
					} else {
						// 删
						if (s.length() == 4 || s.length() == 9) {
							s.delete(s.length() - 1, s.length());
						}
					}

				} else {
					phoneReset.setVisibility(View.GONE);
				}
			}
		});

		//调整身份证格式
		assert etIDCard != null;
		etIDCard.addTextChangedListener(new TextWatcher() {

			private int action = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence str, int start, int before, int count) {
				action = before;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					idReset.setVisibility(View.VISIBLE);
					if (action == 0) {
						// 增
						if (s.length() == 7 || s.length() == 12 || s.length() == 17) {
							s.insert(s.length() - 1, " ");
						}
					} else {
						// 删
						if (s.length() == 7 || s.length() == 12 || s.length() == 17) {
							s.delete(s.length() - 1, s.length());
						}
					}

				} else {
					idReset.setVisibility(View.GONE);
				}

			}
		});

		//调整银行卡格式
		assert etCard != null;
		etCard.addTextChangedListener(new TextWatcher() {

			private int action = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence str2, int start, int before, int count) {
				// before == 0 增
				// before == 1 删
				if (str2.length() > 0) {
					format_card.setText(str2);
				}
				action = before;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					format_card.setVisibility(View.VISIBLE);
					cardReset.setVisibility(View.VISIBLE);
					if (action == 0) {
						// 增
						if (s.length() == 5 || s.length() == 10 || s.length() == 15 || s.length() == 20) {
							s.insert(s.length() - 1, " ");
						}
					} else {
						// 删
						if (s.length() == 5 || s.length() == 10 || s.length() == 15 || s.length() == 20) {
							s.delete(s.length() - 1, s.length());
						}
					}

				} else {
					format_card.setVisibility(View.GONE);
					cardReset.setVisibility(View.GONE);
				}
			}
		});

		etVerify.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					btnNext.setBackground(getResources().getDrawable(R.drawable.shape_login_press));
				} else {
					btnNext.setBackground(getResources().getDrawable(R.drawable.selector_invite_btn));
				}
			}
		});
	}

}
