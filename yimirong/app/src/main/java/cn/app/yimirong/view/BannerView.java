package cn.app.yimirong.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.R;

public class BannerView extends RelativeLayout implements ViewPager.OnPageChangeListener {

	private ViewPager pager;
	private LinearLayout wrapper;

//	private List<RadioButton> dots;

	private List<String> list;

	private BannerAdapter adapter;

	private int currentPage = 0;

	private BannerHandler handler;

	private int delay = 3000;

	private OnBannerClickListener listener;

	public BannerView(Context context) {
		super(context);
		initView(context);
	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	/**
	 * 加载布局文件
	 */
	private void initView(Context context) {
		handler = new BannerHandler(this);
		list = new ArrayList<>();
//		dots = new ArrayList<>();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.layout_banner, this, true);
		pager = (ViewPager) view.findViewById(R.id.layout_banner_pager);
		wrapper = (LinearLayout) view.findViewById(R.id.layout_banner_dots);
		pager.addOnPageChangeListener(this);
	}

	public void stop() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}


	public void refreshBanner(List<String> banners) {
		if (banners == null) {
			throw new RuntimeException("list must not be null");
		}
		list.clear();
		list.addAll(banners);
//		refreshDots();
		if (adapter == null) {
			adapter = new BannerAdapter(list);
			pager.setAdapter(adapter);
			handler.sendEmptyMessageDelayed(0, delay);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	private void refreshDots() {
		wrapper.removeAllViews();
//		dots.clear();
		for (int i = 0; i < list.size(); i++) {
			RadioButton dot = new RadioButton(getContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			if (Build.VERSION.SDK_INT < 11) {
				layoutParams.setMargins(-20, 0, -20, 0);
			}
			dot.setLayoutParams(layoutParams);
			dot.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			dot.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_dot);
			dot.setPadding(5, 0, 5, 0);
			dot.setVisibility(View.VISIBLE);
			if (i == currentPage) {
				dot.setChecked(true);
			} else {
				dot.setChecked(false);
			}
			dot.setClickable(false);
			wrapper.addView(dot);
//			dots.add(dot);
		}
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void setBannerListener(OnBannerClickListener listener) {
		this.listener = listener;
	}

	/**
	 * 切换
	 */
	private void switchItem() {
		if (!list.isEmpty()) {
			pager.setCurrentItem((currentPage + 1) % list.size());
			handler.sendEmptyMessageDelayed(0, delay);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		int lastPage = currentPage;
		currentPage = position;
//		if (lastPage < dots.size()) {
//			dots.get(lastPage).setChecked(false);
//		}
//
//		if (currentPage < dots.size()) {
//			dots.get(currentPage).setChecked(true);
//		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private class BannerAdapter extends PagerAdapter {

		private List<String> list;

		public BannerAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Context context = container.getContext();
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setClickable(true);
			Glide.with(context).load(list.get(position)).into(imageView);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onClick(position);
					}
				}
			});
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}


		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	private static class BannerHandler extends Handler {

		private WeakReference<BannerView> weakref;

		public BannerHandler(BannerView view) {
			this.weakref = new WeakReference<>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				BannerView view = weakref.get();
				if (view != null) {
					view.switchItem();
				}
			}
		}
	}

	public interface OnBannerClickListener {
		public void onClick(int i);
	}
}
