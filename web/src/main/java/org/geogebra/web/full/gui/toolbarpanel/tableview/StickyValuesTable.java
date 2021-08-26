package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.List;

import org.geogebra.common.gui.view.table.TableValuesDimensions;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.ContextMenuTV;
import org.geogebra.web.full.gui.toolbarpanel.TVRowData;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.StickyTable;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Sticky table of values.
 *
 * @author laszlo
 *
 */
public class StickyValuesTable extends StickyTable<TVRowData> implements TableValuesListener {

	// margin to align value cells to header - 3dot empty place
	private static final int VALUE_RIGHT_MARGIN = 36;
	private static final int X_LEFT_PADDING = 16;
	private static final int MIN_COLUMN_WIDTH = 72;

	private TableValuesModel tableModel;
	private TableValuesDimensions dimensions;
	private TableValuesView view;
	private AppW app;
	private HeaderCell headerCell = new HeaderCell();
	private boolean transitioning;

	private static class HeaderCell {
		private String value;

		/**
		 * Header
		 */
		HeaderCell() {
			FlowPanel p = new FlowPanel();
			p.add(new Label("%s"));
			MyToggleButtonW btn = new MyToggleButtonW(
					new NoDragImage(MaterialDesignResources.INSTANCE.more_vert_black(), 24));
			TestHarness.setAttr(btn, "btn_tvHeader3dot");
			p.add(btn);
			value = p.getElement().getInnerHTML();
		}

		/**
		 * @param content
		 *            cell text content
		 * @param width
		 *            width in pixels
		 * @param height
		 *            height in pixels
		 * @return cell HTML markup
		 *
		 */
		SafeHtmlHeader getHtmlHeader(String content, int width, int height) {
			String stringHtmlContent = value.replace("%s", content);
			SafeHtml safeHtmlContent = SafeHtmlUtils.fromTrustedString(stringHtmlContent);
			return new SafeHtmlHeader(makeCell(safeHtmlContent, width, height));
		}
	}

	/**
	 * Class to wrap callback after column delete.
	 *
	 * @author laszlo.
	 *
	 */
	private class ColumnDelete implements Runnable {

		protected ColumnDelete() {
			// non-synthetic constructor
		}

		@Override
		public void run() {
			onDeleteColumn();
		}
	}

	/**
	 * @param app  {@link AppW}
	 * @param view to feed table with data.
	 */
	public StickyValuesTable(AppW app, TableValuesView view) {
		this.app = app;
		this.view = view;
		this.tableModel = view.getTableValuesModel();
		this.dimensions = view.getTableValuesDimensions();
		tableModel.registerListener(this);
		reset();
	}

	@Override
	protected void onHeaderClick(Element source, int column) {
		new ContextMenuTV(app, column > 0 ? view.getGeoAt(column - 1) : null, column - 1)
				.show(source.getAbsoluteLeft(), source.getAbsoluteTop() - 8);
	}

	@Override
	protected void addCells() {
		for (int column = 0; column < tableModel.getColumnCount(); column++) {
			addColumn(column);
		}
	}

	@Override
	protected void addColumn() {
		addColumn(tableModel.getColumnCount() - 1);
	}

	private void addColumn(int column) {
		Column<TVRowData, ?> colValue = getColumnValue(column, dimensions);
		getTable().addColumn(colValue, getHeaderFor(column));
	}

	private Header<SafeHtml> getHeaderFor(int columnIndex) {
		String content = tableModel.getHeaderAt(columnIndex);
		int width = getColumnWidth(dimensions, columnIndex);
		int height = dimensions.getHeaderHeight();
		return headerCell.getHtmlHeader(content, width, height);
	}

