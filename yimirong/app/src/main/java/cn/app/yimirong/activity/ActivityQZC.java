package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.UserProduct;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

public class ActivityQZC extends BaseActivity {

	private ListView listView;

	private InvestHistoryAdapter adapter;

	private List<UserProduct> productList;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_activity_qzc);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("定期资产");

		Intent intent = getIntent();
		String dqmoney = intent.getStringExtra("dqmoney");

		View header = View.inflate(context,
				R.layout.activity_invest_history_header, null);

		TextView vtInvestText = (TextView) header.findViewById(R.id.invest_history_text);
		vtInvestText.setText("定期总资产(元)");
		vtInvestText.setTextSize(15);
		vtInvestText.setTextColor(Color.parseColor("#666666"));
		TextView tvInvestNum = (TextView) header
				.findViewById(R.id.activity_invest_history_num);
		tvInvestNum.setText(dqmoney);

		listView = (ListView) findViewById(R.id.activity_dqzc_listview);
		listView.addHeaderView(header);

	}

	@Override
	public void initData() {
		productList = new ArrayList<>();
		loadUserInvestHistroy();
	}

	/**
	 * 加载用户投资记录
	 */
	private void loadUserInvestHistroy() {
		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getUserProduct(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, final T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						@SuppressWarnings("unchecked")
						List<UserProduct> list = (List<UserProduct>) t;
						productList.clear();
						productList.addAll(list);

						if (adapter == null) {
							adapter = new InvestHistoryAdapter(productList);
							listView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
				}
			});
		}
	}

	/**
	 * 投资记录适配器
	 *
	 * @author android
	 */
	private class InvestHistoryAdapter extends BaseAdapter {

		private List<UserProduct> list;

		public InvestHistoryAdapter(List<UserProduct> list) {
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
			ViewHolder holder;
			if (convertView == null) {

				convertView = View.inflate(context,
						R.layout.activity_qzc_history_itme, null);

				TextView tvName = (TextView) convertView
						.findViewById(R.id.activity_qzc_history_item_name);
				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_qzc_history_item_money);
				TextView tvNum = (TextView) convertView
						.findViewById(R.id.activity_qzc_history_item_num);
				TextView tvTime = (TextView) convertView
						.findViewById(R.id.tv_time);

				holder = new ViewHolder();
				holder.tvName = tvName;
				holder.tvMoney = tvMoney;
				holder.tvNum = tvNum;
				holder.tvTime = tvTime;


				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			UserProduct up = list.get(position);
			if (up != null) {
				holder.tvName.setText(up.pname);
				holder.tvNum.setText(SystemUtils.getDoubleStr(up.money ));
				String time ="";
				if (up.product_list != null) {
					for (UserProduct.SingleProduct singleProduct : up.product_list) {
						time = singleProduct.uietime;
					}
				}
				holder.tvTime.setText(time);
			}

			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvMoney;
		TextView tvNum;
		TextView tvTime;
	}

}
