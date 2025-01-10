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
