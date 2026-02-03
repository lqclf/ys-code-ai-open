package com.eric.common.utils;

import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类 提供日期计算、格式化、转换等常用功能
 * 支持java.util.Date和java.time.LocalDateTime两种日期类型
 * @ClassName:  DateUtils
 * @author:     liuQ
 * @date:       2025-07-22 15:09:39
 * @Copyright   ERIC 微信公众号：Eric的技术杂货库
 */
public class DateUtils {

	// 线程安全的格式化器实例
	private static final DateTimeFormatter YMDHMS_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter YMD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter FULL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final DateTimeFormatter MD_FORMATTER = DateTimeFormatter.ofPattern("MM/dd");
	private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final DateTimeFormatter BASIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
	public static final long MILLIS_PER_DAY = 86_400_000L;
	public static final long MILLIS_PER_HOUR = 3_600_000L;
	public static final long MILLIS_PER_MINUTE = 60_000L;
	public static final long MILLIS_PER_SECOND = 1_000L;

	/**
	 * 获取当前日期（无时间部分）
	 * @return LocalDate 当前系统日期
	 */
	private static LocalDate currentDate() {
		return LocalDate.now();
	}

	/**
	 * 获取当前日期时间
	 * @return LocalDateTime 当前系统日期时间
	 */
	private static LocalDateTime currentDateTime() {
		return LocalDateTime.now();
	}

	/**
	 * 获取当前年份
	 * @return 当前年份（整数形式），如2023
	 */
	public static Integer getYear() {
		return Year.now().getValue();
	}

	/**
	 * 获取当前月份
	 * @return 当前月份（1-12）
	 */
	public static Integer getMonth() {
		return MonthDay.now().getMonthValue();
	}

	/**
	 * 获取当前日期（月中的天数）
	 * @return 当前月中的日期（1-31）
	 */
	public static Integer getDay() {
		return currentDate().getDayOfMonth();
	}

	/**
	 * 获取当前是今年的第几天
	 * @return 一年中的天数（1-366）
	 */
	public static Integer getDayofYear() {
		return currentDate().getDayOfYear();
	}

	/**
	 * 生成时间戳字符串（yyyyMMddHHmmss格式）
	 * @return 14位时间戳字符串，如20230616123045
	 */
	public static String getYMDHms() {
		return LocalDateTime.now().format(YMDHMS_FORMATTER);
	}

	/**
	 * 获取当前时间的毫秒数
	 * @return 当前系统时间的毫秒数（字符串形式）
	 */
	public static String getMillis() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * 获取当前日期字符串（yyyy-MM-dd格式）
	 * @return 标准日期字符串，如2023-06-16
	 */
	public static String getYMD() {
		return currentDate().format(YMD_FORMATTER);
	}

	/**
	 * 获取当前月中的日期（同getDay）
	 * @return 当前月中的日期（1-31）
	 */
	public static Integer getDayofMonth() {
		return currentDate().getDayOfMonth();
	}

	/**
	 * 获取当前月的总天数
	 * @return 当前月份的天数（28-31）
	 */
	public static Integer getActualMaximum() {
		return currentDate().lengthOfMonth();
	}

	/**
	 * 获取当月剩余天数
	 * @return 当前月份剩余的天数
	 */
	public static Integer getEndOfMonth() {
		return currentDate().lengthOfMonth() - currentDate().getDayOfMonth();
	}

	/**
	 * 获取相对日期的字符串表示
	 * @param offsetDays 日期偏移量（正数为未来，负数为过去）
	 * @return yyyy-MM-dd格式的日期字符串
	 */
	public static String getPastDateStr(int offsetDays) {
		return currentDate().plusDays(offsetDays).format(YMD_FORMATTER);
	}

