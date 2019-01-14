package org.geogebra.web.html5.util;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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
	private CellTable<T> headerTable;
	private CellTable<T> valuesTable;
	private ListDataProvider<T> dataProvider;
	private ScrollPanel valueScroller;

	/**
	 * @author laszlo
	 *
	 */
	public class OuterPanel extends ScrollPanel {

		@Override
		public void onResize() {
			super.onResize();
			syncHeaderSizes();
		}
	}

	/**
	 * Constructor.
	 */
	public StickyTable() {
		headerTable = new CellTable<>();
		valuesTable = new CellTable<>();

		headerTable.addStyleName("header");
		valuesTable.addStyleName("values");

		valueScroller = new ScrollPanel();
		valueScroller.addStyleName("valueScroller");

		headerTable.addHandler(this, ClickEvent.getType());

		valueScroller.setWidget(valuesTable);
		createStickyHeader();
		add(valueScroller);
		addStyleName("mainScrollPanel");
		valuesTable.setVisible(true);
		createDataProvider();
	}

	/**
	 * Sync header sizes with content column widths
	 */
	protected abstract void syncHeaderSizes();

	/**
	 * Add initial cells here.
	 *
	 */
	protected abstract void addCells();

	private void createStickyHeader() {
		final ScrollPanel headerScroller = new ScrollPanel();
		final FlowPanel headerMain = new FlowPanel();
		headerMain.add(headerTable);
		headerScroller.add(headerMain);
		headerScroller.addStyleName("headerScroller");
		clear();
		add(headerScroller);

		OuterPanel outerScrollPanel = new OuterPanel(); // used for horizontal
		// scrolling
		outerScrollPanel.addStyleName("outerScrollPanel");
		outerScrollPanel.add(this);
		valueScroller.addScrollHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				syncScrollPosition(headerScroller, headerMain);
			}
		});
	}

	/**
	 * Sync scroll position of the header and the values table.
	 *
	 * @param headerScroller
	 *            scroll panel for the header.
	 * @param headerMain
	 *            Header main panel that contains the table.
	 */
	void syncScrollPosition(ScrollPanel headerScroller, FlowPanel headerMain) {
		int scrollPosition = valueScroller.getHorizontalScrollPosition();
		if (headerMain.getOffsetWidth() < valueScroller.getOffsetWidth() + scrollPosition) {
			headerMain.setWidth((valueScroller.getOffsetWidth() + scrollPosition) + "px");
		}
		headerScroller.setHorizontalScrollPosition(scrollPosition);
	}

	private void createDataProvider() {
		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(valuesTable);

	}

	/**
	 * Adds a new column
	 */
	protected abstract void addColumn();

	/**
	 * Removes the column.
	 *
	 * @param index
	 *            index
	 */
	protected void removeColumn(int index) {
		headerTable.removeColumn(index);

		// In AbstractCellTable model each column remembers its index
		// so deleting last column and let dataProvider do the rest we need.
		valuesTable.removeColumn(valuesTable.getColumnCount() - 1);
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
	 * Called when user removes column.
	 */
	public void onColumnRemoved(int column) {
		removeColumn(column);
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
		valueScroller.getElement().getStyle().setHeight(height, Unit.PX);
	}

	/**
	 * Sets width of the header.
	 *
	 * @param width
	 *            to set.
	 */
	protected void setHeaderWidth(int width) {
		headerTable.getElement().getStyle().setWidth(width, Unit.PX);
	}

	/**
	 *
	 * @return the values table.
	 */
	protected CellTable<T> getValuesTable() {
		return valuesTable;
	}

	/**
	 *
	 * @return the header table.
	 */
	protected CellTable<T> getHeaderTable() {
		return headerTable;
	}

	/**
	 * Scroll to given position horizontally.
	 *
	 * @param pos
	 *            to scroll.
	 */
	public void setHorizontalScrollPosition(final int pos) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getValueScroller().setHorizontalScrollPosition(pos);
			}
		});
	}

	/**
	 *
	 * @return the scroll panel of the values.
	 */
	ScrollPanel getValueScroller() {
		return valueScroller;
	}

	/**
	 * Refreshes table data.
	 */
	public void refresh() {
		refreshData();
		refreshVisibleRange();
		syncHeaderSizes();
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
		valuesTable.setVisibleRange(0, dataProvider.getList().size());
	}

	protected void reset() {
		TableUtils.clear(headerTable);
		TableUtils.clear(valuesTable);
		addCells();
		fillValues(dataProvider.getList());
		refreshVisibleRange();
	}
}
