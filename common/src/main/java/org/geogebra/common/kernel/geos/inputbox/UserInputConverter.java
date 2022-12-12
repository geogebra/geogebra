package org.geogebra.common.kernel.geos.inputbox;

public class UserInputConverter {

	public String pointToUndefined(String text) {
		return "(" + replaceCommas(text.substring(1, text.length() - 1)) + ")";
	}

	private String replaceCommas(String text) {
		String result = text;

		if (text.startsWith(",")) {
			result = text.replaceFirst(",", "?,");
		}

		result = result.replace(",,", ",?,?");

		if (result.endsWith(",")) {
			result = result.replace(",", ",?");
		}

		return result;
	}

	public String matrixToUndefined(String text) {
		return text.replace("{{,},", "{{?,?},")
				.replace(",{,}}", ",{?,?}}")
				.replace(",{,},", ",{?,?},");
	}
}
