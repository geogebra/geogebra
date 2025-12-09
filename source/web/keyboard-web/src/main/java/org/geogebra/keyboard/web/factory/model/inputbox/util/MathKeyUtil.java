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

package org.geogebra.keyboard.web.factory.model.inputbox.util;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.CURLY_EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.CURLY_PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.DEGREE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.Characters;

public class MathKeyUtil {

	/**
	 * first row of math default inputbox math keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addXYZ(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, Characters.x, "x");
		addInputButton(row, buttonFactory, Characters.y, "y");
		addInputButton(row, buttonFactory, Characters.z, "z");
	}

	/**
	 * second row of math default inputbox math keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addSqExpRootFrac(RowImpl row, ButtonFactory buttonFactory) {
		addConstantInputButton(row, buttonFactory, Resource.POWA2, SUP2);
		addConstantInputButton(row, buttonFactory, Resource.POWAB, "^");
		addConstantInputButton(row, buttonFactory, Resource.ROOT, ROOT);
		addConstantInputButton(row, buttonFactory, Resource.RECURRING_DECIMAL, "recurringDecimal");
	}

	/**
	 * fourth row of math default inputbox math keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addParenthesesFractionMixed(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, "(");
		addInputButton(row, buttonFactory, ")");
		addConstantInputButton(row, buttonFactory, Resource.FRACTION, "/");
		addConstantInputButton(row, buttonFactory, Resource.MIXED_NUMBER, "mixedNumber");
	}

	/**
	 * third row of math default inputbox math keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addPiEIDegree(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, CURLY_PI, PI);
		addInputButton(row, buttonFactory, CURLY_EULER, EULER);
		addInputButton(row, buttonFactory, Characters.imaginaryI, "i", "altText.Imaginaryi");
		addInputButton(row, buttonFactory, DEGREE);
	}
}
