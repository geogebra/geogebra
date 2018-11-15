package org.geogebra.web.html5.javax.swing;

import org.geogebra.web.html5.gui.textbox.GTextBox;

/**
 * Wrapper for input[type=number]
 */
public class GSpinnerW extends GTextBox {

	/**
	 * New numeric input.
	 */
	public GSpinnerW() {
		super();
		getElement().setAttribute("type", "number");
		this.setValue("2");
		setMinValue(0.25);
		setMaxValue(10.0);
		setStepValue(0.25);
		addStyleName("ggbSpinner");
	}

	public void setMinValue(Double value) {
		getElement().setAttribute("min", value.toString());
	}

	public void setMaxValue(Double value) {
		getElement().setAttribute("max", value.toString());
	}

	public void setStepValue(Double value) {
		getElement().setAttribute("step", value.toString());
	}

	public void setMinValue(Integer value) {
		getElement().setAttribute("min", value.toString());
	}

	public void setMaxValue(Integer value) {
		getElement().setAttribute("max", value.toString());
	}

	public void setStepValue(Integer value) {
		getElement().setAttribute("step", value.toString());
	}

}
