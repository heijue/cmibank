package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.HQevnt;
import cn.app.yimirong.fragment.HomeFragment;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;

public class AssetNewActivity extends BaseActivity implements View.OnClickListener {

	private TextView tvAllMoney;

	private TextView tvYue;

	private TextView tvDQSum;

	private TextView tvDQProfit;

	private TextView tvDQExpProfit;

	private TextView tvHQMoney;

	private UserProfit mUserProfit;

	private TextView baozhang;

	private double countprofit;

	private String dqmoney;

	private List<Double> money;
	private double allmoney;
	private int[] colors=new int[]{R.color.chart1, R.color.chart2, R.color.chart3, R.color.chart4, R.color.chart5};


	@Override
	public void loadView() {
		setContentView(R.layout.activity_asset_new);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("资产总览");

		RelativeLayout rlYue = (RelativeLayout) findViewById(R.id.activity_assets_yue_wrapper);
		rlYue.setOnClickListener(this);

		RelativeLayout rlDQMoney = (RelativeLayout) findViewById(R.id.activity_assets_dq_wrapper);
		rlDQMoney.setOnClickListener(this);

		RelativeLayout rlDQProfit = (RelativeLayout) findViewById(R.id.activity_assets_dq_profit_wrapper);
		rlDQProfit.setOnClickListener(this);

		RelativeLayout rlExpProfit = (RelativeLayout) findViewById(R.id.activity_assets_exp_profit_wrapper);
		rlExpProfit.setOnClickListener(this);

		RelativeLayout rlKHBMoney = (RelativeLayout) findViewById(R.id.activity_assets_khb_money_wrapper);
		rlKHBMoney.setOnClickListener(this);

		baozhang = (TextView) findViewById(R.id.baozhang_text);

		tvAllMoney = (TextView) findViewById(R.id.activity_assets_allmoney);

		tvYue = (TextView) findViewById(R.id.activity_assets_yue);

		tvDQSum = (TextView) findViewById(R.id.activity_assets_dq_money);

		tvDQProfit = (TextView) findViewById(R.id.activity_assets_dq_profit);

		tvDQExpProfit = (TextView) findViewById(R.id.activity_assets_exp_profit);

		tvHQMoney = (TextView) findViewById(R.id.activity_assets_khb_money);
	}

	@Override
	public void initData() {
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite!=null && shareInvite.Third_party_payment!=null && !shareInvite.Third_party_payment.equals("")){
			baozhang.setText(shareInvite.Third_party_payment);
		}

		Intent intent = getIntent();

		money=new ArrayList<>();

		Bundle dataa = intent.getExtras();
		if (dataa != null) {
			mUserProfit = (UserProfit) dataa.getSerializable("userprofit");
		}

		String balance = "0.00";
		// 账户余额
		if (App.userinfo != null) {
			tvYue.setText(App.userinfo.balance);
			money.add(Double.parseDouble(App.userinfo.balance));
			balance = App.userinfo.balance;
		} else {
			balance = DataMgr.getInstance(appContext).restoreBalance();
			if (StringUtils.isBlank(balance)) {
				balance = "0.00";
			}
			tvYue.setText(balance);
			money.add(Double.parseDouble(balance));
		}

		// 定期投资总额
		double countmoney = intent.getDoubleExtra("countmoney", 0d);
		dqmoney = SystemUtils.getDoubleStr(countmoney);
		tvDQSum.setText(SystemUtils.getDoubleStr(countmoney));

		// 当前定期收益

		double khbasset = intent.getDoubleExtra("khbasset", 0d);
		tvDQProfit.setText(SystemUtils.getDoubleStr(khbasset));

		// 当前体验金收益
		double expmoneycurrentprofit = intent.getDoubleExtra("expmoneycurrentprofit", 0d);
		tvDQExpProfit.setText(SystemUtils.getDoubleStr(expmoneycurrentprofit));

		// 活期总资产
		countprofit = intent.getDoubleExtra("countprofit", 0d);
		tvHQMoney.setText(SystemUtils.getDoubleStr(countprofit));


		// 总资产
		allmoney = countmoney + countprofit + expmoneycurrentprofit + khbasset + Double.parseDouble(balance);
		tvAllMoney.setText(SystemUtils.getDoubleStr(allmoney));

		money.add(countmoney);
		money.add(countprofit);
		money.add(khbasset);
		money.add(expmoneycurrentprofit);
	}


	private void setData(int count, float range) {
		float mult = range;
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		// 随机给饼状图添加数据
		if (allmoney>0) {
			for (int i = 0; i < count; i++) {
				yVals1.add(new Entry(Float.parseFloat(money.get(i) + ""), i));
			}
		}else {
			yVals1.add(new Entry(Float.parseFloat(1.0 + ""), 0));
		}
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count ; i++)
			xVals.add("");

		PieDataSet set1 = new PieDataSet(yVals1,"");
		set1.setSliceSpace(0);

		// 随机添加颜色
		ArrayList<Integer> color = new ArrayList<Integer>();
		if (allmoney>0){
			for (int i = 0; i < count; i++) {
				color.add(getResources().getColor(colors[i]));
			}
		}else {
			color.add(getResources().getColor(R.color.bottom_line));
		}

		color.add(ColorTemplate.getHoloBlue());
		set1.setColors(color);

		PieData data = new PieData(xVals, set1);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_assets_yue_wrapper:
				toActionList();
				break;

			case R.id.activity_assets_dq_wrapper:
				toDQInvestDetail();
				break;

			case R.id.activity_assets_dq_profit_wrapper:
				toHQMoney();

				break;

			case R.id.activity_assets_exp_profit_wrapper:
				toExpProfit();
				break;

			case R.id.activity_assets_khb_money_wrapper:
				toDQProfit();
				break;

			default:
				break;
		}
	}

	/**
	 * 去当前体验金收益
	 */
	private void toExpProfit() {
		Intent intent = new Intent(context, ProfitListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("userprofit", mUserProfit);
		intent.putExtras(bundle);
		intent.putExtra("selected", 5);
		intent.putExtra("countprofit", countprofit);
		startActivity(intent);
	}

	/**
	 * 去当前定期收益
	 */
	private void toDQProfit() {
		Intent intent = new Intent(context, ProfitListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("userprofit", mUserProfit);
		intent.putExtras(bundle);
		intent.putExtra("selected", 4);
		intent.putExtra("countprofit", countprofit);
		startActivity(intent);
	}

	/**
	 * 去收支明细
	 */
	private void toActionList() {
		Intent intent = new Intent(context, ActionListActivity.class);
		startActivity(intent);
	}

	/**
	 * 去快活宝
	 */
	private void toHQMoney() {
//		this.setResult(HomeFragment.AccAssetActivityTag);
		EventBus.getDefault().post(new HQevnt());
		this.finish();
	}

	/**
	 * 去定期投资明细
	 */
	private void toDQInvestDetail() {
		Intent intent = new Intent(context, ActivityQZC.class);
		if (dqmoney!=null) {
			intent.putExtra("dqmoney", dqmoney);
		}
		startActivity(intent);
	}

}
