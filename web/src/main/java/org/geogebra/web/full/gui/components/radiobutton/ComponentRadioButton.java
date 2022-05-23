package org.geogebra.web.full.gui.components.radiobutton;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class ComponentRadioButton extends FlowPanel implements SetLabels {
	private boolean disabled = false;
	private boolean selected;
	private Label radioLabel;
	private String ggbTransKey;
	private Localization loc;
	private Runnable callback;

	/**
	 * default constructor
	 * @param loc - localization
	 * @param data - data
	 */
	public ComponentRadioButton(Localization loc, RadioButtonData data) {
		setSelected(data.isSelected());
		setDisabled(data.isDisabled());
		this.ggbTransKey = data.getLabel();
		this.loc = loc;

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

		radioLabel = new Label(loc.getMenu(data.getLabel()));
		add(radioLabel);

		Dom.addEventListener(this.getElement(), "click", evt -> {
			if (!disabled) {
				if (callback != null) {
					callback.run();
				}

			}
		});
	}

	/**
	 * callback on click
	 * @param callback - callback
	 */
	public void setCallback(Runnable callback) {
		this.callback = callback;
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

	@Override
	public void setLabels() {
		radioLabel.setText(loc.getMenu(ggbTransKey));
	}
}
