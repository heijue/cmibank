package cn.app.yimirong.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.app.yimirong.R;
//import cn.app.base.utilities.YGTools;
//import cn.app.carloanfinanc.R;

public class YinDaoView extends RelativeLayout {
	private Context mContext;
	private View view;
	public TextView TVtY;
	private Resources mResources;

	public YinDaoView(Context context) {

		super(context);
		mContext = context;
		mResources = mContext.getResources();
		init();
	}

	public YinDaoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mResources = mContext.getResources();
		init();

	}


	private void init() {
//        if(this.isInEditMode()){
//
//            return;
//        }
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.layout_yingdao, null);
		//emptyTextView = (TextView) view.findViewById(R.id.mybase_emtyView_text);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.addView(view, params);

		TVtY = (TextView) view.findViewById(R.id.tiyin_bt);


	}

	public void setTiyinBTVIsible(boolean isvisiable) {


		if (isvisiable) {

			TVtY.setVisibility(View.VISIBLE);
		} else {

			TVtY.setVisibility(View.GONE);

		}


	}


}
