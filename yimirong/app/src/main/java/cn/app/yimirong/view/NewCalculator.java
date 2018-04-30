package cn.app.yimirong.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.app.yimirong.R;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;

/**
 * Created by xiaor on 2016/8/26.
 */
public class NewCalculator implements View.OnClickListener {

	// 期限
	private int days;

	// 年化利率
	private double income;

	private Context context;

	private Activity act;

	private EditText etMoney;
	private double money;

	private TextView tvDays, term;

	private EditText etDays;

	private TextView tvIncome;

	private TextView tvProfit;
	private double profit;
	private InputListener listener;
	private char[] buffer = new char[]{};
	private int len = 0;

	private int type;

	public PopupWindow cal;

	/**
	 * 数字按钮
	 */
	private TextView tvNum1;
	private TextView tvNum2;
	private TextView tvNum3;
	private TextView tvNum4;
	private TextView tvNum5;
	private TextView tvNum6;
	private TextView tvNum7;
	private TextView tvNum8;
	private TextView tvNum9;
	private TextView tvNum0;


	private char ch;
	/**
	 * 删除按钮
	 */
	private TextView ivDel;

	/**
	 * 清空按钮
	 */
	private TextView tvClear;

//	private Handler handler=new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case 1:
//					String str = null;
//					for (int i = 0; i < buffer.length-1; i++) {
//						str+=buffer[i];
//					}
//					etMoney.setText(str);
//					break;
//			}
//			super.handleMessage(msg);
//		}
//	};

	public NewCalculator(Context context,Activity act,int type){
		this.context=context;
		this.act=act;
		this.type = type;
		cal = createKeyboard();
	}

	public NewCalculator(Context context, int days, double income,Activity act, int type) {
		this(context,act, type);
		this.days = days;
		this.income = income;
		tvDays.setText("天数     " + days);
		tvIncome.setText("历史年化收益率" + income + "%");
//		this.setInputListener(new InputListener() {
//			@Override
//			public void onInput(char ch) {
//				handleInput(ch);
//			}
//
//			@Override
//			public void onDelete(boolean isDeleteAll) {
//				handleDelete(isDeleteAll);
//			}
//		});
	}

	private void handleDelete(boolean is){
		etMoney.setText("");
	}

