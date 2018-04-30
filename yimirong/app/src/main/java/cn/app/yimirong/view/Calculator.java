package cn.app.yimirong.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.app.yimirong.R;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;

public class Calculator implements EditText.OnFocusChangeListener{

	// 期限
	private int days;

	// 年化利率
	private double income;

	private Context context;

	private EditText etMoney;
	private double money;

	private TextView tvDays, term;

	private EditText etDays;

	private TextView tvIncome,incomeT;

	private TextView tvProfit;
	private double profit;
	public ImageButton btnClose;

	private int type;

	public Dialog dialog;

	public Calculator(Context context, final int type) {
		this.context = context;
		this.type = type;

		dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.new_calculator);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);

		btnClose = (ImageButton) dialog
				.findViewById(R.id.dialog_calculator_close);
//		btnClose.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				closeCalculator();
//			}
//		});

		etMoney = (EditText) dialog.findViewById(R.id.dialog_calculator_input);
		etMoney.requestFocus();
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(etMoney, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		etMoney.setOnFocusChangeListener(this);
		etMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
//				String tmpStr = s.toString();
//				int dotPos = tmpStr.indexOf(".");
//				if (dotPos <= 0) {
//					return;
//				}
//				if (tmpStr.length() - dotPos - 1 > 2) {
//					//小数点后位数大于2，删除多余位数
//					s.delete(dotPos + 3, dotPos + 4);
//				}
				if (type == BaseProduct.TYPE_DQ && s!=null && !s.equals("")){
					calcDQ();
					showCalcResult();
				}
				if (type == BaseProduct.TYPE_KH && etDays.getText().toString().trim().length()>0 && s!=null && !s.equals("")){
					calcHQ();
					showCalcResult();
				}
			}
		});
		tvDays = (TextView) dialog.findViewById(R.id.dialog_calculator_days);

		etDays = (EditText) dialog
				.findViewById(R.id.dialog_calculator_input_days);
		etDays.setOnFocusChangeListener(this);

		etDays.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
					if (type == BaseProduct.TYPE_KH && s != null && !s.equals("")) {
						calcHQ();
						showCalcResult();
					}
			}
		});

		term = (TextView) dialog.findViewById(R.id.qixian_text);

		if (type == BaseProduct.TYPE_DQ) {
			etDays.setVisibility(View.GONE);
			tvDays.setVisibility(View.VISIBLE);
			term.setVisibility(View.VISIBLE);
		} else {
			term.setVisibility(View.GONE);
			tvDays.setVisibility(View.GONE);
			etDays.setVisibility(View.VISIBLE);
		}

		tvIncome = (TextView) dialog
				.findViewById(R.id.dialog_calculator_income);

		incomeT = (TextView) dialog.findViewById(R.id.inconme);

		tvProfit = (TextView) dialog.findViewById(R.id.dialog_calctor_profit);

//		Button btnSubmit = (Button) dialog.findViewById(R.id.dialog_calculator_submit);
//		btnSubmit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				calculate();
//			}
//		});
	}

	public Calculator(Context context, int days, double income, int type) {
		this(context, type);
		this.days = days;
		this.income = income;
		tvDays.setText("天数     " + days);
		tvIncome.setText("历史年化收益率");
		incomeT.setText(income + "%");
	}

	/**
	 * 计算
	 */
	protected void calculate() {
		if (!getInput()) {
			return;
		}

		if (type == BaseProduct.TYPE_DQ) {
			calcDQ();
		} else {
			calcHQ();
		}
		showCalcResult();
	}

	/**
	 * 计算定期收益
	 */
	private void calcDQ() {
		// 计算定期的收益
		if (etMoney.getText().toString().trim().length()>0) {
			money = Double.parseDouble(etMoney.getText().toString().trim());
		}
		profit = money * days * (income / 100.0) / 365.0;
	}

	/**
	 * 计算活期收益
	 */
	private void calcHQ() {
		double temp = 0f;
		if (etMoney.getText().toString().trim().length()>0 && etDays.getText().toString().trim().length()>0) {
			money = Double.parseDouble(etMoney.getText().toString().trim());
			days = Integer.parseInt(etDays.getText().toString().trim());
		}
		if (days > 365) {
			// 天数过大，不予计算
			ToastUtils.show(context, "输入天数不能大于365");
			profit = temp;
			return;
		}

		for (int i = 1; i <= days; i++) {
			double di = (money + temp) * ((income / 100.0) / 365.0);
			temp += di;
		}
		profit = temp;
	}

	/**
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		String moneyStr = etMoney.getText().toString().trim();
		if (StringUtils.isBlank(moneyStr)) {
			ToastUtils.show(context, "请输入金额");
			etMoney.setText("");
			return false;
		} else {
			try {
				money = Double.parseDouble(moneyStr);
			} catch (Exception e) {
				ToastUtils.show(context, "请输入金额");
				etMoney.setText("");
				return false;
			}
			if (money <= 0) {
				ToastUtils.show(context, "请输入金额");
				etMoney.setText("");
				return false;
			}
		}

		if (type == BaseProduct.TYPE_KH
				|| type == BaseProduct.TYPE_KL) {
			// 如果是活期，还要获取理财天数
			String daysStr = etDays.getText().toString().trim();
			if (StringUtils.isBlank(daysStr)) {
				ToastUtils.show(context, "请输入天数");
				etDays.setText("");
				return false;
			} else {
				try {
					days = Integer.parseInt(daysStr);
				} catch (Exception e) {
					ToastUtils.show(context, "请输入天数");
					etDays.setText("");
					return false;
				}
				if (days <= 0) {
					ToastUtils.show(context, "请输入天数");
					etDays.setText("");
					return false;
				}
			}
		}
		return true;
	}

	protected void showCalcResult() {
		tvProfit.setText(SystemUtils.getDoubleStr(profit));
	}

	public void setDays(int days) {
		this.days = days;
		tvDays.setText(days + "");
	}

	public void setIncome(double income) {
		this.income = income;
		BigDecimal bd = new BigDecimal(income + "");
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		tvIncome.setText("历史年化收益率");
		incomeT.setText(bd + "%");
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()){
			case R.id.dialog_calculator_input:
					etMoney.setBackgroundResource(R.drawable.jisuanqi_shape);
					etDays.setBackgroundResource(R.drawable.shape_feed_back);
				break;
			case R.id.dialog_calculator_input_days:
					etMoney.setBackgroundResource(R.drawable.shape_feed_back);
					etDays.setBackgroundResource(R.drawable.jisuanqi_shape);
				break;
		}
	}
}
