package cn.app.yimirong.activity;

import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.yimirong.view.lock.LockView;

public class SetLockActivity extends BaseActivity {

	private TextView tvHead;

	private int lockTimes = 1;

	private String lockPass;

	private View[] views = new View[9];

	private int[] ids = {R.id.activity_create_lock_preview_1,
			R.id.activity_create_lock_preview_2,
			R.id.activity_create_lock_preview_3,
			R.id.activity_create_lock_preview_4,
			R.id.activity_create_lock_preview_5,
			R.id.activity_create_lock_preview_6,
			R.id.activity_create_lock_preview_7,
			R.id.activity_create_lock_preview_8,
			R.id.activity_create_lock_preview_9};

	@Override
	public void loadView() {
		setContentView(R.layout.activity_create_lock);
		shouldVerify = false;
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("设置手势密码");

		initPreview();

		tvHead = (TextView) findViewById(R.id.activity_create_lock_text);
		tvHead.setText("绘制解锁图案，请至少连接4个点");
		LockView lockView = (LockView) findViewById(R.id.activity_create_lock_view);
		lockView.setPassword(true, null);
		lockView.setCallBack(new LockView.CallBack() {

			@Override
			public void onStart() {
				tvHead.setText("完成后松开手指");
			}

			@Override
			public void onFinish(String password) {
				handleFinish(password);
			}

		});
	}

	private void showReset() {
		setTitleRight(true, new OnRightClickListener() {

			@Override
			public void onClick() {
				reset();
			}
		});
		setRightText("重设");
	}

	@Override
	public void initData() {

	}

	/**
	 * 初始化Preview
	 */
	private void initPreview() {
		for (int i = 0; i < ids.length; i++) {
			views[i] = findViewById(ids[i]);
		}
	}

	/**
	 * 更新预览
	 */
	private void updatePreview(String password) {
		char[] array = password.toCharArray();
		for (int i = 0; i < array.length; i++) {
			int num = Integer.parseInt(String.valueOf(array[i]));
			views[num - 1]
					.setBackgroundResource(R.drawable.shape_lock_preview_bg_red);
		}
	}

	/**
	 * 处理绘制结束
	 *
	 * @param password
	 */
	protected void handleFinish(String password) {
		if (StringUtils.isBlank(password)) {
			return;
		}

		if (password.length() < 4) {
			// 长度小于4
			tvHead.setText("至少连接4个点，请重试");
			return;
		}

		if (lockTimes == 1) {
			tvHead.setText("已记录图案，再次绘制进行确认");
			updatePreview(password);
			lockPass = password;
			lockTimes++;
			showReset();
			return;
		}

		if (lockTimes == 2) {
			if (!password.equals(lockPass)) {
				// 两次密码不一致
				tvHead.setText("输入错误，请重试");
			} else {
				// 两次密码一致
				showSuccess();
			}
		}
	}

	/**
	 * 显示成功
	 */
	protected void showSuccess() {
		titleRight.setClickable(false);
		sp.edit().putString("lockPass", lockPass).commit();
		App.isVerified = true;
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"设置成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 1200);
	}

	/**
	 * 重置预览
	 */
	protected void resetPreview() {
		for (int i = 0; i < views.length; i++) {
			views[i].setBackgroundResource(R.drawable.shape_lock_preview_bg_white);
		}
	}

	/**
	 * 重设
	 */
	private void reset() {
		tvHead.setText("绘制解锁图案，请至少连接4个点");
		lockTimes = 1;
		resetPreview();
		setTitleRight(false, null);
	}
}
