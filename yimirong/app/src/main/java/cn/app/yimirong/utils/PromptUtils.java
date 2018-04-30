package cn.app.yimirong.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.app.yimirong.R;
import cn.app.yimirong.activity.FeedBackActivity;


public class PromptUtils {

	/**
	 * debug模式Dialog
	 *
	 * @param activity
	 * @param context
	 * @param title
	 * @param leftBtn
	 * @param rightBtn
	 * @param listener
	 * @return
	 */
	public static Dialog showDebugDialog(Activity activity, Context context, String title, String leftBtn, String rightBtn, final OnDialogClickListener2 listener) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_debug);
		TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_debug_title);
		TextView tvLeft = (TextView) dialog.findViewById(R.id.dialog_debug_left_btn);
		TextView tvRight = (TextView) dialog.findViewById(R.id.dialog_debug_right_btn);

		tvTitle.setText(title);
		tvLeft.setText(leftBtn);
		tvRight.setText(rightBtn);
		tvLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onLeftClick(dialog);
				}
			}
		});
		tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});
		if (activity != null && !activity.isFinishing()) {
			dialog.show();
		}
		return dialog;
	}

	/**
	 * 显示成功消息
	 *
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog showSuccessDialog(Activity activity, Context context,
										   String msg) {
		Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_success);
		TextView tv = (TextView) dialog.findViewById(R.id.dialog_success_msg);
		tv.setText(msg);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
		return dialog;
	}


	public static Dialog showSuccessDialog2(Activity activity, Context context,
										   String msg) {
		Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_show_tishi);
		TextView tv = (TextView) dialog.findViewById(R.id.dialog_success_msg);
		tv.setText(msg);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
		return dialog;
	}



	public static void showDialog1(Activity activity, Context context,
								   String title, String message, String btnText,
								   final OnDialogClickListener1 listener) {
		showDialog1(activity, context, title, message, btnText, listener, false);
	}



	/**
	 * 显示自定义Dialog1
	 *
	 * @param context
	 * @param title
	 * @param message
	 * @param btnText
	 * @param listener
	 */
	public static void showDialog1(Activity activity, Context context,
								   String title, String message, String btnText,
								   final OnDialogClickListener1 listener, boolean cancelable) {

		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_custom1);

		TextView tvTitle = (TextView) dialog
				.findViewById(R.id.dialog_custom1_title);
		TextView tvMessage = (TextView) dialog
				.findViewById(R.id.dialog_custom1_message);
		TextView tvBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom1_btn);

		tvTitle.setText(title);
		tvMessage.setText(Html.fromHtml(message));
		tvBtn.setText(btnText);

		tvBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onClick(dialog);
				}
			}
		});

		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		if (activity != null && !activity.isFinishing()) {
			dialog.show();
		}
	}


	public static void showDialog2(Activity activity,
								   Context context, String message,
								   String content,
								   String rightBtn,
								   final OnDialogClickListener2 listener,
								   boolean cancelable) {
		final Dialog dialog = new Dialog(activity, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_custom2_notitle);
		TextView tvMessage = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_message);

		TextView tvLeftBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_left_btn);

		TextView tvRightBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_right_btn);
		TextView tvMessage1 = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_message1);
		tvMessage.setText(Html.fromHtml(message));
		tvMessage1.setText(content);
		tvRightBtn.setText(rightBtn);

		tvLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onLeftClick(dialog);
				}
			}
		});

		tvRightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});

		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
	}

	public static void showTotalDialog(Activity activity,
								   Context context, String message,
								   String leftBtn,
								   String rightBtn,
								   final OnDialogClickListener2 listener,
								   boolean cancelable) {
		final Dialog dialog = new Dialog(activity, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_total_notice);
		TextView tvMessage = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_message);

		TextView tvLeftBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_left_btn);

		TextView tvRightBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_notitle_right_btn);

		tvMessage.setText(Html.fromHtml(message));
		tvLeftBtn.setText(leftBtn);
		tvRightBtn.setText(rightBtn);

		tvLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onLeftClick(dialog);
				}
			}
		});

		tvRightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});

		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
	}



	public static void showDialog2(Activity activity, Context context,
								   String title, String message, String leftBtn, String rightBtn,
								   final OnDialogClickListener2 listener) {
		showDialog2(activity, context, title, message, leftBtn, rightBtn, listener, false);
	}

	/**
	 * 显示自定义的Dialog2
	 *
	 * @param context
	 * @param title
	 * @param message
	 * @param leftBtn
	 * @param rightBtn
	 * @param listener
	 */
	public static void showDialog2(Activity activity, Context context,
								   String title, String message, String leftBtn, String rightBtn,
								   final OnDialogClickListener2 listener, boolean cancelable) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_custom2);

		TextView tvTitle = (TextView) dialog
				.findViewById(R.id.dialog_custom2_title);

		TextView tvMessage = (TextView) dialog
				.findViewById(R.id.dialog_custom2_message);

		TextView tvLeftBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_left_btn);

		TextView tvRightBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_right_btn);

		tvTitle.setText(title);
		tvMessage.setText(Html.fromHtml(message));
		tvLeftBtn.setText(leftBtn);
		tvRightBtn.setText(rightBtn);

		tvLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onLeftClick(dialog);
				}
			}
		});

		tvRightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});

		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
	}

	/**
	 * 显示自定义的Dialog4
	 *
	 * @param context
	 * @param title
	 * @param message
	 * @param leftBtn
	 * @param rightBtn
	 * @param listener
	 */
	public static void showDialog4(Activity activity, Context context,
								   String title, String message, String leftBtn, String rightBtn,
								   final OnDialogClickListener2 listener, boolean cancelable) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_custom3);

		TextView tvMessage = (TextView) dialog
				.findViewById(R.id.dialog_custom2_message);

		TextView tvLeftBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_left_btn);

		TextView tvRightBtn = (TextView) dialog
				.findViewById(R.id.dialog_custom2_right_btn);

		tvMessage.setText(Html.fromHtml(message));
		tvLeftBtn.setText(leftBtn);
		tvRightBtn.setText(rightBtn);

		tvLeftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onLeftClick(dialog);
				}
			}
		});

		tvRightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});

		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
	}

	public static void showDialog3(Context context, String msg, final OnDialogClickListener1 listener) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_error_msg);
		TextView textMsg = (TextView) dialog.findViewById(R.id.error_msg_text);
		Button textBtn = (Button) dialog.findViewById(R.id.error_msg_btn);
		textMsg.setText(msg);
		textBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick(dialog);
				}
			}
		});
		dialog.show();
	}


	public static void showDialog4(Context context, String msg, final OnDialogClickListener1 listener) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_exp_layout);
		TextView textMsg = (TextView) dialog.findViewById(R.id.error_msg_text);
		ImageView cancel = (ImageView) dialog.findViewById(R.id.exp_img_cancel);
		Button textBtn = (Button) dialog.findViewById(R.id.error_msg_btn);
		textMsg.setText(Html.fromHtml(msg));

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		textBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick(dialog);
				}
			}
		});
		dialog.show();
	}

	public static void showDialog5(Context context, String msg, final OnDialogClickListener1 listener) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_exp_notice);
		TextView textMsg = (TextView) dialog.findViewById(R.id.exp_song_money);
		ImageView cancel = (ImageView) dialog.findViewById(R.id.exp_dialog_dismiss);
		TextView textBtn = (TextView) dialog.findViewById(R.id.exp_dialog_zhucebtn);
		textMsg.setText(Html.fromHtml(msg));

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		textBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick(dialog);
				}
			}
		});
		dialog.show();
	}


	/**
	 * 询问是否验证
	 */
	public static Dialog showIfVerify(Activity activity, Context context,
									  String message, String leftBtn, String rightBtn,
									  final OnDialogClickListener2 listener, boolean cancelable) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_have_title);
		TextView msg = (TextView) dialog.findViewById(R.id.shoujihao_num);
		Button left = (Button) dialog.findViewById(R.id.dialog_btnoff);
		Button right = (Button) dialog.findViewById(R.id.dialog_btnok);

		msg.setText(message);
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁
			dialog.show();
		}
		return dialog;
	}


	/**
	 * 显示评论窗口
	 * */
	public static Dialog
	showEvaluateDialog(Activity activity, final Context context, String allow, final OnDialogClickListener2 listener){
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.evaluate_dialog);
		TextView evaluate = (TextView) dialog.findViewById(R.id.go_evaluate);
		TextView feed = (TextView) dialog.findViewById(R.id.go_feedback);
		TextView dis = (TextView) dialog.findViewById(R.id.dismiss);

		dis.setText(allow);
		dis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null){
					listener.onLeftClick(dialog);
				}
			}
		});

		evaluate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});

		feed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				MobclickAgent.onEvent(context,"fkwt");
				context.startActivity(new Intent(context, FeedBackActivity.class));
			}
		});

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁才显示
			dialog.show();
		}
		return dialog;
	}


	/**
	 * 显示提现信息
	 */
	public static Dialog showCashDialog(Activity activity, Context context, double benci, double really, int sxf, String msg, String left, String right, final OnDialogClickListener2 listener) {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_tiixian_shoufei);
		TextView benciMoney = (TextView) dialog.findViewById(R.id.benci_tixian_jine);
		TextView shijiMoney = (TextView) dialog.findViewById(R.id.shiji_tixian_jine);
		TextView benciCash = (TextView) dialog.findViewById(R.id.benci_shoufei);
		TextView btn_off = (TextView) dialog.findViewById(R.id.dialog_btnoff);
		TextView btn_ok = (TextView) dialog.findViewById(R.id.dialog_btnok);

		benciMoney.setText("￥"+benci);
		shijiMoney.setText("￥"+really);
		double shouxu = (double) sxf;
		benciCash.setText("￥"+shouxu);
		btn_off.setText(left);
		btn_ok.setText(right);

		btn_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (listener != null) {
					listener.onRightClick(dialog);
				}
			}
		});
		if (activity != null && !activity.isFinishing()) {
			// Activity没有被销毁才显示
			dialog.show();
		}
		return dialog;
	}


	public interface OnDialogClickListener1 {
		public void onClick(Dialog dialog);
	}

	public interface OnDialogClickListener2 {

		public void onLeftClick(Dialog dialog);

		public void onRightClick(Dialog dialog);

	}
	public interface OnDialogClickListener3 {

		public void onLeftClick();

		public void onRightClick();

	}

	public static void showDialog3(Dialog dialog, String title,final OnDialogClickListener3 onDialogClickListener,String leftBtn
	,String rightBtn){
		dialog.setContentView(R.layout.dialog_chongzhi_info);
		Button btnCanel = (Button) dialog.findViewById(R.id.dialog_chongzhi_info_cancel);
		btnCanel.setText(leftBtn);
		TextView tv_title = (TextView) dialog.findViewById(R.id.dialog_text);
		tv_title.setText(title);
		Button btnOK = (Button) dialog.findViewById(R.id.dialog_chongzhi_info_ok);
		btnOK.setText(rightBtn);
		dialog.show();
		btnCanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onDialogClickListener.onLeftClick();
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onDialogClickListener.onRightClick();
			}
		});

	}
}
