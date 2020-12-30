package org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.GreekKeyboardFactory;

public class InputBoxDefaultGreekKeyboardFactory extends GreekKeyboardFactory {

	@Override
	public void addControlButtons(RowImpl rowImpl, ButtonFactory buttonFactory, String definition) {
		addInputButton(rowImpl, buttonFactory, '\'');
		addConstantInputButton(rowImpl, buttonFactory, Resource.A_N, "a_n");
	}
}
