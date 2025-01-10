package org.geogebra.web.full.gui.components.radiobutton;

public class RadioButtonData<T> {
	private String label;
	private boolean disabled;
	private T value;

	/**
	 * radio button data
	 * @param label - label
	 * @param disabled - whether is disabled
	 */
	public RadioButtonData(String label, boolean disabled,
			T value) {
		this.label = label;
		this.disabled = disabled;
		this.value = value;
	}

	/**
	 * not disabled radio button data
	 * @param label - label
	 * @param value - value on click
	 */
	public RadioButtonData(String label, T value) {
		this(label, false, value);
	}

	public String getLabel() {
		return label;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public T getValue() {
		return value;
	}
}
