package cn.app.yimirong.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.utils.SystemUtils;

public class KeyBoard implements OnClickListener {

	private Activity activity;

	public PopupWindow popup;

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

	/**
	 * 删除按钮
	 */
	private TextView ivDel;

	/**
	 * 清空按钮
	 */
	private TextView tvClear;

	private InputListener listener;

	private boolean isShow = false;

	private char ch;

	private static KeyBoard keyboard;

	public static KeyBoard getInstance(Activity activity) {
		if (keyboard == null) {
			keyboard = new KeyBoard(activity);
		}
		return keyboard;
	}

	private KeyBoard(Activity activity) {
		this.activity = activity;
		this.popup = createKeyboard();
	}

	/**
	 * 显示键盘
	 */
	public void show(View parent) {
		if (!isShow
				&& popup != null
				&& activity != null
				&& !activity.isFinishing()) {
			int offsetY = SystemUtils.getNavBarHeight(activity);
			popup.showAtLocation(parent, Gravity.BOTTOM, 0, offsetY);
			isShow = true;
		}
	}

	/**
	 * 关闭键盘
	 */
	public void close() {
		if (isShow
				&& popup != null
				&& activity != null
				&& !activity.isFinishing()) {
			popup.dismiss();
			isShow = false;
		}
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
		View view = View.inflate(activity, R.layout.number_keyboard, null);

		PopupWindow popup = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		tvClear = (TextView) view.findViewById(R.id.keyboard_btn_close);

		tvClear.setText("清空");

		tvClear.setOnClickListener(new OnClickListener() {

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
		ivDel.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (listener != null) {
					listener.onDelete(true);
				}
				return true;
			}
		});

		ivDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					// 删除一个字符
					listener.onDelete(false);
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
