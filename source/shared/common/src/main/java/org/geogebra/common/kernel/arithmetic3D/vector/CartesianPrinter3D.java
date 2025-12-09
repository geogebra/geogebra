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

package org.geogebra.common.kernel.arithmetic3D.vector;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.printing.printable.vector.PrintableVector;
import org.geogebra.common.kernel.printing.printer.Printer;
import org.geogebra.common.main.settings.GeneralSettings;

class CartesianPrinter3D implements Printer {

	private final @CheckForNull GeneralSettings settings;

	public CartesianPrinter3D(@CheckForNull GeneralSettings settings) {
		this.settings = settings;
	}

	@Override
	public String print(String xCoord, String yCoord, String zCoord,
			PrintableVector vector, StringTemplate tpl) {
		if (tpl.getStringType().isGiac()) {
			boolean vectorNot3dPoint = vector.isCASVector();
			return (vectorNot3dPoint
					? "ggbvect[" : "point(")
					+ xCoord
					+ ','
					+ yCoord
					+ ','
					+ zCoord
					+ (vectorNot3dPoint ? "]" : ")");
		}
		if (tpl.usePointTemplate()) {
			return "$point("
					+ xCoord
					+ ','
					+ yCoord
					+ ','
					+ zCoord
					+ ')';
		}
		String delimiter = tpl.getCartesianDelimiter(settings);
		return tpl.leftBracket()
				+ xCoord
				+ delimiter
				+ yCoord
				+ delimiter
				+ zCoord
				+ tpl.rightBracket();
	}
}
