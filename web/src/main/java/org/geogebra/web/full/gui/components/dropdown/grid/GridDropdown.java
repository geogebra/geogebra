package org.geogebra.web.full.gui.components.dropdown.grid;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.ImageResourceConverter;
import org.gwtproject.resources.client.ImageResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dropdown which opens a grid of images and text.
 */
public class GridDropdown extends SimplePanel implements ClickHandler {

	private static final int COLUMNS = 3;
	private static final int UNSELECTED_INDEX = -1;

	private GridDropdownListener listener;

	private AppW app;

	private Button button;
	private Grid view;
	private GPopupPanel popup;

	private List<GridItem> items = new ArrayList<>();
	private int selectedIndex = UNSELECTED_INDEX;

	/**
	 * Listens for changes in GridDrodown.
	 */
	public interface GridDropdownListener {

		/**
		 * Called when an item has been selected in the dropdown.
		 *
		 * @param dropdown the dropdown
		 * @param index index of the item
		 */
		void itemSelected(GridDropdown dropdown, int index);
	}

	/**
	 * Data holder describing the state of a grid item.
	 */
	static class GridItem {
		ImageResource resource;
		String title;

		/**
		 * Creates a new GridItem.
		 *
		 * @param resource image
		 * @param title title
		 */
		GridItem(ImageResource resource, String title) {
			this.resource = resource;
			this.title = title;
		}
	}

	/**
	 * Create a new GridDropdown.
	 *
	 * @param app app
	 */
	public GridDropdown(AppW app) {
		this.app = app;
		createButton();
	}

	private void createButton() {
		button = new Button();
		button.addStyleName("dropdownButton");
		button.addClickHandler(this);
		add(button);
	}

	/**
	 * Enable or disable the dropdown button.
	 *
	 * @param enabled true to enable
	 */
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	/**
	 * Set the selected index of the dropdown.
	 *
	 * @param index index of the dropdown.
	 */
	public void setSelectedIndex(int index) {
		if (index < UNSELECTED_INDEX || index >= items.size()) {
			throw new IndexOutOfBoundsException("Index out of bounds");
		}
		selectedIndex = index;
		updateSelectedIndex();
	}

	/**
	 * Returns the selected index of the dropdown.
	 *
	 * @return the selected index
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	private void updateSelectedIndex() {
		String title = "";
		if (selectedIndex >= 0) {
			title = items.get(selectedIndex).title;
		}
		button.setText(title);
	}

	/**
	 * Adds an item to the dropdown.
	 *
	 * @param title title of the cell
	 * @param resource image to display
	 */
	public void addItem(String title, ImageResource resource) {
		items.add(new GridItem(resource, title));
		if (items.size() == 1) {
			setSelectedIndex(0);
		}
	}

	@Override
	public void clear() {
		items.clear();
		setSelectedIndex(UNSELECTED_INDEX);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == button) {
			showGridPopup();
		} else {
			handleGridClick(event);
		}

	}

	private void showGridPopup() {
		view = createGridView();
		view.addClickHandler(this);
		popup = new GPopupPanel(true, true, app.getPanel(), app);
		popup.addStyleName("materialPopupPanel");
		popup.add(view);
		popup.showRelativeTo(button);
	}

	private Grid createGridView() {
		Grid grid = new Grid();
		grid.setStyleName("grid");
		grid.resize(getGridRowCount(), COLUMNS);
		addGridItems(grid);
		return grid;
	}

	private int getGridRowCount() {
		return (int) Math.ceil(items.size() / (double) COLUMNS);
	}

	private void addGridItems(Grid grid) {
		for (int i = 0; i < items.size(); i++) {
			GridItem item = items.get(i);
			Widget cell = createGridItemView(item);
			int row = i / COLUMNS;
			int column = i % COLUMNS;
			grid.setWidget(row, column, cell);

			HTMLTable.CellFormatter formatter = grid.getCellFormatter();
			formatter.addStyleName(row, column, "cell");
			if (i == selectedIndex) {
				formatter.addStyleName(row, column, "cell-active");
			}
		}
	}

	private Widget createGridItemView(GridItem item) {
		FlowPanel panel = new FlowPanel();

		Image image = new Image();
		image.addStyleName("image");
		image.setResource(ImageResourceConverter.convertToOldImageResource(item.resource));
		panel.add(image);

		Label title = new Label();
		title.addStyleName("title");
		title.setText(item.title);
		panel.add(title);

		return panel;
	}

	private void handleGridClick(ClickEvent event) {
		HTMLTable.Cell cell = view.getCellForEvent(event);
		if (cell != null) {
			handleCellClicked(cell);
		}
	}

	private void handleCellClicked(HTMLTable.Cell cell) {
		int column = cell.getCellIndex();
		int row = cell.getRowIndex();
		int itemIndex = row * COLUMNS + column;
		setSelectedIndex(itemIndex);
		if (listener != null) {
			listener.itemSelected(this, itemIndex);
		}
		popup.hide();
	}

	/**
	 * Set the listener of this GridDropdown.
	 *
	 * @param listener the listener
	 */
	public void setListener(GridDropdownListener listener) {
		this.listener = listener;
	}
}
