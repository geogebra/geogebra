package geogebra.web.factories;

import geogebra.common.gui.menubar.RadioButtonMenuBar;

public class Factory extends geogebra.common.factories.Factory{

	@Override
    public RadioButtonMenuBar newRadioButtonMenuBar() {
	    return new geogebra.web.gui.menubar.RadioButtonMenuBar();
    }

}
