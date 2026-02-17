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

package org.geogebra.desktop.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.util.LaTeXCache;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.cache.JLaTeXMathCache;
import com.himamis.retex.renderer.share.exception.ParseException;

public class GeoLaTeXCache implements LaTeXCache {
	// used by Captions, GeoText and DrawParametricCurve to cache LaTeX formulae
	public Object keyLaTeX = null;

	@Override
	public Object getCachedLaTeXKey(String latex, int fontSize, int style,
			GColor fgColor) {
		Object newKey;
		try {

			newKey = JLaTeXMathCache.getCachedTeXFormula(latex,
					TeXConstants.STYLE_DISPLAY, style, fontSize,
					1 /* inset around the label */, fgColor);
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
