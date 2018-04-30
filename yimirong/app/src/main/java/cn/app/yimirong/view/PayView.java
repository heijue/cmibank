package cn.app.yimirong.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import cn.app.yimirong.R;
import cn.app.yimirong.utils.ScreenUtils;
import cn.app.yimirong.utils.SystemUtils;

public class PayView implements OnClickListener {

	/**
	 * 半透明背景
	 */

	public static final int PAYTYPE_ACCOUNT = 1;

	public static final int PAYTYPE_BANK = 2;

	private PopupWindow background;

	private PopupWindow paywindow;

	private PopupWindow keyboard;

	private Context context;

	private OnInputListener listener;

	public boolean isShowing = false;

	/**
	 * 钱数
	 */
	private TextView tvMoneyNum;

	/**
	 * 银行图标
	 */
	private ImageView ivBankIcon;

	/**
	 * 银行名称
	 */
	private TextView tvBankName;

	/**
	 * 密码第1位
	 */
	private ImageView ivPass1;

	/**
	 * 密码第2位
	 */
	private ImageView ivPass2;

	/**
	 * 密码第3位
	 */
	private ImageView ivPass3;

	/**
	 * 密码第4位
	 */
	private ImageView ivPass4;

	/**
	 * 密码第5位
	 */
	private ImageView ivPass5;

	/**
	 * 密码第6位
	 */
	private ImageView ivPass6;

	/**
	 * 输入的密码长度
	 */
	private int inputLen = 0;

	/**
	 * 存储当前输入的字符
	 */
	private char passChar;

	/**
	 * 存储输入的密码
	 */
	private char[] password;


	public PayView(Context context) {
		this.context = context;
		background = createBackground();
		paywindow = createPayWindow();
		keyboard = createKeyboard();
		password = new char[6];
	}

	/**
	 * 设置钱数
	 *
	 * @param money
	 */
	public void setMoney(double money) {
		String moneyStr = SystemUtils.getDoubleStr(money);
		tvMoneyNum.setText("￥" + moneyStr);
	}

