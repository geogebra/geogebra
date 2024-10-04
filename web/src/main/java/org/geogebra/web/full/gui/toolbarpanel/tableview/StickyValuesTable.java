package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.keyboard.TableValuesKeyboardNavigationController;
import org.geogebra.common.gui.view.table.keyboard.TableValuesKeyboardNavigationControllerDelegate;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.ContextMenuTV;
import org.geogebra.web.full.gui.toolbarpanel.DefineFunctionsDialogTV;
import org.geogebra.web.full.gui.toolbarpanel.TVRowData;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.cell.client.Cell;
import org.gwtproject.cell.client.SafeHtmlCell;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.user.cellview.client.Column;
import org.gwtproject.user.cellview.client.Header;
import org.gwtproject.user.cellview.client.SafeHtmlHeader;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import elemental2.dom.NodeList;
import jsinterop.base.Js;

/**
 * Sticky table of values.
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

	private int rowsChange = 0;
	private int columnsChange = 0;
	private int removedColumnByUser = -1;
	private boolean shadedColumns = true;
	DefineFunctionsDialogTV defFuncDialog;
	private TableValuesKeyboardNavigationController controller;
	GPoint lastEdit = null;

	public MathKeyboardListener getKeyboardListener() {
		return editor.getKeyboardListener();
	}

	private static class HeaderCell {
		private final String value;

		/**
		 * Header
		 */
		HeaderCell() {
			FlowPanel main = new FlowPanel();
			main.setStyleName("content");
			main.addStyleName(Shades.NEUTRAL_900.getFgColName());
			main.add(new Label("%s"));
			StandardButton menuButton = new StandardButton(MaterialDesignResources.INSTANCE
					.more_vert_black(), 24);
			TestHarness.setAttr(menuButton, "btn_tvHeader3dot");
			main.add(menuButton);
			value = main.getElement().getString();
		}

		/**
		 * @param content
		 *            cell text content
		 * @return cell HTML markup
		 *
		 */
		SafeHtmlHeader getHtmlHeader(String content) {
			String stringHtmlContent = value.replace("%s", content);
			return new SafeHtmlHeader(() -> stringHtmlContent);
		}
	}

	/**
	 * @param app  {@link AppW}
	 * @param view to feed table with data.
	 */
	public StickyValuesTable(AppW app, TableValuesView view) {
		getTable().addStyleName("shaded");
		this.app = app;
		this.view = view;
		this.tableModel = view.getTableValuesModel();
		tableModel.registerListener(this);
		editor = new TableEditor(this, app);
		controller = new TableValuesKeyboardNavigationController(
				(TableValues) app.getGuiManager().getTableValuesView(),
				new TableValuesKeyboardNavigationControllerDelegate() {
					@Override
					public void focusCell(int row, int column) {
						lastEdit = new GPoint(column, row);
						editor.startEditing(row, column, false);
					}

					@Override
					public void refocusCell(int row, int column) {
						editor.startEditing(row, column, true);
					}

					@Override
					public void unfocusCell(int row, int column, boolean isTransferringFocus) {
						editor.stopEditing();
					}

					@CheckForNull
					@Override
					public String getCellEditorContent(int row, int column) {
						return editor.getText();
					}

					@Override
					public void invalidCellContentDetected(int row, int column) {
						// not needed
					}
				});
		editor.controller = controller;
		reset();
		addHeadClickHandler((row, column, evt) -> {
			Element el = Js.uncheckedCast(evt.target);
			if (el != null && (el.hasClassName("button") || el.getParentNode() != null
					&& el.getParentElement().hasClassName("button"))) {
				onHeaderClick(el, column);
			}
			return false;
		});
		addBodyPointerDownHandler((row, column, evt) -> {
			if (row <= tableModel.getRowCount()
					&& column <= tableModel.getColumnCount()) {
				if (column == tableModel.getColumnCount() || isColumnEditable(column)) {
					controller.select(row, column);
					editor.adjustCursor(evt);
					return true;
				}
			}
			return false;
		});
		addMouseOverHandler((row, column, evt) -> {
			Element el = Js.uncheckedCast(evt.target);
			if (el != null && el.hasClassName("errorStyle")) {
				Label toast = new Label(app.getLocalization().getMenu("UseNumbersOnly"));
				toast.addStyleName("errorToast");
				toast.addStyleName(Shades.NEUTRAL_700.getName());
				toast.getElement().setId("errorToastID");
				toast.getElement().getStyle().setLeft(el.getAbsoluteRight() + 8, Unit.PX);
				toast.getElement().getStyle().setTop(el.getAbsoluteTop() - 66, Unit.PX);
				app.getAppletFrame().add(toast);
			}
			return false;
		});
		addMouseOutHandler((row, column, evt) -> {
			Element toast = DOM.getElementById("errorToastID");
			if (toast != null) {
				toast.removeFromParent();
			}
			return false;
		});
	}

	@Override
	public void openDefineFunctions() {
		if (defFuncDialog == null) {
			DialogData data = new DialogData("DefineFunctions", "Cancel", "OK");
			defFuncDialog = new DefineFunctionsDialogTV(app, data);
		}
		defFuncDialog.setLabels();
		defFuncDialog.show();
	}

	private boolean isColumnEditable(int column) {
		return view.getEvaluatable(column) instanceof GeoList;
	}

	public boolean columnNotEditable(int column) {
		return view.getEvaluatable(column) instanceof GeoFunctionable;
	}

	private void onHeaderClick(Element source, int column) {
		contextMenu = new ContextMenuTV(app, view,
				view.getEvaluatable(column).toGeoElement(), column);
		contextMenu.show(source, 0, source.getClientHeight() + CONTEXT_MENU_OFFSET);
	}

	@Override
	protected void addCells() {
		for (int column = 0; column < tableModel.getColumnCount(); column++) {
			addColumn(column);
		}
		if (shadedColumns) {
			addEmptyColumn(0);
			addEmptyColumn(1);
			if (columnsChange < 0) {
				addEmptyColumn(2);
			}
		}
	}

	private void addEmptyColumn(int position) {
		Column<TVRowData, SafeHtml> col = new DataTableSafeHtmlColumn(-1);
		TableCell cell = new TableCell("", false);
		SafeHtmlHeader header = new SafeHtmlHeader(cell.getHTML());
		if (columnsChange > 0 && position == 1) {
			header.setHeaderStyleNames("addColumnAut");
			resetAfterAnimationEnds("addColumnAut", true, false);
		} else if (position == 2) {
			header.setHeaderStyleNames("deleteColumnAut");
			resetAfterAnimationEnds("deleteColumnAut", true, true);
		}
		getTable().addColumn(col, header);
		if (rowsChange < 0) {
			resetAfterAnimationEnds("deleteRowAut", false, true);
		} else if (rowsChange > 0) {
			resetAfterAnimationEnds("addRowAuto", false, false);
		}
	}

	private void resetAfterAnimationEnds(String className, boolean column, boolean remove) {
		app.invokeLater(() -> {
			Element el;
			if (column) {
				el = getHeaderElementByClassName("." + className);
			} else {
				el = getTableElementByClassName("." + className);
			}
			if (remove) {
				Dom.addEventListener(el, "animationend", e -> resetAndRefreshEditor());
			} else {
				if (!el.hasAttribute("data-listeners")) {
					el.setAttribute("data-listeners", "true");
					Dom.addEventListener(el, "animationend",
							e -> removeAnimationStyleName(el, className));
				}
			}
			columnsChange = 0;
			rowsChange = 0;
		});
	}

	private void removeAnimationStyleName(Element el, String styleName) {
		el.removeClassName(styleName);
	}

	protected void addColumn() {
		addColumn(tableModel.getColumnCount() - 1);
	}

	/**
	 * Decreases the number of columns by removing the last column.
	 */
	protected void decreaseColumnNumber() {
		// In AbstractCellTable model each column remembers its index
		// so deleting last column and let dataProvider do the rest we need.
		getTable().removeColumn(getTable().getColumnCount() - 1);
		resetAndRefreshEditor();
	}

	private void resetAndRefreshEditor() {
		boolean wasAttached = editor.isAttached();
		reset();
		if (wasAttached && lastEdit != null) {
			refreshEditingState();
		}
	}

	private void refreshEditingState() {
		editor.stopEditing();
		if (isColumnFocusable(lastEdit.x) && (lastEdit.y <= tableModel.getRowCount())) {
			controller.select(lastEdit.y, lastEdit.x);
		} else {
			app.hideKeyboard();
		}
		lastEdit = null;
	}

	private boolean isColumnFocusable(int column) {
		return column == tableModel.getColumnCount() || tableModel.isColumnEditable(column);
	}

	private void addColumn(int column) {
		Column<TVRowData, ?> colValue = getColumnValue(column);
		getTable().addColumn(colValue, getHeaderFor(column));
		if (isColumnEditable(column)) {
			getTable().addColumnStyleName(column, "editableColumn");
		}
	}

	private Header<SafeHtml> getHeaderFor(int columnIndex) {
		String headerHTMLName = TableUtil.getHeaderHtml(tableModel, columnIndex);
		return headerCell.getHtmlHeader(headerHTMLName);
	}

	@Override
	protected void fillValues(List<TVRowData> rows) {
		rows.clear();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			rows.add(new TVRowData(row, tableModel));
		}

		rows.add(new TVRowData(tableModel.getRowCount(), tableModel));
		rows.add(new TVRowData(tableModel.getRowCount(), tableModel));

		if (rowsChange < 0) {
			for (int i = 0; i < Math.abs(rowsChange); i++) {
				rows.add(new TVRowData(tableModel.getRowCount(), tableModel));
			}
			getTable().setRowStyles((row, rowIndex) -> {
				if (rowIndex >= tableModel.getRowCount() + 2) {
					return "deleteRowAut";
				}
				return null;
			});
		} else if (rowsChange > 0) {
			getTable().setRowStyles((row, rowIndex) -> {
				if (rowsChange > 0 && rowIndex == tableModel.getRowCount() + 1) {
					return "addRowAuto";
				}
				return null;
			});
		}
	}

	private Column<TVRowData, SafeHtml> getColumnValue(final int col) {
		return new DataTableSafeHtmlColumn(col);
	}

	private void removeRowsBeforeReset() {
		NodeList<elemental2.dom.Element> elems = getColumnElements(removedColumnByUser);
		if (elems != null && elems.length > 0) {
			int rowsDeleted = elems.getLength() - 2 - tableModel.getRowCount();
			for (int i = 1; i <= Math.abs(rowsDeleted); i++) {
				elemental2.dom.Element e = elems.getAt(elems.getLength() - i);
				elemental2.dom.Element parent = e.parentElement;
				parent.classList.add("deleteRowAut");
			}
		}
		rowsChange = 0;
	}

	/**
	 * Deletes the specified column from the table
	 * @param column - column to delete.
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
	 * @param height - to set.
	 */
	@Override
	public void setHeight(int height) {
		setBodyHeight(height);
		if (contextMenu != null) {
			contextMenu.hide(); // hide context menu on resize
			contextMenu = null;
		}
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * @param geo - to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		if (geo == null) {
			return;
		}

		Element headerElement = getHeaderElement(view.getColumn(geo));
		if (headerElement != null) {
			Element headerCell = headerElement.getParentElement();
			int offset = headerCell.getAbsoluteLeft() - getTable().getAbsoluteLeft();
			int sx = headerCell.getOffsetWidth() + offset - getOffsetWidth();
			setHorizontalScrollPosition(Math.max(0, sx));
		}
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model,
			GeoEvaluatable evaluatable, int column) {
		if (column != tableModel.getColumnCount()) {
			deleteColumn(column);
			removedColumnByUser = column;
		} else {
			columnsChange = -1;
			resetAndRefreshEditor();
		}
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		reset();
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		reset();
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		rowsChange -= 1;
		if (transitioning) {
			removeRowsBeforeReset();
		} else {
			resetAndRefreshEditor();
		}
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		reset();
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		rowsChange = 1;
		reset();
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		columnsChange = 1;
		addColumn();

		// Safest way to keep integrity at load.
		// Note that CellTable is highly optimized so no heavy overload.
		reset();
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
	 * Scroll the least amount so that the given cell is fully visible. Won't scroll
	 * the view if it is not necessary.
	 * @param cell cell to scroll into view
	 */
	public void scrollIntoView(Element cell) {
		int top = cell.getOffsetTop();
		int headerHeight = LINE_HEIGHT;
		int verticalScrollPosition = getScroller().getVerticalScrollPosition();

		if (top - headerHeight < verticalScrollPosition) {
			getScroller().setVerticalScrollPosition(top - headerHeight);
		} else if (top - verticalScrollPosition > getScroller().getOffsetHeight() - headerHeight) {
			getScroller().setVerticalScrollPosition(
					top - getScroller().getOffsetHeight() + headerHeight);
		}

		int left = cell.getOffsetLeft();
		int cellWidth = cell.getOffsetWidth();
		int horizontalScrollPosition = getScroller().getHorizontalScrollPosition();
		int scrollerContentWidth = getScroller().getWidget().getOffsetWidth();

		if (left < horizontalScrollPosition) {
			getScroller().setHorizontalScrollPosition(left);
		} else if (left + cellWidth > horizontalScrollPosition + scrollerContentWidth) {
			getScroller().setHorizontalScrollPosition(left +  cellWidth - scrollerContentWidth);
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
			boolean hasError = col >= 0 && object.isCellErroneous(col);
			TableCell cell = new TableCell(valStr, hasError);
			return cell.getHTML();
		}

		@Override
		public String getCellStyleNames(Cell.Context context, TVRowData object) {
			return super.getCellStyleNames(context, object)
					+ (col < 0 || isColumnEditable(col) ? " editableCell" : "")
					+ (col >= 0 && object.isCellErroneous(col) ? " errorCell" : "")
					+ (col >= 0 && columnNotEditable(col) ? " notEditable" : "")
					+ (col < 0 ? " emptyColumn" : "")
					+ (col < 0 && columnsChange < 0 ? " deleteColumnAut" : "");
		}
	}

	public int getRowsChange() {
		return rowsChange;
	}

	public int getColumnsChange() {
		return columnsChange;
	}

	/**
	 * Disable shaded style.
	 */
	public void disableShadedColumns() {
		shadedColumns = false;
	}
}
