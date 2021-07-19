package org.geogebra.web.html5.util;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Table with sticky header.
 *
 * @param <T>
 *            Type of table cells.
 *
 */
public abstract class StickyTable<T> extends FlowPanel {
	private CellTable<T> cellTable;
	private ListDataProvider<T> dataProvider;
	private ScrollPanel scroller;

	/**
	 * Create a sticky table.
	 */
	public StickyTable() {
		cellTable = new CellTable<>();

		cellTable.addStyleName("values");

		scroller = new ScrollPanel();
		scroller.addStyleName("scroller");
		CustomScrollbar.apply(scroller);
		scroller.addStyleName("customScrollbar");

		scroller.setWidget(cellTable);
		add(scroller);
		addStyleName("mainScrollPanel");
		cellTable.setVisible(true);
		createDataProvider();
	}

	protected void addCellClickHandler(CellClickHandler clickHandler) {
		cellTable.addDomHandler(event -> {
			Element element = event.getNativeEvent().getEventTarget().cast();
			Element cell = getTargetCell(element);
			if (cell != null) {
				int col = getParentIndex(cell);
				int row = getParentIndex(cell.getParentElement());
				clickHandler.onClick(row, col, element);
			}
		}, ClickEvent.getType());
	}

	protected Element getTargetCell(Element start) {
		Element cell = start;
		while (cell != null && !cell.hasTagName("TD") && !cell.hasTagName("TH")) {
			cell = cell.getParentElement();
		}
		return cell;
	}

	/**
	 * Add initial cells here.
	 *
	 */
	protected abstract void addCells();

	private void createDataProvider() {
		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(cellTable);

	}

	/**
	 * Adds a new column
	 */
	protected abstract void addColumn();

	/**
	 * Decreases the number of columns by removing the last column.
	 */
	protected void decreaseColumnNumber() {

		// In AbstractCellTable model each column remembers its index
		// so deleting last column and let dataProvider do the rest we need.
		cellTable.removeColumn(cellTable.getColumnCount() - 1);
		reset();
	}

	/**
	 * Called when user adds a column.
	 */
	public void onColumnAdded() {
		addColumn();

		// Safest way to keep integrity at load.
		// Note that CellTable is highly optimized so no heavy overload.
		reset();
	}

	/**
	 * @param data
	 *            to fill with.
	 */
	protected abstract void fillValues(List<T> data);

	protected int getParentIndex(Node currHeaderCell) {
		Element parent = currHeaderCell.getParentElement();
		NodeList<Node> headerNodes = parent.getChildNodes();
		for (int i = 0; i < headerNodes.getLength(); i++) {
			Node node = headerNodes.getItem(i);
			// check if header cell is the one it was clicked on
			if (node.equals(currHeaderCell)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param column to get
	 * @return the header element.
	 */
	public static Element getHeaderElement(int column) {
		// gives the columnth element of the header row. (nth-child is 1 indexed)
		NodeList<Element> list = Dom.querySelectorAll(
				".values tr th:nth-child(" + (column + 1) + ") .cell");
		return list != null ? list.getItem(0) : null;
	}

	/**
	 * @param column
	 *            to get
	 * @return the list of the specified value column elements (without the header).
	 */
	public static NodeList<Element> getColumnElements(int column) {
		// gives the columnth element of each row of the value table. (nth-child is 1 indexed)
		return Dom.querySelectorAll(".values tr td:nth-child(" + (column + 1) + ") .cell");
	}

	/**
	 * Sets height of the body.
	 *
	 * @param height
	 *            to set.
	 */
	protected void setBodyHeight(int height) {
		scroller.getElement().getStyle().setHeight(height, Unit.PX);
	}

	/**
	 *
	 * @return the values table.
	 */
	protected CellTable<T> getTable() {
		return cellTable;
	}

	/**
	 * Scroll to given position horizontally.
	 *
	 * @param pos
	 *            to scroll.
	 */
	public void setHorizontalScrollPosition(final int pos) {
		Scheduler.get().scheduleDeferred(() -> getScroller().setHorizontalScrollPosition(pos));
	}

	/**
	 *
	 * @return the scroll panel of the values.
	 */
	ScrollPanel getScroller() {
		return scroller;
	}

	/**
	 * Refreshes table data.
	 */
	public void refresh() {
		refreshData();
		refreshVisibleRange();
	}

	private void refreshData() {
		if (dataProvider == null) {
			return;
		}
		fillValues(dataProvider.getList());
		dataProvider.refresh();
	}

	private void refreshVisibleRange() {
		if (dataProvider == null) {
			return;
		}
		cellTable.setVisibleRange(0, dataProvider.getList().size());
	}

	/**
	 * Rebuild the UI
	 */
	protected void reset() {
		TableUtils.clear(cellTable);
		addCells();
		fillValues(dataProvider.getList());
		refreshVisibleRange();
	}
}
