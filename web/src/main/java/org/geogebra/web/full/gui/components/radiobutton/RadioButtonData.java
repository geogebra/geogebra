package org.geogebra.web.full.gui.components.radiobutton;

import java.util.function.Consumer;

public class RadioButtonData {
	private String label;
	private boolean selected;
	private boolean disabled;
	private Consumer<Boolean> callback;

	/**
	 * radio button data
	 * @param label - label
	 * @param selected - whether is selected
	 * @param disabled - whether is disabled
	 * @param callback - callback on click
	 */
	public RadioButtonData(String label, boolean selected, boolean disabled,
			Consumer<Boolean> callback) {
		this.label = label;
		this.selected = selected;
		this.disabled = disabled;
		this.callback = callback;
	}

	/**
	 * not disabled radio button data
	 * @param label - label
	 * @param selected - whether is selected
	 * @param callback - callback on click
	 */
	public RadioButtonData(String label, boolean selected, Consumer<Boolean> callback) {
		this(label, selected, false, callback);
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

	public Consumer<Boolean> getCallback() {
		return callback;
	}
}
