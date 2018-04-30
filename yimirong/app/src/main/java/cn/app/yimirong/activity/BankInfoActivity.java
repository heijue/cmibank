package cn.app.yimirong.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.utils.BitmapUtils;
import cn.app.yimirong.utils.GlideRoundTransform;
import cn.app.yimirong.utils.GlideUtils;

public class BankInfoActivity extends BaseActivity {

	private TextView tvName;
	private TextView tvBank;
	private TextView tvCard;
	private ImageView bankLogo;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_bank_info);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("银行卡信息");
		tvName = (TextView) findViewById(R.id.activity_bank_info_name);
		tvBank = (TextView) findViewById(R.id.activity_bank_info_bank);
		tvCard = (TextView) findViewById(R.id.activity_bank_info_card);
		bankLogo = (ImageView) findViewById(R.id.bank_logo);
	}

	@Override
	public void initData() {
		updateView();
	}

	private void updateView() {
		if (App.userinfo != null && App.userinfo.identity != null) {
			tvName.setText(App.userinfo.identity.realName);
			tvBank.setText(App.userinfo.identity.bankname);
			String str = App.userinfo.identity.cardnoTop + "     ****   ****     "
					+ App.userinfo.identity.cardno;
			tvCard.setText(str);
			String url = API.BANK_ICON_BASE + App.userinfo.identity.bankid + ".jpg";
			Glide.with(this).load(url).centerCrop().placeholder(R.drawable.yinhangbeijing)
					.transform(new GlideRoundTransform(this,2,this.getResources().getColor(R.color.white)))
					.diskCacheStrategy(DiskCacheStrategy.SOURCE).into(bankLogo);
		}
	}

}
