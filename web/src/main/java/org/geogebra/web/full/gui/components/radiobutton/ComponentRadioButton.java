package org.geogebra.web.full.gui.components.radiobutton;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class ComponentRadioButton extends FlowPanel {
	private boolean disabled = false;
	private boolean selected;

	/**
	 * default constructor
	 * @param loc - localization
	 * @param transKey - label of button
	 * @param selected - selected state
	 * @param callback - callback on click
	 */
	public ComponentRadioButton(Localization loc, String transKey, boolean selected,
			Runnable callback) {
		setSelected(selected);

		addStyleName("radioButton");
		FlowPanel radioBg = new FlowPanel();
		radioBg.addStyleName("radioBg");
		radioBg.addStyleName("ripple");
		add(radioBg);

		SimplePanel outerCircle = new SimplePanel();
		outerCircle.addStyleName("outerCircle");
		radioBg.add(outerCircle);

		SimplePanel innerCircle = new SimplePanel();
		innerCircle.addStyleName("innerCircle");
		radioBg.add(innerCircle);

		Label radioLabel = new Label(loc.getMenu(transKey));
		add(radioLabel);

		Dom.addEventListener(this.getElement(), "click", (evt -> {
			if (!disabled) {
				setSelected(!isSelected());
				if (callback != null) {
					callback.run();
				}
			}
		}));
	}

	/**
	 * constructor for not selected radio button
	 * @param loc - localization
	 * @param transKey - label of button
	 * @param callback - callback on click
	 * @param disabled - disabled state
	 */
	public ComponentRadioButton(Localization loc, String transKey, Runnable callback,
			boolean disabled) {
		this(loc, transKey, false, callback);
		setDisabled(disabled);
	}

	/**
	 * set disabled state of radio button
	 * @param isDisabled - true if should be disabled
	 */
	public void setDisabled(boolean isDisabled) {
		disabled = isDisabled;
		Dom.toggleClass(this, "disabled", disabled);
	}

	/**
	 * set selected state of radio button
	 * @param isSelected - true if should be selected
	 */
	public void setSelected(boolean isSelected) {
		selected = isSelected;
		if (!this.getStyleName().contains("selected") && isSelected) {
			addStyleName("selected");
		}
	}

	public boolean isSelected() {
		return selected;
	}
}
