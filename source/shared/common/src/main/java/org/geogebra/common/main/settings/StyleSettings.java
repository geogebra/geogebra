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

package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GColor;

public class StyleSettings {

	private boolean buttonShadows = false;
	private double buttonRounding = 0.2;
	private GColor buttonBorderColor = GColor.BLACK;

	/**
	 * @param b whether buttons have shadows
	 */
	public void setButtonShadows(boolean b) {
		this.buttonShadows = b;
	}

	/**
	 * @param percent how rounded buttons are
	 */
	public void setButtonRounding(double percent) {
		if (!Double.isFinite(percent)) {
			this.buttonRounding = 0.2;
		} else if (percent < 0) {
			this.buttonRounding = 0;
		} else if (percent > 0.9) {
			this.buttonRounding = 0.9;
		} else {
			this.buttonRounding = percent;
		}
	}

	/**
	 * @param colorString css string specifying the border color of buttons
	 */
	public void setButtonBorderColor(String colorString) {
		this.buttonBorderColor = GColor.parseHexColor(colorString);
	}

	/**
	 * @return how rounded buttons are
	 */
	public double getButtonRounding() {
		return buttonRounding;
	}

	/**
	 * @return whether buttons have shadows
	 */
	public boolean getButtonShadows() {
		return buttonShadows;
	}

	/**
	 * @return border color of GeoButtons
	 */
	public GColor getButtonBorderColor() {
		return buttonBorderColor;
	}
}
