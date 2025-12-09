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

package org.geogebra.common.gui;

/**
 * Direction of rotation of the 3D view.
 */
public enum SliderInput {
	/**
	 * 3D rotation around z
	 */
	ROTATE_Z("Rotation around z", 0, 360),

	/**
	 * 3D rotation around horizontal axis perpendicular to camera
	 */
	TILT("Tilt", -90, 90);

	private double min;
	private String description;
	private double max;

	private SliderInput(String s, double min, double max) {
		this.description = s;
		this.min = min;
		this.max = max;
	}

	/**
	 * @return minimal value of the slider
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @return maximal value of the slider
	 */
	public double getMax() {
		return max;
	}

	/**
	 * TODO screen reader features are English only for now
	 * 
	 * @return english description
	 */
	public String getDescription() {
		return description;
	}

}
