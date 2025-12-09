/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.components;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.util.MulticastEvent;
import org.geogebra.web.html5.gui.inputfield.UpDownArrowHandler;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Widget;

public class DropDownComboBoxController implements SetLabels, UpDownArrowHandler {
	private final Widget parent;
	private ComponentDropDownPopup dropDown;
	private List<AriaMenuItem> dropDownElementsList;
	private final Supplier<List<String>> items;
	private final List<Runnable> changeHandlers = new ArrayList<>();
	private final MulticastEvent<String> onHighlighted = new MulticastEvent<>();

	/**
	 * popup controller for dropdown and combo box
	 * @param app apps
	 * @param parent dropdown or combo box
	 * @param items list of items in popup
	 * @param labelKey label of drop down or combo box
	 * @param onClose handler to run on close
	 */
	public DropDownComboBoxController(final AppW app, Widget parent,
			Supplier<List<String>> items, String labelKey, Runnable onClose) {
		this.parent = parent;
		this.items = items;

		init(app, labelKey, onClose);
	}

	private void init(AppW app, String labelKey, Runnable onClose) {
		createPopup(app, labelKey, parent, onClose);
		setElements(items.get());
		setSelectedOption(-1);
	}

	private void createPopup(final AppW app, String labelKey, Widget parent, Runnable onClose) {
		dropDown = new ComponentDropDownPopup(app, 32, parent, labelKey, onClose);
		dropDown.addAutoHidePartner(parent.getElement());
	}

	/**
	 * open/close dropdown
	 * @param isFullWidth whether dropdown should have full width
	 */
	public void toggleAsDropDown(boolean isFullWidth) {
		if (isOpened()) {
			closePopup();
		} else {
			showAsDropDown(isFullWidth);
		}
		AriaHelper.setAriaExpanded(parent, isOpened());
		Dom.toggleClass(parent, "active", isOpened());

	}

	private void highlightSelectedElement(int index, boolean highlight) {
		if (index >= 0 && index < dropDownElementsList.size()) {
			Dom.toggleClass(dropDownElementsList.get(index), "selectedDropDownElement",
					highlight);
			AriaHelper.setAriaSelected(dropDownElementsList.get(index), highlight);
		}
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
			AriaMenuItem item = new AriaMenuItem(dropDownList.get(i), null, () -> {
				setSelectedOption(currentIndex);
				for (Runnable handler: changeHandlers) {
					handler.run();
				}
			});
			AriaHelper.setRole(item, "option");
			item.getElement().setId(DOM.createUniqueId());
			item.setStyleName("dropDownElement");
			dropDownElementsList.add(item);
		}
		setupDropDownMenu(dropDownElementsList);
	}

	void setSelectedOption(int idx) {
		highlightSelectedElement(dropDown.getSelectedIndex(), false);
		highlightSelectedElement(idx, true);
		dropDown.setSelectedIndex(idx);
		onHighlighted.notifyListeners(idx < 0 ? null : dropDown.getSelectedId(idx));
	}

	private void setupDropDownMenu(List<AriaMenuItem> menuItems) {
		dropDown.clear();
		List<Integer> dividers = getGroupDividerIndices();
		for (int i = 0 ; i < menuItems.size() ; i++) {
			if (dividers != null && dividers.contains(i))  {
				dropDown.addDivider();
			}
			dropDown.addItem(menuItems.get(i));
		}
	}

	//APPS-7182
	private List<Integer> getGroupDividerIndices() {
		//if (property instanceof AbstractGroupedEnumeratedProperty) {
		//int[] groupDividerIndices = ((AbstractGroupedEnumeratedProperty<?>)
		//property).getGroupDividerIndices();
		//return IntStream.of(groupDividerIndices).boxed().collect(Collectors.toList());
		//}
		return null;
	}

	public int getSelectedIndex() {
		return dropDown.getSelectedIndex();
	}

	@Override
	public void setLabels() {
		setElements(items.get());
	}

	public ComponentDropDownPopup getPopup() {
		return dropDown;
	}

	public boolean isOpened() {
		return dropDown.isOpened();
	}

	/**
	 * CLose popup.
	 */
	public void closePopup() {
		dropDown.close();
		AriaHelper.setAriaExpanded(parent, false);
	}

	/**
	 * get currently selected text
	 * @return selected text
	 */
	public String getSelectedText() {
		if (getSelectedIndex() < 0 || getSelectedIndex() >= dropDownElementsList.size()) {
			return "";
		}
		return dropDownElementsList.get(getSelectedIndex()).getText();
	}

	/**
	 * show popup and position as combo-box
	 */
	public void showAsComboBox() {
		dropDown.positionAtBottomAnchor();
	}

	/**
	 * shop popup and position as dropdown
	 * @param isFullWidth - is dropdown should have full width
	 */
	public void showAsDropDown(boolean isFullWidth) {
		dropDown.setAutoFocus(true);
		dropDown.positionAtBottomAnchor();
		if (isFullWidth) {
			dropDown.setWidthInPx(parent.asWidget().getElement().getClientWidth());
		}
	}

	/**
	 * Add a change handler.
	 * @param changeHandler change handler
	 */
	public void addChangeHandler(Runnable changeHandler) {
		this.changeHandlers.add(changeHandler);
	}

	/**
	 * reset dropdown to property value
	 */
	public void resetFromModel(Dropdown propertyDropDown) {
		Integer index = propertyDropDown.getSelectedItemIndex();
		if (index == null) {
			index = 0;
		}
		setSelectedOption(index);
	}

	/**
	 * on text input from user
	 */
	public void onInputChange(String input) {
		setSelectedOption(possibleSelectedIndex(input));
		for (Runnable handler: changeHandlers) {
			handler.run();
		}
	}

	/**
	 * @param input text field input
	 * @return index of input in {@link DropDownComboBoxController#items}, if contains it,
	 * -1 otherwise
	 */
	public int possibleSelectedIndex(String input) {
		if (input != null) {
			List<String> items = this.items.get();
			if (items != null) {
				for (int i = 0; i < items.size(); i++) {
					if (items.get(i).equals(input)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * @param popupID popup DOM ID
	 */
	public void setPopupID(String popupID) {
		dropDown.setPopupID(popupID);
	}

	@Override
	public void handleUpArrow() {
		moveSelection(getSelectedIndex() == -1 ? 0 : -1);
	}

	private void moveSelection(int increment) {
		if (!isOpened()) {
			showAsComboBox();
		}
		dropDown.forceKeyboardFocus(true);
		int size = dropDownElementsList.size();
		setSelectedOption((dropDown.getSelectedIndex() + size + increment) % size);
	}

	@Override
	public void handleDownArrow() {
		moveSelection(1);
	}

	/**
	 * @param inputElement element to which this should return focus on close
	 */
	public void setFocusAnchor(Element inputElement) {
		dropDown.setFocusAnchor(inputElement);
	}

	/**
	 * Add listener for changed highlighting.
	 * @param listener listener
	 */
	public void addHighlightingListener(MulticastEvent.Listener<String> listener) {
		onHighlighted.addListener(listener);
	}
}
