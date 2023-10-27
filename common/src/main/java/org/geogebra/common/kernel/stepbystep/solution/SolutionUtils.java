package org.geogebra.common.kernel.stepbystep.solution;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class SolutionUtils {

	public static String getColorHex(int color) {
		switch (color % 5) {
			case 1:
				return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_RED);
			case 2:
				return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE);
			case 3:
				return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN);
			case 4:
				return "#" + StringUtil.toHexString(GeoGebraColorConstants.PURPLE_600);
			case 0:
				return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_ORANGE);
			default:
				return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLACK);
		}
	}

	static List<TextElement> getDefaultText(SolutionStepType sst, Localization loc,
			HasLaTeX[] parameters) {
		String translated = loc.getMenuDefault(sst.getKey(), sst.getDefault());

		if (parameters == null) {
			return substitute(translated, null, null);
		}

		translated = replaceSpecial(sst, translated, parameters.length);

		String[] serializedDefault = new String[parameters.length];
		String[] serializedPlain = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			serializedDefault[i] = parameters[i].toLaTeXString(loc, false);
			serializedPlain[i] = parameters[i].toString();
		}

		return substitute(translated, serializedDefault, serializedPlain);
	}

	static List<TextElement> getDetailedText(SolutionStepType sst, Localization loc,
			HasLaTeX[] parameters, List<Integer> colors) {
		String translated = loc.getMenuDefault(sst.getKey(), sst.getDetailed());

		List<TextElement> result;
		if (parameters == null) {
			result = substitute(translated, null, null);
		} else {
			translated = replaceSpecial(sst, translated, parameters.length);

			String[] serializedColored = new String[parameters.length];
			String[] serializedPlain = new String[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				serializedColored[i] = parameters[i].toLaTeXString(loc, true);
				serializedPlain[i] = parameters[i].toString();
			}

			result = substitute(translated, serializedColored, serializedPlain);
		}

		if (colors != null) {
			for (Integer color : colors) {
				if (color != 0) {
					String colorText = "\\fgcolor{" + getColorHex(color)
							+ "}{\\,\\bullet}";
					result.add(new TextElement(colorText, colorText));
				}
			}
		}

		return result;
	}

	private static String replaceSpecial(SolutionStepType sst, String translated, int n) {
		switch (sst) {
		case LIST:
		case SOLUTIONS:
			return translated.replace("%0", getList(0, n));
		case FACTOR_GCD:
			return translated.replace("%2", "%" + (n - 1))
					.replace("%1", "%" + (n - 2))
					.replace("%0", getList(0, n - 2));
		default:
			return translated;
		}
	}

	private static String getList(int from, int to) {
		StringBuilder sb = new StringBuilder();

		for (int i = from; i < to; i++) {
			if (i != from) {
				sb.append(", ");
			}
			sb.append("%");
			sb.append(i);
		}

		return sb.toString();
	}

	private static List<TextElement> substitute(String str, String[] latex, String[] plainText) {
		List<TextElement> result = new ArrayList<>();

		StringBuilder plainBuilder = new StringBuilder();
		StringBuilder latexBuilder = new StringBuilder();
		boolean mathMode = false;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			if (ch == '$') {
				if (mathMode) {
					result.add(new TextElement(latexBuilder.toString(), plainBuilder.toString()));
					latexBuilder.setLength(0);
					plainBuilder.setLength(0);
				} else {
					if (plainBuilder.length() != 0) {
						result.add(new TextElement(plainBuilder.toString()));
						plainBuilder.setLength(0);
						latexBuilder.setLength(0);
					}
				}

				mathMode = !mathMode;
			} else if (ch == '%') {
				i++;
				int pos = str.charAt(i) - '0';

				if (mathMode) {
					if (pos >= 0 && pos < latex.length) {
						latexBuilder.append(latex[pos]);
						plainBuilder.append(plainText[pos]);
					}
				} else {
					if (plainBuilder.length() != 0) {
						result.add(new TextElement(plainBuilder.toString()));
						plainBuilder.setLength(0);
					}

					if (pos >= 0 && pos < latex.length) {
						result.add(new TextElement(latex[pos], plainText[pos]));
					}
				}
			} else {
				plainBuilder.append(ch);
				if (mathMode) {
					latexBuilder.append(ch);
				}
			}
		}

		if (!"".equals(plainBuilder.toString())) {
			result.add(new TextElement(plainBuilder.toString()));
		}

		return result;
	}

}
