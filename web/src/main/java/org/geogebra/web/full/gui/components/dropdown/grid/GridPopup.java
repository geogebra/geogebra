package org.geogebra.web.full.gui.components.dropdown.grid;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.HTMLTable;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class GridPopup extends GPopupPanel {
	private final int columns;
	private static final int UNSELECTED_INDEX = -1;
	private Grid view;
	private final List<GridItem> items = new ArrayList<>();
	private int selectedIndex = UNSELECTED_INDEX;
	private final Runnable parentUpdateCB;
	private GridDropdownListener listener;
	private Grid grid;

	/**
	 * Listens for changes in GridDrodown.
	 */
	public interface GridDropdownListener {

		/**
		 * Called when an item has been selected in the dropdown.
		 *
		 * @param index index of the item
		 */
		void itemSelected(int index);
	}

	/**
	 * Data holder describing the state of a grid item.
	 */
	static class GridItem {
		ImageResource resource;
		String titleTransKey;

		/**
		 * Creates a new GridItem.
		 *
		 * @param resource image
		 * @param titleTransKey title translation key
		 */
		GridItem(ImageResource resource, String titleTransKey) {
			this.resource = resource;
			this.titleTransKey = titleTransKey;
		}
	}

	/**
	 * @param app - application
	 * @param parentUpdateCB - callback to update parent
	 * @param columns - columns
	 */
	public GridPopup(AppW app, Runnable parentUpdateCB, int columns) {
		super(true, true, app.getAppletFrame(), app);
		addStyleName("materialPopupPanel");
		this.columns = columns;
		this.parentUpdateCB = parentUpdateCB;
	}

	/**
	 * @param anchor - anchor button
	 */
	public void showGridPopup(StandardButton anchor) {
		updateGui();
		showRelativeTo(anchor);
	}

	/**
	 * fill grid and update ui
	 */
	public void updateGui() {
		super.clear();
		view = createGridView();
		view.addClickHandler(this::handleGridClick);

		add(view);
	}

	public Grid getView() {
		return view;
	}

	private Grid createGridView() {
		grid = new Grid();
		grid.setStyleName("grid");
		grid.resize(getGridRowCount(), columns);
		addGridItems();
		return grid;
	}

	private int getGridRowCount() {
		return (int) Math.ceil(items.size() / (double) columns);
	}

	private void addGridItems() {
		for (int i = 0; i < items.size(); i++) {
			GridItem item = items.get(i);
			Widget cell = createGridItemView(item);
			int row = i / columns;
			int column = i % columns;
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
		image.setResource(item.resource);
		panel.add(image);

		Label title = new Label();
		title.addStyleName("title");
		title.setText(app.getLocalization().getMenu(item.titleTransKey));
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
		int itemIndex = row * columns + column;

		setSelectedIndex(itemIndex);
		updateVisualSelection(row, column);
		if (listener != null) {
			listener.itemSelected(itemIndex);
		}

		hide();
	}

	private void updateVisualSelection(int row, int column) {
		if (grid != null) {
			HTMLTable.CellFormatter formatter = grid.getCellFormatter();
			for (int i = 0; i < grid.getRowCount(); i++) {
				for (int j = 0; j < grid.getColumnCount(); j++) {
					formatter.removeStyleName(i, j, "cell-active");
				}
			}
			formatter.addStyleName(row, column, "cell-active");
		}
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
		if (parentUpdateCB != null) {
			parentUpdateCB.run();
		}
	}

	/**
	 * Adds an item to the dropdown.
	 *
	 * @param titleTransKey title of the cell
	 * @param resource image to display
	 */
	public void addItem(String titleTransKey, ImageResource resource) {
		items.add(new GridItem(resource, titleTransKey));
		if (items.size() == 1) {
			setSelectedIndex(0);
		}
	}

	@Override
	public void clear() {
		items.clear();
		setSelectedIndex(UNSELECTED_INDEX);
	}

	public String getSelectedItemText() {
		return items.get(selectedIndex).titleTransKey;
	}

	public void setListener(GridDropdownListener listener) {
		this.listener = listener;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}
}
