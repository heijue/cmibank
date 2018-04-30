package cn.app.yimirong.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaor on 2016/11/28.
 */

public class MarketUtils {



	public static ArrayList<String> getInstallAppMarkets(Context context) {
		ArrayList<String>  pkgList = new ArrayList<>();
		pkgList.add("com.xiaomi.market");
		pkgList.add("com.qihoo.appstore");
		pkgList.add("com.wandoujia.phoenix2");
		pkgList.add("com.tencent.android.qqdownloader");
		pkgList.add("com.huawei.appmarket");
		pkgList.add("com.hiapk.marketpho");
		pkgList.add("cn.goapk.market");
		pkgList.add("com.taobao.appcenter");
		pkgList.add("com.baidu.appsearch");
		pkgList.add("com.dragon.android.pandaspace");
		pkgList.add("com.yingyonghui.marke");
		pkgList.add("com.mappn.gfan");
		pkgList.add("com.pp.assistant");
		pkgList.add("com.oppo.market");
		pkgList.add("cn.goapk.market");
		pkgList.add("zte.com.market");
		pkgList.add("com.yulong.android.coolmart");
		pkgList.add("com.lenovo.leos.appstore");
		pkgList.add("com.coolapk.market");
		pkgList.add("com.dragon.android.pandaspace");

		ArrayList<String> pkgs = new ArrayList<String>();
		if (context == null){
			return pkgs;
		}
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.APP_MARKET");
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
		if (infos != null && infos.size() > 0){
			int size = infos.size();
			for (int i = 0; i < size; i++) {
				String pkgName = "";
				try {
					ActivityInfo activityInfo = infos.get(i).activityInfo;
					pkgName = activityInfo.packageName;
	
	
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!TextUtils.isEmpty(pkgName))
					pkgs.add(pkgName);
			}
		}
		if(pkgs.size() > 0){
			pkgList.removeAll(pkgs);
		}
		pkgs.addAll(pkgList);
		return pkgs;
	}
	
	
	public static ArrayList<String> getSystemMarket2(Context context){
		ArrayList<String> name = new ArrayList<>();
		
	
		PackageManager packageManager = context.getPackageManager();
		Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
		mIntent.addCategory("android.intent.category.APP_MARKET");
		List<ResolveInfo> listAllApps = packageManager.queryIntentActivities(mIntent, 0);
		
		for(int i = 0;i < listAllApps.size();i++){
			
			ResolveInfo appInfo = listAllApps.get(i);
			String pkgName = appInfo.activityInfo.packageName;//获取包名
			//根据包名获取PackageInfo mPackageInfo;（需要处理异常）
			PackageInfo mPackageInfo = null;
			try {
				mPackageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
				if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				    //系统应用
					name.add(pkgName);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		return name;
	}
	
	public static ArrayList<String> getSystemMarket(Context context){
		PackageManager pm = context.getPackageManager();
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.APP_MARKET");
//		List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		ArrayList<String> name = new ArrayList<>();
//		for(int i = 0;i < infos.size();i++){
//			ActivityInfo activityInfo = infos.get(i).activityInfo;
//			String pkgName = activityInfo.packageName;
//			name.add(pkgName);
//		}
		for(PackageInfo info : packages){
			if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
				if(info.packageName.contains("market") || info.packageName.contains("store")){
					name.add(info.packageName);
				}
			}
			
		}
		
		return name;
	}


	/**
	 * ������������������������������������������
	 *
	 * @param context
	 * @return
	 */
	public static ArrayList<String> queryInstalledMarketPkgs(Context context) {
		ArrayList<String> pkgs = new ArrayList<String>();
		if (context == null)
			return pkgs;
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.APP_MARKET");
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
		if (infos == null || infos.size() == 0)
			return pkgs;
		int size = infos.size();
		for (int i = 0; i < size; i++) {
			String pkgName = "";
			try {
				ActivityInfo activityInfo = infos.get(i).activityInfo;
				pkgName = activityInfo.packageName;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!TextUtils.isEmpty(pkgName))
				pkgs.add(pkgName);

		}
		return pkgs;
	}

	/**
	 * ������������������������������������
	 *
	 * @param context
	 * @param pkgs
	 *            ���������������������
	 * @return ������������������������
	 */
	public static ArrayList<String> filterInstalledPkgs(Context context,
														ArrayList<String> pkgs) {
		ArrayList<String> empty = new ArrayList<String>();
		if (context == null || pkgs == null || pkgs.size() == 0)
			return empty;
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPkgs = pm.getInstalledPackages(0);
		int li = installedPkgs.size();
		int lj = pkgs.size();
		for (int j = 0; j < lj; j++) {
			for (int i = 0; i < li; i++) {
				String installPkg = "";
				String checkPkg = pkgs.get(j);
				try {
					installPkg = installedPkgs.get(i).applicationInfo.packageName;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (TextUtils.isEmpty(installPkg))
					continue;
				if (installPkg.equals(checkPkg)) {
					empty.add(installPkg);
					break;
				}

			}
		}
		return empty;
	}

	/**
	 * ���������app������������
	 *
	 * @param appPkg
	 *            App���������
	 * @param marketPkg
	 *            ������������������ ,���������""���������������������������������������������������,������������������������������������������������������������������������������������
	 */
	public static void launchAppDetail(String appPkg, String marketPkg, Context context) {
		try {
			if (TextUtils.isEmpty(appPkg)) {
				return;
			}
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!TextUtils.isEmpty(marketPkg)) {
				intent.setPackage(marketPkg);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void launchAppDetail(String appPkg,Context context){
		try {
			if (TextUtils.isEmpty(appPkg)) {
				return;
			}
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (checkApkExist(context, intent)) {
				context.startActivity(intent);
			}else {
				ToastUtils.show(context,"您尚未安装任何应用市场");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void launchAppDetail(String appPkg, ArrayList<String> marketPkg, Context context) {
		try {
			if (TextUtils.isEmpty(appPkg)) {
				return;
			}
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (marketPkg.size() > 0) {
				for (int i = 0; i < marketPkg.size();i++) {
					intent.setPackage(marketPkg.get(i));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					if (checkApkExist(context, intent)) {
						context.startActivity(intent);
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
		return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static boolean checkApkExist(Context context,Intent intent) {
		List<ResolveInfo> list =  context.getPackageManager().queryIntentActivities(intent, 0);
		if(list.size() > 0){
			return true;
		}
		return false;
	}



}