	protected void handleInput(char ch) {
		buffer[len] = ch;
		len++;
		etMoney.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String str = null;
				for (int i = 0; i < buffer.length-1; i++) {
					str+=buffer[i];
				}
				etMoney.setText(str);
				return false;
			}
		});
	}


	/**
	 * 设置输入监听
	 *
	 * @param listener
	 */
	public void setInputListener(InputListener listener) {
		this.listener = listener;
	}




	/**
	 * 创建数字键盘
	 */
	public PopupWindow createKeyboard() {

		final View view = View.inflate(context, R.layout.new_calculator, null);

		PopupWindow popup = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);

		popup.setAnimationStyle(R.style.mypopwindow_anim_style);
		WindowManager.LayoutParams params = act.getWindow().getAttributes();
		params.alpha = 0.7f;
		act.getWindow().setAttributes(params);

		popup.showAtLocation(act.getWindow().getDecorView(), Gravity.BOTTOM, 0, WindowManager.LayoutParams.WRAP_CONTENT);

		ImageButton btnClose = (ImageButton) view
				.findViewById(R.id.dialog_calculator_close);

		btnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cal.dismiss();
			}
		});

		tvClear = (TextView) view.findViewById(R.id.keyboard_btn_close);

		tvClear.setText(".");

		tvClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onDelete(true);
				}
			}
		});

		tvNum1 = (TextView) view.findViewById(R.id.soft_key_1);
		tvNum1.setOnClickListener(this);

		tvNum2 = (TextView) view.findViewById(R.id.soft_key_2);
		tvNum2.setOnClickListener(this);

		tvNum3 = (TextView) view.findViewById(R.id.soft_key_3);
		tvNum3.setOnClickListener(this);

		tvNum4 = (TextView) view.findViewById(R.id.soft_key_4);
		tvNum4.setOnClickListener(this);

		tvNum5 = (TextView) view.findViewById(R.id.soft_key_5);
		tvNum5.setOnClickListener(this);

		tvNum6 = (TextView) view.findViewById(R.id.soft_key_6);
		tvNum6.setOnClickListener(this);

		tvNum7 = (TextView) view.findViewById(R.id.soft_key_7);
		tvNum7.setOnClickListener(this);

		tvNum8 = (TextView) view.findViewById(R.id.soft_key_8);
		tvNum8.setOnClickListener(this);

		tvNum9 = (TextView) view.findViewById(R.id.soft_key_9);
		tvNum9.setOnClickListener(this);

		tvNum0 = (TextView) view.findViewById(R.id.soft_key_0);
		tvNum0.setOnClickListener(this);

		ivDel = (TextView) view.findViewById(R.id.keyboard_backspace);
		ivDel.setOnClickListener(this);

		// 长按删除所有
		ivDel.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (listener != null) {
					listener.onDelete(true);
				}
				return true;
			}
		});

		ivDel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					// 删除一个字符
					listener.onDelete(false);
				}
			}
		});

		etMoney = (EditText) view.findViewById(R.id.dialog_calculator_input);
		etMoney.setFocusable(true);
		etMoney.setFocusableInTouchMode(true);
		etMoney.requestFocus();
		etMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String tmpStr = s.toString();
				int dotPos = tmpStr.indexOf(".");
				if (dotPos <= 0) {
					return;
				}
				if (tmpStr.length() - dotPos - 1 > 2) {
					//小数点后位数大于2，删除多余位数
					s.delete(dotPos + 3, dotPos + 4);
				}
				if (type == BaseProduct.TYPE_DQ && s!=null && !s.equals("")){
					calcDQ();
					showCalcResult();
				}
			}
		});

		tvDays = (TextView) view.findViewById(R.id.dialog_calculator_days);

		etDays = (EditText) view
				.findViewById(R.id.dialog_calculator_input_days);
		etDays.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (type == BaseProduct.TYPE_KH && s!=null && !s.equals("")){
					calcHQ();
					showCalcResult();
				}
			}
		});

		term = (TextView) view.findViewById(R.id.qixian_text);

		if (type == BaseProduct.TYPE_DQ) {
			etDays.setVisibility(View.GONE);
			tvDays.setVisibility(View.VISIBLE);
			term.setVisibility(View.VISIBLE);
		} else {
			term.setVisibility(View.GONE);
			tvDays.setVisibility(View.GONE);
			etDays.setVisibility(View.VISIBLE);
		}

		tvIncome = (TextView) view
				.findViewById(R.id.dialog_calculator_income);

		tvProfit = (TextView) view.findViewById(R.id.dialog_calctor_profit);


		popup.setAnimationStyle(R.style.mypopwindow_anim_style);

		popup.setOutsideTouchable(false);

		return popup;
	}

	/**
	 * 计算
	 */
	protected void calculate() {

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
		profit = money * days * (income / 100.0) / 365.0;
	}

	/**
	 * 计算活期收益
	 */
	private void calcHQ() {
		double temp = 0f;
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
		tvIncome.setText("历史年化收益率" + bd + "%");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.soft_key_1:
				ch = '1';
				break;

			case R.id.soft_key_2:
				ch = '2';
				break;

			case R.id.soft_key_3:
				ch = '3';
				break;

			case R.id.soft_key_4:
				ch = '4';
				break;

			case R.id.soft_key_5:
				ch = '5';
				break;

			case R.id.soft_key_6:
				ch = '6';
				break;

			case R.id.soft_key_7:
				ch = '7';
				break;

			case R.id.soft_key_8:
				ch = '8';
				break;

			case R.id.soft_key_9:
				ch = '9';
				break;

			case R.id.soft_key_0:
				ch = '0';
				break;

			default:
				break;
		}

		if (listener != null) {
			listener.onInput(ch);
		}
	}


	/**
	 * 输入监听
	 *
	 * @author android
	 */
	public interface InputListener {

		public void onInput(char ch);

		public void onDelete(boolean isDeleteAll);

	}

}
