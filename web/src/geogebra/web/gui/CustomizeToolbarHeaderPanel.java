package geogebra.web.gui;

import geogebra.common.main.Localization;


public class CustomizeToolbarHeaderPanel extends AuxiliaryHeaderPanel {

	CustomizeToolbarHeaderPanel(Localization loc, MyHeaderPanel gui) {
	    super(loc, gui);
	    setLabels();
    }
	
	@Override
    public void setLabels(){
		this.setText(loc.getMenu("Toolbar.Customize"));
	}

}
