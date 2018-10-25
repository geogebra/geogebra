package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TableUtils;

import com.google.gwt.cell.client.SafeHtmlCell;
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
	private Label emptyLabel;
	private Label emptyInfo;
	private FlowPanel emptyPanel;
	private AppW app;
	private ScrollPanel scrollPanel;
	private List<RowData> rows = new ArrayList<>();
	
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
		 * @param column
		 *            the col
		 * @return the cell value
		 */
		public String getValue() {
			return getTableValuesModel().getCellAt(row, 1);
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

	private void createGUI() {
		main = new FlowPanel();
		headerTable = new CellTable<>();
		table = new CellTable<>();
		if (app.has(Feature.TABLE_VIEW_TEST_DATA)) {
			addTestData();
		}

		buildTable();
		// scrollPanel = new ScrollPanel(table);
		// main.add(scrollPanel);
	}

	private GeoFunction createFunction(String definition) {
		Kernel kernel = app.getKernel();
		AlgebraProcessor processor = kernel.getAlgebraProcessor();
		return processor.evaluateToFunction(definition, true);
	}

	private void addTestData() {
		GeoFunction sinx = createFunction("sin(x)");
		sinx.setLabel("f");
		GeoFunction cosx = createFunction("cos(x)");
		add(sinx);
		cosx.setLabel("g");
		add(cosx);
		setValues(-1, 1, 0.5);
		showColumn(sinx);
		showColumn(cosx);
	}

	private void buildTable() {
		TableUtils.clear(headerTable);
		addColumnsForTable(headerTable);
		main.add(headerTable);
		TableUtils.clear(table);
		addValuesForTable(table);
		tableInit();
		main.add(table);

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

	private void addValues() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @return the main widget of the view.
	 */
	public Widget getWidget() {
		return isEmpty() ? getEmptyPanel() : main;
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

	private static Column<RowData, SafeHtml> getColumnValue(final int idx) {
		Column<RowData, SafeHtml> column = new Column<RowData, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RowData object) {
				return SafeHtmlUtils.fromTrustedString(object.getValue());
			}

		};
		return column;
	}

	private void addColumnsForTable(CellTable<RowData> tb) {
		TableValuesModel m = getTableValuesModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			if (true) {
				Column<RowData, ?> col = getColumnName(i);
				if (col != null) {
					SafeHtmlBuilder sb = new SafeHtmlBuilder();
					sb.append(
							SafeHtmlUtils.fromSafeConstant("<div>" + m.getHeaderAt(i) + "</div>"));

					tb.addColumn(col, sb.toSafeHtml());
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

}
