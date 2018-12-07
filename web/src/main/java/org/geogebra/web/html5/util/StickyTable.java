package org.geogebra.web.html5.util;

import java.util.List;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
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

	/** Template to create a cell */
	static final CellTemplates TEMPLATES =
			GWT.create(CellTemplates.class);
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
			syncHeaderSizes();
		}
	}

	/**
	 * Constructor.
	 */
	public StickyTable() {
		createGUI();
	}

	/**
	 * Sync header sizes with content column widths
	 */
	protected void syncHeaderSizes() {

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {
				doSyncHeaderSizes();
			}
		});
	}

	/**
	 * Sync header sizes to value table.
	 */
	protected void doSyncHeaderSizes() {
		NodeList<Element> tableRows = valuesTable.getElement().getElementsByTagName("tbody")
				.getItem(0)
				.getElementsByTagName("tr");
		if (tableRows.getLength() == 0) {
			return;
		}

		NodeList<Element> firstRow = tableRows.getItem(0).getElementsByTagName("td");

		for (int i = 0; i < valuesTable.getColumnCount(); i++) {
			int w = firstRow.getItem(i).getOffsetWidth();
			headerTable.setColumnWidth(i, w + "px");
		}

		headerTable.getElement().getStyle().setWidth(valuesTable.getOffsetWidth(), Unit.PX);
	}

	private void createGUI() {
		headerTable = new CellTable<>();
		headerTable = new CellTable<>();
		headerTable.addStyleName("header");

		valuesTable = new CellTable<>();
		valuesTable.addStyleName("values");

		valueScroller = new ScrollPanel();
		valueScroller.addStyleName("valueScroller");

		addHeaderClickHandler();
	}

	/**
	 * build and fill table with data.
	 */
	public void build() {
		TableUtils.clear(valuesTable);
		TableUtils.clear(headerTable);
		createDataProvider();
		addCells();
		valueScroller.setWidget(valuesTable);
		createStickyHeader();
		add(valueScroller);
		addStyleName("mainScrollPanel");
		syncHeaderSizes();
		valuesTable.setVisibleRange(0, dataProvider.getList().size());
		valuesTable.setVisible(true);
		refresh();
	}

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

	/**
	 * 
	 * @return data of all rows
	 */
	protected List<T> getRows() {
		return dataProvider.getList();
	}

	private void createDataProvider() {
		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(valuesTable);
		fillValues(dataProvider.getList());
	}

	/**
	 * Adds a new column
	 * 
	 * @param column
	 */
	protected abstract void addColumn();

	/**
	 * Removes the column.
	 * 
	 * @param index
	 */
	protected void removeColumn(int index) {
		headerTable.removeColumn(index);
	}

	/**
	 * Called when user adds a column.
	 */
	public void onColumnAdded() {
		addColumn();
		if (dataProvider == null) {
			return;
		}
		fillValues(dataProvider.getList());
		refresh();
	}
	/**
	 * 
	 * @param data
	 *            to fill with.
	 */
	protected abstract void fillValues(List<T> data);

	/**
	 * Only call this from constructor
	 */
	private void addHeaderClickHandler() {
		ClickHandler popupMenuClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());
				if (el != null && el.getParentNode() != null && el
						.getParentElement().hasClassName("MyToggleButton")) {
					Node buttonParent = el.getParentNode().getParentNode();
					toggleButtonClick(buttonParent.getParentNode(), el);
				}
			}
		};
		headerTable.addHandler(popupMenuClickHandler, ClickEvent.getType());
	}

	/**
	 * @param buttonParent
	 *            parent of the button
	 * @param el
	 *            element for positioning
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
	 * @author Balazs
	 *
	 */
	public interface CellTemplates extends SafeHtmlTemplates {
		/**
		 * @param message
		 *            of the cell.
		 * @param width
		 *            of the cell.
		 * @param height
		 *            of the cell.
		 * @return HTML representation of the cell content.
		 */
		@SafeHtmlTemplates.Template("<div style=\"width:{1}px;height:{2}px;line-height:{2}px;\""
				+ "class=\"cell\"><div class=\"content\">{0}</div></div>")
		SafeHtml cell(SafeHtml message, int width, int height);
	}

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
	 * @param column
	 *            to get
	 * @return the header element.
	 */
	public static Element getHeaderElement(int column) {
		// gives the (column+1)th element of the header row.
		NodeList<Element> list = Dom
				.querySelectorAll(".header tr th:nth-child(" + (column + 1) + ") .cell");
		return list != null ? list.getItem(0) : null;
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
	 * 
	 * @return if refresh was successful.
	 */
	public boolean refresh() {
		if (dataProvider == null) {
			return false;
		}
		dataProvider.refresh();
		syncHeaderSizes();
		return true;
	}
}
