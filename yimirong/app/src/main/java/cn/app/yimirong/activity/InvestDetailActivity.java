package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.bean.UserProduct;
import cn.app.yimirong.model.bean.UserProduct.SingleProduct;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

public class InvestDetailActivity extends BaseActivity {

	private TextView detailMoney;

	private TextView detailProfit;

	private ListView listView;

	private InvestDetailAdapter adapter;

	private UserProduct up;

	private BaseProduct product;
	private TextView rateOfReturn;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_invest_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);

		detailMoney = (TextView) findViewById(R.id.activity_invest_detail_money);

		detailProfit = (TextView) findViewById(R.id.activity_invest_detail_profit);
		rateOfReturn = (TextView) findViewById(R.id.rate_of_return);

		listView = (ListView) findViewById(R.id.activity_invest_detail_list);
	}

	@Override
	public void initData() {
		showLoading("玩命加载中");
		Intent intent = getIntent();
		Serializable s = intent.getExtras().getSerializable("invest_detail");
		if (s instanceof UserProduct) {
			up = (UserProduct) s;
		}

		if (up != null) {
			// 设置标题为产品名称
			DecimalFormat df = new DecimalFormat("######0.00");
			setTitleText(up.pname);
			detailMoney.setText(SystemUtils.getDoubleStr(up.money));
			detailProfit.setText(df.format(up.profit));
			adapter = new InvestDetailAdapter(up.product_list);
			listView.setAdapter(adapter);
			loadProductDetail();
			String income = up.product_list.get(0).income;
			rateOfReturn.setText(income+"%");
		}
	}

	/**
	 * 投资详情数据适配器
	 *
	 * @author android
	 */
	private class InvestDetailAdapter extends BaseAdapter {

		private List<SingleProduct> list;

		public InvestDetailAdapter(List<SingleProduct> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_invest_detail_item, null);

				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_money);
				TextView tvProfit = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_profit);
				ImageView ivBank = (ImageView) convertView
						.findViewById(R.id.activity_invest_detail_item_bank);
				TextView tvType = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_type);
				TextView tvBuyTime = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_buytime);
				TextView tvDays = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_days);
				TextView tvEndTime = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_endtime);
				TextView tvBackPath = (TextView) convertView
						.findViewById(R.id.activity_invest_detail_item_backpath);
				RelativeLayout toProduct = (RelativeLayout) convertView
						.findViewById(R.id.to_product_detail);
				RelativeLayout toContract = (RelativeLayout) convertView
						.findViewById(R.id.to_contract_detail);

				holder = new ViewHolder();

				holder.tvMoney = tvMoney;
				holder.tvProfit = tvProfit;
				holder.ivBank = ivBank;
				holder.tvType = tvType;
				holder.tvBuyTime = tvBuyTime;
				holder.tvDays = tvDays;
				holder.tvEndTime = tvEndTime;
				holder.tvBackPath = tvBackPath;
				holder.toProduct = toProduct;
				holder.toContract = toContract;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final SingleProduct sp = list.get(position);
			if (sp != null) {
				holder.tvMoney.setText(SystemUtils.getDoubleStr(sp.money));
				holder.tvProfit.setText(SystemUtils.getDoubleStr(sp.profit));
				String buyDate = TimeUtils.getTimeFromSeconds(sp.buytime);
				holder.tvBuyTime.setText(buyDate);
				String endTime = TimeUtils.addDay(1, sp.uietime);
				holder.tvEndTime.setText(endTime);
				String serverDate = TimeUtils.getServerDate(TimeUtils.DATE_FORMAT_DATE);
				int days = SystemUtils.getDays(serverDate, endTime);
				String str = "0";
				if (days >= 1) {
					str = days - 1 + "";
				}
				holder.tvDays.setText(str);
				holder.tvBackPath.setText("账户余额");
				if ("1".equals(sp.paytype)) {
					// 账户余额支付
					holder.tvType.setText("账户余额");
					holder.ivBank.setVisibility(View.INVISIBLE);
				} else {
					// 银行卡支付
					UserInfo userInfo = App.userinfo;
					if (userInfo != null && userInfo.identity != null) {
						holder.tvType.setText(App.userinfo.identity.bankname);
						holder.ivBank.setVisibility(View.VISIBLE);
						String url = API.BANK_ICON_BASE + userInfo.identity.bankid + ".jpg";
						Glide.with(parent.getContext())
								.load(url)
								.into(holder.ivBank);
					}
				}

				holder.toProduct.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (product != null) {
							boolean canBuy = true;
							boolean isSellOut = Integer.parseInt(product.status) != BaseProduct.STATE_SELLING;
							if (isSellOut) {
								canBuy = false;
							}
							Intent intent = new Intent(activity, ProductDetailActivity.class);
							Bundle data = new Bundle();
							data.putSerializable("product", product);
							data.putInt("productType", product.ptype);
							data.putInt("productState", Integer.parseInt(product.status));
							data.putString("title", product.pname);
							data.putString("pid", product.pid);
							data.putInt("canbuyuser", Integer.parseInt(product.canbuyuser));
							data.putBoolean("canbuy", canBuy);
							data.putBoolean("issellout", isSellOut);
							data.putBoolean("isyugao", product.isYuGao());
							intent.putExtras(data);
							startActivity(intent);
						}
					}
				});

				holder.toContract.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, WebViewActivity.class);
						intent.putExtra("title", "电子合同");
						intent.putExtra("bid", sp.id);
						startActivity(intent);
					}
				});

			}
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvMoney;
		TextView tvProfit;
		ImageView ivBank;
		TextView tvType;
		TextView tvBuyTime;
		TextView tvDays;
		TextView tvEndTime;
		TextView tvBackPath;
		RelativeLayout toContract;
		RelativeLayout toProduct;
	}

	/**
	 * 加载产品详情
	 */
	private void loadProductDetail() {
		if (pmodel != null) {
			pmodel.getProductDetail(2, up.product_list.get(0).pid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, final T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof BaseProduct) {
						product = (BaseProduct) t;
					}
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg);
					closeLoading();
				}
			});
		}
	}

}
