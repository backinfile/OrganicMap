package com.backinfile.support;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.backinfile.map.model.Point;

public class Utils {
	public static final String UTF8 = "utf-8";
	private static final Random RANDOM = new Random();
	public static final String LETTER_AND_NUMBER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	public static final String NUMBER = "1234567890";

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}

	public static <T> ArrayList<T> subList(List<T> list, int fromIndex, int toIndex) {
		return new ArrayList<T>(list.subList(fromIndex, toIndex));
	}

	public static int indexOf(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (value == array[i]) {
				return i;
			}
		}
		return -1;
	}

	public static int[] str2IntArray(String str) {
		String[] strs = str.split(",");
		int[] result = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}

	public static void setRndSeed(long seed) {
		RANDOM.setSeed(seed);
	}

	public static double nextDouble() {
		return RANDOM.nextDouble();
	}

	public static int nextInt(int a, int b) {
		return RANDOM.nextInt(b - a) + a;
	}

	public static double nextDouble(double a, double b) {
		return RANDOM.nextDouble() * (b - a) + a;
	}

	public static String getBlankString(int n) {
		var sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	public static String getRandomToken() {
		var rnd = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			int index = rnd.nextInt(LETTER_AND_NUMBER.length());
			sb.append(LETTER_AND_NUMBER.charAt(index));
		}
		return sb.toString();
	}

	public static String getRandomNumber(int n) {
		var rnd = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			int index = rnd.nextInt(NUMBER.length());
			sb.append(NUMBER.charAt(index));
		}
		return sb.toString();
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	public static String getStackTraceAsString(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter, false));
		stringWriter.flush();
		return stringWriter.getBuffer().toString();
	}

	public static void int2bytes(int num, byte[] bytes, int offset) {
		for (int i = 0; i < 4; i++) {
			bytes[offset + i] = (byte) (num >>> (24 - i * 8));
		}
	}

	public static int bytes2Int(byte[] bytes, int offset) {
		int num = 0;
		for (int i = offset; i < offset + 4; i++) {
			num <<= 8;
			num |= (bytes[i] & 0xFF);
		}
		return num;
	}

	public static int getHashCode(String str) {
		int h = 0;
		for (int i = 0; i < str.length(); i++) {
			h = 31 * h + str.charAt(i);
		}
		return h;
	}

	// S=(1/2)*(x1y2+x2y3+x3y1-x1y3-x2y1-x3y2)
	// ?????????????????????0??????????????????0?????????
	public static float calcArea(Point p1, Point p2, Point p3) {
		return (p1.getX() * p2.getY() + p2.getX() * p3.getY() + p3.getX() * p1.getY() - p1.getX() * p3.getY()
				- p2.getX() * p1.getY() - p3.getX() * p2.getY()) / 2f;
	}

	public static <T> List<T> asList(int rightShift, T... values) {
		LinkedList<T> array = new LinkedList<T>();
		array.addAll(Arrays.asList(values));
		for (int i = 0; i < rightShift; i++) {
			array.addFirst(array.pollLast());
		}
		return array;
	}

	public static <T> void sort(Collection<T> collection, Function1<Integer, T> keyMap) {
		List<SortPair<T>> pairs = new ArrayList<>();
		for (var value : collection) {
			pairs.add(new SortPair<T>(value, keyMap.invoke(value)));
		}
		Collections.sort(pairs, (p1, p2) -> Integer.compare(p1.key, p2.key));
		collection.clear();
		for (var p : pairs) {
			collection.add(p.value);
		}
	}

	private static class SortPair<T> {
		public T value;
		public Integer key;

		public SortPair(T value, Integer key) {
			this.value = value;
			this.key = key;
		}
	}

	public static void main(String[] args) {
		List<String> strings = new ArrayList<String>();
		strings.add("123");
		strings.add("12345");
		strings.add("123456");
		strings.add("1234");
		strings.add("12345678");
		strings.add("12");
		sort(strings, str -> -str.length());
		System.out.println(strings.toString());
	}
}
