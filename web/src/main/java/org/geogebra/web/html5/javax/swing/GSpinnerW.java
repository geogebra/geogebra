package org.geogebra.web.html5.javax.swing;

import org.geogebra.web.html5.gui.textbox.GTextBox;

public class GSpinnerW extends GTextBox {

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

	public double getMinValue(Double value) {
		return Double.parseDouble(getElement().getAttribute("min"));
	}

	public double getMaxValue(Double value) {
		return Double.parseDouble(getElement().getAttribute("max"));
	}

	public double getStepValue(Double value) {
		return Double.parseDouble(getElement().getAttribute("step"));
	}

}
