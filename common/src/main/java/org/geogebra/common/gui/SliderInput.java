package org.geogebra.common.gui;

public enum SliderInput {
	ROTATE_Z("Rotation around z", 0, 360),

	TILT("Tilt", -90, 90);

	private double min;
	private String description;
	private double max;

	private SliderInput(String s, double min, double max) {
		this.description = s;
		this.min = min;
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public String getDescription() {
		return description;
	}

}
