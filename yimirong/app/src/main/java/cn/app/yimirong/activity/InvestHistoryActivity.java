package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.UserProduct;
import cn.app.yimirong.model.bean.UserProduct.SingleProduct;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

public class InvestHistoryActivity extends BaseActivity {

	private ListView listView;

	private InvestHistoryAdapter adapter;

	private List<UserProduct> productList;

	private RelativeLayout weikong_layout;

	private TextView tvInvestNum,tvProfit;
	private View header;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_invest_history);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("投资记录");
		Intent intent = getIntent();
		int buyNum = intent.getIntExtra("buynum",0);
		weikong_layout = (RelativeLayout) findViewById(R.id.history_null);
		listView = (ListView) findViewById(R.id.activity_invest_history_listview);

		if (buyNum > 0) {
			weikong_layout.setVisibility(View.GONE);
			header = View.inflate(context,
					R.layout.invest_history_new_head, null);

			tvInvestNum = (TextView) header
					.findViewById(R.id.daishou_money);
			tvProfit = (TextView) header
					.findViewById(R.id.expect_profit);


			listView.addHeaderView(header);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					if (position > 0) {
						UserProduct up = (UserProduct) adapter
								.getItem(position - 1);
						toInvestDetail(up);
					}
				}
			});
		} else {
			listView.setVisibility(View.GONE);
			weikong_layout.setVisibility(View.VISIBLE);
		}

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
					if (t != null && t instanceof List) {
						@SuppressWarnings("unchecked")
						List<UserProduct> list = (List<UserProduct>) t;
						if (!list.isEmpty()) {
							productList.clear();
							productList.addAll(list);

							if (adapter == null) {
								adapter = new InvestHistoryAdapter(productList);
								listView.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}

							int ben = 0;
							double allProfit = 0.0;

							for (int i = 0; i < productList.size(); i++) {
								ben += (int) productList.get(i).money;
								String income = productList.get(i).product_list.get(0).income;

								long start = TimeUtils.getStartTimeOfDay(Long.parseLong(productList.get(i).product_list.get(0).buytime));
								long end = TimeUtils.getTimeInSecondsFromString(productList.get(i).product_list.get(0).uietime);
								int days = (int) (end - start) / 86400;
								allProfit = allProfit + getDouble(productList.get(i).money * Double.parseDouble(income) / 365 * days/100,2);
							}
							if (header!=null) {
								tvInvestNum.setText(String.valueOf(ben));
								tvProfit.setText(String.valueOf(getDouble(allProfit,2)));
							}
							closeLoading();
						}else {
							closeLoading();
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
	 * 跳转到投资记录详情页面
	 *
	 * @param up
	 */
	protected void toInvestDetail(UserProduct up) {
		Intent intent = new Intent(context, InvestDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("invest_detail", up);
		intent.putExtras(bundle);
		startActivity(intent);
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
						R.layout.activity_invest_history_new_item, null);

				TextView tvName = (TextView) convertView
						.findViewById(R.id.activity_history_item_name);
				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_history_item_money);
				TextView tvNum = (TextView) convertView
						.findViewById(R.id.activity_history_item_num);
				TextView tvProfit = (TextView) convertView
						.findViewById(R.id.activity_history_item_profit);
				TextView tvBuytime = (TextView) convertView
						.findViewById(R.id.activity_history_item_buy);
				TextView tvEndtime = (TextView) convertView
						.findViewById(R.id.daoqitime);
				TextView tvDaoqi = (TextView) convertView
						.findViewById(R.id.daoqi);
				RelativeLayout tvEndlayout = (RelativeLayout) convertView
						.findViewById(R.id.endtime_layout);
				TextView income = (TextView) convertView
						.findViewById(R.id.annual_yield_value);
				holder = new ViewHolder();
				holder.tvName = tvName;
				holder.tvMoney = tvMoney;
				holder.tvBuy = tvBuytime;
				holder.tvNum = tvNum;
				holder.tvProfit = tvProfit;
				holder.tvDaoqi = tvDaoqi;
				holder.tvEndtime = tvEndtime;
				holder.tvEndlayout = tvEndlayout;
				holder.income = income;
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			UserProduct up = list.get(position);
			if (up != null) {
				double allProfit = 0.0;
				List<SingleProduct> product_list = up.product_list;
				for (int j=0;j<product_list.size();j++){
					long start = TimeUtils.getStartTimeOfDay(Long.parseLong(product_list.get(j).buytime));
					long end = TimeUtils.getTimeInSecondsFromString(product_list.get(j).uietime);
					int days = (int) (end - start)/86400;
					allProfit += product_list.get(j).money*Double.parseDouble(product_list.get(j).income)/365*days;
				}

				holder.tvName.setText(up.pname);
				holder.tvBuy.setText("购于"+TimeUtils.getTimeFromSeconds
						(up.buytime));
				holder.tvMoney.setText((int) (up.money) + "元");
				holder.tvNum.setText(up.product_list.size() + "笔");
				holder.tvProfit.setText(getDouble(allProfit/100,2) + "元");
				holder.income.setText(up.product_list.get(0).income+"%");
				// 计算时间差，判断是否即将到期
				SingleProduct sp = up.product_list.get(0);
				String end = sp.uietime;

				int days = SystemUtils.getDays(
						TimeUtils.getServerDate(TimeUtils.DATE_FORMAT_DATE), end);
				if (days <= 5) {
					if (days == 0) {
						holder.tvDaoqi.setTextColor(Color.parseColor("#ff4747"));
						holder.tvDaoqi.setText("今日14:30-16:30还款");
						holder.tvEndtime.setText("");
					} else {
						holder.tvDaoqi.setTextColor(Color.parseColor("#ff4747"));
						holder.tvEndtime.setTextColor(Color.parseColor("#ff4747"));
						holder.tvDaoqi.setText("即将到期：");
						holder.tvEndtime.setText(TimeUtils.addDay(1, sp.uietime));
					}
				} else {
					holder.tvDaoqi.setTextColor(Color.parseColor("#4c4c4c"));
					holder.tvEndtime.setTextColor(Color.parseColor("#000000"));
					holder.tvDaoqi.setText("到期时间：");
					holder.tvEndtime.setText(TimeUtils.addDay(1, sp.uietime));
				}

			}

			return convertView;
		}
	}

	/**
	 * a为一个带有未知位小数的实数
	 * 对其取b位小数
	 * @param a
	 * @param b
	 * @return
	 */
	static double getDouble(double a,int b){
		int x=0;
		int y=1;
		for(int i=0;i<b;i++){
			y=y*10;
		}
		x=(int)(a*y);
		return (double)x/y;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvMoney;
		TextView tvBuy;
		TextView tvNum;
		TextView tvProfit,income;
		TextView tvDaoqi;
		TextView tvEndtime;
		RelativeLayout tvEndlayout;
	}
}
