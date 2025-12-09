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

package org.geogebra.web.full.gui.toolbar.mow.popupcomponents;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.GeoGebraColorConstants;

public final class PenColorValues  {

	/**
	 * @return colors for pen tool
	 */
	public static List<GColor> values() {
		return Arrays.asList(
				GeoGebraColorConstants.GEOGEBRA_OBJECT_BLACK,
				GColor.newColorRGB(0x975FA8),
				GColor.DEFAULT_PURPLE,
				GColor.newColorRGB(0x1565C0),
				GColor.newColorRGB(0x388C83),
				GColor.WHITE,
				GColor.newColorRGB(0xFFCC02),
				GColor.newColorRGB(0xE07415),
				GColor.newColorRGB(0xD32F2F),
				null);
	}

	private PenColorValues() {
		// utility class
	}
}