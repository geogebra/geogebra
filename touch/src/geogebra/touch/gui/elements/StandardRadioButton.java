package geogebra.touch.gui.elements;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.LookAndFeel;

import com.google.gwt.user.client.ui.RadioButton;

public class StandardRadioButton extends RadioButton {
    LookAndFeel laf = TouchEntryPoint.getLookAndFeel();

    public StandardRadioButton(String name, String label) {
	super(name, label);

    }

    @Override
    public void setValue(Boolean value) {
	super.setValue(value);

	if (value.booleanValue()) {
	    // StandardRadioButton.this.getElement().setAttribute("style",
	    // "background-image: url(" +
	    // StandardRadioButton.this.laf.getIcons().radioButtonActive().getSafeUri().asString()
	    // + ")");

	} else {
	    // StandardRadioButton.this.getElement().setAttribute("style",
	    // "background-image: url(" +
	    // StandardRadioButton.this.laf.getIcons().radioButtonInactive().getSafeUri().asString()
	    // + ")");

	}
    }

}
