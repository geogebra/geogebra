package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.List;

import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.ContextMenuTV;
import org.geogebra.web.full.gui.toolbarpanel.TVRowData;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.StickyTable;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import elemental2.dom.NodeList;
import jsinterop.base.Js;

/**
 * Sticky table of values.
 *
 * @author laszlo
 *
 */
public class StickyValuesTable extends StickyTable<TVRowData> implements TableValuesListener {

	private static final int CONTEXT_MENU_OFFSET = 4; // distance from three-dot button
	private static final int LINE_HEIGHT = 56;
	protected final TableValuesModel tableModel;
	protected final TableValuesView view;
	private final AppW app;
	private final HeaderCell headerCell = new HeaderCell();
	private boolean transitioning;
	private ContextMenuTV contextMenu;
	private final TableEditor editor;

	public MathKeyboardListener getKeybaordListener() {
		return editor.getKeyboardListener();
	}

	private static class HeaderCell {
		private final String value;

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
		 * @return cell HTML markup
		 *
		 */
		SafeHtmlHeader getHtmlHeader(String content) {
			String stringHtmlContent = value.replace("%s", content);
			return new SafeHtmlHeader(makeCell(stringHtmlContent));
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
		tableModel.registerListener(this);
		editor = new TableEditor(this, app);
		reset();
		addHeadClickHandler((row, column, evt) -> {
			Element el = Js.uncheckedCast(evt.target);
			if (el != null && (el.hasClassName("MyToggleButton") || el.getParentNode() != null
					&& el.getParentElement().hasClassName("MyToggleButton"))) {
				onHeaderClick(el, column);
			}
			return false;
		});
		addBodyPointerDownHandler((row, column, evt) -> {
			if (tableModel.getRowCount() == 0) {
				try {
					view.setValues(0, 10, 1);
				} catch (InvalidValuesException e) {
					e.printStackTrace();
				}
			}
			if (row < tableModel.getRowCount()
					&& column < tableModel.getColumnCount()) {
				if (isColumnEditable(column)) {
					editor.startEditing(row, column, evt);
					return true;
				}
			} else if (column == tableModel.getColumnCount()) {
				// do nothing now, start editing empty column in follow up ticket
			} else if (row == tableModel.getRowCount()) {
				// do nothing now, start editing empty row in follow up ticket
			}
			return false;
		});
	}

	private boolean isColumnEditable(int column) {
		return view.getEvaluatable(column) instanceof GeoList;
	}

	private void onHeaderClick(Element source, int column) {
		this.contextMenu = new ContextMenuTV(app, view, view.getGeoAt(column), column);
		contextMenu.show(source.getAbsoluteLeft(), source.getAbsoluteTop()
						+ source.getClientHeight() + CONTEXT_MENU_OFFSET);
	}

	@Override
	protected void addCells() {
		for (int column = 0; column < tableModel.getColumnCount(); column++) {
			addColumn(column);
		}
		addEmptyColumn();
		addEmptyColumn();
	}

	private void addEmptyColumn() {
		Column<TVRowData, SafeHtml> col = new DataTableSafeHtmlColumn(-1);

		getTable().addColumn(col, new SafeHtmlHeader(makeCell("")));
	}

	@Override
	protected void addColumn() {
		addColumn(tableModel.getColumnCount() - 1);
	}

	private void addColumn(int column) {
		Column<TVRowData, ?> colValue = getColumnValue(column);
		getTable().addColumn(colValue, getHeaderFor(column));
		if (isColumnEditable(column)) {
			getTable().addColumnStyleName(column, "editableColumn");
		}
	}

	private Header<SafeHtml> getHeaderFor(int columnIndex) {
		String content = tableModel.getHeaderAt(columnIndex);
		return headerCell.getHtmlHeader(content);
	}

	@Override
	protected void fillValues(List<TVRowData> rows) {
		rows.clear();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			rows.add(new TVRowData(row, tableModel));
		}
		rows.add(new TVRowData(tableModel.getRowCount(), tableModel));
		rows.add(new TVRowData(tableModel.getRowCount(), tableModel));
	}

	/**
	 * Makes a cell as SafeHtml.
	 *
	 * @param content
	 *            of the cell.
	 * @return SafeHtml of the cell.
	 */
	static SafeHtml makeCell(String content) {
		return () -> "<div class=\"content\">" + content + "</div>";
	}

	private Column<TVRowData, SafeHtml> getColumnValue(final int col) {
		return new DataTableSafeHtmlColumn(col);
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
		NodeList<elemental2.dom.Element> elems = getColumnElements(column);
		Element header = getHeaderElement(column);

		if (elems == null || elems.getLength() == 0 || header == null) {
			decreaseColumnNumber();
			return;
		}
		transitioning = true;
		header.getParentElement().addClassName("deleteCol");
		Dom.addEventListener(header, "transitionend", e -> onDeleteColumn());
		app.invokeLater(() -> header.addClassName("delete"));

		for (int i = 0; i < elems.getLength(); i++) {
			elemental2.dom.Element e = elems.getAt(i);
			e.classList.add("deleteCol");
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
		if (contextMenu != null) {
			contextMenu.hide(); // hide context menu on resize
			contextMenu = null;
		}
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

		Element headerElement = getHeaderElement(view.getColumn(geo));
		if (headerElement != null) {
			setHorizontalScrollPosition(headerElement.getParentElement().getAbsoluteLeft()
					- getAbsoluteLeft());
		}
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
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
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
	 * Scroll so that cell at given offset is visible
	 * @param pos offset top of the cell
	 */
	public void scrollIntoView(final int pos) {
		if (pos - getScroller().getVerticalScrollPosition() < LINE_HEIGHT) {
			getScroller().setVerticalScrollPosition(pos - LINE_HEIGHT);
		} else if (pos - getScroller().getVerticalScrollPosition()
				> getScroller().getOffsetHeight() - LINE_HEIGHT) {
			getScroller().setVerticalScrollPosition(pos
					- getScroller().getOffsetHeight() + LINE_HEIGHT);
		}
	}

	private class DataTableSafeHtmlColumn extends Column<TVRowData, SafeHtml> {

		private final int col;

		public DataTableSafeHtmlColumn(int col) {
			super(new SafeHtmlCell());
			this.col = col;
		}

		@Override
		public SafeHtml getValue(TVRowData object) {
			String valStr = col < 0 ? "" : object.getValue(col);
			return makeCell(valStr);
		}

		@Override
		public String getCellStyleNames(Cell.Context context, TVRowData object) {
			return super.getCellStyleNames(context, object)
					+ (col < 0 || isColumnEditable(col) ? " editableCell" : "");
		}
	}
}
