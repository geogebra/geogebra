package geogebra.web.factories;

import geogebra.common.factories.Factory;
import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.javax.swing.RelationPane;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.dialog.InputDialogW;
import geogebra.web.gui.dialog.TextInputDialogW;
import geogebra.web.gui.infobar.InfoBarW;

public class FactoryW extends Factory{


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

	@Override
	public RelationPane newRelationPane() {
		return new geogebra.web.javax.swing.RelationPaneW();
	}

	
	
}
