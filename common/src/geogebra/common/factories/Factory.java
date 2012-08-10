package geogebra.common.factories;

import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;

public abstract class Factory {
	public static Factory prototype;
	
	public abstract RadioButtonMenuBar newRadioButtonMenuBar(App app);
	public abstract GOptionPane newGOptionPane();
}
