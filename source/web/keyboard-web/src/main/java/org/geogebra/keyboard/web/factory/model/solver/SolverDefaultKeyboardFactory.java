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

package org.geogebra.keyboard.web.factory.model.solver;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.GEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addFirstRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addFourthRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addSecondRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addThirdRow;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.CharacterProvider;

public class SolverDefaultKeyboardFactory implements KeyboardModelFactory {

	private CharacterProvider charProvider;

	public SolverDefaultKeyboardFactory(CharacterProvider characterProvider) {
		charProvider = characterProvider;
	}

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow();
		addInputButton(row, buttonFactory, charProvider.xForButton(), charProvider.xAsInput());
		addInputButton(row, buttonFactory, charProvider.yForButton(), charProvider.yAsInput());
		addInputButton(row, buttonFactory, charProvider.zForButton(), charProvider.zAsInput());
		addConstantInputButton(row, buttonFactory, Resource.ABS, "|");
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addFirstRow(row, buttonFactory);

		row = mathKeyboard.nextRow();
		addConstantInputButton(row, buttonFactory, Resource.POWA2, SUP2);
		addConstantInputButton(row, buttonFactory, Resource.POWAB, "^");
		addConstantInputButton(row, buttonFactory, Resource.ROOT, ROOT);
		addConstantInputButton(row, buttonFactory, Resource.N_ROOT, "nroot");

		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addSecondRow(row, buttonFactory);

		row = mathKeyboard.nextRow();
		addInputButton(row, buttonFactory, "<");
		addInputButton(row, buttonFactory, ">");
		addInputButton(row, buttonFactory, LEQ);
		addInputButton(row, buttonFactory, GEQ);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addThirdRow(row, buttonFactory);

		row = mathKeyboard.nextRow();
		addInputButton(row, buttonFactory, "(");
		addInputButton(row, buttonFactory, ")");
		addConstantInputButton(row, buttonFactory, Resource.FRACTION, "/");
		addConstantInputButton(row, buttonFactory, Resource.MIXED_NUMBER, "mixedNumber");
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addFourthRow(row, buttonFactory);

		return mathKeyboard;
	}
}

