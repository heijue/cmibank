package cn.app.yimirong.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cn.app.yimirong.App;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.LeftTime;
import cn.app.yimirong.model.bean.SpecialDays;

/**
 * TimeUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	public static final SimpleDateFormat DEFAULT_DATE_MINUTE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_CHINA_DAY = new SimpleDateFormat(
			"yyyy年MM月dd日", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_CHINA_MONTH = new SimpleDateFormat(
			"yyyy年MM月", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_DAY_MINUTE = new SimpleDateFormat(
			"MM-dd HH:mm", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_DATE_DOT = new SimpleDateFormat(
			"yyyy.MM.dd", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_MONTH_DAY = new SimpleDateFormat(
			"MM月dd日", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_CHINA_HOUR = new SimpleDateFormat(
			"HH:mm", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_MONTH = new SimpleDateFormat("MM", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("dd", Locale.CHINA);
	public static final SimpleDateFormat DATE_FORMAT_HOUR = new SimpleDateFormat("HH", Locale.CHINA);

	static {
		DEFAULT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DEFAULT_DATE_MINUTE.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_DATE.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_CHINA_DAY.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_CHINA_MONTH.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_DAY_MINUTE.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_MONTH_DAY.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_HOUR.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_MONTH.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		DATE_FORMAT_DAY.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
	}

	private TimeUtils() {
		throw new AssertionError();
	}

	/**
	 * long time to string
	 *
	 * @param timeInMillis
	 * @param dateFormat
	 * @return
	 */
	public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
		return dateFormat.format(new Date(timeInMillis));
	}

	public static String getTimeFromSeconds(long seconds,
											SimpleDateFormat dateFormat) {
		return dateFormat.format(new Date(seconds * 1000));
	}

	/**
	 * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
	 *
	 * @param timeInMillis
	 * @return
	 */
	public static String getTime(long timeInMillis) {
		return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 根据秒数计算时间
	 *
	 * @param seconds
	 * @return
	 */
	public static String getTimeFromSeconds(long seconds) {
		return getTime(seconds * 1000, DATE_FORMAT_DATE);
	}

	/**
	 * 根据秒数计算时间2
	 *
	 * @param seconds
	 * @return
	 */
	public static String getTimeFromSeconds2(long seconds) {
		return getTime(seconds * 1000, DATE_FORMAT_DAY_MINUTE);
	}

	/**
	 * 根据秒数计算时间2
	 *
	 * @param seconds
	 * @return
	 */
	public static String getTimeFromSeconds3(long seconds) {
		return getTime(seconds * 1000, DATE_FORMAT_CHINA_HOUR);
	}

	/**
	 * get current time in milliseconds
	 *
	 * @return
	 */
	public static long getCurrentTimeInLong() {
		return System.currentTimeMillis();
	}

	/**
	 * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
	 *
	 * @return
	 */
	public static String getCurrentTimeInString() {
		return getTime(getCurrentTimeInLong());
	}

	/**
	 * get current time in milliseconds
	 *
	 * @return
	 */
	public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
		return getTime(getCurrentTimeInLong(), dateFormat);
	}

	/**
	 * 获取时间
	 *
	 * @param ms
	 * @return
	 */
	public static String getTime(String ms) {
		long time = Long.parseLong(ms);
		return getTime(time, DATE_FORMAT_DATE);
	}

	public static String getTimeExp(String ms) {
		long time = Long.parseLong(ms)*1000;
		return getTime(time, DATE_FORMAT_DATE_DOT);
	}

	public static String getTimeFromSeconds(String seconds) {
		long time = Long.parseLong(seconds);
		return getTime(time * 1000, DATE_FORMAT_DATE);
	}

	public static String getTimeFromSeconds(String seconds,
											SimpleDateFormat format) {
		long time = 0;
		try {
			time = Long.parseLong(seconds);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return getTime(time * 1000, format);
	}

	public static long getTimeFromString(String dateStr) {
		try {
			Date date = DEFAULT_DATE_FORMAT.parse(dateStr);
			long ms = date.getTime();
			return ms;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 从时间字符串解析出时间，秒数
	 *
	 * @param dateStr
	 * @return 解析出的秒数
	 */
	public static long getTimeInSecondsFromString(String dateStr) {
		try {
			Date date = DATE_FORMAT_DATE.parse(dateStr);
			long ms = date.getTime();
			return ms / 1000;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 从时间字符串解析出时间，秒数
	 *
	 * @param dateStr
	 * @return 解析出的秒数
	 */
	public static long getTimeInSecondsFromString(String dateStr,
												  SimpleDateFormat format) {
		try {
			Date date = format.parse(dateStr);
			long ms = date.getTime();
			return ms / 1000;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 根据秒数获取剩余时间 时分秒
	 *
	 * @param time
	 * @return
	 */
	public static LeftTime getLeftTime(long time) {
		int hours = (int) (time / 3600);
		int minutes = (int) ((time % 3600) / 60);
		int seconds = (int) ((time % 3600) % 60);

		LeftTime leftTime = new LeftTime();
		leftTime.hours = hours;
		leftTime.minutes = minutes;
		leftTime.seconds = seconds;

		leftTime.hshi = NumberUtils.getShi(hours);
		leftTime.hge = NumberUtils.getGe(hours);

		leftTime.mshi = NumberUtils.getShi(minutes);
		leftTime.mge = NumberUtils.getGe(minutes);

		leftTime.sshi = NumberUtils.getShi(seconds);
		leftTime.sge = NumberUtils.getGe(seconds);

		return leftTime;
	}

	/**
	 * 返回当前时间
	 *
	 * @return
	 */
	public static long getCurrentTimeSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 增加天数
	 *
	 * @return
	 */
	public static String addDay(int day, String date) {
		long seconds = TimeUtils.getTimeInSecondsFromString(date,
				TimeUtils.DATE_FORMAT_DATE);
		long newSeconds = seconds + 3600 * 24 * day;
		String newDate = TimeUtils.getTimeFromSeconds(newSeconds,
				TimeUtils.DATE_FORMAT_DATE);
		return newDate;
	}

	public static String addDay(int day, long seconds, SimpleDateFormat format) {
		long newSeconds = seconds + 3600 * 24 * day;
		if (format == null) {
			format = DATE_FORMAT_DATE;
		}
		String newDate = TimeUtils.getTimeFromSeconds(newSeconds, format);
		return newDate;
	}

	@SuppressWarnings("deprecation")
	public static String getDayOfWeek(long seconds, int add) {
		long newSeconds = seconds + add * 3600 * 24;
		Date date = new Date(newSeconds * 1000);
		int day = date.getDay();
		String week = null;
		switch (day) {
			case 0:
				week = "星期日";
				break;
			case 1:
				week = "星期一";
				break;
			case 2:
				week = "星期二";
				break;
			case 3:
				week = "星期三";
				break;
			case 4:
				week = "星期四";
				break;
			case 5:
				week = "星期五";
				break;
			case 6:
				week = "星期六";
				break;
			default:
				week = "";
				break;
		}
		return week;
	}

	/**
	 * 获取当前时间--小时
	 *
	 * @return
	 */
	public static int getServerHour(long s) {
		long servertime = System.currentTimeMillis() / 1000 - App.delta;
		String hour = getTimeFromSeconds(s, DATE_FORMAT_HOUR);
		int h = 0;
		h = Integer.parseInt(hour);
		return h;
	}

	public static int getServerHour() {
		long servertime = System.currentTimeMillis() / 1000 - App.delta;
		String hour = getTimeFromSeconds(servertime, DATE_FORMAT_HOUR);
		int h = 0;
		h = Integer.parseInt(hour);
		return h;
	}

	public static int getServerYear(){
		long servertime = System.currentTimeMillis() / 1000 - App.delta;
		String hour = getTimeFromSeconds(servertime, DATE_FORMAT_YEAR);
		int year = 0;
		year = Integer.parseInt(hour);
		return year;
	}

	public static int getServerMonth(){
		long servertime = System.currentTimeMillis() / 1000 - App.delta;
		String hour = getTimeFromSeconds(servertime, DATE_FORMAT_MONTH);
		int month = 0;
		month = Integer.parseInt(hour);
		return month;
	}

	/**
	 * 获取小时数
	 *
	 * @return
	 */
	public static int getHour(long seconds) {
		int h = 0;
		String hour = getTimeFromSeconds(seconds, DATE_FORMAT_HOUR);
		h = Integer.parseInt(hour);
		return h;
	}

	/**
	 * 获取服务器时间
	 *
	 * @return
	 */
	public static long getServerTime() {
		long serverTime = System.currentTimeMillis() / 1000 - App.delta;
		return serverTime;
	}

	/**
	 * 获取服务器时间
	 *
	 * @param format
	 * @return
	 */
	public static String getServerDate(SimpleDateFormat format) {
		String serverTime = null;

		long ms = getServerTime() * 1000;

		Date date = new Date(ms);

		if (format == null) {
			format = DATE_FORMAT_DATE;
		}
		serverTime = format.format(date);

		return serverTime;
	}

	/**
	 * 获取上线时间和今天相差的天数
	 *
	 * @param onlineTime
	 * @return
	 */
	public static int getDaysFromOnlineTime(String onlineTime) {
		String serverDate = getServerDate(DATE_FORMAT_DATE);

		long serverSeconds = getTimeInSecondsFromString(serverDate,
				DATE_FORMAT_DATE);
		long onlineSeconds = getTimeInSecondsFromString(onlineTime,
				DATE_FORMAT_DATE);

		int days = (int) ((onlineSeconds - serverSeconds) / (24 * 3600));

		if (days < 0) {
			days = 0;
		}

		return days;
	}

	/**
	 * 今天是星期几 0表示星期日
	 *
	 * @return
	 */
	public static int getServerDayOfWeek() {
		long serverTime = getServerTime();
		Date date = new Date(serverTime * 1000);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek - 1;
	}

	/**
	 * 获取星期几
	 *
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(String date, SimpleDateFormat format) {
		Date d = null;
		if (format == null) {
			format = DATE_FORMAT_DATE;
		}
		try {
			d = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		cal.setTime(d);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek - 1;
	}


	/**
	 * 判断指定日期是否是工作日
	 *
	 * @param date
	 * @return 不是工作日就是非工作日
	 */
	public static boolean isWorkDay(String date) {
		if (StringUtils.isBlank(date)) {
			throw new RuntimeException("日期不能为空");
		}

		try {
			DATE_FORMAT_DATE.parse(date);
		} catch (Exception e) {
			throw new RuntimeException("日期格式不正确");
		}

		boolean isWorkday = true;

		if (DataMgr.sd == null) {
			return isWorkday;
		}

		List<String> holidays = DataMgr.sd.holidays;
		List<String> workdays = DataMgr.sd.workdays;

		if (workdays.contains(date)) {
			// 如果是法定调班，是工作日
			isWorkday = true;
		} else {
			// 不是法定调班
			if (holidays.contains(date)) {
				// 如果是法定调休
				isWorkday = false;
			} else {
				//不是调休
				//判断是不是周末
				int dayOfWeek = getDayOfWeek(date, DATE_FORMAT_DATE);
				if (dayOfWeek == 0 || dayOfWeek == 6) {
					//如果是周末
					isWorkday = false;
				} else {
					isWorkday = true;
				}
			}
		}
		return isWorkday;
	}

	/**
	 * 获取一天开始时间
	 *
	 * @return
	 */
	public static long getStartTimeOfDay(long seconds) {
		Date date = new Date(seconds * 1000);
		String time = DATE_FORMAT_DATE.format(date);
		String startTime = time + " 00:00:00";
		long startTimeMillis = 0;
		try {
			Date startDate = DEFAULT_DATE_FORMAT.parse(startTime);
			startTimeMillis = startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startTimeMillis/1000;
	}

	/**
	 * 获取一天结束时间
	 *
	 * @param seconds
	 * @return
	 */
	public static long getEndTimeOfDay(long seconds) {
		Date date = new Date(seconds * 1000);
		String time = DATE_FORMAT_DATE.format(date);
		String endTime = time + " 23:59:59";
		long endTimeMillis = 0;
		try {
			Date startDate = DEFAULT_DATE_FORMAT.parse(endTime);
			endTimeMillis = startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return endTimeMillis;
	}

	/**
	 * 解析specialdays
	 *
	 * @param json
	 * @return
	 */
	public static SpecialDays parseSpecialDays(String json) {
		SpecialDays sd = new SpecialDays();
		sd.holidays = new ArrayList<String>();
		sd.workdays = new ArrayList<String>();
		if (!JSONUtils.isJsonString(json)) {
			return sd;
		}
		try {
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonHolidays = null;
			JSONArray jsonWorkdays = null;
			if (!jsonObj.isNull("holidays") && jsonObj.get("holidays") instanceof JSONArray) {
				jsonHolidays = jsonObj.getJSONArray("holidays");
			}

			if (!jsonObj.isNull("workdays") && jsonObj.get("workdays") instanceof JSONArray) {
				jsonWorkdays = jsonObj.getJSONArray("workdays");
			}

			if (jsonHolidays != null) {
				int length = jsonHolidays.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonDate = jsonHolidays.getJSONObject(i);
					String value = jsonDate.getString("date");
					sd.holidays.add(value);
				}
			}

			if (jsonWorkdays != null) {
				int length = jsonWorkdays.length();
				for (int i = 0; i < length; i++) {
					JSONObject jsonDate = jsonWorkdays.getJSONObject(i);
					String value = jsonDate.getString("date");
					sd.workdays.add(value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sd;
	}

	public static String getFormatedTime(long seconds) {
		int hh = (int) (seconds / 3600);
		int mm = (int) ((seconds % 3600) / 60);
		int ss = (int) ((seconds % 3600) % 60);
		return (hh < 10 ? ("0" + hh) : hh) + ":" + (mm < 10 ? ("0" + mm) : mm) + ":" + (ss < 10 ? ("0" + ss) : ss);
	}
}
