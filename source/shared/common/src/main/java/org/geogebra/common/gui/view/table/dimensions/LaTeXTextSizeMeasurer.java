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

package org.geogebra.common.gui.view.table.dimensions;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

/** Measures text size using ReTeX library */
public class LaTeXTextSizeMeasurer implements TextSizeMeasurer {

	private int fontSize;

	/**
	 * Creates a new LaTeXTextSizeMeasurer
	 * @param fontSize font size
	 */
	public LaTeXTextSizeMeasurer(int fontSize) {
		this.fontSize = fontSize;
	}

	@Override
	public int getWidth(String text) {
		try {
			TeXFormula formula = new TeXFormula(text);  // this may throw (for invalid formulas)
			TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize);
			icon.setInsets(new Insets(1, 1, 1, 1));
			return icon.getIconWidth();
		} catch (Exception ex) {
			// don't propagate exceptions from formula parsing (e.g., APPS-4584)
		}
		return 0;
	}
}
