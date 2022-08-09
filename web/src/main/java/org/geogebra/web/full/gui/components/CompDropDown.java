package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CompDropDown extends FlowPanel {
	private final AppW app;
	private Label selectedOption;
	private ComponentDropDownPopup dropDown;
	private List<AriaMenuItem> dropDownElementsList;
	private boolean isDisabled = false;

	/**
	 * constructor
	 * @param app - see {@link AppW}
	 * @param label - label of drop-down
	 * @param property - popup elements
	 */
	public CompDropDown(AppW app, String label, Property property) {
		this.app = app;
		addStyleName("dropDown");

		buildGUI(label);
		createDropDownMenu(app);
		setElements(Arrays.asList(((EnumerableProperty) property).getValues()));
		setSelectedOption(0);
	}

	private void buildGUI(String labelStr) {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelStr != null && !labelStr.isEmpty()) {
			Label label = new Label(app.getLocalization().getMenu(labelStr));
			label.addStyleName("label");
			optionHolder.add(label);
		}

		selectedOption = new Label();
		selectedOption.addStyleName("selectedOption");
		optionHolder.add(selectedOption);
		add(optionHolder);

		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML("<svg xmlns=\"http://www.w3.org/2000/svg\" "
				+ "width=\"24\" height=\"24\" viewBox=\"0 0 24 24\"><path d=\"M7 10l5 5 5-5z\"/>"
				+ "<path fill=\"none\" d=\"M0 0h24v24H0z\"/></svg>");
		add(arrowIcon);
	}

	private void createDropDownMenu(final AppW app) {
		dropDown = new ComponentDropDownPopup(app, 24, selectedOption);
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
		}
	}

	public void setSelectedOption(int idx) {
		dropDown.setSelectedIndex(idx);
		selectedOption.setText(dropDownElementsList.get(idx).getElement().getInnerText());
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
		for (AriaMenuItem menuItem : menuItems) {
			dropDown.addItem(menuItem);
		}
	}

	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		Dom.toggleClass(this, "disabled", disabled);
	}
}
