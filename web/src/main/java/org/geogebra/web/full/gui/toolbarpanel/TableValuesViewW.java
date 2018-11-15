package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesDimensions;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.TableUtils;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * HTML representation of the Table of Values View.
 *
 * @author laszlo
 *
 */
public class TableValuesViewW extends TableValuesView implements SetLabels {

	private static final int HEADER_HEIGHT = 48;

	/** Template to create a cell */
	static final CellTemplates TEMPLATES =
			GWT.create(CellTemplates.class);
	private CellTable<RowData> headerTable;
	private CellTable<RowData> table;
	private FlowPanel main;
	private Label emptyLabel;
	private Label emptyInfo;
	private FlowPanel emptyPanel;
	private AppW app;
	private ScrollPanel scrollPanel;
	private List<RowData> rows = new ArrayList<>();
	private NoDragImage moreImg;

	private class ColumnDelete implements Runnable {
		Runnable cb = null;
		private int column = -1;
		private Element elem;

		ColumnDelete(int column, Element elem, Runnable cb) {
			this.column = column;
			this.elem = elem;
			this.cb = cb;
		}

		@Override
		public void run() {
			onDeleteColumn(column, elem, cb);
		}
	}

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

	private class RowData {
		private int row;

		public RowData(int row) {
			this.setRow(row);
		}

		/**
		 * @return the column header.
		 */
		public String getHeader() {
			return getTableValuesModel().getHeaderAt(1);
		}

		/**
		 *
		 * @param col
		 *            the column
		 * @return the cell value
		 */
		public String getValue(int col) {
			if (getRow() < getTableValuesModel().getRowCount()
					&& col < getTableValuesModel().getColumnCount()) {
				return getTableValuesModel().getCellAt(getRow(), col);
			}
			return "";
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}
	}

	/**
	 * @param app1
	 *            {@link AppW}.
	 */
	public TableValuesViewW(AppW app1) {
		super(app1.getKernel());
		this.app = app1;
		createGUI();
	}

