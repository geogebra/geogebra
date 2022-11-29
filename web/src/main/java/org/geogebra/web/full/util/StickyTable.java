package org.geogebra.web.full.util;

import java.util.List;

import org.geogebra.web.html5.gui.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;

import elemental2.dom.EventListener;
import jsinterop.base.Js;

/**
 * Table with sticky header.
 *
 * @param <T>
 *            Type of table cells.
 *
 */
public abstract class StickyTable<T> extends FlowPanel {
	private final CellTableWithBody cellTable;
	private ListDataProvider<T> dataProvider;
	private final ScrollPanel scroller;

	/**
	 * Create a sticky table.
	 */
	public StickyTable() {
		cellTable = new CellTableWithBody();

		cellTable.addStyleName("values");

		scroller = new ScrollPanel();
		scroller.addStyleName("scroller");
		CustomScrollbar.apply(scroller);
		scroller.addStyleName("customScrollbar");
		FlowPanel wrapper = new FlowPanel();
		wrapper.add(cellTable);
		scroller.setWidget(wrapper);
		add(scroller);
		addStyleName("mainScrollPanel");
		cellTable.setVisible(true);
		createDataProvider();
	}

	protected void addBodyPointerDownHandler(CellClickHandler clickHandler) {
		Dom.addEventListener(cellTable.getTableBodyElement(), "pointerdown",
				getDomEventHandler(clickHandler));
	}

	protected void addHeadClickHandler(CellClickHandler clickHandler) {
		Dom.addEventListener(cellTable.getTableHeadElement(), "click",
				getDomEventHandler(clickHandler));
	}

	protected void addMouseOverHandler(CellClickHandler clickHandler) {
		Dom.addEventListener(cellTable.getTableBodyElement(), "mouseover",
				getDomEventHandler(clickHandler));
	}

	protected void addMouseOutHandler(CellClickHandler clickHandler) {
		Dom.addEventListener(cellTable.getTableBodyElement(), "mouseout",
				getDomEventHandler(clickHandler));
	}

	private EventListener getDomEventHandler(CellClickHandler eventHandler) {
		return event -> {
			Element element = Js.uncheckedCast(event.target);
			Element cell = getTargetCell(element);
			if (cell != null) {
				int col = getParentIndex(cell);
				int row = getParentIndex(cell.getParentElement());
				if (eventHandler.onClick(row, col, event)) {
					event.preventDefault();
				}
			}
		};
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
	 * @param data
	 *            to fill with.
	 */
	protected abstract void fillValues(List<T> data);

	protected int getParentIndex(Node currHeaderCell) {
		Element parent = currHeaderCell.getParentElement();
		if (parent != null) {
			NodeList<Node> headerNodes = parent.getChildNodes();
			for (int i = 0; i < headerNodes.getLength(); i++) {
				Node node = headerNodes.getItem(i);
				// check if header cell is the one it was clicked on
				if (node.equals(currHeaderCell)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * @param column to get
	 * @return the header element.
	 */
	public Element getHeaderElement(int column) {
		return Dom.querySelectorForElement(cellTable.getTableHeadElement(),
				".values tr th:nth-child(" + (column + 1) + ") .content");
	}

	public Element getHeaderElementByClassName(String className) {
		return Dom.querySelectorForElement(cellTable.getTableHeadElement(), className);
	}

	public Element getTableElementByClassName(String className) {
		return Dom.querySelectorForElement(cellTable.getTableBodyElement(), className);
	}

	/**
	 * @param column
	 *            to get
	 * @return the list of the specified value column elements (without the header).
	 */
	public elemental2.dom.NodeList<elemental2.dom.Element> getColumnElements(int column) {
		elemental2.dom.Element body = Js.uncheckedCast(cellTable.getTableBodyElement());
		// gives the columnth element of each row of the value table. (nth-child is 1 indexed)
		return body.querySelectorAll(".values tr td:nth-child(" + (column + 1) + ")");
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
	public CellTable<T> getTable() {
		return cellTable;
	}

	/**
	 * Scroll to given position horizontally.
	 *
	 * @param pos
	 *            to scroll.
	 */
	public void setHorizontalScrollPosition(final int pos) {
		Scheduler.get().scheduleDeferred(() -> scroller.setHorizontalScrollPosition(pos));
	}

	/**
	 * @return the scroll panel of the values.
	 */
	protected ScrollPanel getScroller() {
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

	public Panel getTableWrapper() {
		return (Panel) scroller.getWidget();
	}

	public Element getCell(int row, int column) {
		return cellTable.getTableBodyElement().getChild(row).getChild(column).cast();
	}

	public void flush() {
		cellTable.flush();
	}

	/**
	 * @param col column
	 * @param row row
	 * @return whether a cell at given coordinates exists and is not hidden by shadow
	 */
	public boolean hasCell(int col, int row) {
		return col >= 0 && col < cellTable.getColumnCount() - 1
				&& row >= 0 && row < cellTable.getRowCount() - 1;
	}

	private class CellTableWithBody extends CellTable<T> {

		@Override
		public TableSectionElement getTableBodyElement() {
			return super.getTableBodyElement();
		}

		@Override
		public TableSectionElement getTableHeadElement() {
			return super.getTableHeadElement();
		}
	}

	/**
	 * Sets height of the values to be able to scroll.
	 * @param height - to set.
	 */
	public void setHeight(int height) {
		setBodyHeight(height);
	}
}
