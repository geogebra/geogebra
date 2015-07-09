/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.util;

import org.geogebra.common.GeoGebraConstants;

public class Util extends Object {

	/**
	 * Removes < > " * / ? | \ and replaces them with underscore (_) Michael
	 * Borcherds 2007-11-23
	 */
	public static String processFilename(String name) {
		int length = name != null ? name.length() : 0;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = name.charAt(i);
			if (c == '<' || c == '>' || c == '"' || c == ':' || c == '*'
					|| c == '/' || c == '\\' || c == '?' || c == '\u00a3' || // seems
																				// to
																				// turn
																				// into
																				// ?
																				// inside
																				// zips
					c == '|') {
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
	public static int getNextHigestNumberInSortedList(int num, int[] nums) {

		for (int i = 0; i < nums.length; i++) {
			if (num <= nums[i]) {
				return nums[i];
			}
		}

		return nums[nums.length - 1];
	}

	/**
	 * @param fontSize
	 *            desired size
	 * @return valid, supported fontSize
	 */
	public static int getValidFontSize(int fontSize) {
		return getNextHigestNumberInSortedList(fontSize,
				GeoGebraConstants.VALID_FONT_SIZES);
	}
}
