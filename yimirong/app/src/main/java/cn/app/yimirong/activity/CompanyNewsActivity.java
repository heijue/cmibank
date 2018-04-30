package cn.app.yimirong.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ComNews;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;
import cn.app.yimirong.view.pull.PullableListView;

public class CompanyNewsActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener{

	private PullToRefreshLayout refresh;
	private PullableListView listView;
	private List<ComNews> list;
	private BaseAdapter adapter;
	private int currentPage = 2;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_company_news);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("公司动态");

		refresh = (PullToRefreshLayout) findViewById(R.id.activity_action_list_pullable);
		refresh.setOnRefreshListener(this);

		listView = (PullableListView) findViewById(R.id.news_comlistview);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ComNews cn = (ComNews) adapter.getItem(position);
				toOurWeb(cn);
			}
		});

	}

	private void toOurWeb(ComNews no) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", no.title);
		intent.putExtra("url",
				no.link);
		startActivity(intent);
	}

	@Override
	public void initData() {

		if (list == null){
			list = new ArrayList<>();
		}

		Intent intent = getIntent();
		list = (List<ComNews>) intent.getSerializableExtra("cList");

		if (adapter == null){
			adapter = new CompanyNewsAdapter();
			if (!list.isEmpty()) {
				listView.setAdapter(adapter);
			}
		}

	}

	private void getCmNews(){
		if (amodel != null){
			amodel.getCompanyNews(currentPage, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof List){
						List<ComNews> clist = (List<ComNews>)t;
						if (clist.size() == 0){
							ToastUtils.show(context, "没有更多数据了");
						}else {
							list.addAll(clist);
							adapter.notifyDataSetChanged();
							currentPage++;
						}
						refreshDone(PullToRefreshLayout.SUCCEED);
					}

				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					refreshDone(PullToRefreshLayout.FAIL);
				}
			});
		}
	}

	private void refreshDone(int succeed) {
		refresh.loadmoreFinish(succeed);
	}

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		refreshDone(PullToRefreshLayout.SUCCEED);
	}

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		getCmNews();
	}

	public class CompanyNewsAdapter extends BaseAdapter{

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
			if (convertView == null){
				holder = new ViewHolder();
				convertView = View.inflate(context,R.layout.company_news_item,null);

				holder.nImg = (ImageView)convertView.findViewById(R.id.company_news_img);
				holder.ntime = (TextView)convertView.findViewById(R.id.news_time);
				holder.ntitle = (TextView)convertView.findViewById(R.id.news_title);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}

			ComNews cn = (ComNews) list.get(position);
			holder.ntitle.setText(cn.title);
			holder.ntime.setText(TimeUtils.getTimeFromSeconds(cn.ctime, TimeUtils.DEFAULT_DATE_MINUTE));
			Glide.with(parent.getContext())
					.load(cn.img)
					.into(holder.nImg);

			return convertView;
		}
	}

	private class ViewHolder {
		TextView ntitle;
		TextView ntime;
		ImageView nImg;
	}

}
