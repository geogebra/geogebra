package geogebra.html5.gui;

import geogebra.common.main.Localization;


public class LanguageHeaderPanel extends AuxiliaryHeaderPanel {

	LanguageHeaderPanel(Localization loc, MyHeaderPanel gui) {
	    super(loc, gui);
	    setLabels();
    }
	
	@Override
    public void setLabels(){
		this.setText(loc.getMenu("Language"));
	}

}
