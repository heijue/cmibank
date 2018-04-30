package cn.app.yimirong.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.view.YinDaoView;

/**
 * 首次进入导航
 *
 * @author android
 */
public class FirstNavActivity extends BaseActivity {

	private ViewPager viewPager;
	private PagerAdapter adapter;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_first_nav);
		isStatusBarTint = false;
		shouldVerify = false;
	}

	@Override
	public void initView() {
		setTitleBar(false);
		viewPager = (ViewPager) findViewById(R.id.activity_first_nav_pager);
	}

	@Override
	public void initData() {
		adapter = new ImagesAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		viewPager = null;
		adapter = null;
	}

	private class ImagesAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView((View) obj);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			YinDaoView iv = new YinDaoView(context);

			if (position == 0) {
				iv.setBackgroundResource(R.drawable.ydy_01);

			} else if (position == 1) {
				iv.setBackgroundResource(R.drawable.ydy_02);
			} else if (position == 2){
				iv.setBackgroundResource(R.drawable.ydy_03);
			}else {
				iv.setBackgroundResource(R.drawable.ydy_04);
			}

			if (position == 3) {
				iv.setTiyinBTVIsible(true);
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						toMain();
						MobclickAgent.onEvent(context,"firstbtn");
					}
				});
			}
			container.addView(iv);
			return iv;
		}
	}

	private void toMain() {
		startActivity(new Intent(context, MainActivity.class));
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

}