	/**
	 * 获取相对日期的Date对象
	 * @param offsetDays 日期偏移量（正数为未来，负数为过去）
	 * @return java.util.Date对象
	 */
	public static Date getPastDate(int offsetDays) {
		return Date.from(currentDate().plusDays(offsetDays)
				.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 获取相对日期的LocalDateTime对象
	 * @param offsetDays 日期偏移量（正数为未来，负数为过去）
	 * @return LocalDateTime对象
	 */
	public static LocalDateTime getPastLocalDateTime(int offsetDays) {
		return currentDate().plusDays(offsetDays).atStartOfDay();
	}

	/**
	 * 获取相对日期的月/日格式
	 * @param offsetDays 日期偏移量（正数为未来，负数为过去）
	 * @return MM/dd格式的字符串，如06/15
	 */
	public static String getPastDateTime(int offsetDays) {
		return currentDate().plusDays(offsetDays).format(MD_FORMATTER);
	}

	/**
	 * 计算指定日期的相对日期(Date版本)
	 * @param date 基准日期
	 * @param offsetDays 日期偏移量（正数为未来，负数为过去）
	 * @return 计算后的java.util.Date对象
	 */
	public static Date getSpecialDatePastDate(Date date, int offsetDays) {
		return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				.plusDays(offsetDays)
				.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 计算指定日期的相对日期(LocalDateTime版本)
	 * @param dateTime 基准日期时间
	 * @param offsetDays 日期偏移量（正数为未来，负数为过去）
	 * @return 计算后的LocalDateTime对象
	 */
	public static LocalDateTime getSpecialDatePastDates(LocalDateTime dateTime, int offsetDays) {
		return dateTime.plusDays(offsetDays);
	}

	/**
	 * 获取指定日期的前一天
	 * @param specifiedDay 基准日期字符串（yy-MM-dd格式）
	 * @return yyyy-MM-dd格式的前一天日期字符串
	 */
	public static String getSpecifiedDayBefore(String specifiedDay) {
		return parseToLocalDate(specifiedDay, BASIC_DATE_FORMATTER)
				.minusDays(1)
				.format(YMD_FORMATTER);
	}

	/**
	 * 获取指定日期的后一天
	 * @param specifiedDay 基准日期字符串（yy-MM-dd格式）
	 * @return yyyy-MM-dd格式的后一天日期字符串
	 */
	public static String getSpecifiedDayAfter(String specifiedDay) {
		return parseToLocalDate(specifiedDay, BASIC_DATE_FORMATTER)
				.plusDays(1)
				.format(YMD_FORMATTER);
	}

	/**
	 * 获取两个日期之间的所有日期（闭区间）
	 * @param startDate 开始日期（yyyy-MM-dd格式）
	 * @param endDate 结束日期（yyyy-MM-dd格式）
	 * @return 日期字符串列表（包含起止日期）
	 */
	public static List<String> getMonthBetween(String startDate, String endDate) {
		List<String> result = new ArrayList<>();
		LocalDate start = parseToLocalDate(startDate, YMD_FORMATTER);
		LocalDate end = parseToLocalDate(endDate, YMD_FORMATTER);

		// 包含开始日期到结束日期的所有日期
		while (!start.isAfter(end)) {
			result.add(start.format(YMD_FORMATTER));
			start = start.plusDays(1);
		}
		return result;
	}

	/**
	 * 获取某年某月的所有日期
	 * @param year 年份（如2023）
	 * @param month 月份（1-12）
	 * @return 该月所有日期的字符串列表（yyyy-MM-dd格式）
	 */
	public static List<String> getMonthFullDay(Integer year, Integer month) {
		List<String> days = new ArrayList<>();
		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate date = yearMonth.atDay(1);

		// 遍历当月所有日期
		while (date.getMonthValue() == month) {
			days.add(date.format(YMD_FORMATTER));
			date = date.plusDays(1);
		}
		return days;
	}

	/**
	 * 判断指定日期是否为周末
	 * @param date 日期字符串（yyyy-MM-dd格式）
	 * @return true-周末，false-工作日
	 */
	public static Boolean isWeekend(String date) {
		DayOfWeek dayOfWeek = parseToLocalDate(date, YMD_FORMATTER).getDayOfWeek();
		return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
	}

	/**
	 * 获取上个月份字符串
	 * @return yyyy-MM格式的上个月份，如2023-05
	 */
	public static String getLastMonth() {
		return YearMonth.now().minusMonths(1).format(YEAR_MONTH_FORMATTER);
	}

	/**
	 * 获取本月第一天
	 * @return yyyy-MM-dd格式的本月首日
	 */
	public static String getMonthFisrtDate() {
		return currentDate().withDayOfMonth(1).format(YMD_FORMATTER);
	}

	/**
	 * 获取本月最后一天
	 * @return yyyy-MM-dd格式的本月最后一日
	 */
	public static String getMonthLastDate() {
		return currentDate().with(TemporalAdjusters.lastDayOfMonth()).format(YMD_FORMATTER);
	}

	/**
	 * 获取上个月第一天
	 * @return yyyy-MM-dd格式的上月首日
	 */
	public static String getLastMonthFisrtDate() {
		return currentDate().minusMonths(1).withDayOfMonth(1).format(YMD_FORMATTER);
	}

	/**
	 * 获取上个月最后一天
	 * @return yyyy-MM-dd格式的上月最后一日
	 */
	public static String getLastMonthLastDate() {
		return currentDate().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(YMD_FORMATTER);
	}

	/**
	 * 从Date对象获取年份
	 * @param date java.util.Date对象
	 * @return 年份整数
	 */
	public static Integer getYearByDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).getYear();
	}

	/**
	 * 从LocalDateTime对象获取年份
	 * @param dateTime LocalDateTime对象
	 * @return 年份整数
	 */
	public static Integer getYearByDate(LocalDateTime dateTime) {
		return dateTime.getYear();
	}

	/**
	 * 从Date对象获取月份
	 * @param date java.util.Date对象
	 * @return 月份整数（1-12）
	 */
	public static Integer getMonthByDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).getMonthValue();
	}

	/**
	 * 从LocalDateTime对象获取月份
	 * @param dateTime LocalDateTime对象
	 * @return 月份整数（1-12）
	 */
	public static Integer getMonthByDate(LocalDateTime dateTime) {
		return dateTime.getMonthValue();
	}

	/**
	 * 获取指定年份月份的天数
	 * @param year 年份
	 * @param month 月份（1-12）
	 * @return 该月的天数
	 */
	public static Integer getLastDayOfMonth(Integer year, Integer month) {
		return YearMonth.of(year, month).lengthOfMonth();
	}

	/**
	 * 获取指定日期的上月年月(Date版本)
	 * @param date java.util.Date对象
	 * @return yyyy-MM格式的上个月份字符串
	 */
	public static String getYearmonthByDate(Date date) {
		YearMonth current = YearMonth.from(date.toInstant().atZone(ZoneId.systemDefault()));
		return current.minusMonths(1).format(YEAR_MONTH_FORMATTER);
	}

	/**
	 * 获取指定日期的上月年月(LocalDateTime版本)
	 * @param dateTime LocalDateTime对象
	 * @return yyyy-MM格式的上个月份字符串
	 */
	public static String getYearmonthByDate(LocalDateTime dateTime) {
		YearMonth current = YearMonth.from(dateTime);
		return current.minusMonths(1).format(YEAR_MONTH_FORMATTER);
	}

	/**
	 * 获取指定日期的年月(Date版本)
	 * @param date java.util.Date对象
	 * @return yyyy-MM格式的月份字符串
	 */
	public static String getYearMonthByDate(Date date) {
		return YearMonth.from(date.toInstant().atZone(ZoneId.systemDefault()))
				.format(YEAR_MONTH_FORMATTER);
	}

	/**
	 * 获取指定日期的年月(LocalDateTime版本)
	 * @param dateTime LocalDateTime对象
	 * @return yyyy-MM格式的月份字符串
	 */
	public static String getYearMonthByDate(LocalDateTime dateTime) {
		return YearMonth.from(dateTime).format(YEAR_MONTH_FORMATTER);
	}

	/**
	 * 日期转星期（简写）
	 * @param datetime 日期字符串（yyyy-MM-dd格式）
	 * @return 星期简写，如"周一"
	 */
	public static String dateToWeek(String datetime) {
		return formatDayOfWeek(parseToLocalDate(datetime, YMD_FORMATTER),
				new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"});
	}

	/**
	 * 日期转星期（全称）
	 * @param datetime 日期字符串（yyyy-MM-dd格式）
	 * @return 星期全称，如"星期一"
	 */
	public static String dateToWeek1(String datetime) {
		return formatDayOfWeek(parseToLocalDate(datetime, YMD_FORMATTER),
				new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"});
	}

	/**
	 * 计算两个日期的毫秒差(Date版本)
	 * @param time1 第一个日期
	 * @param time2 第二个日期
	 * @return 两个日期之间的毫秒差（绝对值）
	 */
	public static long dateDiffer(Date time1, Date time2) {
		if (time1 == null || time2 == null) return 0;
		return Math.abs(time1.getTime() - time2.getTime());
	}

	/**
	 * 计算两个日期的毫秒差(LocalDateTime版本)
	 * @param time1 第一个日期时间
	 * @param time2 第二个日期时间
	 * @return 两个日期之间的毫秒差（绝对值）
	 */
	public static long dateDiffer(LocalDateTime time1, LocalDateTime time2) {
		if (time1 == null || time2 == null) return 0;
		return Math.abs(Duration.between(time1, time2).toMillis());
	}

	/**
	 * 计算两个日期的秒差（带两位小数）(Date版本)
	 * @param bgdate 开始时间
	 * @param eddate 结束时间
	 * @return 两个日期之间的秒数差（浮点数）
	 */
	public static double dateDifferSeconds(Date bgdate, Date eddate) {
		long diff = eddate.getTime() - bgdate.getTime();
		return Double.parseDouble(new DecimalFormat("#.00").format(diff / 1000.0));
	}

	/**
	 * 计算两个日期的秒差（带两位小数）(LocalDateTime版本)
	 * @param bgDateTime 开始时间
	 * @param edDateTime 结束时间
	 * @return 两个日期之间的秒数差（浮点数）
	 */
	public static double dateDifferSeconds(LocalDateTime bgDateTime, LocalDateTime edDateTime) {
		double diff = Duration.between(bgDateTime, edDateTime).toMillis();
		return Double.parseDouble(new DecimalFormat("#.00").format(diff / 1000.0));
	}

	/**
	 * 获取当前完整日期时间字符串
	 * @return yyyy-MM-dd HH:mm:ss 格式的当前时间
	 */
	public static String getFullDateTime() {
		return LocalDateTime.now().format(FULL_DATETIME_FORMATTER);
	}

	/**
	 * 获取当前时间字符串（不含日期）
	 * @return HH:mm:ss 格式的当前时间
	 */
	public static String getCurrentTime() {
		return LocalTime.now().format(TIME_FORMATTER);
	}

	/**
	 * 日期字符串转换为Date对象
	 * @param dateStr 日期字符串（yyyy-MM-dd格式）
	 * @return 对应的Date对象
	 */
	public static Date parseDate(String dateStr) {
		return Date.from(LocalDate.parse(dateStr, YMD_FORMATTER)
				.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 日期字符串转换为LocalDateTime对象
	 * @param dateStr 日期字符串（yyyy-MM-dd格式）
	 * @return 对应的LocalDateTime对象（时间部分为00:00:00）
	 */
	public static LocalDateTime parseLocalDateTime(String dateStr) {
		return LocalDate.parse(dateStr, YMD_FORMATTER).atStartOfDay();
	}

	/**
	 * 完整日期时间字符串转换为Date对象
	 * @param dateTimeStr 日期时间字符串（yyyy-MM-dd HH:mm:ss格式）
	 * @return 对应的Date对象
	 */
	public static Date parseDateTime(String dateTimeStr) {
		return Date.from(LocalDateTime.parse(dateTimeStr, FULL_DATETIME_FORMATTER)
				.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 完整日期时间字符串转换为LocalDateTime对象
	 * @param dateTimeStr 日期时间字符串（yyyy-MM-dd HH:mm:ss格式）
	 * @return 对应的LocalDateTime对象
	 */
	public static LocalDateTime parseLocalDateTimeFull(String dateTimeStr) {
		return LocalDateTime.parse(dateTimeStr, FULL_DATETIME_FORMATTER);
	}

	/**
	 * 计算两个日期之间的天数差(Date版本)
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return 两个日期之间的天数差（正数表示endDate在startDate之后）
	 */
	public static long daysBetween(Date startDate, Date endDate) {
		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(start, end);
	}

	/**
	 * 计算两个日期之间的天数差(LocalDateTime版本)
	 * @param startDateTime 开始日期时间
	 * @param endDateTime 结束日期时间
	 * @return 两个日期之间的天数差（正数表示endDateTime在startDateTime之后）
	 */
	public static long daysBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return ChronoUnit.DAYS.between(startDateTime, endDateTime);
	}

	/**
	 * 判断两个日期是否为同一天(Date版本)
	 * @param date1 第一个日期
	 * @param date2 第二个日期
	 * @return true-同一天，false-不同天
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) return false;
		LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate1.isEqual(localDate2);
	}

	/**
	 * 判断两个日期是否为同一天(LocalDateTime版本)
	 * @param dateTime1 第一个日期时间
	 * @param dateTime2 第二个日期时间
	 * @return true-同一天，false-不同天
	 */
	public static boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2) {
		if (dateTime1 == null || dateTime2 == null) return false;
		return dateTime1.toLocalDate().isEqual(dateTime2.toLocalDate());
	}

	/**
	 * 获取本周的第一天（周一）
	 * @return yyyy-MM-dd格式的周一日期
	 */
	public static String getFirstDayOfWeek() {
		return currentDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.format(YMD_FORMATTER);
	}

	/**
	 * 获取本周的最后一天（周日）
	 * @return yyyy-MM-dd格式的周日日期
	 */
	public static String getLastDayOfWeek() {
		return currentDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
				.format(YMD_FORMATTER);
	}

	/**
	 * 获取指定日期所在周的开始和结束日期(Date版本)
	 * @param date 指定日期
	 * @return 包含两个元素的数组：[0]=周一日期，[1]=周日日期
	 */
	public static String[] getWeekRange(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		return new String[]{
				monday.format(YMD_FORMATTER),
				sunday.format(YMD_FORMATTER)
		};
	}

	/**
	 * 获取指定日期所在周的开始和结束日期(LocalDateTime版本)
	 * @param dateTime 指定日期时间
	 * @return 包含两个元素的数组：[0]=周一日期，[1]=周日日期
	 */
	public static String[] getWeekRange(LocalDateTime dateTime) {
		LocalDate localDate = dateTime.toLocalDate();
		LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		return new String[]{
				monday.format(YMD_FORMATTER),
				sunday.format(YMD_FORMATTER)
		};
	}

	/**
	 * 获取指定日期的开始时间（00:00:00）(Date版本)
	 * @param date 指定日期
	 * @return 当天的开始时间
	 */
	public static Date getStartOfDay(Date date) {
		return Date.from(date.toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 获取指定日期的开始时间（00:00:00）(LocalDateTime版本)
	 * @param dateTime 指定日期时间
	 * @return 当天的开始时间
	 */
	public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
		return dateTime.toLocalDate().atStartOfDay();
	}

	/**
	 * 获取指定日期的结束时间（23:59:59.999）(Date版本)
	 * @param date 指定日期
	 * @return 当天的结束时间
	 */
	public static Date getEndOfDay(Date date) {
		return Date.from(date.toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate().atTime(23, 59, 59, 999_000_000)
				.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 获取指定日期的结束时间（23:59:59.999）(LocalDateTime版本)
	 * @param dateTime 指定日期时间
	 * @return 当天的结束时间
	 */
	public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
		return dateTime.toLocalDate().atTime(23, 59, 59, 999_000_000);
	}

	/**
	 * 获取指定月份的第一天(Date版本)
	 * @param year 年份
	 * @param month 月份（1-12）
	 * @return 该月第一天的Date对象
	 */
	public static Date getFirstDayOfMonth(int year, int month) {
		return Date.from(YearMonth.of(year, month).atDay(1)
				.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 获取指定月份的第一天(LocalDateTime版本)
	 * @param year 年份
	 * @param month 月份（1-12）
	 * @return 该月第一天的LocalDateTime对象
	 */
	public static LocalDateTime getFirstDayOfMonthDateTime(int year, int month) {
		return YearMonth.of(year, month).atDay(1).atStartOfDay();
	}

	/**
	 * 获取指定月份的最后一天(Date版本)
	 * @param year 年份
	 * @param month 月份（1-12）
	 * @return 该月最后一天的Date对象
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		return Date.from(YearMonth.of(year, month).atEndOfMonth()
				.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 获取指定月份的最后一天(LocalDateTime版本)
	 * @param year 年份
	 * @param month 月份（1-12）
	 * @return 该月最后一天的LocalDateTime对象
	 */
	public static LocalDateTime getLastDayOfMonthDateTime(int year, int month) {
		return YearMonth.of(year, month).atEndOfMonth().atStartOfDay();
	}

	/**
	 * 计算年龄（精确到年）(Date版本)
	 * @param birthDate 出生日期
	 * @return 当前年龄（整数）
	 */
	public static int calculateAge(Date birthDate) {
		LocalDate birthDay = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return Period.between(birthDay, LocalDate.now()).getYears();
	}

	/**
	 * 计算年龄（精确到年）(LocalDateTime版本)
	 * @param birthDateTime 出生日期时间
	 * @return 当前年龄（整数）
	 */
	public static int calculateAge(LocalDateTime birthDateTime) {
		LocalDate birthDay = birthDateTime.toLocalDate();
		return Period.between(birthDay, LocalDate.now()).getYears();
	}

	/**
	 * 日期格式转换
	 * @param dateStr 原始日期字符串
	 * @param originalFormat 原始格式（如"yyyy/MM/dd"）
	 * @param targetFormat 目标格式（如"yyyy-MM-dd"）
	 * @return 转换格式后的日期字符串
	 */
	public static String convertDateFormat(String dateStr, String originalFormat, String targetFormat) {
		DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern(originalFormat);
		DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern(targetFormat);
		return LocalDate.parse(dateStr, originalFormatter).format(targetFormatter);
	}

	/**
	 * 判断日期是否在指定范围内(Date版本)
	 * @param date 待检查日期
	 * @param startDate 范围开始日期
	 * @param endDate 范围结束日期
	 * @return true-在范围内（包含边界），false-不在范围内
	 */
	public static boolean isDateInRange(Date date, Date startDate, Date endDate) {
		Instant instant = date.toInstant();
		return !instant.isBefore(startDate.toInstant()) && !instant.isAfter(endDate.toInstant());
	}

	/**
	 * 判断日期是否在指定范围内(LocalDateTime版本)
	 * @param dateTime 待检查日期时间
	 * @param startDateTime 范围开始日期时间
	 * @param endDateTime 范围结束日期时间
	 * @return true-在范围内（包含边界），false-不在范围内
	 */
	public static boolean isDateInRange(LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return !dateTime.isBefore(startDateTime) && !dateTime.isAfter(endDateTime);
	}

	/**
	 * 获取当前季度
	 * @return 当前季度（1-4）
	 */
	public static int getCurrentQuarter() {
		int month = getMonth();
		return (month - 1) / 3 + 1;
	}

	/**
	 * 获取季度开始日期(Date版本)
	 * @param year 年份
	 * @param quarter 季度（1-4）
	 * @return 季度第一天的Date对象
	 */
	public static Date getStartOfQuarter(int year, int quarter) {
		int startMonth = (quarter - 1) * 3;
		return getFirstDayOfMonth(year, startMonth + 1);
	}

	/**
	 * 获取季度开始日期(LocalDateTime版本)
	 * @param year 年份
	 * @param quarter 季度（1-4）
	 * @return 季度第一天的LocalDateTime对象
	 */
	public static LocalDateTime getStartOfQuarterDateTime(int year, int quarter) {
		int startMonth = (quarter - 1) * 3;
		return getFirstDayOfMonthDateTime(year, startMonth + 1);
	}

	/**
	 * 获取季度结束日期(Date版本)
	 * @param year 年份
	 * @param quarter 季度（1-4）
	 * @return 季度最后一天的Date对象
	 */
	public static Date getEndOfQuarter(int year, int quarter) {
		int endMonth = quarter * 3;
		return getLastDayOfMonth(year, endMonth);
	}

	/**
	 * 获取季度结束日期(LocalDateTime版本)
	 * @param year 年份
	 * @param quarter 季度（1-4）
	 * @return 季度最后一天的LocalDateTime对象
	 */
	public static LocalDateTime getEndOfQuarterDateTime(int year, int quarter) {
		int endMonth = quarter * 3;
		return getLastDayOfMonthDateTime(year, endMonth);
	}

	/**
	 * 时间戳转日期字符串
	 * @param timestamp 时间戳（毫秒）
	 * @param format 目标格式（如"yyyy-MM-dd HH:mm:ss"）
	 * @return 格式化后的日期字符串
	 */
	public static String timestampToDate(long timestamp, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return Instant.ofEpochMilli(timestamp)
				.atZone(ZoneId.systemDefault())
				.format(formatter);
	}

	/**
	 * 日期字符串转时间戳
	 * @param dateStr 日期字符串
	 * @param format 日期格式
	 * @return 对应的时间戳（毫秒）
	 */
	public static long dateToTimestamp(String dateStr, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(dateStr, formatter)
				.atZone(ZoneId.systemDefault())
				.toInstant()
				.toEpochMilli();
	}

	/**
	 * 获取当前时间戳（秒）
	 * @return 当前时间戳（秒级）
	 */
	public static long getCurrentTimestampSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取当前时间戳（毫秒）
	 * @return 当前时间戳（毫秒级）
	 */
	public static long getCurrentTimestampMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 添加指定数量的工作日（跳过周末）(Date版本)
	 * @param startDate 起始日期
	 * @param workdays 要添加的工作日数量
	 * @return 计算后的日期
	 */
	public static Date addWorkDays(Date startDate, int workdays) {
		if (workdays == 0) return startDate;

		LocalDate date = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int direction = workdays < 0 ? -1 : 1;

		while (workdays != 0) {
			date = date.plusDays(direction);
			// 跳过周末
			if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY ||
					date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				workdays -= direction;
			}
		}

		return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 添加指定数量的工作日（跳过周末）(LocalDateTime版本)
	 * @param startDateTime 起始日期时间
	 * @param workdays 要添加的工作日数量
	 * @return 计算后的日期时间
	 */
	public static LocalDateTime addWorkDays(LocalDateTime startDateTime, int workdays) {
		if (workdays == 0) return startDateTime;

		LocalDate date = startDateTime.toLocalDate();
		int direction = workdays < 0 ? -1 : 1;

		while (workdays != 0) {
			date = date.plusDays(direction);
			// 跳过周末
			if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY ||
					date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				workdays -= direction;
			}
		}

		return date.atTime(startDateTime.toLocalTime());
	}

	/**
	 * 计算两个日期之间的工作日天数（不含周末）(Date版本)
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return 工作日天数
	 */
	public static long calculateWorkDays(Date startDate, Date endDate) {
		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		long days = 0;
		LocalDate temp = start;

		while (!temp.isAfter(end)) {
			if (!(temp.getDayOfWeek() == DayOfWeek.SATURDAY ||
					temp.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				days++;
			}
			temp = temp.plusDays(1);
		}

		return days;
	}

	/**
	 * 计算两个日期之间的工作日天数（不含周末）(LocalDateTime版本)
	 * @param startDateTime 开始日期时间
	 * @param endDateTime 结束日期时间
	 * @return 工作日天数
	 */
	public static long calculateWorkDays(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		LocalDate start = startDateTime.toLocalDate();
		LocalDate end = endDateTime.toLocalDate();

		long days = 0;
		LocalDate temp = start;

		while (!temp.isAfter(end)) {
			if (!(temp.getDayOfWeek() == DayOfWeek.SATURDAY ||
					temp.getDayOfWeek() == DayOfWeek.SUNDAY)) {
				days++;
			}
			temp = temp.plusDays(1);
		}

		return days;
	}

	/**
	 * 计算时间差（人性化格式）(Date版本)
	 * @param endDate 结束时间
	 * @param nowDate 开始时间
	 * @return 时间差字符串，如"3天2小时15分钟30.25秒"
	 */
	public static String getDatePoor(Date endDate, Date nowDate) {
		Duration duration = Duration.between(nowDate.toInstant(), endDate.toInstant());
		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;
		double seconds = duration.getSeconds() % 60 + duration.getNano() / 1_000_000_000.0;

		StringBuilder sb = new StringBuilder();
		if (days != 0) sb.append(days).append("天");
		if (hours != 0) sb.append(hours).append("小时");
		if (minutes != 0) sb.append(minutes).append("分钟");
		if (seconds > 0) sb.append(new DecimalFormat("#.00").format(seconds)).append("秒");

		return sb.toString();
	}

	/**
	 * 计算时间差（人性化格式）(LocalDateTime版本)
	 * @param endDateTime 结束时间
	 * @param nowDateTime 开始时间
	 * @return 时间差字符串，如"3天2小时15分钟30.25秒"
	 */
	public static String getDatePoor(LocalDateTime endDateTime, LocalDateTime nowDateTime) {
		Duration duration = Duration.between(nowDateTime, endDateTime);
		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;
		double seconds = duration.getSeconds() % 60 + duration.getNano() / 1_000_000_000.0;

		StringBuilder sb = new StringBuilder();
		if (days != 0) sb.append(days).append("天");
		if (hours != 0) sb.append(hours).append("小时");
		if (minutes != 0) sb.append(minutes).append("分钟");
		if (seconds > 0) sb.append(new DecimalFormat("#.00").format(seconds)).append("秒");

		return sb.toString();
	}

	// ================= 私有辅助方法 ================= //

	// 安全解析日期字符串
	private static LocalDate parseToLocalDate(String dateStr, DateTimeFormatter formatter) {
		return LocalDate.parse(dateStr, formatter);
	}

	// 格式化星期信息
	private static String formatDayOfWeek(LocalDate date, String[] weekDays) {
		// Java星期值：1=Monday, 7=Sunday
		// 数组索引：0=周日, 1=周一,...6=周六
		int index = date.getDayOfWeek().getValue() % 7;
		return weekDays[index];
	}
}
