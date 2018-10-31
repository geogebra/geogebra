package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TableUtils;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
	private CellTable<RowData> headerTable;
	private CellTable<RowData> table;
	private FlowPanel main;
	private FlowPanel tvPanel;
	private Label emptyLabel;
	private Label emptyInfo;
	private FlowPanel emptyPanel;
	private AppW app;
	private ScrollPanel scrollPanel;
	private List<RowData> rows = new ArrayList<>();
	private ScrollPanel holderPanel;
	private OuterPanel outerScrollPanel;
	private NoDragImage moreImg;
	
	/**
	 * @author laszlo
	 *
	 */
	public class OuterPanel extends ScrollPanel {

		@Override
		public void onResize() {
			setHeaderSizes();
		}
	}

	private class RowData {
		private int row;

		public RowData(int row) {
			this.row = row;
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
			return getTableValuesModel().getCellAt(row, col);
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

	/**
	 * Sync header sizes with content column widths
	 */
	protected void setHeaderSizes() {

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {
				NodeList<Element> tableRows = table.getElement().getElementsByTagName("tbody")
						.getItem(0).getElementsByTagName("tr");
				if (tableRows.getLength() == 0) {
					return;
				}

				NodeList<Element> firstRow = tableRows.getItem(0).getElementsByTagName("td");

				for (int i = 0; i < table.getColumnCount(); i++) {
					int w = firstRow.getItem(i).getOffsetWidth();
					headerTable.setColumnWidth(i, w + "px");
				}

				int tableWidth = table.getOffsetWidth();
				headerTable.getElement().getStyle().setWidth(tableWidth, Unit.PX);

			}

		});
	}

	private void createGUI() {
		main = new FlowPanel();
		tvPanel = new FlowPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("tvScrollPanel");
		headerTable = new CellTable<>();
		table = new CellTable<>();
		buildTable();
		scrollPanel.setWidget(table);

		tvPanel.add(scrollPanel);
		tvPanel.addStyleName("tvPanel");
		main.add(tvPanel);
	}

	private void addHeader() {
		headerTable = new CellTable<>();
		TableUtils.clear(headerTable);
		addColumnsForTable(headerTable);
		headerTable.addStyleName("tvHeader");
		headerTable.addStyleName("tvTable");

		holderPanel = new ScrollPanel();
		final FlowPanel innerHolderPanel = new FlowPanel();
		innerHolderPanel.add(headerTable);
		holderPanel.add(innerHolderPanel);
		holderPanel.addStyleName("tvHeaderHolderPanel");

		tvPanel.add(holderPanel);

		outerScrollPanel = new OuterPanel(); // used for horizontal
												// scrolling
		outerScrollPanel.addStyleName("outerScrollPanel");
		outerScrollPanel.add(tvPanel);
		table.addStyleName("hiddenheader");

		scrollPanel.addScrollHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				int scrollPosition = scrollPanel.getHorizontalScrollPosition();
				if (innerHolderPanel.getOffsetWidth() < scrollPanel.getOffsetWidth()
						+ scrollPosition) {
					innerHolderPanel
							.setWidth((scrollPanel.getOffsetWidth() + scrollPosition) + "px");
				}
				holderPanel.setHorizontalScrollPosition(scrollPosition);
			}
		});

	}

	@Override
	public void add(GeoElement geo) {
		if (!(geo instanceof Evaluatable)) {
			return;
		}

		if (hasColumn((Evaluatable) geo)) {
			return;
		}

		super.add(geo);
		showColumn((Evaluatable) geo);
	}

	private Widget getMain() {
		createGUI();
		return main;
	}
	private void buildTable() {
		addHeader();
		table.addStyleName("tvTable");
		TableUtils.clear(table);
		addValuesForTable(table);
		tableInit();
		setHeaderSizes();
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

	private static Column<RowData, SafeHtml> getColumnName(final int idx) {
		Column<RowData, SafeHtml> nameColumn = new Column<RowData, SafeHtml>(
				new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getHeader());
			}

		};
		return nameColumn;
	}

	private static Column<RowData, SafeHtml> getColumnValue(final int col) {
		Column<RowData, SafeHtml> column = new Column<RowData, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.append(SafeHtmlUtils.fromTrustedString("<div>"));
				sb.append(SafeHtmlUtils.fromSafeConstant(object.getValue(col)));
				sb.append(SafeHtmlUtils.fromTrustedString("</div>"));
				return sb.toSafeHtml();
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

	private SafeHtml getHeaderHtml(final int column) {
		FlowPanel p = new FlowPanel();
		p.add(new Label(getTableValuesModel().getHeaderAt(column)));
		if (column != 0) {
			MyToggleButtonW btn = new MyToggleButtonW(getMoreImage());
			p.add(btn);
		}

		return SafeHtmlUtils.fromTrustedString("<div>" + p.getElement().getInnerHTML() + "</div>");
	}

	private void addColumnsForTable(CellTable<RowData> tb) {
		TableValuesModel m = getTableValuesModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			if (true) {
				Column<RowData, ?> col = getColumnName(i);
				if (col != null) {
					tb.addColumn(col, getHeaderHtml(i));
				}
			}
		}
	}

	private void addValuesForTable(CellTable<RowData> tb) {
		TableValuesModel m = getTableValuesModel();
		for (int column = 0; column < m.getColumnCount(); column++) {
			Column<RowData, ?> col = getColumnValue(column);
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
		scrollPanel.getElement().getStyle().setHeight(height - headerTable.getOffsetHeight(),
				Unit.PX);
	}

}
