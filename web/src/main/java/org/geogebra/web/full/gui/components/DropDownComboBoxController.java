package org.geogebra.web.full.gui.components;


import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

public class DropDownComboBoxController implements SetLabels {
	private CompDropDownComboBoxI anchor;
	private ComponentDropDownPopup dropDown;
	private List<AriaMenuItem> dropDownElementsList;
	private List<String> items;
	private boolean isDisabled = false;

	/**
	 * popup controller for dropdown and combobox
	 * @param app - apps
	 * @param anchor - anchor
	 * @param widget
	 * @param items - list of items in popup
	 * @param onClose - handler to run on close
	 */
	public DropDownComboBoxController(final AppW app, CompDropDownComboBoxI anchor, Widget widget,
			List<String> items, Runnable onClose) {
		this.anchor = anchor;
		this.items = items;

		createPopup(app, anchor.asWidget(), onClose);
		setElements(items);
		setSelectedOption(0);
	}

	public void createPopup(final AppW app, Widget widget, Runnable onClose) {
		dropDown = new ComponentDropDownPopup(app, 32, widget, onClose);
		dropDown.addAutoHidePartner(anchor.asWidget().getElement());

		ClickStartHandler.init(anchor.asWidget(), new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!isDisabled) {
					anchor.toggleExpanded();
				}
			}
		});
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
	private void setElements(final List<String> dropDownList) {
		dropDownElementsList = new ArrayList<>();

		for (int i = 0; i < dropDownList.size(); ++i) {
			final int currentIndex = i;
			AriaMenuItem item = new AriaMenuItem(dropDownList.get(i), true,
					() -> setSelectedOption(currentIndex));

			item.setStyleName("dropDownElement");
			dropDownElementsList.add(item);
		}
		setupDropDownMenu(dropDownElementsList);
	}

	 void setSelectedOption(int idx) {
		highlightSelectedElement(dropDown.getSelectedIndex(), idx);
		dropDown.setSelectedIndex(idx);
		anchor.updateSelectionText(getSelectedText());
	}

	private void setupDropDownMenu(List<AriaMenuItem> menuItems) {
		dropDown.clear();
		for (AriaMenuItem menuItem : menuItems) {
			dropDown.addItem(menuItem);
		}
	}

	public int getSelectedIndex() {
		return dropDown.getSelectedIndex();
	}

	@Override
	public void setLabels() {
		setElements(items);
		anchor.updateSelectionText(getSelectedText());
	}

	public ComponentDropDownPopup getPopup() {
		return dropDown;
	}

	public boolean isOpened() {
		return dropDown.isOpened();
	}

	public void closePopup() {
		dropDown.close();
	}

	/**
	 * get currently selected text
	 * @return selected text
	 */
	public String getSelectedText() {
		return dropDownElementsList.get(getSelectedIndex()).getText();
	}

	/**
	 * show popup and position as combobox
	 */
	public void showAsComboBox() {
		dropDown.positionAsComboBox();
	}

	/**
	 * shop popup and position as dropdown
	 */
	public void showAsDropDown() {
		dropDown.positionAsDropDown();
		dropDown.setWidthInPx(anchor.asWidget().getElement().getClientWidth());
	}
}
