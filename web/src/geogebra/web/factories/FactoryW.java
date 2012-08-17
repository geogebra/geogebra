package geogebra.web.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.web.gui.dialog.TextInputDialogW;

public class FactoryW extends Factory{

	@Override
    public RadioButtonMenuBar newRadioButtonMenuBar(App app) {
	    return new geogebra.web.gui.menubar.RadioButtonMenuBarW(app);
    }

	@Override
    public GOptionPane newGOptionPane() {
		return new geogebra.web.javax.swing.GOptionPaneW();
    }

	@Override
	public TextInputDialog newTextInputDialog(App app, String title,
	        GeoText editGeo, GeoPointND startPoint, int cols, int rows,
	        boolean isTextMode) {
		return new TextInputDialogW(app, title, editGeo, startPoint, cols,
		        rows, isTextMode);
	}

}
