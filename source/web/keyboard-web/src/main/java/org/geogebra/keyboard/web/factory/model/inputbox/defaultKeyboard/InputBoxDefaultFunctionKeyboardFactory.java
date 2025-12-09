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

package org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.FunctionKeyUtil;

public class InputBoxDefaultFunctionKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl functionKeyboard = new KeyboardModelImpl();
		float width = 5.0f / 3;

		RowImpl row = functionKeyboard.nextRow();
		FunctionKeyUtil.addSinCosTan(row, buttonFactory, width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		FunctionKeyUtil.addPowEPow10NRootAbs(row, buttonFactory);

		row = functionKeyboard.nextRow();
		FunctionKeyUtil.addInverseSinCosTan(row, buttonFactory, width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		FunctionKeyUtil.addLessGtLessEqGtEq(row, buttonFactory);

		row = functionKeyboard.nextRow();
		FunctionKeyUtil.addSecCscCot(row, buttonFactory, width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		FunctionKeyUtil.addProcExclNotEqBack(row, buttonFactory);

		row = functionKeyboard.nextRow();
		FunctionKeyUtil.addLnLog10LogB(row, buttonFactory, width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		FunctionKeyUtil.addAnLeftRightEnter(row, buttonFactory);

		return functionKeyboard;
	}
}
