package org.geogebra.common.gui.util.slider;

class SliderData <T> {
	private T min;
	private T max;
	private T step;

	T getMin() {
		return min;
	}

	void setMin(T min) {
		this.min = min;
	}

	T getMax() {
		return max;
	}

	void setMax(T max) {
		this.max = max;
	}

	T getStep() {
		return step;
	}

	void setStep(T step) {
		this.step = step;
	}
}
