package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.himamis.retex.editor.share.util.GWTKeycodes;

public class ComponentCombobox extends FlowPanel implements SetLabels {
	private final AppW appW;
	private EnumerableProperty property;
	private FlowPanel contentPanel;
	private AutoCompleteTextFieldW inputTextField;
	private FormLabel labelText;
	private String labelTextKey;
	private SimplePanel arrowIcon;
	private ComponentDropDownPopup dropDown;
	private List<AriaMenuItem> dropDownElementsList;
	private int lastSelectedIdx;
	private boolean isDisabled = false;

	/**
	 * Constructor
	 * @param app - see {@link AppW}
	 * @param label - label of combobox
	 * @param property - popup items
	 */
	public ComponentCombobox(AppW app, String label, EnumerableProperty property) {
		appW = app;
		labelTextKey = label;
		this.property = property;

		addStyleName("combobox");
		buildGUI();
		addFocusBlurHandlers();
		addHoverHandlers();
		addFieldKeyAndPointerHandler();

		createDropDownMenu(appW);
		setElements(Arrays.asList(property.getValues()));
		setSelectedOption(0);
	}

	private void buildGUI() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("inputPanel");

		inputTextField = new AutoCompleteTextFieldW(-1, appW, false, null, false);
		inputTextField.addStyleName("textField");

		if (labelTextKey != null) {
			labelText = new FormLabel().setFor(inputTextField);
			labelText.setStyleName("inputLabel");
			labelText.setText(appW.getLocalization().getMenu(labelTextKey));
			add(labelText);
		}

		arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());

		contentPanel.add(inputTextField);
		contentPanel.add(arrowIcon);
		add(contentPanel);
	}

	private void addFocusBlurHandlers() {
		inputTextField.getTextBox()
				.addFocusHandler(event -> addStyleName("focusState"));
		inputTextField.getTextBox()
				.addBlurHandler(event -> removeStyleName("focusState"));
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputTextField.getTextBox()
				.addMouseOverHandler(event -> addStyleName("hoverState"));
		inputTextField.getTextBox()
				.addMouseOutHandler(event -> removeStyleName("hoverState"));
	}

	private void addFieldKeyAndPointerHandler() {
		inputTextField.addKeyUpHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER) {
				toggleExpanded();
			}
		});
	}

	private void createDropDownMenu(final AppW app) {
		dropDown = new ComponentDropDownPopup(app, 24, inputTextField, this::onClose);
		dropDown.addAutoHidePartner(getElement());

		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!isDisabled) {
					toggleExpanded();
				}
			}
		});
	}

	private void onClose() {
		removeStyleName("active");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE.arrow_drop_down()
				.withFill(GColor.BLACK.toString()).getSVG());
		resetTextField();
	}

	/**
	 * Expand/collapse the dropdown.
	 */
	protected void toggleExpanded() {
		if (dropDown.isOpened()) {
			inputTextField.setFocus(false);
			resetTextField();
			dropDown.close();
		} else {
			dropDown.showAtPoint(getAbsoluteLeft(), getElement().getAbsoluteBottom());
			Scheduler.get().scheduleDeferred(() -> {
				inputTextField.selectAll();
			});
		}
		Dom.toggleClass(this, "active", dropDown.isOpened());
		GColor arrowCol = dropDown.isOpened()
				? GeoGebraColorConstants.GEOGEBRA_ACCENT : GColor.BLACK;
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE.arrow_drop_down()
				.withFill(arrowCol.toString()).getSVG());
	}

	private void resetTextField() {
		if (inputTextField.getText().isEmpty()) {
			inputTextField.setText(dropDownElementsList.get(
					lastSelectedIdx).getText());
		}
	}

	private void setSelectedOption(int idx) {
		lastSelectedIdx = idx;
		dropDown.setSelectedIndex(idx);
		inputTextField.setText(dropDownElementsList.get(idx).getElement().getInnerText());
	}

	/**
	 * Set the elements of the dropdown list
	 *
	 * @param dropDownList
	 *            List of strings which will be shown in the dropdown list
	 */
	public void setElements(final List<String> dropDownList) {
		dropDownElementsList = new ArrayList<>();

		for (int i = 0; i < dropDownList.size(); ++i) {
			final int currentIndex = i;
			AriaMenuItem item = new AriaMenuItem(
					MainMenu.getMenuBarHtmlEmptyIcon(dropDownList.get(i)), true,
					() -> {
						setSelectedOption(currentIndex);
					});

			item.setStyleName("dropDownElement");
			dropDownElementsList.add(item);
		}
		setupDropDownMenu(dropDownElementsList);
	}

	private void setupDropDownMenu(List<AriaMenuItem> menuItems) {
		dropDown.clear();
		for (AriaMenuItem menuItem : menuItems) {
			dropDown.addItem(menuItem);
		}
	}

	/**
	 * Disable drop-down component
	 * @param disabled - true, if drop-down should be disabled
	 */
	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		inputTextField.setEnabled(!disabled);
		Dom.toggleClass(this, "disabled", disabled);
	}

	@Override
	public void setLabels() {
		if (labelText != null) {
			labelText.setText(appW.getLocalization().getMenu(labelTextKey));
		}
		setElements(Arrays.asList(property.getValues()));
	}
}