	private AppW getApp() {
		return app;
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
	void doSyncHeaderSizes() {
		NodeList<Element> tableRows = table.getElement().getElementsByTagName("tbody").getItem(0)
				.getElementsByTagName("tr");
		if (tableRows.getLength() == 0) {
			return;
		}

		NodeList<Element> firstRow = tableRows.getItem(0).getElementsByTagName("td");

		for (int i = 0; i < table.getColumnCount(); i++) {
			int w = firstRow.getItem(i).getOffsetWidth();
			headerTable.setColumnWidth(i, w + "px");
		}

		headerTable.getElement().getStyle().setWidth(table.getOffsetWidth(), Unit.PX);
	}

	private void createGUI() {
		main = new FlowPanel();
		FlowPanel tvPanel = new FlowPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("tvScrollPanel");
		headerTable = new CellTable<>();

		table = new CellTable<>();

		// Building table
		addHeader(tvPanel);
		table.addStyleName("tvTable");
		TableUtils.clear(table);
		addValuesForTable(table);
		tableInit();
		syncHeaderSizes();

		scrollPanel.setWidget(table);

		tvPanel.add(scrollPanel);
		tvPanel.addStyleName("tvPanel");
		main.add(tvPanel);
	}

	private void addHeader(FlowPanel tvPanel) {
		headerTable = new CellTable<>();
		TableUtils.clear(headerTable);
		addColumnsForTable(headerTable);
		headerTable.addStyleName("tvHeader");
		headerTable.addStyleName("tvTable");
		addHeaderClickHandler();

		final ScrollPanel holderPanel = new ScrollPanel();
		final FlowPanel innerHolderPanel = new FlowPanel();
		innerHolderPanel.add(headerTable);
		holderPanel.add(innerHolderPanel);
		holderPanel.addStyleName("tvHeaderHolderPanel");

		tvPanel.add(holderPanel);

		OuterPanel outerScrollPanel = new OuterPanel(); // used for horizontal
												// scrolling
		outerScrollPanel.addStyleName("outerScrollPanel");
		outerScrollPanel.add(tvPanel);

		scrollPanel.addScrollHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				syncScrollPosition(holderPanel, innerHolderPanel);
			}
		});
	}

	/**
	 * Sync scroll position of the header and the values table.
	 * 
	 * @param panel
	 *            Header holder panel.
	 * @param innerPanel
	 *            Header inner panel.
	 */
	void syncScrollPosition(ScrollPanel panel, FlowPanel innerPanel) {
		int scrollPosition = scrollPanel.getHorizontalScrollPosition();
		if (innerPanel.getOffsetWidth() < scrollPanel.getOffsetWidth() + scrollPosition) {
			innerPanel.setWidth((scrollPanel.getOffsetWidth() + scrollPosition) + "px");
		}
		panel.setHorizontalScrollPosition(scrollPosition);
	}

	private Widget getMain() {
		createGUI();
		return main;
	}

	private void buildData() {
		rows.clear();
		for (int row = 0; row < getTableValuesModel().getRowCount(); row++) {
			rows.add(new RowData(row));
		}
	}

	private void tableInit() {
		buildData();
		table.setRowCount(rows.size());
		table.setVisibleRange(0, rows.size());
		table.setRowData(0, rows);
		table.setVisible(true);
	}

	/**
	 *
	 * @return the main widget of the view.
	 */
	public Widget getWidget() {
		return isEmpty() ? getEmptyPanel() : getMain();
	}

	private Widget getEmptyPanel() {
		if (emptyPanel == null) {
			buildEmptyPanel();
		}
		return emptyPanel;
	}

	private void buildEmptyPanel() {
		this.emptyPanel = new FlowPanel();
		this.emptyPanel.addStyleName("emptyTablePanel");
		NoDragImage emptyImage = new NoDragImage(
				MaterialDesignResources.INSTANCE.toolbar_table_view_black(),
				56);
		emptyImage.getElement().setAttribute("role", "decoration");
		emptyImage.addStyleName("emptyTableImage");
		FlowPanel emptyImageWrap = new FlowPanel();
		emptyImageWrap.add(emptyImage);
		this.emptyLabel = new Label();
		this.emptyLabel.addStyleName("emptyTableLabel");
		this.emptyInfo = new Label();
		this.emptyInfo.addStyleName("emptyTableInfo");
		emptyImageWrap.addStyleName("emptyTableImageWrap");
		emptyPanel.add(emptyImageWrap);
		emptyPanel.add(emptyLabel);
		emptyPanel.add(emptyInfo);
	}

	@Override
	public void setLabels() {
		if (emptyPanel != null) {
			emptyLabel.setText(app.getLocalization().getMenu("TableValuesEmptyTitle"));
			emptyInfo.setText(app.getLocalization().getMenu("TableValuesEmptyDescription"));
		}
	}

	private static Column<RowData, SafeHtml> getColumnName() {
		Column<RowData, SafeHtml> nameColumn = new Column<RowData, SafeHtml>(
				new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getHeader());
			}
		};
		return nameColumn;
	}

	private static Column<RowData, SafeHtml> getColumnValue(final int col,
			final TableValuesDimensions dimensions) {
		Column<RowData, SafeHtml> column = new Column<RowData, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				String valStr = object.getValue(col);
				boolean empty = "".equals(valStr);
				SafeHtml value = SafeHtmlUtils.fromSafeConstant(valStr);
				int width = empty ? 0 : getColumnWidth(dimensions, col);
				int height = empty ? 0 : dimensions.getRowHeight(object.getRow());
				SafeHtml cell = TEMPLATES.cell(value, "tvValueCell",
						width,
						height);

				return cell;
			}
		};
		return column;
	}

	private NoDragImage getMoreImage() {
		if (moreImg == null) {
			moreImg = new NoDragImage(MaterialDesignResources.INSTANCE.more_vert_black(), 24);
		}
		return moreImg;
	}

	private void addHeaderClickHandler() {
		ClickHandler popupMenuClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());
				if (el != null && el.getParentNode() != null && el
						.getParentElement().hasClassName("MyToggleButton")) {
					Node buttonParent = el.getParentNode().getParentNode();
					toggleButtonClick(buttonParent, el);
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
					new ContextMenuTV(getApp(), i > 0 ? getGeoAt(i - 1) : null,
							i - 1).show(
									new GPoint(el.getAbsoluteLeft(),
											el.getAbsoluteTop() - 8));
				}
			}
		}
	}

	private SafeHtml getHeaderHtml(final int col) {
		FlowPanel p = new FlowPanel();
		p.add(new Label(getTableValuesModel().getHeaderAt(col)));
		MyToggleButtonW btn = new MyToggleButtonW(getMoreImage());
		p.add(btn);

		SafeHtml html = SafeHtmlUtils.fromTrustedString(p.getElement().getInnerHTML());
		TableValuesDimensions dimensions = getTableValuesDimensions();
		return TEMPLATES.cell(html, "tvHeaderCell",
				getColumnWidth(dimensions, col),
				dimensions.getHeaderHeight());
	}

	/**
	 * Gives the preferred width of a column.
	 * 
	 * @param dimensions
	 *            The column sizes
	 * @param column
	 *            particular column index.
	 * @return the calculated width of the column.
	 */
	static int getColumnWidth(TableValuesDimensions dimensions, int column) {
		return Math.max(dimensions.getColumnWidth(column), dimensions.getHeaderWidth(column));
	}

	private void addColumnsForTable(CellTable<RowData> tb) {
		TableValuesModel m = getTableValuesModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			Column<RowData, ?> col = getColumnName();
			if (col != null) {
				tb.addColumn(col, getHeaderHtml(i));
			}
		}
	}

	private void addValuesForTable(CellTable<RowData> tb) {
		TableValuesModel m = getTableValuesModel();
		for (int column = 0; column < m.getColumnCount(); column++) {
			Column<RowData, ?> col = getColumnValue(column, getTableValuesDimensions());
			tb.addColumn(col);
		}
	}

	/**
	 * Sets height of the view.
	 *
	 * @param height
	 *            to set.
	 */
	public void setHeight(int height) {
		scrollPanel.getElement().getStyle().setHeight(height - HEADER_HEIGHT,
				Unit.PX);
	}

	/**
	 * @author .
	 *
	 */
	public interface CellTemplates extends SafeHtmlTemplates {
		/**
		 * @param message
		 *            of the cell.
		 * @param style
		 *            of the cell.
		 * @param width
		 *            of the cell.
		 * @param height
		 *            of the cell.
		 * @return HTML representation of the cell content.
		 */
		@SafeHtmlTemplates.Template("<div style=\"width:{2}px;height:{3}px;line-height:{3}px;\""
				+ "class=\"{1}\">{0}</div>")
		SafeHtml cell(SafeHtml message, String style, int width, int height);
	}

	private static NodeList<Element> getColumnElements(int column) {
		return Dom.querySelectorAll(".tvTable tr td:nth-child(" + (column + 1) + ") .tvValueCell");
	}

	private static Element getHeaderElement(int column) {
		NodeList<Element> list = Dom
				.querySelectorAll(".tvTable tr th:nth-child(" + (column + 1) + ") .tvHeaderCell");
		return list != null ? list.getItem(0) : null;
	}

	/**
	 * Deletes the specified column from the view.
	 * 
	 * @param column
	 *            column to delete.
	 * @param cb
	 *            to run on transition end.
	 */
	public void deleteColumn(int column, Runnable cb) {
		NodeList<Element> elems = getColumnElements(column);
		Element header = getHeaderElement(column);

		if (elems == null || elems.getLength() == 0 || header == null) {
			return;
		}

		int tableWidth = table.getOffsetWidth() - header.getOffsetWidth();


		header.addClassName("delete");
		CSSEvents.runOnTransition(new ColumnDelete(column, header, cb), header, "delete");

		for (int i = 0; i < elems.getLength(); i++) {
			Element e = elems.getItem(i);
			e.addClassName("delete");
		}
		headerTable.getElement().getStyle().setWidth(tableWidth, Unit.PX);
	}

	/**
	 * Runs on column delete.
	 * 
	 * @param column
	 *            the deleted column number.
	 * 
	 * @param header
	 *            The table header HTML element
	 * 
	 * @param cb
	 *            custom callback to run on column delete.
	 */
	void onDeleteColumn(int column, Element header, Runnable cb) {
		if (isEmpty()) {
			if (cb != null) {
				cb.run();
			}
		} else {
			header.getParentElement().removeFromParent();
		}
	}

	@Override
	protected TableValuesViewDimensions newTableValuesViewDimensions(GFontRenderContext context) {
		return new TableValuesViewDimensionsW(getTableValuesModel(), AwtFactory.getPrototype(),
				context);
	}

}
