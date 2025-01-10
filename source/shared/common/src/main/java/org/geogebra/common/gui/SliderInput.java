package org.geogebra.common.gui;

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
