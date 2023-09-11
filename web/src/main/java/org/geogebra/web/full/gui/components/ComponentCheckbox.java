package org.geogebra.web.full.gui.components;

import java.util.function.Consumer;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

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
		checkMark.getElement().setInnerHTML(MaterialDesignResources
				.INSTANCE.check_white().getSVG());
		checkMark.addStyleName("checkmark");
		checkbox.add(background);
		checkbox.add(checkMark);

		FlowPanel checkboxBg = new FlowPanel();
		checkboxBg.addStyleName("hoverBg");
		checkboxBg.addStyleName("ripple");
		checkbox.add(checkboxBg);
		add(checkbox);

		if (!templateTxt.isEmpty()) {
			checkboxLbl = BaseWidgetFactory.INSTANCE.newPrimaryText("", "checkboxLbl");
			add(checkboxLbl);
		}

		Dom.addEventListener(this.getElement(), "click", evt -> {
			if (!disabled) {
				setSelected(!isSelected());
				if (callback != null) {
					callback.accept(isSelected());
				}
			}
		});

		setSelected(setSelected);
		setLabels();
		addAccessibilityInfo();
	}

	private void addAccessibilityInfo() {
		AriaHelper.setLabel(this, loc.getMenu(checkboxTxt));
		AriaHelper.setTabIndex(this, 0);
		AriaHelper.setRole(this, "checkbox");
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
		AriaHelper.setChecked(this, selected);
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
		if (checkboxLbl != null) {
			Dom.toggleClass(checkboxLbl, "disabled", disabled);
		}
	}

	@Override
	public void setLabels() {
		if (checkboxLbl != null) {
			checkboxLbl.setText(loc.getMenu(checkboxTxt));
			AriaHelper.setLabel(this, loc.getMenu(checkboxTxt));
		}
	}
}
