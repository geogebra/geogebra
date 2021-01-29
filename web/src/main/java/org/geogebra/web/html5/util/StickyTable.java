package org.geogebra.web.html5.util;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public abstract class StickyTable<T> extends FlowPanel implements ClickHandler {
	private CellTable<T> cellTable;
	private ListDataProvider<T> dataProvider;
	private ScrollPanel scroller;

	/**
	 * Create a sticky table.
	 */
	public StickyTable() {
		cellTable = new CellTable<>();

		cellTable.addStyleName("values");
		cellTable.addHandler(this, ClickEvent.getType());

		scroller = new ScrollPanel();
		scroller.addStyleName("scroller");
		scroller.addStyleName("customScrollbar");

		scroller.setWidget(cellTable);
		add(scroller);
		addStyleName("mainScrollPanel");
		cellTable.setVisible(true);
		createDataProvider();
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

	@Override
	public void onClick(ClickEvent event) {
		Element el = Element.as(event.getNativeEvent().getEventTarget());
		if (el != null && el.getParentNode() != null
				&& el.getParentElement().hasClassName("MyToggleButton")) {
			Node buttonParent = el.getParentNode().getParentNode();
			toggleButtonClick(buttonParent.getParentNode(), el);
		}
	}

	/**
	 * @param buttonParent parent of the button
	 * @param el           element for positioning
	 */
	protected void toggleButtonClick(Node buttonParent, Element el) {
		if (buttonParent != null && buttonParent.getParentNode() != null
				&& buttonParent.getParentNode().getParentElement() != null) {
			// parent tag with the header children
			Element parent = buttonParent.getParentNode().getParentElement();
			// header column which was clicked on
			Node currHeaderCell = buttonParent.getParentNode();
			// get list of header cells
			NodeList<Node> headerNodes = parent.getChildNodes();
			for (int i = 0; i < headerNodes.getLength(); i++) {
				Node node = headerNodes.getItem(i);
				// check if header cell is the one it was clicked on
				if (node.equals(currHeaderCell)) {
					onHeaderClick(el, i);
				}
			}
		}
	}

	/**
	 * Called when header is clicked.
	 *
	 * @param source
	 *            of the click.
	 * @param column
	 *            header index clicked on
	 */
	protected abstract void onHeaderClick(Element source, int column);

	/**
	 * @param column
	 *            to get
	 * @return the list of the specified value column elements (without the header).
	 */
	public static NodeList<Element> getColumnElements(int column) {
		// gives the (column+1)th element of each row of the value table
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
