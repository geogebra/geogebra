package geogebra.common.factories;

import geogebra.common.gui.dialog.TextInputDialog;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

public abstract class Factory {
	public static Factory prototype;
	
	public abstract RadioButtonMenuBar newRadioButtonMenuBar(App app);
	public abstract GOptionPane newGOptionPane();
	public abstract TextInputDialog newTextInputDialog(App app, String title, GeoText editGeo,
            GeoPointND startPoint, int cols, int rows, boolean isTextMode);
}
