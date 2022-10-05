package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CompDropDown extends FlowPanel implements SetLabels {
	private final AppW app;
	private Label label;
	private String labelKey;
	private Label selectedOption;
	private List<String> items;
	private ComponentDropDownPopup dropDown;
	private List<AriaMenuItem> dropDownElementsList;
	private boolean isDisabled = false;
	private Runnable changeHandler;

	/**
	 * Material rop-down component
	 * @param app - see {@link AppW}
	 * @param label - label of drop-down
	 * @param items - popup elements
	 */
	public CompDropDown(AppW app, String label, List<String> items) {
		this.app = app;
		labelKey = label;
		this.items = items;
		addStyleName("dropDown");

		buildGUI(label);
		createDropDownMenu(app);
		setElements(items);
		setSelectedOption(0);
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelStr != null && !labelStr.isEmpty()) {
			label = new Label(app.getLocalization().getMenu(labelStr));
			label.addStyleName("label");
			optionHolder.add(label);
		}

		selectedOption = new Label();
		selectedOption.addStyleName("selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);

		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());
		add(arrowIcon);
	}

	private void createDropDownMenu(final AppW app) {
		dropDown = new ComponentDropDownPopup(app, 32, selectedOption);
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

	/**
	 * Expand/collapse the dropdown.
	 */
	protected void toggleExpanded() {
		if (dropDown.isOpened()) {
			dropDown.close();
		} else {
			dropDown.show();
			dropDown.setWidthInPx(getStyleElement().getClientWidth());
		}
	}

	private void setSelectedOption(int idx) {
		highlightSelectedElement(dropDown.getSelectedIndex(), idx);
		dropDown.setSelectedIndex(idx);
		selectedOption.setText(dropDownElementsList.get(idx).getElement().getInnerText());
	}

	public int getSelectedIndex() {
		return dropDown.getSelectedIndex();
	}

	private void highlightSelectedElement(int previousSelectedIndex,
			int currentSelectedIndex) {
		dropDownElementsList.get(previousSelectedIndex)
				.removeStyleName("selectedDropDownElement");
		dropDownElementsList.get(currentSelectedIndex)
				.addStyleName("selectedDropDownElement");
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
			AriaMenuItem item = new AriaMenuItem(dropDownList.get(i), true,
					() -> {
				setSelectedOption(currentIndex);
				if (changeHandler != null) {
					changeHandler.run();
				}
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
		Dom.toggleClass(this, "disabled", disabled);
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(app.getLocalization().getMenu(labelKey));
		}

		setElements(items);
		selectedOption.setText(dropDownElementsList.get(dropDown.getSelectedIndex()).getText());
	}

	public void setChangeHandler(Runnable changeHandler) {
		this.changeHandler = changeHandler;
	}
}
