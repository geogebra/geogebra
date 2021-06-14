package org.geogebra.keyboard.web.factory.model.inputbox.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Cursive {

	public static final int MAX_CAPTION_LENGHT = 3;
	private static final Map<String, String> map;

	static {
		String[][] values = {
				{"A", "\uD835\uDC34"},
				{"B", "\uD835\uDC35"},
				{"C", "\uD835\uDC36"},
				{"D", "\uD835\uDC37"},
				{"E", "\uD835\uDC38"},
				{"F", "\uD835\uDC39"},
				{"G", "\uD835\uDC3A"},
				{"H", "\uD835\uDC3B"},
				{"I", "\uD835\uDC3C"},
				{"J", "\uD835\uDC3D"},
				{"K", "\uD835\uDC3E"},
				{"L", "\uD835\uDC3F"},
				{"M", "\uD835\uDC40"},
				{"N", "\uD835\uDC41"},
				{"O", "\uD835\uDC42"},
				{"P", "\uD835\uDC43"},
				{"Q", "\uD835\uDC44"},
				{"R", "\uD835\uDC45"},
				{"S", "\uD835\uDC46"},
				{"T", "\uD835\uDC47"},
				{"U", "\uD835\uDC48"},
				{"V", "\uD835\uDC49"},
				{"W", "\uD835\uDC4A"},
				{"X", "\uD835\uDC4B"},
				{"Y", "\uD835\uDC4C"},
				{"Z", "\uD835\uDC4D"},
				{"a", "\uD835\uDC4E"},
				{"b", "\uD835\uDC4F"},
				{"c", "\uD835\uDC50"},
				{"d", "\uD835\uDC51"},
				{"e", "\uD835\uDC52"},
				{"f", "\uD835\uDC53"},
				{"g", "\uD835\uDC54"},
				{"h", "\uD835\uDE29"},
				{"i", "\uD835\uDC56"},
				{"j", "\uD835\uDC57"},
				{"k", "\uD835\uDC58"},
				{"l", "\uD835\uDC59"},
				{"m", "\uD835\uDC5A"},
				{"n", "\uD835\uDC5B"},
				{"o", "\uD835\uDC5C"},
				{"p", "\uD835\uDC5D"},
				{"q", "\uD835\uDC5E"},
				{"r", "\uD835\uDC5F"},
				{"s", "\uD835\uDC60"},
				{"t", "\uD835\uDC61"},
				{"u", "\uD835\uDC62"},
				{"v", "\uD835\uDC63"},
				{"w", "\uD835\uDC64"},
				{"x", "\uD835\uDC65"},
				{"y", "\uD835\uDC66"},
				{"z", "\uD835\uDC67"}

		};
		map = Stream.of(values).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	}

	/**
	 * @param text
	 *            to make cursive.
	 * @return the cursive version of letter if it exists, null otherwise.
	 */
	public static String getCursiveCaption(String text) {
		return hasIndex(text)
				? getCursiveBoldTextWithIndex(text)
				: getCursiveBoldText(text);
	}

	private static boolean hasIndex(String stripped) {
		return stripped.indexOf('_') != -1;
	}

	static String getCursiveBoldTextWithIndex(String stripped) {
		String[] list = stripped.split("_");
		String varName = list[0];
		String subscript = list[1];
		if (varName.length() > 2) {
			return getCursiveBoldText(varName);
		} else if (subscript != null) {
			int remaining = 2 * (MAX_CAPTION_LENGHT - varName.length());
			if (remaining >= subscript.length()) {
				return toCursiveBold(varName) + "_" + toCursiveBold(subscript);
			} else {
				return toCursiveBold(varName) + "_" + toCursiveBold(
						subscript.substring(0, remaining));
			}
		}
		return varName;
	}

	private static String getCursiveBoldText(String input) {
		return toCursiveBold(truncate(input));
	}

	private static String truncate(String name) {
		return name.length() > Cursive.MAX_CAPTION_LENGHT
				? name.substring(0,	Cursive.MAX_CAPTION_LENGHT)
				: name;
	}

	private static String toCursiveBold(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			String character = String.valueOf(text.charAt(i));
			sb.append(map.getOrDefault(character, character));
		}
		return sb.toString();
	}

	private Cursive() {
		// hide constructor
	}
}