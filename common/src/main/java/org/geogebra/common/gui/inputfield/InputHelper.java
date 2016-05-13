package org.geogebra.common.gui.inputfield;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.Korean;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

public class InputHelper {
	public static boolean needsAutocomplete(StringBuilder curWord,
			Kernel kernel) {
		if ("ko".equals(kernel.getLocalization().getLanguage())) {
			if (Korean.flattenKorean(curWord.toString()).length() < 2) {
				return false;
			}
		} else if (curWord.length() < 3) {
			return false;
		}
		return kernel.lookupLabel(curWord.toString()) == null;
	}

	public static void centerText(GeoElement[] geos,
			EuclidianViewInterfaceCommon ev) {
		// create texts in the middle of the visible view
		// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
		if (geos != null && geos.length > 0 && geos[0] != null
				&& geos[0].isGeoText()) {
			GeoText text = (GeoText) geos[0];
			if (!text.isTextCommand() && text.getStartPoint() == null) {

				Construction cons = text.getConstruction();

				boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint p = new GeoPoint(text.getConstruction(), null,
						(ev.getXmin() + ev.getXmax()) / 2,
						(ev.getYmin() + ev.getYmax()) / 2, 1.0);
				cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

				try {
					text.setStartPoint(p);
					text.update();
				} catch (CircularDefinitionException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	private static boolean isOpenBracket(char c, boolean onlySquare) {
		return c == '[' || (!onlySquare && c == '{')
				|| (!onlySquare && c == '(');
	}

	private static boolean isCloseBracket(char c, boolean onlySquare) {
		return c == ']' || (!onlySquare && c == '}')
				|| (!onlySquare && c == ')');
	}
	public static int updateCurrentWord(boolean searchRight,
			StringBuilder curWord,
			String text, int caretPos, boolean onlySquareBrackets) {
		int curWordStart;
		if (text == null)
			return -1;

		if (searchRight) {
			// search to right first to see if we are inside [ ]
			boolean insideBrackets = false;
			curWordStart = caretPos;

			while (curWordStart < text.length()) {
				char c = text.charAt(curWordStart);
				if (isOpenBracket(c, onlySquareBrackets))
					break;
				if (isCloseBracket(c, onlySquareBrackets))
					insideBrackets = true;
				curWordStart++;
			}

			// found [, so go back until we get a ]
			if (insideBrackets) {
				while (caretPos > 0 && !isOpenBracket(text.charAt(caretPos),
						onlySquareBrackets))
					caretPos--;
			}
		}

		// search to the left
		curWordStart = caretPos - 1;
		while (curWordStart >= 0 &&
				(curWordStart >= text.length()
				|| StringUtil.isLetterOrDigitOrUnderscore(
								text.charAt(curWordStart)))) {
			--curWordStart;
		}
		curWordStart++;
		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while (curWordEnd < length && StringUtil
				.isLetterOrDigitOrUnderscore(text.charAt(curWordEnd)))
			++curWordEnd;

		curWord.setLength(0);
		if (curWordEnd <= length) {
			curWord.append(text.substring(curWordStart, curWordEnd));
		} else {
			Log.debug("CARET OUTSIDE");
		}

		// remove '[' at end
		if (curWord.length() > 0 && isOpenBracket(
				curWord.charAt(curWord.length() - 1), onlySquareBrackets)) {
			curWord.setLength(curWord.length() - 1);
		}
		Log.debug("CURRENT WORD:" + curWord);
		return curWordStart;
	}
}
