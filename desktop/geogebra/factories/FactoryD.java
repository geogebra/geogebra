package geogebra.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.main.App;

public class FactoryD extends Factory{

	@Override
	public RadioButtonMenuBar newRadioButtonMenuBar(App app) {
		return new geogebra.gui.menubar.RadioButtonMenuBarD(app);
	}

}
