package geogebra.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
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

	@Override
	public TextInputDialog newTextInputDialog(App app, String title,
			GeoText editGeo, GeoPointND startPoint, int cols, int rows,
			boolean isTextMode) {
		return new geogebra.gui.dialog.TextInputDialog(app, title, editGeo, startPoint, cols,
		        rows, isTextMode);
	}

	
}
