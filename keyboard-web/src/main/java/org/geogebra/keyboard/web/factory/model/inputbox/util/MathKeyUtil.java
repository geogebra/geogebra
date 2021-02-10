package org.geogebra.keyboard.web.factory.model.inputbox.util;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.DEGREE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.INFINITY;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
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
		addButton(row, buttonFactory.createEmptySpace(1.2f));
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
		addConstantInputButton(row, buttonFactory, Resource.FRACTION, "/");
	}

	/**
	 * third row of math default inputbox math keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addImInfDegComma(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, Characters.imaginaryI, "i", "altText.Imaginaryi");
		addInputButton(row, buttonFactory, INFINITY);
		addInputButton(row, buttonFactory, DEGREE);
		addInputButton(row, buttonFactory, ",");
	}

	/**
	 * fourth row of math default inputbox math keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addParenthesesPiE(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, "(");
		addInputButton(row, buttonFactory, ")");
		addInputButton(row, buttonFactory, PI);
		addInputButton(row, buttonFactory, "e", EULER);
	}
}
