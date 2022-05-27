package org.geogebra.web.full.gui.components;

import java.util.function.Consumer;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * material design checkbox component
 */
public class ComponentCheckbox extends FlowPanel implements SetLabels {
	private Localization loc;
	private boolean selected;
	private boolean disabled = false;
	private FlowPanel checkbox;
	private Label checkboxLbl;
	private String checkboxTxt;

	/**
	 * @param loc - localization
	 * @param setSelected - true if checkbox should be selected
	 * @param templateTxt - text of checkbox
	 * @param callback - on click action
	 */
	public ComponentCheckbox(Localization loc, boolean setSelected, String templateTxt,
			Consumer<Boolean> callback) {
		this.loc = loc;
		this.selected = setSelected;
		this.checkboxTxt = templateTxt;

		addStyleName("checkboxPanel");
		checkbox = new FlowPanel();
		checkbox.addStyleName("checkbox");
		if (selected) {
			checkbox.addStyleName("selected");
		}

		SimplePanel background = new SimplePanel();
		background.addStyleName("background");
		SimplePanel checkMark = new SimplePanel();
		checkMark.getElement().setInnerHTML("<svg class=\"checkmarkSvg\" "
				+ "viewBox=\"0 0 24 24\"><path class=\"checkmarkPath\" "
				+ "fill=\"none\" stroke=\"white\" d=\"M1.73,12.91 8.1,19.28 22.79,4.59\">"
				+ "</path></svg>");
		checkMark.addStyleName("checkmark");
		checkbox.add(background);
		checkbox.add(checkMark);

		FlowPanel checkboxBg = new FlowPanel();
		checkboxBg.addStyleName("hoverBg");
		checkboxBg.addStyleName("ripple");
		checkbox.add(checkboxBg);

		checkboxLbl = new Label();
		checkboxLbl.setStyleName("checkboxLbl");
		add(checkbox);
		add(checkboxLbl);
		Dom.addEventListener(this.getElement(), "click", evt -> {
			if (!disabled) {
				setSelected(!isSelected());
				if (callback != null) {
					callback.accept(isSelected());
				}
			}
		});

		setLabels();
	}

	/**
	 * @param loc - localization
	 * @param setSelected - true if checkbox should be selected
	 * @param templateTxt - text of checkbox
	 */
	public ComponentCheckbox(Localization loc, boolean setSelected, String templateTxt) {
		this(loc, setSelected, templateTxt, null);
	}

	/**
	 * @return true if checkbox is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            true if switch is on
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateCheckboxStyle();
	}

	/**
	 * update style of checkbox depending on its status (selected or not selected)
	 */
	public void updateCheckboxStyle() {
		Dom.toggleClass(checkbox, "selected", isSelected());
	}

	/**
	 * set disabled state of checkbox
	 * @param isDisabled - true if should be disabled
	 */
	public void setDisabled(boolean isDisabled) {
		disabled = isDisabled;
		Dom.toggleClass(checkbox, "disabled", disabled);
		Dom.toggleClass(checkboxLbl, "disabled", disabled);
	}

	@Override
	public void setLabels() {
		checkboxLbl.setText(loc.getMenu(checkboxTxt));
	}
}
