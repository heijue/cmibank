package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;

public class DQBuyDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView tvName;
	private TextView tvMoney;
	private TextView tvPayType;
	protected TextView tvPayNum;

	private ActionLog actionlog;

	private BaseProduct product;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_dqbuy_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("购买详情");

		tvName = (TextView) findViewById(R.id.activity_dqbuy_detail_pname);
		tvMoney = (TextView) findViewById(R.id.activity_dqbuy_detail_money);
		tvPayType = (TextView) findViewById(R.id.activity_dqbuy_detail_paytype);
		tvPayNum = (TextView) findViewById(R.id.activity_dqbuy_detail_paynum);
		TextView tvProduct = (TextView) findViewById(R.id.activity_dqbuy_detail_product);
		assert tvProduct != null;
		tvProduct.setOnClickListener(this);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			actionlog = (ActionLog) data.getSerializable("actionlog");
		}
		loadProductDetail();
	}

	private void loadProductDetail() {
		if (pmodel != null) {
			showLoading("玩命向钱冲");
			pmodel.getProductDetail(BaseProduct.TYPE_DQ, actionlog.pid,
					new ResponseHandler() {
						@Override
						public <T> void onSuccess(String response, T t) {
							super.onSuccess(response, t);
							closeLoading();
							if (t != null && t instanceof BaseProduct) {
								product = (BaseProduct) t;
								updateView(actionlog);
							}
						}

						@Override
						public void onFailure(String errorCode, String msg) {
							super.onFailure(errorCode, msg);
							closeLoading();
							ToastUtils.show(context, msg);
							updateView(actionlog);
						}
					});
		}
	}

	/**
	 * 更新界面
	 */
	private void updateView(ActionLog actionlog) {
		if (actionlog != null) {
			tvName.setText(actionlog.pname);

			tvMoney.setText(SystemUtils.getDoubleStr(actionlog.money));

			if ("1".equals(actionlog.desc)) {
				tvPayType.setText("账户余额支付");
			} else {
				tvPayType.setText("银行卡支付");
			}
			tvPayNum.setText(actionlog.orderid);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_dqbuy_detail_product:
				toProductDetail();
				break;
		}
	}

	/**
	 * 去产品详情
	 */
	private void toProductDetail() {
		Intent intent = new Intent(context, ProductDetailActivity.class);
		Bundle data = new Bundle();
		if (product != null) {
			product.state = BaseProduct.STATE_SELLOUT;
			data.putSerializable("product", product);
			data.putInt("productType", product.ptype);
			data.putInt("productState", product.state);
			data.putString("pid", product.pid);
			data.putString("title", product.pname);
			data.putBoolean("canbuy", false);
			data.putBoolean("issellout", true);
			data.putBoolean("isyugao", false);
			intent.putExtras(data);
			startActivity(intent);

		} else {
			if (actionlog != null) {
				data.putString("pid", actionlog.pid);
				data.putInt("productType", BaseProduct.TYPE_DQ);
				data.putInt("productState", BaseProduct.STATE_SELLOUT);
				data.putString("title", actionlog.pname);
				data.putBoolean("canbuy", false);
				data.putBoolean("issellout", true);
				data.putBoolean("isyugao", false);
				intent.putExtras(data);
				startActivity(intent);
			}
		}
	}
}
