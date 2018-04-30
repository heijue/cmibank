package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.model.bean.Notice;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.impl.MsgPresenter;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

/**
 * com.kdlc.activity.more.MessageActivity
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener{

	private MsgPresenter presenter;

	private ListView listView;

	private List<Message> messages;

	private TextView ableExp,usingExp;

	private View ableLine,usingLine;

	private ImageView meDot,noDot;

	private MessageAdapter adapter;

	private RelativeLayout noLayout,meLayout;

	private List<Notice> nlist;

	private boolean isPush;

	private boolean isNotice = false;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_message);
	}

	@Override
	protected void onDestroy() {
		presenter.unBind();
		super.onDestroy();
	}

	@Override
	public void initView() {
		setTitleBack(true);
		titleBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPush) {
					toMain();
				} else {
					finish();
				}
			}
		});

		LinearLayout control = (LinearLayout)findViewById(R.id.message_control);

		if (App.isLogin){
			control.setVisibility(View.VISIBLE);
		}else {
			control.setVisibility(View.GONE);
		}

		ableExp = (TextView) findViewById(R.id.able_exp_text);
		usingExp = (TextView) findViewById(R.id.using_exp_text);
		ableLine = findViewById(R.id.able_exp_line);
		usingLine = findViewById(R.id.using_exp_line);
		noLayout = (RelativeLayout) findViewById(R.id.able_exp_layout);
		meLayout = (RelativeLayout) findViewById(R.id.using_exp_layout);
		meDot = (ImageView) findViewById(R.id.using_hot);
		noDot = (ImageView) findViewById(R.id.able_hot);

		noLayout.setOnClickListener(this);
		meLayout.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.activity_message_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View parent,
									int position, long id) {
				if (isNotice) {
					toDetail(position);
				}else {
					Notice no = (Notice) adapter.getItem(position);
					if (no.link != null && !no.link.equals("")) {
						toOurWeb(no);
					}
				}
			}
		});
	}

	public void updateUI(){
		if (isNotice){
			meDot.setVisibility(View.GONE);
			usingExp.setTextColor(Color.parseColor("#ff4747"));
			usingLine.setVisibility(View.VISIBLE);
			ableExp.setTextColor(Color.parseColor("#666666"));
			ableLine.setVisibility(View.GONE);
		}else {
			noDot.setVisibility(View.GONE);
			usingExp.setTextColor(Color.parseColor("#666666"));
			usingLine.setVisibility(View.GONE);
			ableExp.setTextColor(Color.parseColor("#ff4747"));
			ableLine.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void initData() {

		Intent intent = getIntent();
		isPush = intent.getBooleanExtra("isPush", false);
		isNotice = intent.getBooleanExtra("notice",false);
		nlist = (List<Notice>) intent.getSerializableExtra("nlist");

		presenter = new MsgPresenter();
		presenter.bind(this);
		presenter.loadMessages();
		if (isNotice) {
			messages = presenter.getMessages();
			if (App.isLogin) {
				getNotice();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						showNoDot();
					}
				},300);
			}
		}else {
			messages = presenter.getMessages();
			showMeDot();
		}

		if (App.isLogin){
			setTitleText("我的消息");
			updateUI();
		}else{
			setTitleText("公告");
		}

	}

	public void showMeDot(){
		long mtime = DataMgr.getInstance(context).restorMtime();
		if (mtime == 0){
			if (messages.size() > 0){
				meDot.setVisibility(View.VISIBLE);
			}else {
				meDot.setVisibility(View.GONE);
			}
		}else {
			int num = 0;
			for (int i = 0; i < messages.size(); i++) {
				if (Long.parseLong(messages.get(i).ctime) > mtime) {
					num += 1;
				}
			}
			if (num > 0) {
				meDot.setVisibility(View.VISIBLE);
			}else {
				meDot.setVisibility(View.GONE);
			}
		}
	}

	public void showNoDot(){
		long ntime = DataMgr.getInstance(context).restorNtime();
		if (nlist != null) {
			if (ntime == 0) {
				if (nlist.size() > 0) {
					noDot.setVisibility(View.VISIBLE);
				} else {
					noDot.setVisibility(View.GONE);
				}
			} else {
				int num = 0;
				for (int i = 0; i < nlist.size(); i++) {
					if (Long.parseLong(nlist.get(i).ctime) > ntime) {
						num += 1;
					}
				}
				if (num > 0) {
					noDot.setVisibility(View.VISIBLE);
				} else {
					noDot.setVisibility(View.GONE);
				}
			}
		}

	}

	@Override
	public void updateView(Bundle data) {
		super.updateView(data);
		if (isNotice) {
			if (messages == null){
				messages = presenter.getMessages();
				ToastUtils.show(context,messages.size());
			}
			refreshMessages();
		}else {
			if (nlist != null && nlist.size()==0){
				getNotice();
			}
			refreshNotice();
		}
	}

	private void refreshNotice(){
		if (nlist != null){
			sortNotice();
			adapter = new MessageAdapter();
			listView.setAdapter(adapter);
		}
	}

	/**
	 * 刷新
	 */
	private void refreshMessages() {
		sortMessages();
			adapter = new MessageAdapter();
			listView.setAdapter(adapter);

	}


	public void sortNotice(){
		Collections.sort(nlist, new Comparator<Notice>() {
			@Override
			public int compare(Notice lhs, Notice rhs) {
				long onlinetime1 = Long.parseLong(lhs.ctime);
				long onlinetime2 = Long.parseLong(rhs.ctime);
				if (onlinetime1 < onlinetime2) {
					return 1;
				}

				if (onlinetime1 > onlinetime2) {
					return -1;
				}
				return 0;
			}
		});
	}

	/**
	 * 排序
	 */
	protected void sortMessages() {
		Collections.sort(messages, new Comparator<Message>() {

			@Override
			public int compare(Message o1, Message o2) {
				long onlinetime1 = Long.parseLong(o1.onlinetime);
				long onlinetime2 = Long.parseLong(o2.onlinetime);
				if (onlinetime1 < onlinetime2) {
					return 1;
				}

				if (onlinetime1 > onlinetime2) {
					return -1;
				}

				return 0;
			}
		});
	}

	/**
	 * 去详情
	 *
	 * @param position
	 */
	protected void toDetail(int position) {
		Message msg = messages.get(position);
		presenter.setReaded(position);
		adapter.notifyDataSetChanged();
		Intent intent = new Intent(context, MsgDetailActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("message", msg);
		intent.putExtras(data);
		startActivity(intent);
	}

	public void getNotice(){
		if (amodel != null){
			amodel.getUserNotice(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof List){
						nlist = (List<Notice>) t;
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
				}
			});
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.able_exp_layout:
				isNotice = false;
				DataMgr.getInstance(context).saveNtime(TimeUtils.getServerTime());
				getNotice();
				updateUI();
				updateView(null);
				break;

			case R.id.using_exp_layout:
				isNotice = true;
				DataMgr.getInstance(context).saveMtime(TimeUtils.getServerTime());
				updateUI();
				updateView(null);
				break;

		}
	}

	/**
	 * 公告数据适配器
	 *
	 * @author wen
	 */
	private class MessageAdapter extends BaseAdapter {

		private List<Message> list;

		private List<Notice> nlist2;

		public MessageAdapter() {
			if (isNotice) {
				this.list = messages;
			}else {
				this.nlist2 = nlist;
			}
		}


		@Override
		public int getCount() {
			if (isNotice) {
				return list.size();
			}else {
				return nlist2.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (isNotice) {
				return list.get(position);
			}else {
				return nlist2.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_message_item, null);

				TextView tvTitle = (TextView) convertView
						.findViewById(R.id.activity_message_item_title);
				TextView tvBottomTime = (TextView) convertView.findViewById(R.id.message_item_date);
				TextView tvBottomLook = (TextView) convertView.findViewById(R.id.bottom_look);
				TextView tvContent = (TextView) convertView.findViewById(R.id.message_content);
				ImageView ivLeft = (ImageView) convertView.findViewById(R.id.left_image);


				holder = new ViewHolder();
				holder.tvBottomLook = tvBottomLook;
				holder.tvBottomTime = tvBottomTime;
				holder.tvTitle = tvTitle;
				holder.tvContent = tvContent;
				holder.ivLeft = ivLeft;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (isNotice){
				holder.ivLeft.setBackground(getResources().getDrawable(R.drawable.gonggao));
				holder.tvBottomLook.setVisibility(View.VISIBLE);
				Message msg = list.get(position);
				if (msg != null) {
					holder.tvBottomTime.setText(TimeUtils.getTimeFromSeconds(msg.onlinetime, TimeUtils.DEFAULT_DATE_MINUTE));
					holder.tvTitle.setText(msg.title);
				}
			}else {
				holder.tvBottomLook.setVisibility(View.GONE);
				final Notice no = nlist2.get(position);
				if (no != null){
					holder.tvBottomTime.setText(TimeUtils.getTimeFromSeconds(no.ctime, TimeUtils.DEFAULT_DATE_MINUTE));
					holder.tvTitle.setText(no.title);
					holder.tvContent.setText(no.content);
					if (no.content.contains("体验金")) {
						holder.ivLeft.setBackground(getResources().getDrawable(R.drawable.tiyanjin));
					}
					if (no.content.contains("转出")) {
						holder.ivLeft.setBackground(getResources().getDrawable(R.drawable.qianzhuanchu));
					}
					if (no.link != null && !no.link.equals("")){

						holder.tvBottomLook.setVisibility(View.VISIBLE);
//						holder.tvBottomLook.setOnClickListener(new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								toOurWeb(no.link,no.title);
//							}
//						});
					}

				}
			}

			return convertView;
		}
	}

	private void toOurWeb(Notice no) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", no.title);
		intent.putExtra("url",
				no.link);
		startActivity(intent);
	}


	private static class ViewHolder {
		TextView tvBottomTime;
		TextView tvTitle;
		TextView tvBottomLook;
		TextView tvContent;
		ImageView ivLeft;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPush) {
				toMain();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void toMain() {
		Intent intent = new Intent(context, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.alpha_in_200, R.anim.alpha_out_200);
		finish();
	}

}
