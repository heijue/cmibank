package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.model.bean.NewExp;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;

public class ExpNewActivity extends BaseActivity implements View.OnClickListener{

	private TextView yesterdayExp,expIncome,ableExp,usingExp,nullText;

	private View ableLine,usingLine;

	private ListView ticketView;

	private RelativeLayout ableLayout,usingLayout,nullLayout,yesterLayout;

	private TextView tvInUse;

	private List<NewExp> list;

	private List<NewExp> uselist;

	private BaseAdapter adapter;

	private boolean isUsing=false;

	private double countprofit;

	private List<NewExp> ableList,usingList;
	private String expId;
	private String str;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_exp_new);
		EventBus.getDefault().register(this);
	}
	public void onEventMainThread(UerExp exp) {
		useEXP(expId);
	}
	@Override
	public void initView() {
		setTitleBack(true);
		setTitleRight(true, new OnRightClickListener() {
			@Override
			public void onClick() {
				showDesc();
			}
		});
		setRightText("使用说明");
		setTitleText("我的体验金");

		yesterLayout = (RelativeLayout) findViewById(R.id.yesterday_exp_profit);
		yesterdayExp = (TextView) findViewById(R.id.yesterday_exp_money);
		expIncome = (TextView) findViewById(R.id.expect_income);
		ableExp = (TextView) findViewById(R.id.able_exp_text);
		usingExp = (TextView) findViewById(R.id.using_exp_text);
		nullText = (TextView) findViewById(R.id.ticket_null_text);
		ableLine = findViewById(R.id.able_exp_line);
		usingLine = findViewById(R.id.using_exp_line);
		ticketView = (ListView) findViewById(R.id.exp_tikect_listview);
		ableLayout = (RelativeLayout) findViewById(R.id.able_exp_layout);
		usingLayout = (RelativeLayout) findViewById(R.id.using_exp_layout);
		nullLayout = (RelativeLayout) findViewById(R.id.exp_ticket_null_layout);
		tvInUse = (TextView) findViewById(R.id.in_the_vote_money);

		yesterLayout.setOnClickListener(this);
		ableLayout.setOnClickListener(this);
		usingLayout.setOnClickListener(this);

		ticketView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (!isUsing) {
					final NewExp exp = (NewExp)adapter.getItem(position);
					if (exp != null) {
//						double profit = Double.parseDouble(exp.money)*Double.parseDouble(exp.income)/100/365*Double.parseDouble(exp.days);
//						int expProfit = (int)(profit*100);
						str = "<font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;获得<font color='#ff4747'>" + exp.money + "元</font>的体验金资格，" + exp.income + "%的历史年化收益，"
								+ exp.days + "天体验时间。点击即可轻松将收益收入囊中哦！</font>";
						expId = exp.id;
						useDxp();
					}
				}
			}
		});

	}

	private void showDesc() {
		Intent intent = new Intent(this, ExpDescActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
	}
	private void useDxp() {
		Intent intent = new Intent(this, UseExpActivity.class);
		intent.putExtra("STR_INFO",str);
		startActivity(intent);
		overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
	}

	private void useEXP(String id){
		showLoading("玩命向钱冲");
		if (amodel != null){
			amodel.useExpMoney(id, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					loadNEWExpMoney();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
					closeLoading();
				}
			});
		}
	}

	/**
	 * 获取新体验金余额
	 */
	private void loadNEWExpMoney() {
		if (amodel != null) {
			amodel.newExpMoney(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);

					if (t != null && t instanceof List) {
						uselist = (List<NewExp>)t;
						final Dialog dialog = PromptUtils.showSuccessDialog(activity, context,
								"使用成功");
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								initData();
								dialog.dismiss();
							}
						}, 1000);
					}
					closeLoading();
				}

				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
				}
			});
		}
	}

	@Override
	public void initData() {
		showLoading("玩命向钱冲");
		List<NewExp> all = (List<NewExp>) getIntent().getSerializableExtra("explist");
		double yestaday = getIntent().getDoubleExtra("yestoday_exp",0.0);
		countprofit = getIntent().getDoubleExtra("countprofit", 0d);

		yesterdayExp.setText(SystemUtils.getDoubleStr(yestaday));

		double expnow = 0.00;

		if (uselist!=null){
			all.clear();
			all.addAll(uselist);
		}

		if(list==null){
			list = new ArrayList<>();
			ableList = new ArrayList<>();
			usingList = new ArrayList<>();
		}

		if (all!=null && all.size()>0) {
			ableList.clear();
			usingList.clear();
			expIncome.setText("8.00%");
			for (int i = 0; i < all.size(); i++) {
				if (all.get(i).status.equals("0")) {
					ableList.add(all.get(i));
				} else {
					usingList.add(all.get(i));
					expnow += Double.parseDouble(all.get(i).money);
				}
			}
			tvInUse.setText(SystemUtils.getDoubleStr(expnow));
		}

		ifUsing();

		if (adapter == null) {
			adapter = new ExpAdapter();
			ticketView.setAdapter(adapter);
		}
		closeLoading();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void ifUsing(){

		if (list!=null) {
			list.clear();
		}

		if (isUsing){
			usingExp.setTextColor(Color.parseColor("#ff4747"));
			usingLine.setVisibility(View.VISIBLE);
			ableExp.setTextColor(Color.parseColor("#666666"));
			ableExp.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.icon_3),null,null);
			usingExp.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.icon_4),null,null);
			nullText.setText("您暂无使用中体验金券");
			ableLine.setVisibility(View.GONE);
			list.addAll(usingList);
		}else {
			usingExp.setTextColor(Color.parseColor("#666666"));
			ableExp.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.icon_4),null,null);
			usingExp.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.icon_3),null,null);
			usingLine.setVisibility(View.GONE);
			ableExp.setTextColor(Color.parseColor("#ff4747"));
			nullText.setText("您暂无可用体验金券");
			ableLine.setVisibility(View.VISIBLE);
			list.addAll(ableList);
		}

		if (list.size()>0){
			ticketView.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
		}else{
			ticketView.setVisibility(View.GONE);
			nullLayout.setVisibility(View.VISIBLE);
		}

		if (adapter!=null){
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.able_exp_layout:
				isUsing=false;
				ifUsing();
				break;

			case R.id.using_exp_layout:
				isUsing=true;
				ifUsing();
				break;

			case R.id.yesterday_exp_profit:
				toExpYesterday();
				break;
		}
	}

	private void toExpYesterday(){
		Intent intent = new Intent(ExpNewActivity.this,ExpYesterdayProfitActivity.class);
		intent.putExtra("countprofit",countprofit);
		startActivity(intent);
	}

	private class ExpAdapter extends BaseAdapter{

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

			ViewHolder holder=null;
			if(convertView==null){
				convertView = View.inflate(context,R.layout.exp_ticket_item,null);
				holder = new ViewHolder();
				holder.time = (TextView) convertView.findViewById(R.id.ticket_term);
				holder.days = (TextView) convertView.findViewById(R.id.ticket_days);
				holder.money = (TextView) convertView.findViewById(R.id.exp_money);
				holder.user_mode = (TextView) convertView.findViewById(R.id.tv_user_mode);
				holder.name = (TextView) convertView.findViewById(R.id.ticket_name);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			NewExp exp = (NewExp)getItem(position);
			holder.name.setText(exp.name);
			if (isUsing){
				holder.user_mode.setText("使用中");
				holder.money.setText(exp.money);
				holder.time.setVisibility(View.GONE);
				long start = TimeUtils.getStartTimeOfDay(Long.parseLong(exp.utime));
				long now = TimeUtils.getCurrentTimeSeconds();
				int day = (int)(now-start)/86400;
				if(now-start>86400) {
					holder.days.setText("已使用"+day+"天");
				}else {
					holder.days.setText("正在体验");
				}
			}else {
				holder.user_mode.setText("立即使用");
				holder.money.setText(exp.money);
				holder.time.setVisibility(View.VISIBLE);
				holder.time.setText("有效期："+ TimeUtils.getTimeExp(exp.ctime)+"-"+ TimeUtils.getTimeExp(exp.etime));
				long end = Long.parseLong(exp.etime);
				long now = TimeUtils.getCurrentTimeSeconds();
				int day = (int)(end-now)/86400;
				if(end-now>86400) {
					holder.days.setText(day + "天后过期");
				}else {
					holder.days.setText("当天到期");
				}
			}

			return convertView;
		}
	}

	private class ViewHolder {
		TextView money;
		TextView days;
		TextView time;
		TextView name;
		TextView user_mode;

	}

}
