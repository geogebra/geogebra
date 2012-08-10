package geogebra.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;

public class FactoryD extends Factory{

	@Override
	public RadioButtonMenuBar newRadioButtonMenuBar(App app) {
		return new geogebra.gui.menubar.RadioButtonMenuBarD(app);
	}

	@Override
	public GOptionPane newGOptionPane() {
		return new geogebra.javax.swing.GOptionPaneD();
	}

	
}
