package geogebra.web.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;

public class FactoryW extends Factory{

	@Override
    public RadioButtonMenuBar newRadioButtonMenuBar(App app) {
	    return new geogebra.web.gui.menubar.RadioButtonMenuBarW(app);
    }

	@Override
    public GOptionPane newGOptionPane() {
		return new geogebra.web.javax.swing.GOptionPaneW();
    }

}
