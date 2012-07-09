package geogebra.common.factories;

import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.main.App;

public abstract class Factory {
	public static Factory prototype;
	
	public abstract RadioButtonMenuBar newRadioButtonMenuBar(App app);
}
