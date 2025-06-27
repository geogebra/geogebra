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

import elemental2.dom.KeyboardEvent;

/**
 * material design checkbox component
 */
public class ComponentCheckbox extends FlowPanel implements SetLabels {
	private final Localization loc;
	private boolean isSelected;
	private FlowPanel checkbox;
	private Label checkboxLbl;
	private final String checkboxTxt;
	private final Consumer<Boolean> callback;

	/**
	 * @param loc {@link Localization}
	 * @param selected whether the checkbox should be selected by default
	 * @param checkboxText label of checkbox
	 * @param callback click handler
	 */
	public ComponentCheckbox(Localization loc, boolean selected, String checkboxText,
			Consumer<Boolean> callback) {
		this.loc = loc;
		isSelected = selected;
		this.checkboxTxt = checkboxText;
		this.callback = callback;

		addStyleName("checkboxPanel");
		buildComponent();
		addClickAndKeyHandler();

		setSelected(selected);
		setLabels();
		addAccessibilityInfo();
	}

	/**
	 * @param loc {@link Localization}
	 * @param selected whether the checkbox should be selected by default
	 * @param callback click handler
	 */
	public ComponentCheckbox(Localization loc, boolean selected, Consumer<Boolean> callback) {
		this(loc, selected, "", callback);
	}

	/**
	 * @param loc {@link Localization}
	 * @param selected whether it should be selected by default
	 * @param checkboxText label of checkbox
	 */
	public ComponentCheckbox(Localization loc, boolean selected, String checkboxText) {
		this(loc, selected, checkboxText, null);
	}

	private void buildComponent() {
		checkbox = new FlowPanel();
		checkbox.addStyleName("checkbox");

		addCheckbox();
		addLabel();
	}

	private void addCheckbox() {
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
	}

	private void addLabel() {
		if (!checkboxTxt.isEmpty()) {
			addStyleName("withLabel");
			checkboxLbl = BaseWidgetFactory.INSTANCE.newPrimaryText("", "checkboxLbl");
			add(checkboxLbl);
		}
	}

	private void addClickAndKeyHandler() {
		Dom.addEventListener(this.getElement(), "click", event -> runAction());

		Dom.addEventListener(this.getElement(), "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("Space".equals(e.code)) {
				runAction();
			}
		});
	}

	private void runAction() {
		if (!isDisabled()) {
			setSelected(!isSelected());
			if (callback != null) {
				callback.accept(isSelected());
			}
		}
	}

	private void addAccessibilityInfo() {
		AriaHelper.setLabel(this, loc.getMenu(checkboxTxt));
		AriaHelper.setTabIndex(this, 0);
		AriaHelper.setRole(this, "checkbox");
	}

	/**
	 * @return true if checkbox is selected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param selected whether the checkbox is checked or not
	 */
	public void setSelected(boolean selected) {
		isSelected = selected;
		updateCheckboxStyle();
		AriaHelper.setChecked(this, selected);
	}

	/**
	 * Set disabled state of checkbox
	 * @param disabled whether checkbox should be disabled or enabled
	 */
	public void setDisabled(boolean disabled) {
		Dom.toggleClass(this, "disabled", disabled);
	}

	private boolean isDisabled() {
		return getStyleName().contains("disabled");
	}

	/**
	 * update style of checkbox depending on its status (selected or not selected)
	 */
	public void updateCheckboxStyle() {
		Dom.toggleClass(checkbox, "selected", isSelected());
	}

	@Override
	public void setLabels() {
		if (checkboxLbl != null) {
			checkboxLbl.setText(loc.getMenu(checkboxTxt));
			AriaHelper.setLabel(this, loc.getMenu(checkboxTxt));
		}
	}
}
