package geogebra.web.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.web.gui.dialog.InputDialogW;
import geogebra.web.gui.dialog.TextInputDialogW;
import geogebra.web.gui.infobar.InfoBarW;
import geogebra.web.main.AppW;

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
	
	@Override
    public InfoBarW newInfoBar(App app) {
		return new InfoBarW(app);
    }

	@Override
    public InputDialog newInputDialog(App app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
	        GeoElement geo) {
	    return new InputDialogW((AppW)app, message, title,
	    		initString, autoComplete, handler, geo);
    }
	
}
