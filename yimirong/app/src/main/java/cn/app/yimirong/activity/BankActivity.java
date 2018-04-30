package cn.app.yimirong.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.db.dao.BankDao;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class BankActivity extends BaseActivity {

	private ListView listView;
	private BanksAdapter adapter;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_bank);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("选择银行");
		listView = (ListView) findViewById(R.id.activity_bank_list);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
									int position, long arg3) {
				EventBus.getDefault().post(adapter.getItem(position));
				finish();
			}
		});
	}

	@Override
	public void initData() {
		loadBankList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 加载银行列表
	 */
	private void loadBankList() {
		if (cmodel != null) {
			showLoading("玩命向钱冲");
			cmodel.getBankList(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						List<Bank> list = (List<Bank>) t;
						updateList(list);
						//先清空
						BankDao.getInstance(context).clear();
						//再插入
						BankDao.getInstance(context).insert(list);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
					//请求失败时从数据库获取
					List<Bank> list = BankDao.getInstance(context).queryAll();
					if (list != null) {
						updateList(list);
					}
				}
			});
		}
	}

	/**
	 * 更新银行列表
	 *
	 * @param list
	 */
	private void updateList(List<Bank> list) {
		List<Bank> list2 = new ArrayList<Bank>();
		for (Bank bank : list) {
			if (bank.isShow) {
				list2.add(bank);
			}
		}
		if (adapter == null) {
			adapter = new BanksAdapter(list2);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 列表数据适配器
	 */
	private class BanksAdapter extends BaseAdapter {
		private List<Bank> list;
		private BitmapUtils bitmap;

		public BanksAdapter(List<Bank> list) {
			this.list = list;
			bitmap = new BitmapUtils(context);
			bitmap.configDiskCacheEnabled(true);
			bitmap.configMemoryCacheEnabled(true);
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
						R.layout.activity_bank_item, null);

				ImageView iv = (ImageView) convertView
						.findViewById(R.id.activity_bank_item_icon);

				TextView tv = (TextView) convertView
						.findViewById(R.id.activity_bank_item_name);

				TextView tv2 = (TextView) convertView.findViewById(R.id.activity_bank_item_single);

				holder = new ViewHolder();

				holder.bankIcon = iv;

				holder.bankName = tv;

				holder.bankSingle = tv2;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Bank bank = list.get(position);
			holder.bankName.setText(bank.name);
			bitmap.display(holder.bankIcon, bank.url);
			holder.bankSingle.setText(StringUtils.getSingleInfo(bank));
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView bankIcon;
		TextView bankName;
		TextView bankSingle;
	}

}
