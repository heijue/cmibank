package cn.app.yimirong.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.BuyRecord;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;
import cn.app.yimirong.view.pull.PullToRefreshLayout.OnRefreshListener;

public class BuyRecordActivity extends BaseActivity implements
		OnRefreshListener {

	private PullToRefreshLayout refresher;

	private ListView listView;
	private BaseAdapter adapter;
	private List<BuyRecord> list;
	private RelativeLayout kongLayout;

	private String pid;

	private int productType;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_buy_record);
	}

	@Override
	public void initView() {
		setTitleText("购买记录");
		setTitleBack(true);

		kongLayout = (RelativeLayout) findViewById(R.id.goumaijilu_kong_layout);
		refresher = (PullToRefreshLayout) findViewById(R.id.activity_buy_record_refresher);
		refresher.setOnRefreshListener(this);

		listView = (ListView) findViewById(R.id.activity_buy_record_listview);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		pid = intent.getStringExtra("pid");
		productType = intent.getIntExtra("type", BaseProduct.TYPE_DQ);
		showLoading("玩命向钱冲");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				refresher.autoRefresh(false);
			}
		}, Constant.FIRST_IN_REFRESH_DELAY);
	}

	/**
	 * 从网络加载数据
	 */
	private void loadBuyRecords() {
		if (pmodel != null) {
			pmodel.getProductBuyInfo(productType, pid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof List) {
						@SuppressWarnings("unchecked")
						List<BuyRecord> records = (List<BuyRecord>) t;

						if (list == null) {
							list = new ArrayList<BuyRecord>();
						}

						list.clear();
						list.addAll(records);

						sort();
						if (list.size() == 0) {
							kongLayout.setVisibility(View.VISIBLE);
							refresher.setVisibility(View.GONE);
						} else {
							kongLayout.setVisibility(View.GONE);
							refresher.setVisibility(View.VISIBLE);
							if (adapter == null) {
								adapter = new RecordAdapter(list);
								listView.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
						}

						refresher
								.refreshFinish(PullToRefreshLayout.SUCCEED);

					}
					closeLoading();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					refresher.refreshFinish(PullToRefreshLayout.FAIL);
					closeLoading();
				}
			});
		}
	}

	/**
	 * 排序
	 */
	protected void sort() {
		Collections.sort(list, new Comparator<BuyRecord>() {

			@Override
			public int compare(BuyRecord o1, BuyRecord o2) {
				long t1 = Long.parseLong(o1.ctime);
				long t2 = Long.parseLong(o2.ctime);

				if (t1 < t2) {
					return 1;
				}

				if (t1 > t2) {
					return -1;
				}

				return 0;
			}
		});
	}

	private class RecordAdapter extends BaseAdapter {

		private List<BuyRecord> list;

		public RecordAdapter(List<BuyRecord> list) {
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
				convertView = View.inflate(appContext,
						R.layout.activity_buy_record_item, null);
				TextView tv1 = (TextView) convertView
						.findViewById(R.id.activity_gmjl_number);
				TextView tv2 = (TextView) convertView
						.findViewById(R.id.activity_gmjl_time);
				TextView tv3 = (TextView) convertView
						.findViewById(R.id.activity_gmjl_money);

				holder = new ViewHolder();
				holder.tvNumber = tv1;
				holder.tvTime = tv2;
				holder.tvMoney = tv3;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			BuyRecord record = list.get(position);
			if (record != null) {
				holder.tvNumber.setText(record.account);
				long s = Long.parseLong(record.ctime);
				holder.tvTime.setText(TimeUtils.getTimeFromSeconds(s,
						TimeUtils.DEFAULT_DATE_FORMAT));
				holder.tvMoney.setText("￥" + record.money);
			}
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvNumber;
		TextView tvTime;
		TextView tvMoney;
	}

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		loadBuyRecords();
	}

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		refresher.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}

}