	public void setPayType(int paytype) {
		if (paytype == PAYTYPE_BANK) {
			ivBankIcon.setVisibility(View.VISIBLE);
		} else {
			ivBankIcon.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置银行图标
	 *
	 * @param bankIcon
	 */
	public void setBankIcon(Drawable bankIcon) {
		ivBankIcon.setImageDrawable(bankIcon);
	}

	public void setBankIcon(String url) {
		BitmapUtils bitmapUtil = new BitmapUtils(context);
		bitmapUtil.display(ivBankIcon, url);
	}

	public void setBankName(String bankName) {
		tvBankName.setText(bankName);
	}

	/**
	 * 显示
	 */
	public void show(View parent) {
		if (!isShowing) {
			inputLen = 0;
			clearAllPassImage();
			background.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER,
					0, 0);
			int margin = (int) ScreenUtils.dpToPx(context, 100);
			paywindow.showAtLocation(parent, Gravity.CENTER_HORIZONTAL
					| Gravity.TOP, 0, margin);
			int offsetY = SystemUtils.getNavBarHeight(context);
			keyboard.showAtLocation(parent, Gravity.BOTTOM, 0, offsetY);
			isShowing = true;
		}
	}

	/**
	 * 消失
	 */
	public void dismiss() {
		if (isShowing) {
			keyboard.dismiss();
			paywindow.dismiss();
			background.dismiss();
			isShowing = false;
			inputLen = 0;
			clearAllPassImage();
		}
	}

	/**
	 * 设置显示监听
	 *
	 * @param listener
	 */
	public void setOnInputListener(OnInputListener listener) {
		this.listener = listener;
	}

	/**
	 * 创建背景dialog
	 */
	public PopupWindow createBackground() {
		View view = View.inflate(context, R.layout.full_screen_trans, null);
		PopupWindow popup = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		popup.setOutsideTouchable(false);
		return popup;
	}

	/**
	 * 创建支付对话框
	 */
	public PopupWindow createPayWindow() {

		View view = View.inflate(context, R.layout.dialog_pay, null);

		int w = (int) ScreenUtils.dpToPx(context, 280);
		int y = (int) ScreenUtils.dpToPx(context, 262);
		PopupWindow popup = new PopupWindow(view, w, y);

		popup.setAnimationStyle(R.style.PayDialogAnimation);

		ImageButton btnClose = (ImageButton) view
				.findViewById(R.id.dialog_close_btn);

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		tvMoneyNum = (TextView) view.findViewById(R.id.dialog_money_num);

		ivBankIcon = (ImageView) view.findViewById(R.id.dialog_bank_icon);

		tvBankName = (TextView) view.findViewById(R.id.dialog_bank_name);

		ivPass1 = (ImageView) view.findViewById(R.id.dialog_password_1);
		ivPass2 = (ImageView) view.findViewById(R.id.dialog_password_2);
		ivPass3 = (ImageView) view.findViewById(R.id.dialog_password_3);
		ivPass4 = (ImageView) view.findViewById(R.id.dialog_password_4);
		ivPass5 = (ImageView) view.findViewById(R.id.dialog_password_5);
		ivPass6 = (ImageView) view.findViewById(R.id.dialog_password_6);

		popup.setOutsideTouchable(false);

		return popup;
	}

	/**
	 * 创建数字键盘
	 */
	public PopupWindow createKeyboard() {

		View view = View.inflate(context, R.layout.number_keyboard, null);

		PopupWindow popup = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		TextView btnClose = (TextView) view
				.findViewById(R.id.keyboard_btn_close);

		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

        /*
	  数字按钮
     */
		TextView tvNum1 = (TextView) view.findViewById(R.id.soft_key_1);
		tvNum1.setOnClickListener(this);

		TextView tvNum2 = (TextView) view.findViewById(R.id.soft_key_2);
		tvNum2.setOnClickListener(this);

		TextView tvNum3 = (TextView) view.findViewById(R.id.soft_key_3);
		tvNum3.setOnClickListener(this);

		TextView tvNum4 = (TextView) view.findViewById(R.id.soft_key_4);
		tvNum4.setOnClickListener(this);

		TextView tvNum5 = (TextView) view.findViewById(R.id.soft_key_5);
		tvNum5.setOnClickListener(this);

		TextView tvNum6 = (TextView) view.findViewById(R.id.soft_key_6);
		tvNum6.setOnClickListener(this);

		TextView tvNum7 = (TextView) view.findViewById(R.id.soft_key_7);
		tvNum7.setOnClickListener(this);

		TextView tvNum8 = (TextView) view.findViewById(R.id.soft_key_8);
		tvNum8.setOnClickListener(this);

		TextView tvNum9 = (TextView) view.findViewById(R.id.soft_key_9);
		tvNum9.setOnClickListener(this);

		TextView tvNum0 = (TextView) view.findViewById(R.id.soft_key_0);
		tvNum0.setOnClickListener(this);

        /*
      删除按钮
     */
		TextView ivDel = (TextView) view.findViewById(R.id.keyboard_backspace);
		ivDel.setOnClickListener(this);

		// 长按删除所有
		ivDel.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				inputLen = 0;
				clearAllPassImage();
				return true;
			}
		});

		ivDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (inputLen > 0) {
					clearPassImage();
					inputLen--;
				}
			}
		});

		popup.setAnimationStyle(R.style.KeyboardDialogAnimation);

		popup.setOutsideTouchable(false);

		return popup;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.soft_key_1:
				passChar = '1';
				break;

			case R.id.soft_key_2:
				passChar = '2';
				break;

			case R.id.soft_key_3:
				passChar = '3';
				break;

			case R.id.soft_key_4:
				passChar = '4';
				break;

			case R.id.soft_key_5:
				passChar = '5';
				break;

			case R.id.soft_key_6:
				passChar = '6';
				break;

			case R.id.soft_key_7:
				passChar = '7';
				break;

			case R.id.soft_key_8:
				passChar = '8';
				break;

			case R.id.soft_key_9:
				passChar = '9';
				break;

			case R.id.soft_key_0:
				passChar = '0';
				break;

			default:
				break;
		}

		password[inputLen] = passChar;
		inputLen++;
		showPassImage();

		// 输满6位
		if (inputLen == 6) {
			// 输入完毕
			dismiss();
			String passStr = new String(password);
			// ToastUtils.show(context, passStr);
			if (listener != null) {
				listener.onInputDone(passStr);
			}
		}

	}

	private void showPassImage() {
		int PASS_IMAGE = R.drawable.shape_dot_black;
		switch (inputLen) {
			case 1:
				ivPass1.setImageResource(PASS_IMAGE);
				break;

			case 2:
				ivPass2.setImageResource(PASS_IMAGE);
				break;

			case 3:
				ivPass3.setImageResource(PASS_IMAGE);
				break;

			case 4:
				ivPass4.setImageResource(PASS_IMAGE);
				break;

			case 5:
				ivPass5.setImageResource(PASS_IMAGE);
				break;

			case 6:
				ivPass6.setImageResource(PASS_IMAGE);
				break;
		}
	}

	private void clearPassImage() {
		switch (inputLen) {

			case 1:
				ivPass1.setImageDrawable(null);
				break;

			case 2:
				ivPass2.setImageDrawable(null);
				break;

			case 3:
				ivPass3.setImageDrawable(null);
				break;

			case 4:
				ivPass4.setImageDrawable(null);
				break;

			case 5:
				ivPass5.setImageDrawable(null);
				break;

		}
	}

	private void clearAllPassImage() {
		ivPass1.setImageDrawable(null);
		ivPass2.setImageDrawable(null);
		ivPass3.setImageDrawable(null);
		ivPass4.setImageDrawable(null);
		ivPass5.setImageDrawable(null);
		ivPass6.setImageDrawable(null);
	}

	public interface OnInputListener {
		public void onInputDone(String password);
	}

}
