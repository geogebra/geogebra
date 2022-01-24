package org.geogebra.web.full.gui.components.radiobutton;

public class RadioButtonData {
	private String label;
	private boolean selected;
	private boolean disabled;

	/**
	 * radio button data
	 * @param label - label
	 * @param selected - whether is selected
	 * @param disabled - whether is disabled
	 */
	public RadioButtonData(String label, boolean selected, boolean disabled) {
		this.label = label;
		this.selected = selected;
		this.disabled = disabled;
	}

	/**
	 * not disabled radio button data
	 * @param label - label
	 * @param selected - whether is selected
	 */
	public RadioButtonData(String label, boolean selected) {
		this(label, selected, false);
	}

	public String getLabel() {
		return label;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isDisabled() {
		return disabled;
	}
}
