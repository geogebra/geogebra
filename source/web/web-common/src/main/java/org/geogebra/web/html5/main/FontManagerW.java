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

package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.FontManager;
import org.geogebra.web.awt.GFontW;

/**
 * This class takes care of storing and creating fonts.
 * 
 * @author Zbynek (based on Desktop FontManager)
 *
 */
public class FontManagerW extends FontManager {

	@Override
	public void setFontSize(int size) {
		// fontSize = size;
	}

	@Override
	public GFont getFontCanDisplay(String testString, boolean serif,
			int fontStyle, int fontSize1) {
		GFontW ret = new GFontW(
				serif ? GFontW.GEOGEBRA_FONT_SERIF
						: GFontW.GEOGEBRA_FONT_SANSERIF,
				fontStyle,
				fontSize1);
		return ret;
	}

}
