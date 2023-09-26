package org.geogebra.desktop.util;

import org.geogebra.common.util.LaTeXCache;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.cache.JLaTeXMathCache;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class GeoLaTeXCache implements LaTeXCache {
	// used by Captions, GeoText and DrawParametricCurve to cache LaTeX formulae
	public Object keyLaTeX = null;

	@Override
	public Object getCachedLaTeXKey(String latex, int fontSize, int style,
			Object fgColor) {
		Object newKey;
		try {

			newKey = JLaTeXMathCache.getCachedTeXFormula(latex,
					TeXConstants.STYLE_DISPLAY, style, fontSize,
					1 /*
						 * inset around the label
						 */, (Color) fgColor);
		} catch (ParseException e) {
			if (keyLaTeX != null) {
				// remove old key from cache
				try {
					JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
			throw e;
		}
		if (keyLaTeX != null && !keyLaTeX.equals(newKey)) {
			// key has changed, remove old key from cache
			try {
				JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

		keyLaTeX = newKey;
		return keyLaTeX;

	}

	@Override
	public void remove() {
		if (keyLaTeX != null) {
			try {
				JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

	}

}
