/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.util;

import org.geogebra.common.util.debug.Log;

public final class Util {

	/** available font sizes (will be reused in OptionsAdvanced) */
	final private static int[] MENU_FONT_SIZES = { 12, 14, 16, 18, 20, 24, 28,
			32, 48 };

	/**
	 * used when value is needed through a callback
	 */
	static final public class Wrap<T> {
		private T value;

		public Wrap(T value) {
			this.value = value;
		}

		public void set(T value) {
			this.value = value;
		}

		public T get() {
			return value;
		}
	}

	/**
	 * Removes &lt; &gt; " * / ? | \ and replaces them with underscore (_)
	 * 
	 * @author Michael Borcherds
	 * @param name
	 *            suggested filename
	 * @return valid filename
	 */
	public static String processFilename(String name) {
		int length = name != null ? name.length() : 0;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = name.charAt(i);
			// u00a3 seems to turn into ? inside zips
			if (c == '<' || c == '>' || c == '"' || c == ':' || c == '*'
					|| c == '/' || c == '\\' || c == '?' || c == '\u00a3'
					|| c == '|') {
				sb.append("_");
			} else {
				sb.append(c);
			}
		}

		if (sb.length() == 0) {
			sb.append("geogebra");
		}

		return sb.toString();
	}

	/**
	 * 
	 * Optimised for short code - checks every number in List. Use
	 * ConcurrentSkipListMap for longer lists
	 * 
	 * @param num
	 *            number to check against list
	 * @param nums
	 *            list of numbers
	 * @return next highest number in the list (fallback: return last number in
	 *         the list)
	 */
	public static int getNextHighestNumberInSortedList(int num, int[] nums) {

		for (int i = 0; i < nums.length; i++) {
			if (num <= nums[i]) {
				return nums[i];
			}
		}

		return nums[nums.length - 1];
	}

	public static int menuFontSizes(int i) {
		return MENU_FONT_SIZES[i];
	}

	public static int menuFontSizesLength() {
		return MENU_FONT_SIZES.length;
	}

	/**
	 * @param fontSize
	 *            desired size
	 * @return valid, supported fontSize
	 */
	public static int getValidFontSize(int fontSize) {
		return getNextHighestNumberInSortedList(fontSize, MENU_FONT_SIZES);
	}

	/**
	 * @param fn
	 *            filename to check
	 * @return filename with extension changed eg ".gif" -&gt; ".png"
	 */
	public static String checkImageExtension(String fn) {

		String ret;

		FileExtensions ext = StringUtil.getFileExtension(fn);

		if (!ext.isAllowedImage() && !"".equals(fn)) {

			// all bitmaps (except JPG) saved as PNG
			// eg .TIFF/.TIF/.BMP
			ret = StringUtil.changeFileExtension(fn, FileExtensions.PNG);
			Log.debug(
					"changing image extension " + ext + " -> " + ret);
		} else {

			ret = fn;
		}

		return ret;
	}
}
