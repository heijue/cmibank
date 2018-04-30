package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.Questions;
import cn.app.yimirong.model.http.ResponseHandler;

public class QAActivity extends BaseActivity {

	private ListView listView;

	private List<Questions> list;

	private QAListAdapter adapter;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_qa);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("常见问题");
		listView = (ListView) findViewById(R.id.activity_qa_list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Questions q = (Questions) adapter.getItem(position);
				toDetail(q);
			}
		});
	}

	@Override
	public void initData() {
		list = new ArrayList<Questions>();
		adapter = new QAListAdapter(list);
		listView.setAdapter(adapter);
		loadCache();
		loadQuestions();
	}

	/**
	 * 从缓存获取
	 */
	private void loadCache() {
		List<Questions> list2 = DataMgr.getInstance(appContext)
				.restoreQuestions();
		if (list2 != null) {
			list.addAll(list2);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 从网络获取常见问题
	 */
	private void loadQuestions() {
		if (cmodel != null) {
			showLoading("玩命向钱冲");
			cmodel.getQuestions(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						if (list != null) {
							list.clear();
						}
						@SuppressWarnings("unchecked")
						List<Questions> qs = (List<Questions>) t;
						if (qs.size() > 0) {
							if (list.size() == 0) {
								list.addAll(qs);
							} else {
								boolean isExists = false;
								for (int i = 0; i < qs.size(); i++) {
									isExists = false;
									String id = qs.get(i).nid;
									for (int j = 0; j < list.size(); j++) {
										String id2 = list.get(j).nid;
										if (id.equals(id2)) {
											isExists = true;
											break;
										}
									}

									if (!isExists) {
										list.add(qs.get(i));
									}
								}
							}

							sort();

							if (adapter != null) {
								adapter.notifyDataSetChanged();
							}
							DataMgr.getInstance(appContext).saveQuestions(list);
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
	 * 排序
	 */
	protected void sort() {
		Collections.sort(list, new Comparator<Questions>() {
			@Override
			public int compare(Questions o1, Questions o2) {
				int nid1 = Integer.parseInt(o1.nid);
				int nid2 = Integer.parseInt(o2.nid);
				if (nid1 < nid2) {
					return 1;
				} else if (nid1 > nid2) {
					return -1;
				} else {
					return 0;
				}
			}
		});
	}

	/**
	 * 去详情
	 *
	 * @param q
	 */
	protected void toDetail(Questions q) {
		Intent intent = new Intent(context, QADetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("questions", q);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private class QAListAdapter extends BaseAdapter {

		private List<Questions> list;

		private BitmapUtils bitmapUtils;

		private int[] img = new int[]{R.drawable.zhucedenglu, R.drawable.kaxiangguan, R.drawable.yaoqinghaoyou, R.drawable.tixian2, R.drawable.chongzhi2, R.drawable.tiyanjin2, R.drawable.licaishouyi,};

		public QAListAdapter(List<Questions> list) {
			this.list = list;
			bitmapUtils = new BitmapUtils(context);
			bitmapUtils.configDefaultLoadingImage(R.drawable.changjianwenti);
			bitmapUtils.configDiskCacheEnabled(true);
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
						R.layout.layout_activity_qa_item, null);
				ImageView icon = (ImageView) convertView
						.findViewById(R.id.activity_qa_icon);
				TextView title = (TextView) convertView
						.findViewById(R.id.activity_qa_title);

				holder = new ViewHolder();
				holder.icon = icon;
				holder.title = title;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Questions q = (Questions) getItem(position);
			if (q != null) {
				holder.title.setText(q.title);
                bitmapUtils.display(holder.icon, q.icon);
				holder.icon.setImageResource(img[position]);
			}

			return convertView;
		}
	}

	private class ViewHolder {
		public ImageView icon;
		public TextView title;
	}

}
