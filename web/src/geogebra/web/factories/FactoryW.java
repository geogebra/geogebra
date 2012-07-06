package geogebra.web.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.menubar.RadioButtonMenuBar;

public class FactoryW extends Factory{

	@Override
    public RadioButtonMenuBar newRadioButtonMenuBar() {
	    return new geogebra.web.gui.menubar.RadioButtonMenuBarW();
    }

}
