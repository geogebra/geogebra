package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Dropdown material design component
 *
 * @author kriszta
 */

public class ComponentDropDown extends FlowPanel {

	private static final int ITEM_HEIGHT = 32;
	private Label titleLabel;
	private Label selectedOptionLabel;
	private List<AriaMenuItem> dropDownElementsList;
	private DropDownSelectionCallback selectionCallback;
	private ComponentDropDownPopup dropDown;

	/**
	 *
	 * @param app
	 *            AppW
	 */
	public ComponentDropDown(AppW app) {
		buildGui(app);
	}

	private void buildGui(AppW app) {
		setStyleName("dropDownSelectorContainer");

		FlowPanel contentPanel = new FlowPanel();
		contentPanel.setStyleName("dropDownSelector");

		createTitleLabel();
		createSelectedOptionLabel();
		createDropDownMenu(app);

		contentPanel.add(titleLabel);
		contentPanel.add(selectedOptionLabel);

		add(contentPanel);
	}

	private void createTitleLabel() {
		titleLabel = new Label();
		titleLabel.setStyleName("titleLabel");
	}

	private void createSelectedOptionLabel() {
		selectedOptionLabel = new Label();
		selectedOptionLabel.setStyleName("selectedOptionLabel");
	}

	private void createDropDownMenu(final AppW app) {
		dropDown = new ComponentDropDownPopup(app, ITEM_HEIGHT, selectedOptionLabel, null);
		dropDown.addAutoHidePartner(getElement());
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggleExpanded();
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
			dropDown.positionAsDropDown();
		}
	}

	private void setupDropDownMenu(List<AriaMenuItem> menuItems) {
		for (AriaMenuItem menuItem : menuItems) {
			dropDown.addItem(menuItem);
		}
	}

	/**
	 * set the title of the dropdown in the preview view
	 *
	 * @param title
	 *            the localized title which is displayed above the selected
	 *            option
	 */
	public void setTitleText(String title) {
		titleLabel.setText(title);
	}

	/**
	 * set the selected option in the preview view
	 *
	 * @param selected
	 *            index of the selected item from the dropdown list
	 */
	public void setSelected(int selected) {
		highlightSelectedElement(dropDown.getSelectedIndex(), selected);
		dropDown.setSelectedIndex(selected);
		selectedOptionLabel.setText(
				dropDownElementsList.get(selected).getElement().getInnerText());
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
			AriaMenuItem item = new AriaMenuItem(dropDownList.get(i), null,
					() -> {
						setSelected(currentIndex);
						fireSelected(currentIndex);
					});

			item.setStyleName("dropDownElement");
			dropDownElementsList.add(item);
		}
		setupDropDownMenu(dropDownElementsList);
	}

	/**
	 * Notify callback
	 *
	 * @param currentIndex
	 *            selected index
	 */
	void fireSelected(int currentIndex) {
		if (selectionCallback != null) {
			selectionCallback.onSelectionChanged(currentIndex);
		}
	}

	/**
	 * set itemSelected callback
	 *
	 * @param callback
	 *            which will be called after an item was selected from the
	 *            dropdown
	 *
	 */
	public void setDropDownSelectionCallback(
			DropDownSelectionCallback callback) {
		selectionCallback = callback;
	}

	/**
	 * Selection callback
	 */
	public interface DropDownSelectionCallback {
		/**
		 * @param index
		 *            selected index
		 */
		void onSelectionChanged(int index);
	}
}
