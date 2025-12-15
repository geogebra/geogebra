/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

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

		/**
		 * Replace wrapped value
		 * @param value value
		 */
		public void set(T value) {
			this.value = value;
		}

		/**
		 * @return wrapped value
		 */
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

	/**
	 * @param i index
	 * @return font size for the UI
	 */
	public static int menuFontSizes(int i) {
		return MENU_FONT_SIZES[i];
	}

	/**
	 * @return number of available font sizes for the UI.
	 */
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

	/**
	 * Executes the given {@link Callable} and returns its result,
	 * or {@code null} if an exception occurs.
	 * @param callable the callable to execute
	 * @return the result of {@code callable.call()},
	 * or {@code null} if the callable throws any exception
	 * @param <T> the type of the result produced by the callable
	 */
	public static <T> T tryOrNull(@Nonnull Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception exception) {
			return null;
		}
	}
}
