package geogebra.common.factories;

import geogebra.common.gui.menubar.RadioButtonMenuBar;

public abstract class Factory {
	public static Factory prototype;
	
	public abstract RadioButtonMenuBar newRadioButtonMenuBar();
}
