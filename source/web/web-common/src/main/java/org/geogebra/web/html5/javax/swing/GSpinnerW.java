/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * @param value minimum
	 */
	public void setMinValue(Double value) {
		getElement().setAttribute("min", value.toString());
	}

	/**
	 * @param value maximum
	 */
	public void setMaxValue(Double value) {
		getElement().setAttribute("max", value.toString());
	}

	/**
	 * @param value step
	 */
	public void setStepValue(Double value) {
		getElement().setAttribute("step", value.toString());
	}

	/**
	 * @param value minu=imum
	 */
	public void setMinValue(Integer value) {
		getElement().setAttribute("min", value.toString());
	}

	/**
	 * @param value maximum
	 */
	public void setMaxValue(Integer value) {
		getElement().setAttribute("max", value.toString());
	}

	/**
	 * @param value step
	 */
	public void setStepValue(Integer value) {
		getElement().setAttribute("step", value.toString());
	}

}