	@Override
	protected void fillValues(List<TVRowData> rows) {
		rows.clear();
		if (tableModel.getColumnCount() < 2) {
			// quit now, otherwise 5 empty rows will be initialized
			return;
		}
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			rows.add(new TVRowData(row, tableModel));
		}
	}

	/**
	 * Makes a cell as SafeHtml.
	 *
	 * @param content
	 *            of the cell.
	 * @param width
	 *            of the cell.
	 * @param height
	 *            of the cell.
	 * @return SafeHtml of the cell.
	 */
	static SafeHtml makeCell(SafeHtml content, int width, int height) {
		SafeStylesBuilder sb = new SafeStylesBuilder();
		sb.width(width, Unit.PX).height(height, Unit.PX).trustedNameAndValue("line-height", height,
				Unit.PX);
		return  () -> "<div style=\"" + sb.toSafeStyles().asString() + "\" class=\"cell\">"
				+ "<div class=\"content\">" + content.asString() + "</div></div>";
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
		int w = Math.max(dimensions.getColumnWidth(column), dimensions.getHeaderWidth(column))
				+ VALUE_RIGHT_MARGIN;
		if (column == 0) {
			w += X_LEFT_PADDING;
		}
		return Math.max(w, MIN_COLUMN_WIDTH + X_LEFT_PADDING);
	}

	private static Column<TVRowData, SafeHtml> getColumnValue(final int col,
			final TableValuesDimensions dimensions) {
		Column<TVRowData, SafeHtml> column = new Column<TVRowData, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(TVRowData object) {
				String valStr = object.getValue(col);
				boolean empty = "".equals(valStr);
				SafeHtml value = SafeHtmlUtils.fromSafeConstant(valStr);
				int width = empty ? 0 : getColumnWidth(dimensions, col);
				int height = empty ? 0 : dimensions.getRowHeight(object.getRow());
				return makeCell(value, width, height);
			}
		};
		return column;
	}

	/**
	 * Deletes the specified column from the table
	 *
	 * @param column
	 *            column to delete.
	 */
	public void deleteColumn(int column) {
		if (transitioning) {
			// multiple simultaneous deletions
			reset();
			return;
		}
		NodeList<Element> elems = getColumnElements(column);
		Element header = getHeaderElement(column);

		if (elems == null || elems.getLength() == 0 || header == null) {
			decreaseColumnNumber();
			return;
		}
		transitioning = true;

		header.addClassName("delete");

		CSSEvents.runOnTransition(new ColumnDelete(), header, "delete");

		for (int i = 0; i < elems.getLength(); i++) {
			Element e = elems.getItem(i);
			e.addClassName("delete");
		}
	}

	@Override
	protected void reset() {
		super.reset();
		transitioning = false;
	}

	/**
	 * Runs on column delete.
	 */
	void onDeleteColumn() {
		transitioning = false;
		if (!isLastColumnDeleted()) {
			decreaseColumnNumber();
		}
	}

	private boolean isLastColumnDeleted() {
		return view.getTableValuesModel().getColumnCount() == 0;
	}

	/**
	 * Sets height of the values to be able to scroll.
	 *
	 * @param height
	 *            to set.
	 */
	public void setHeight(int height) {
		setBodyHeight(height);
	}

	/**
	 *
	 * Scroll table view to the corresponding column of the geo.
	 *
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		if (geo == null) {
			return;
		}

		int pos = 0;
		int col = view.getColumn(geo);
		for (int i = 0; i < col; i++) {
			pos += getColumnWidth(dimensions, i);
		}
		setHorizontalScrollPosition(pos);
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model,
			GeoEvaluatable evaluatable, int column) {
		deleteColumn(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		//
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		onColumnAdded();
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		refresh();
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		reset();
	}

	/**
	 * @param column to get
	 * @return the header element.
	 */
	private static Element getHeaderElement(int column) {
		// gives the (column+1)th element of the header row.
		NodeList<Element> list = Dom.querySelectorAll(
				".values tr th:nth-child(" + (column + 1) + ") .cell");
		return list != null ? list.getItem(0) : null;
	}
}
