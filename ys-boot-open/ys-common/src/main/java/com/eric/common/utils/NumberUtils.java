package com.eric.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


/**
 * 数字计算工具类
 * @ClassName:   NumberUtils
 * @author:  liuQ
 * @date:    2025-06-16 14:47:59
 * @Copyright ERIC 微信公众号：Eric的技术杂货库
 */
public class NumberUtils {

	/*
	 * ********************************************************  数据加法  ******************************************************************
	 */
	/**
	 * 两个任意类型数相加，精确decimal位（a+b），返回Double类型
	 * @ClassName: addToDouble
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Double
	 */
	public static <T> Double addToDouble(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.add(f2);
		if(decimal == null) {
			return f.doubleValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.doubleValue();
	}


	/**
	 * 两个任意类型数相加，精确decimal位（a+b），返回Float类型
	 * @ClassName: addToFloat
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Float
	 */
	public static <T> Float addToFloat(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.add(f2);
		if(decimal == null) {
			return f.floatValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.floatValue();
	}

	/**
	 * 两个任意类型数相加，精确decimal位（a+b），返回BigDecimal类型
	 * @ClassName: addToBigDecimal
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return BigDecimal
	 */
	public static <T> BigDecimal addToBigDecimal(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.add(f2);
		return scale(f, decimal);
	}

	/**
	 * 两个任意类型数相加（a+b），返回Integer类型
	 * @ClassName: addToInteger
	 * @param a
	 * @param b
	 * @return Integer
	 */
	public static <T> Integer addToInteger(T a, T b) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.add(f2);
		return f.intValue();
	}
	/**
	 * 多个任意类型数相加（a + b + ...），返回Integer类型
	 * @ClassName: addToInteger
	 * @param a
	 * @param b
	 * @return Integer
	 */
	@SafeVarargs
    public static <T> Integer addToIntegerList(T... a) {
		BigDecimal sum = BigDecimal.ZERO;
		for (T t : a) {
			BigDecimal f1 = new BigDecimal(String.valueOf(t));
			sum = sum.add(f1);
		}
		return sum.intValue();
	}

	/**
	 * 多个任意类型数相加，精确decimal位（a + b + ...），返回Double类型
	 * @ClassName: addToDouble
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Double
	 */
	@SafeVarargs
	public static <T> Double addToDoubleList(Integer decimal, T... a) {
		BigDecimal sum = BigDecimal.ZERO;
		for (T t : a) {
			BigDecimal f1 = new BigDecimal(String.valueOf(t));
			sum = sum.add(f1);
		}
		if(decimal == null) {
			return sum.doubleValue();
		}
		BigDecimal scale = sum.setScale(decimal, RoundingMode.HALF_UP);
		return scale.doubleValue();
	}

	/**
	 * 多个任意类型数相加，精确decimal位（a + b + ...），返回BigDecimal类型
	 * @ClassName: addToBigDecimal
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return BigDecimal
	 */
	@SafeVarargs
    public static <T> BigDecimal addToBigDecimalList(Integer decimal, T... a) {
		BigDecimal sum = BigDecimal.ZERO;
		for (T t : a) {
			BigDecimal f1 = new BigDecimal(String.valueOf(t));
			sum = sum.add(f1);
		}
		return scale(sum, decimal);
	}

	/**
	 * 多个任意类型数相加，精确decimal位（a+b+...），返回Float类型
	 * @ClassName: addToFloat
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Float
	 */
	@SafeVarargs
    public static <T> Float addToFloatList(Integer decimal, T... a) {
		BigDecimal sum = BigDecimal.ZERO;
		for (T t : a) {
			BigDecimal f1 = new BigDecimal(String.valueOf(t));
			sum = sum.add(f1);
		}
		if(decimal == null) {
			return sum.floatValue();
		}
		BigDecimal scale = sum.setScale(decimal, RoundingMode.HALF_UP);
		return scale.floatValue();
	}

	/*
	 * ********************************************************  数据减法  ******************************************************************
	 */
	/**
	 * 两个任意类型数相减，精确decimal位（a-b），返回Double类型
	 * @ClassName: subtractToDouble
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Double
	 */
	public static <T> Double subtractToDouble(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.subtract(f2);
		if(decimal == null) {
			return f.doubleValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.doubleValue();
	}

	/**
	 * 两个任意类型数相减，精确decimal位（a-b），返回Float类型
	 * @ClassName: subtractToFloat
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Float
	 */
	public static <T> Float subtractToFloat(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.subtract(f2);
		if(decimal == null) {
			return f.floatValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.floatValue();
	}

	/**
	 * 两个任意类型数相减，精确decimal位（a-b），返回BigDecimal类型
	 * @ClassName: subtractToBigDecimal
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return BigDecimal
	 */
	public static <T> BigDecimal subtractToBigDecimal(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.subtract(f2);
		return scale(f, decimal);
	}

	/**
	 * 两个任意类型数相减（a-b），返回Integer类型
	 * @ClassName: subtractToInteger
	 * @param a
	 * @param b
	 * @return Integer
	 */
	public static <T> Integer subtractToInteger(T a, T b) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.subtract(f2);
		return f.intValue();
	}


	/*
	 * ********************************************************  数据乘法  ******************************************************************
	 */
	/**
	 * 两个任意类型数相乘，精确decimal位（a*b），返回Double类型
	 * @ClassName: multiplyToDouble
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Double
	 */
	public static <T> Double multiplyToDouble(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.multiply(f2);
		if(decimal == null) {
			return f.doubleValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.doubleValue();
	}

	/**
	 * 两个任意类型数相乘，精确decimal位（a*b），返回Float类型
	 * @ClassName: multiplyToFloat
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Float
	 */
	public static <T> Float multiplyToFloat(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.multiply(f2);
		if(decimal == null) {
			return f.floatValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.floatValue();
	}

	/**
	 * 两个任意类型数相乘，精确decimal位（a*b），返回BigDecimal类型
	 * @ClassName: multiplyToBigDecimal
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return BigDecimal
	 */
	public static <T> BigDecimal multiplyToBigDecimal(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.multiply(f2);
		return scale(f, decimal);
	}

	/**
	 * 两个任意类型数相乘（a*b），返回Integer类型
	 * @ClassName: multiplyToInteger
	 * @param a
	 * @param b
	 * @return Integer
	 */
	public static <T> Integer multiplyToInteger(T a, T b) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.multiply(f2);
		return f.intValue();
	}

	/*
	 * ********************************************************  数据除法  ******************************************************************
	 */
	/**
	 * 两个任意类型数相除，精确decimal位（a/b），返回Double类型
	 * @ClassName: divideToDouble
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Double
	 */
	public static <T> Double divideToDouble(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.divide(f2, 4, RoundingMode.HALF_UP);
		if(decimal == null) {
			return f.doubleValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.doubleValue();
	}

	/**
	 * 两个任意类型数相除，精确decimal位（a/b），返回Float类型
	 * @ClassName: divideToFloat
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Float
	 */
	public static <T> Float divideToFloat(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.divide(f2);
		if(decimal == null) {
			return f.floatValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.floatValue();
	}

	/**
	 * 两个任意类型数相除，精确decimal位（a/b），返回BigDecimal类型
	 * @ClassName: divideToBigDecimal
	 * @param a
	 * @param b
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return BigDecimal
	 */
	public static <T> BigDecimal divideToBigDecimal(T a, T b, Integer decimal) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.divide(f2);
		return scale(f, decimal);
	}

	/**
	 * 两个任意类型数相除（a/b），返回Integer类型
	 * @ClassName: divideToInteger
	 * @param a
	 * @param b
	 * @return Integer
	 */
	public static <T> Integer divideToInteger(T a, T b) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal f = f1.divide(f2);
		return f.intValue();
	}

	/*
	 * ********************************************************  四舍五入保留指定的小数点位数  ******************************************************************
	 */
	/**
	 * 四舍五入保留指定的小数点位数 BigDecimal
	 * @ClassName: scale
	 * @param a
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return BigDecimal
	 */
	public static BigDecimal scale(BigDecimal a, Integer decimal) {
		if(decimal == null) {
			return a;
		}
		return a.setScale(decimal, RoundingMode.HALF_UP);
	}

	/**
	 * 四舍五入保留指定的小数点位数 Float
	 * @ClassName: scale
	 * @param a
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Float
	 */
	public static Float scale(Float a, Integer decimal) {
		BigDecimal f = BigDecimal.valueOf(a);
		if(decimal == null) {
			return f.floatValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.floatValue();
	}

	/**
	 * 四舍五入保留指定的小数点位数 Double
	 * @ClassName: scale
	 * @param a
	 * @param decimal 精确位数 为null时不做精确度处理
	 * @return Double
	 */
	public static Double scale(Double a, Integer decimal) {
		BigDecimal f = BigDecimal.valueOf(a);
		if(decimal == null) {
			return f.doubleValue();
		}
		BigDecimal scale = f.setScale(decimal, RoundingMode.HALF_UP);
		return scale.doubleValue();
	}

	/*
	 * ********************************************************  各数据类型比较大小  ******************************************************************
	 */
	/**
	 * <p> 各数据类型比较大小，a=b返回0，a>b返回1，a<b返回-1 </p>
	 * @ClassName: compare
	 * @param <T>
	 * @param a
	 * @param b
	 * @return 0、1、-1
	 */
	public static <T> Integer compare(T a, T b) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		return f1.compareTo(f2);
	}

	/**
	 * <p>  判断两值是否相等true相等，否则false </p>
	 * @ClassName: isEqual
	 * @param <T>
	 * @param a
	 * @param b
	 * @return true、false
	 */
	public static <T> boolean isEqual(T a, T b) {
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		return f1.compareTo(f2) == 0;
	}



	/*
	 * ********************************************************  计算百分比  ******************************************************************
	 */
	/**
	 * <p> 计算a/b的百分比，精确decimal位 ，任意数据类型</p>
	 * @ClassName: percent
	 * @param <T>
	 * @param a
	 * @param b
	 * @param decimal 精确decimal位
	 * @return
	 */
	public static <T> String percent(T a, T b, Integer decimal) {
		String percent = "";
		BigDecimal sub = new BigDecimal("100.0");
		BigDecimal f1 = new BigDecimal(String.valueOf(a));
		BigDecimal f2 = new BigDecimal(String.valueOf(b));
		BigDecimal x = f1.multiply(sub);
		BigDecimal y = f2.multiply(sub);
		double x_ = x.doubleValue();
		double y_ = y.doubleValue();
		double z = x_/y_;
		String patter = "##0.";
		if(decimal == null) {
			patter = "##0.00%";
		}else if(decimal == 0) {
			patter = "##0%";
		}else {
			for(int i = 0; i< decimal; i++) {
				patter += "0";
			}
			patter += "%";
		}
		DecimalFormat df = new DecimalFormat(patter);
		if(Math.abs(z) < 0.000000000001) {
			percent = "0.00%";
		}else {
			percent = df.format(z);
		}

		return percent;
	}
}
