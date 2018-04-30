package cn.app.yimirong.utils;

public class NumberUtils {

	/**
	 * 获取10位
	 *
	 * @param num
	 * @return
	 */
	public static int getShi(int num) {
		int shi = num / 10;
		return shi;
	}

	/**
	 * 获取个位
	 *
	 * @param num
	 * @return
	 */
	public static int getGe(int num) {
		int ge = num % 10;
		return ge;
	}

}
