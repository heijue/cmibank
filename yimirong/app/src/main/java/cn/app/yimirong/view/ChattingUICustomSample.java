package cn.app.yimirong.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMChattingPageUI;
import com.alibaba.mobileim.conversation.YWConversation;

import cn.app.yimirong.R;

/**
 * Created by xiaor on 2017/1/3.
 */

public class ChattingUICustomSample extends IMChattingPageUI {

	public ChattingUICustomSample(Pointcut pointcut) {
		super(pointcut);
	}

	@Override
	public int getDefaultHeadImageResId() {
		return R.drawable.ico_logo_online_36;
	}


	/**
	 * 是否需要圆角矩形的头像
	 *
	 * @return true:需要圆角矩形
	 * <br>
	 * false:不需要圆角矩形，默认为圆形
	 * <br>
	 * 注：如果返回true，则需要使用{@link #getRoundRectRadius()}给出圆角的设置半径，否则无圆角效果
	 */
	@Override
	public boolean isNeedRoundRectHead() {
		return true;
	}


	/**
	 * 返回设置圆角矩形的圆角半径大小
	 *
	 * @return 0:如果{@link #isNeedRoundRectHead()}返回true，此处返回0则表示头像显示为直角正方形
	 */
	@Override
	public int getRoundRectRadius() {
		return 10;
	}

	@Override
	public View getCustomTitleView(Fragment fragment, final Context context, LayoutInflater inflater, YWConversation conversation) {

		View v = inflater.inflate(R.layout.kefu_chat_bar,null);

		TextView title = (TextView)v.findViewById(R.id.title_bar_title_text);
		title.setText("易米融客服");

		TextView back = (TextView)v.findViewById(R.id.title_bar_back_text);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity ac = (Activity)context;
				ac.finish();
			}
		});

		return v;
	}
}
