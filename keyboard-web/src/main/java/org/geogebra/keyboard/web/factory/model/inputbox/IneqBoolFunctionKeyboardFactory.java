package org.geogebra.keyboard.web.factory.model.inputbox;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.FunctionKeyUtil;
import org.geogebra.keyboard.web.factory.model.inputbox.util.MathKeyUtil;

public class IneqBoolFunctionKeyboardFactory implements KeyboardModelFactory {

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
		MathKeyUtil.addPiEIDegree(row, buttonFactory);

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
