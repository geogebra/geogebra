package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.impl.AbstractGroupedEnumeratedProperty;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Widget;

public class DropDownComboBoxController implements SetLabels {
	private final Widget parent;
	private ComponentDropDownPopup dropDown;
	private List<AriaMenuItem> dropDownElementsList;
	private final List<String> items;
	private final List<Runnable> changeHandlers = new ArrayList<>();
	private NamedEnumeratedProperty<?> property;

	/**
	 * popup controller for dropdown and combobox
	 * @param app - apps
	 * @param parent - dropdown or combobox
	 * @param items - list of items in popup
	 * @param onClose - handler to run on close
	 */
	public DropDownComboBoxController(final AppW app, Widget parent,
			List<String> items, Runnable onClose) {
		this.parent = parent;
		this.items = items;

		init(app, onClose);
	}

	private void init(AppW app, Runnable onClose) {
		createPopup(app, parent, onClose);
		setElements(items);
		setSelectedOption(-1);
	}

	private void createPopup(final AppW app, Widget parent, Runnable onClose) {
		dropDown = new ComponentDropDownPopup(app, 32, parent, onClose);
		dropDown.addAutoHidePartner(parent.getElement());
	}

	/**
	 * open/close dropdown
	 * @param isFullWidth - whether dropdown should have full width
	 */
	public void toggleAsDropDown(boolean isFullWidth) {
		if (isOpened()) {
			closePopup();
		} else {
			showAsDropDown(isFullWidth);
		}
		Dom.toggleClass(parent, "active", isOpened());

	}

	private void highlightSelectedElement(int index, boolean highlight) {
		if (index >= 0 && index < dropDownElementsList.size()) {
			dropDownElementsList.get(index)
					.setStyleName("selectedDropDownElement", highlight);
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
				if (property != null) {
					property.setIndex(currentIndex);
				}
				for (Runnable handler: changeHandlers) {
					handler.run();
				}
			});

			item.setStyleName("dropDownElement");
			dropDownElementsList.add(item);
		}
		setupDropDownMenu(dropDownElementsList);
	}

	void setSelectedOption(int idx) {
		highlightSelectedElement(dropDown.getSelectedIndex(), false);
		highlightSelectedElement(idx, true);
		dropDown.setSelectedIndex(idx);
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

	private List<Integer> getGroupDividerIndices() {
		if (property instanceof AbstractGroupedEnumeratedProperty) {
			List<Integer> listOfDividers = IntStream.of(((AbstractGroupedEnumeratedProperty <?>)
					property).getGroupDividerIndices()).boxed().collect(Collectors.toList());
			return listOfDividers;
		}
		return null;
	}

	public int getSelectedIndex() {
		return dropDown.getSelectedIndex();
	}

	@Override
	public void setLabels() {
		if (property != null) {
			setElements(Arrays.asList(property.getValueNames()));
		} else {
			setElements(items);
		}
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

	public void setProperty(NamedEnumeratedProperty<?> property) {
		this.property = property;
	}

	/**
	 * reset dropdown to property value
	 */
	public void resetFromModel() {
		if (property.getIndex() > -1) {
			setSelectedOption(property.getIndex());
		}
	}

	/**
	 * on text input from user
	 */
	public void onInputChange() {
		setSelectedOption(-1);
		for (Runnable handler: changeHandlers) {
			handler.run();
		}
	}
}
