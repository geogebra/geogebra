package org.geogebra.keyboard.web.factory.model.inputbox.util;

import java.util.HashMap;
import java.util.Map;

public class CursiveBoldLetter {

	private static Map<String, String> cursiveBold;

	private static Map<String, String> getCursiveBoldLetters() {
		if (cursiveBold == null) {
			cursiveBold = new HashMap<>();
			cursiveBold.put("a", "\uD835\uDC82");
			cursiveBold.put("b", "\uD835\uDC83");
			cursiveBold.put("c", "\uD835\uDC84");
			cursiveBold.put("d", "\uD835\uDC85");
			cursiveBold.put("e", "\uD835\uDC86");
			cursiveBold.put("f", "\uD835\uDC87");
			cursiveBold.put("g", "\uD835\uDC88");
			cursiveBold.put("h", "\uD835\uDC89");
			cursiveBold.put("i", "\uD835\uDC8A");
			cursiveBold.put("j", "\uD835\uDC8B");
			cursiveBold.put("k", "\uD835\uDC8C");
			cursiveBold.put("l", "\uD835\uDC8D");
			cursiveBold.put("m", "\uD835\uDC8E");
			cursiveBold.put("n", "\uD835\uDC8F");
			cursiveBold.put("o", "\uD835\uDC90");
			cursiveBold.put("p", "\uD835\uDC91");
			cursiveBold.put("q", "\uD835\uDC92");
			cursiveBold.put("r", "\uD835\uDC93");
			cursiveBold.put("s", "\uD835\uDC94");
			cursiveBold.put("t", "\uD835\uDC95");
			cursiveBold.put("u", "\uD835\uDC96");
			cursiveBold.put("v", "\uD835\uDC97");
			cursiveBold.put("w", "\uD835\uDC98");
			// x,y,z should be only italic, not bold
			cursiveBold.put("x", "\uD835\uDC65");
			cursiveBold.put("y", "\uD835\uDC66");
			cursiveBold.put("z", "\uD835\uDC67");
		}

		return cursiveBold;
	}

	/**
	 * @param letter
	 *            letter
	 * @return the cursive and bold version of letter if it exists, null otherwise.
	 */
	public static String getCursiveBoldLetter(String letter) {
		return getCursiveBoldLetters().get(letter);
	}
}
