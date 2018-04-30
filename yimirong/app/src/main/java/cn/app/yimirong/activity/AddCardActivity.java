package cn.app.yimirong.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;

public class AddCardActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout rlAddCard;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_add_card);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("银行卡管理");
		rlAddCard = (RelativeLayout) findViewById(R.id.activity_add_card_wrapper);
		rlAddCard.setOnClickListener(this);
	}

	@Override
	public void initData() {

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_add_card_wrapper:
				toBindCard();
				break;

			default:
				break;
		}
	}

	/**
	 * 去绑定银行卡
	 */
	private void toBindCard() {
		Class claz = BindActivity.class;
		Intent intent = new Intent(context, claz);
		startActivity(intent);
		finish();
	}

}
