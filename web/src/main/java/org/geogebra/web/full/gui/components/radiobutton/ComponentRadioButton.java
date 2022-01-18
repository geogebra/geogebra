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
	 * @param data - data
	 * @param callback - callback on click
	 */
	public ComponentRadioButton(Localization loc, RadioButtonData data,	Runnable callback) {
		setSelected(data.isSelected());
		setDisabled(data.isDisabled());

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

		Label radioLabel = new Label(loc.getMenu(data.getLabel()));
		add(radioLabel);

		Dom.addEventListener(this.getElement(), "click", evt -> {
			if (!disabled) {
				if (callback != null) {
					callback.run();
				}
				setSelected(!isSelected());
			}
		});
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
		if (!isSelected) {
			removeStyleName("selected");
		}
	}

	public boolean isSelected() {
		return selected;
	}
}
