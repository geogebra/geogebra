package org.geogebra.keyboard.base.model.impl.factory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.GEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class FunctionKeyUtil {

	/**
	 * left first row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 * @param width width
	 */
	public static void addSinCosTan(RowImpl row, ButtonFactory buttonFactory, float width) {
		addTranslateInputCommandButton(row, buttonFactory, "sin", "sin", width);
		addTranslateInputCommandButton(row, buttonFactory, "cos", "cos", width);
		addTranslateInputCommandButton(row, buttonFactory, "tan", "tan", width);
	}

	/**
	 * left second row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	* @param width width
	 */
	public static void addInverseSinCosTan(RowImpl row, ButtonFactory buttonFactory, float width) {
		addTranslateInputCommandButton(row, buttonFactory, "asin",
				"altText.asin", "asin", width);
		addTranslateInputCommandButton(row, buttonFactory, "acos",
				"altText.acos", "acos", width);
		addTranslateInputCommandButton(row, buttonFactory, "atan",
				"altText.atan", "atan", width);
	}

	/**
	 * left third row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 * @param width width
	 */
	public static void addSecCscCot(RowImpl row, ButtonFactory buttonFactory, float width) {
		addTranslateInputCommandButton(row, buttonFactory, "sec", "sec", width);
		addTranslateInputCommandButton(row, buttonFactory, "csc", "csc", width);
		addTranslateInputCommandButton(row, buttonFactory, "cot", "cot", width);
	}

	/**
	 * left fourth row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 * @param width width
	 */
	public static void addLnLog10LogB(RowImpl row, ButtonFactory buttonFactory, float width) {
		addInputButton(row, buttonFactory, "ln", width);
		addConstantInputButton(row, buttonFactory, Resource.LOG_10, "log_{10}", width);
		addConstantInputButton(row, buttonFactory, Resource.LOG_B, "logb", width);
	}

	/**
	 * right first row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addPowEPow10NRootAbs(RowImpl row, ButtonFactory buttonFactory) {
		addConstantInputButton(row, buttonFactory, Resource.POWE_X, EULER + "^");
		addConstantInputButton(row, buttonFactory, Resource.POW10_X, "10^");
		addConstantInputButton(row, buttonFactory, Resource.N_ROOT, "nroot");
		addConstantInputButton(row, buttonFactory, Resource.ABS, "|");
	}

	/**
	 * right second row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addLessGtLessEqGtEq(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, "<");
		addInputButton(row, buttonFactory, ">");
		addInputButton(row, buttonFactory, LEQ);
		addInputButton(row, buttonFactory, GEQ);
	}

	/**
	 * right third row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addProcExclNotEqBack(RowImpl row, ButtonFactory buttonFactory) {
		addInputButton(row, buttonFactory, "%");
		addInputButton(row, buttonFactory, "!");
		addInputButton(row, buttonFactory, NOT_EQUAL_TO);
		addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
				Action.BACKSPACE_DELETE);
	}

	/**
	 * right fourth row of default inputbox function keyboard
	 * @param row row
	 * @param buttonFactory factory
	 */
	public static void addAnLeftRightEnter(RowImpl row, ButtonFactory buttonFactory) {
		addConstantInputButton(row, buttonFactory, Resource.A_N, "a_n");
		addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);
	}
}
