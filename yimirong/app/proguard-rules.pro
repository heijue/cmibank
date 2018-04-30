# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:


-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-ignorewarnings
-optimizationpasses 5  #指定代码压缩级别
-dontpreverify
#WebView
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#FMAgent
-dontwarn android.os.**
-dontwarn com.android.internal.**
-dontwarn cn.fraudmetrix.android.**
-keepclasseswithmembers class cn.fraudmetrix.android.**

#删除Log日志6
-assumenosideeffects class android.util.Log {
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

#基本组件
-keep public class * extends android.cn.app.Activity
-keep public class * extends android.cn.app.Application
-keep public class * extends android.cn.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.cn.app.backup.BackupAgentHelper

#eventbus
-keep class cn.app.yimirong.event.**{*;}
#utils
-keep class cn.app.yimirong.utils.JSONUtils{*;}
#宝付支付
-keep class com.baofoo.sdk.vip.** {*;}

#连连支付SDK混淆规则
-keep class com.kdlc.pay.llpay.** {*;}
-keep class com.yintong.secure.activityproxy.PayIntro$LLJavascriptInterface{*;}
-keep public class com.yintong.** {
    <fields>;
    <methods>;
}

#xUtils
-keep class * extends java.lang.annotation.Annotation { *; }
-keep class com.lidroid.xutils.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }

#信鸽推送
-keep public class * extends android.cn.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.**  {* ;}
-keep class com.tencent.mid.**  {* ;}

#Bugly
-keep public class com.tencent.bugly.**{*;}

#友盟统计
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class cn.app.yimirong.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#友盟分享
-dontusemixedcaseclassnames
	-dontshrink
	-dontoptimize
	-dontwarn com.google.android.maps.**
	-dontwarn android.webkit.WebView
	-dontwarn com.umeng.**
	-dontwarn com.tencent.weibo.sdk.**
	-dontwarn com.facebook.**
	-keep public class javax.**
	-keep public class android.webkit.**
	-dontwarn android.support.v4.**
	-keep enum com.facebook.**
	-keepattributes Exceptions,InnerClasses,Signature
	-keepattributes *Annotation*
	-keepattributes SourceFile,LineNumberTable

	-keep public interface com.facebook.**
	-keep public interface com.tencent.**
	-keep public interface com.umeng.socialize.**
	-keep public interface com.umeng.socialize.sensor.**
	-keep public interface com.umeng.scrshot.**
	-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
	-keep public class com.umeng.socialize.* {*;}


	-keep class com.facebook.**
	-keep class com.facebook.** { *; }
	-keep class com.umeng.scrshot.**
	-keep public class com.tencent.** {*;}
	-keep class com.umeng.socialize.sensor.**
	-keep class com.umeng.socialize.handler.**
	-keep class com.umeng.socialize.handler.*
	-keep class com.umeng.weixin.handler.**
	-keep class com.umeng.weixin.handler.*
	-keep class com.umeng.qq.handler.**
	-keep class com.umeng.qq.handler.*
	-keep class UMMoreHandler{*;}
	-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
	-keep class com.tencent.mm.sdk.modelmsg.** implements 	com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
	-keep class im.yixin.sdk.api.YXMessage {*;}
	-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
	-keep class com.tencent.mm.sdk.** {
  	 *;
	}
	-keep class com.tencent.mm.opensdk.** {
   *;
	}
	-dontwarn twitter4j.**
	-keep class twitter4j.** { *; }

	-keep class com.tencent.** {*;}
	-dontwarn com.tencent.**
	-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
	}
	-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
		}
	-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
	}

	-keep class com.tencent.open.TDialog$*
	-keep class com.tencent.open.TDialog$* {*;}
	-keep class com.tencent.open.PKDialog
	-keep class com.tencent.open.PKDialog {*;}
	-keep class com.tencent.open.PKDialog$*
	-keep class com.tencent.open.PKDialog$* {*;}

	-keep class com.sina.** {*;}
	-dontwarn com.sina.**
	-keep class  com.alipay.share.sdk.** {
	   *;
	}
	-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
	}

	-keep class com.linkedin.** { *; }
	-keepattributes Signature

#Gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.kdlc.model.bean.** { *; }

#Zxing
-keep class com.google.zxing.** { *; }

#EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

#Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Native方法
-keepclasseswithmembernames class * {
    native <methods>;
}

#AndFix
-keep class * extends java.lang.annotation.Annotation

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
 #富友
 -keep class com.fuiou.mobile.**{*;}
 -keep class android.webkit.**{*;}

 #butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}




-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
-keep class cn.app.yimirong.model.bean.** { *; }


-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
 private void readObject(java.io.ObjectInputStream);
 java.lang.Object writeReplace();
java.lang.Object readResolve();
}
-keep public class * implements java.io.Serializable {*;}


-keepattributes EnclosingMethod
# glide 的混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }