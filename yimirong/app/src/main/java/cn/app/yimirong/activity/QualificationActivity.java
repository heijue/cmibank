package cn.app.yimirong.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;

public class QualificationActivity extends BaseActivity {

	private ListView imgLv;

	private BaseAdapter adapter;

	private ArrayList<String> picList;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_qualification);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("公司资质");

		imgLv = (ListView) findViewById(R.id.image_listview);
		imgLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}

	@Override
	public void initData() {

		picList = new ArrayList<>();
		picList.add("http://img.ivsky.com/img/bizhi/pre/201601/27/february_2016-001.jpg");
		picList.add("http://img.ivsky.com/img/bizhi/pre/201601/27/february_2016-002.jpg");
		picList.add("http://img.ivsky.com/img/bizhi/pre/201601/27/february_2016-003.jpg");
		picList.add("http://img.ivsky.com/img/bizhi/pre/201601/27/february_2016-004.jpg");
		picList.add("http://img.ivsky.com/img/tupian/pre/201511/16/chongwugou.jpg");

		if (adapter==null){
			adapter = new QualificationAdapter();
			imgLv.setAdapter(adapter);
		}

	}

	private class QualificationAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return picList.size();
		}

		@Override
		public Object getItem(int position) {
			return picList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if (convertView==null){
				convertView = View.inflate(context,R.layout.qualification_item,null);
				holder = new ViewHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.zizhi_img);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			Glide.with(context).load(picList.get(position)).into(holder.img);
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView img;
	}

}
