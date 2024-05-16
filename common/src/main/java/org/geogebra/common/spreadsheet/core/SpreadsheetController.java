package org.geogebra.common.spreadsheet.core;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @apiNote This type is not designed to be thread-safe.
 */
public final class SpreadsheetController {

	private final ContextMenuItems contextMenuItems;
	private final SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	final private TabularData<?> tabularData;

	//@NonOwning
	private SpreadsheetControlsDelegate controlsDelegate;
	private final TableLayout layout;

	private final SpreadsheetStyle style;
	private DragState dragAction;
	private Rectangle viewport = new Rectangle(0, 0, 0, 0);
	private @CheckForNull ViewportAdjuster viewportAdjuster;
	private @CheckForNull UndoProvider undoProvider;

	/**
	 * @param tabularData underlying data for the spreadsheet
	 */
	public SpreadsheetController(TabularData<?> tabularData) {
		this.tabularData = tabularData;
		resetDragAction();
		style = new SpreadsheetStyle(tabularData.getFormat());
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), TableLayout.DEFAUL_CELL_HEIGHT,
				TableLayout.DEFAULT_CELL_WIDTH);
		contextMenuItems = new ContextMenuItems(this, selectionController, getCopyPasteCut());
	}

	TableLayout getLayout() {
		return layout;
	}

	SpreadsheetStyle getStyle() {
		return style;
	}

	/**
	 * Visible for tests
	 * @return {@link ContextMenuItems}
	 */
	ContextMenuItems getContextMenuItems() {
		return contextMenuItems;
	}

	// - TabularData

	public Object contentAt(int row, int column) {
		return tabularData.contentAt(row, column);
	}

	// - TabularSelection

	public void clearSelection() {
		selectionController.clearSelections();
	}

	/**
	 * @param row row index
	 * @param extend whether to extend selection (SHIFT)
	 * @param addSelection whether to add a separate selection (CTRL)
	 */
	public void selectRow(int row, boolean extend, boolean addSelection) {
		selectionController.selectRow(row, extend, addSelection);
	}

	/**
	 * @param column column index
	 * @param extend whether to extend selection (SHIFT)
	 * @param addSelection whether to add a separate selection (CTRL)
	 */
	public void selectColumn(int column, boolean extend, boolean addSelection) {
		selectionController.selectColumn(column, extend, addSelection);
	}

	/**
	 * @param selection Selection that is to be selected
	 * @param extend Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add the selection to the current selection (CTRL)
	 */
	public void select(TabularRange selection, boolean extend, boolean addSelection) {
		selectionController.select(new Selection(selection),
				extend, addSelection);
	}

	/**
	 * Select all cells
	 */
	public void selectAll() {
		selectionController.selectAll();
	}

	public void selectCell(int rowIndex, int columnIndex, boolean extend, boolean addSelection) {
		selectionController.selectCell(rowIndex, columnIndex, extend, addSelection);
	}

	// default visibility, same as Selection class
	Stream<Selection> getSelections() {
		return selectionController.getSelections();
	}

	boolean isSelected(int row, int column) {
		return selectionController.isSelected(row, column);
	}

	public String getColumnName(int column) {
		return tabularData.getColumnName(column);
	}

	public String getRowName(int column) {
		return tabularData.getRowName(column);
	}

	boolean showCellEditor(int row, int column) {
		if (controlsDelegate == null) {
			return false;
		}
		Rectangle editorBounds = getEditorBounds(row, column);
		SpreadsheetCellEditor editor = controlsDelegate.getCellEditor();
		editor.setBounds(editorBounds);

		editor.setContent(tabularData.contentAt(row, column));
		editor.setAlign(tabularData.getAlignment(row, column));
		editor.setTargetCell(row, column);
		resetDragAction();
		return true;
	}

	private Rectangle getEditorBounds(int row, int column) {
		return layout.getBounds(row, column)
				.translatedBy(-viewport.getMinX() + layout.getRowHeaderWidth(),
						-viewport.getMinY() + layout.getColumnHeaderHeight());
	}

	/**
	 * Process the editor input, update corresponding cell and hide the editor
	 */
	public void saveContentAndHideCellEditor() {
		if (controlsDelegate != null) {
			SpreadsheetCellEditor editor = controlsDelegate.getCellEditor();
			if (editor != null && editor.isVisible()) {
				editor.onEnter();
				editor.hide();
			}
		}
	}

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		this.controlsDelegate = controlsDelegate;
	}

	public void setViewportAdjustmentHandler(ViewportAdjustmentHandler viewportAdjustmentHandler) {
		this.viewportAdjuster = new ViewportAdjuster(getLayout(), viewportAdjustmentHandler);
	}

	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
	}

	public void setUndoProvider(UndoProvider undoProvider) {
		this.undoProvider = undoProvider;
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 */
	// TODO I think all coordinates (everywhere in spreadsheet) should be floats
	public void handlePointerDown(int x, int y, Modifiers modifiers) {
		saveContentAndHideCellEditor();
		if (controlsDelegate != null) {
			controlsDelegate.hideContextMenu();
		}
		dragAction = getDragAction(x, y);
		if (modifiers.shift) {
			setDragStartLocationFromSelection();
		}
		if (dragAction.isModifyingOperation()) {
			return;
		}
		int column = findColumnOrHeader(x);
		int row = findRowOrHeader(y);

		if (viewportAdjuster != null) {

			viewportAdjuster.adjustViewportIfNeeded(row, column, viewport);
		}

		if (modifiers.secondaryButton && controlsDelegate != null) {
			if (isSelected(row, column) && shouldKeepSelectionForContextMenu()) {
				showContextMenu(x, y, selectionController.getUppermostSelectedRowIndex(),
						selectionController.getBottommostSelectedRowIndex(),
						selectionController.getLeftmostSelectedColumnIndex(),
						selectionController.getRightmostSelectedColumnIndex());
				return;
			}
			showContextMenu(x, y, row, row, column, column);
		}

		if (row >= 0 && column >= 0 && selectionController.isOnlyCellSelected(row, column)) {
			showCellEditor(row, column);
			return;
		}
		if (!modifiers.ctrlOrCmd && !modifiers.shift && selectionController.hasSelection()) {
			selectionController.clearSelections();
		}

		if (row == -1 && column == -1) { // Select all
			selectAll();
		} else if (column == -1) { // Select row
			selectRow(row, modifiers.shift, modifiers.ctrlOrCmd);
		} else if (row == -1) { // Select column
			selectColumn(column, modifiers.shift, modifiers.ctrlOrCmd);
		} else { // Select cell
			select(TabularRange.range(row, row, column, column),
					modifiers.shift, modifiers.ctrlOrCmd);
		}
	}

	private int findRowOrHeader(int y) {
		return y < layout.getColumnHeaderHeight() ? -1
				: layout.findRow(y + viewport.getMinY());
	}

	private int findColumnOrHeader(int x) {
		return x < layout.getRowHeaderWidth() ? - 1
				: layout.findColumn(x + viewport.getMinX());
	}

	private void showContextMenu(int x, int y, int fromRow, int toRow, int fromCol, int toCol) {
		if (controlsDelegate != null) {
			controlsDelegate.showContextMenu(contextMenuItems.get(fromRow, toRow, fromCol, toCol),
					new GPoint(x, y));
		}
		resetDragAction();
	}

	private void setDragStartLocationFromSelection() {
		Selection lastSelection = selectionController.getLastSelection();
		if (lastSelection == null) {
			return;
		}
		TabularRange lastRange = lastSelection.getRange();
		dragAction = new DragState(MouseCursor.DEFAULT,
				lastRange.getMinColumn(), lastRange.getMinRow());
	}

	DragState getDragAction(int x, int y) {
		GPoint2D draggingDot = getDraggingDot();
		if (draggingDot != null && draggingDot.distance(x, y) < 18) {
			return new DragState(MouseCursor.DRAG_DOT, layout.findRow(y + viewport.getMinY()),
					layout.findColumn(x + viewport.getMinX()));
		}
		return layout.getResizeAction(x, y, viewport);
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 */
	public void handlePointerUp(int x, int y, Modifiers modifiers) {
		Stream<Selection> selections = getSelections();
		switch (dragAction.cursor) {
		case RESIZE_X:
			if (isSelected(-1, dragAction.startColumn)) {
				double width = layout.getWidthForColumnResize(dragAction.startColumn,
						x + viewport.getMinX());
				selections.forEach(selection -> {
					if (selection.getType() == SelectionType.COLUMNS) {
						layout.setWidthForColumns(width, selection.getRange().getMinColumn(),
								selection.getRange().getMaxColumn());
					}
				});
			}
			storeUndoInfo();
			break;
		case RESIZE_Y:
			if (isSelected(dragAction.startRow, -1)) {
				double height = layout.getHeightForRowResize(dragAction.startRow,
						y + viewport.getMinY());
				selections.forEach(selection -> {
					if (selection.getType() == SelectionType.ROWS) {
						layout.setHeightForRows(height, selection.getRange().getMinRow(),
								selection.getRange().getMaxRow());
					}
				});
			}
			storeUndoInfo();
			break;
		case DEFAULT:
		default:
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd);
		// TODO implement formula propagation with DRAG_DOT
		}
		resetDragAction();
	}

	private void resetDragAction() {
		dragAction = new DragState(MouseCursor.DEFAULT, -1, -1);
	}

	/**
	 * Handles keys being pressed
	 * @param keyCode Key Code
	 * @param key unicode value
	 * @param modifiers Modifiers
	 */
	public void handleKeyPressed(int keyCode, String key, Modifiers modifiers) {
		boolean cellSelectionChanged = false;
		if (selectionController.hasSelection()) {
			switch (keyCode) {
			case JavaKeyCodes.VK_LEFT:
				moveLeft(modifiers.shift);
				cellSelectionChanged = true;
				break;
			case JavaKeyCodes.VK_TAB:
			case JavaKeyCodes.VK_RIGHT:
				moveRight(modifiers.shift);
				cellSelectionChanged = true;
				break;
			case JavaKeyCodes.VK_UP:
				moveUp(modifiers.shift);
				cellSelectionChanged = true;
				break;
			case JavaKeyCodes.VK_DOWN:
				moveDown(modifiers.shift);
				cellSelectionChanged = true;
				break;
			case JavaKeyCodes.VK_A:
				if (modifiers.ctrlOrCmd) {
					selectionController.selectAll();
					return;
				}
				startTyping(key, modifiers);
				break;
			case JavaKeyCodes.VK_ENTER:
				showCellEditorAtSelection();
				return;
			default:
				startTyping(key, modifiers);
			}
		}
		if (cellSelectionChanged) {
			adjustViewportIfNeeded();
		}
	}

	private void startTyping(String key, Modifiers modifiers) {
		SpreadsheetControlsDelegate controls = controlsDelegate;
		if (!modifiers.ctrlOrCmd && !modifiers.alt && !StringUtil.empty(key)
				&& controls != null) {
			showCellEditorAtSelection();
			controls.getCellEditor().setContent("");
			controls.getCellEditor().type(key);
		}
	}

	private void showCellEditorAtSelection() {
		Selection last = getLastSelection();
		TabularRange range = last == null ? null : last.getRange();
		if (range != null) {
			showCellEditor(range.getFromRow(), range.getFromColumn());
		}
	}

	/**
	 * Move focus down and adjust viewport
	 */
	public void onEnter() {
		moveDown(false);
		adjustViewportIfNeeded();
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	public void moveLeft(boolean extendingCurrentSelection) {
		selectionController.moveLeft(extendingCurrentSelection);
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	public void moveRight(boolean extendingCurrentSelection) {
		selectionController.moveRight(extendingCurrentSelection, layout.numberOfColumns());
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	public void moveUp(boolean extendingCurrentSelection) {
		selectionController.moveUp(extendingCurrentSelection);
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	public void moveDown(boolean extendingCurrentSelection) {
		selectionController.moveDown(extendingCurrentSelection, layout.numberOfRows());
	}

	/**
	 * Adjusts the viewport if the selected cell or column is not fully visible
	 */
	private void adjustViewportIfNeeded() {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null && viewportAdjuster != null) {
			viewportAdjuster.adjustViewportIfNeeded(
					lastSelection.getRange().getToRow(),
					lastSelection.getRange().getToColumn(),
					viewport);
		}
	}

	@CheckForNull Selection getLastSelection() {
		return selectionController.getLastSelection();
	}

	/**
	 * @param x event x-coordinate in pixels
	 * @param y event y-coordinate in pixels
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerMove(int x, int y, Modifiers modifiers) {
		switch (dragAction.cursor) {
		case RESIZE_X:
			// only handle the dragged column here, the rest of selection on pointer up
			// otherwise left border of dragged column could move, causing feedback loop
			double width = layout.getWidthForColumnResize(dragAction.startColumn,
					x + viewport.getMinX());
			layout.setWidthForColumns(width, dragAction.startColumn, dragAction.startColumn);
			break;
		case RESIZE_Y:
			double height = layout.getHeightForRowResize(dragAction.startRow,
					y + viewport.getMinY());
			layout.setHeightForRows(height, dragAction.startRow, dragAction.startRow);
			break;
		default:
		case DEFAULT:
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd);
		}
	}

	/**
	 * @return selections limited to data size
	 */
	public List<TabularRange> getVisibleSelections() {
		return getSelections().map(this::intersectWithDataRange)
				.collect(Collectors.toList());
	}

	private void extendSelectionByDrag(int x, int y, boolean addSelection) {
		if (dragAction.startColumn >= 0 || dragAction.startRow >= 0) {
			int row = findRowOrHeader(y);
			int column = findColumnOrHeader(x);

			TabularRange range =
					new TabularRange(dragAction.startRow, dragAction.startColumn, row, column);
			selectionController.select(new Selection(range), false, addSelection);
		}
	}

	private TabularRange intersectWithDataRange(Selection selection) {
		return selection.getRange().restrictTo(tabularData.numberOfRows(),
				tabularData.numberOfColumns());
	}

	/**
	 * @param column column index
	 * @return whether selection contains at least one cell in given column
	 */
	public boolean isSelectionIntersectingColumn(int column) {
		return selectionController.getSelections()
				.anyMatch(sel -> sel.getRange().intersectsColumn(column));
	}

	/**
	 * @param row row index
	 * @return whether selection contains at least one cell in given row
	 */
	public boolean isSelectionIntersectingRow(int row) {
		return selectionController.getSelections()
				.anyMatch(sel -> sel.getRange().intersectsRow(row));
	}

	@CheckForNull GPoint2D getDraggingDot() {
		if (isEditorActive()) {
			return null;
		}
		List<TabularRange> visibleSelections = getVisibleSelections();
		if (!visibleSelections.isEmpty()) {
			TabularRange lastSelection = visibleSelections.get(visibleSelections.size() - 1);
			Rectangle bounds = layout.getBounds(lastSelection, viewport);
			if (bounds != null && bounds.getMaxX() >  layout.getRowHeaderWidth()
					&& bounds.getMaxY() > layout.getColumnHeaderHeight()) {
				return new GPoint2D(bounds.getMaxX(), bounds.getMaxY());
			}
			return null;
		}
		return null;
	}

	/**
	 * Deletes a row at the given index<br/>
	 * <b>In case there are multiple selections present, deletes all rows where a cell
	 * is selected</b>
	 * @param row Row index
	 */
	public void deleteRowAt(int row) {
		if (!selectionController.hasSelection() || selectionController.isOnlyRowSelected(row)) {
			deleteRowAndResizeRemainingRows(row);
		} else {
			deleteRowsForMultiCellSelection();
		}
		storeUndoInfo();
	}

	/**
	 * <b>Important note - only delete rows in a descending order (bottom to top)</b><br/>
	 * Deletes a row at given index and resizes the remaining rows in ascending order
	 * @param row Row index
	 */
	private void deleteRowAndResizeRemainingRows(int row) {
		tabularData.deleteRowAt(row);
		if (layout != null) {
			layout.resizeRemainingRowsAscending(row, tabularData.numberOfRows());
		}
	}

	private void deleteRowsForMultiCellSelection() {
		List<Integer> allRowIndexes = selectionController.getAllRowIndexes();
		allRowIndexes.sort(Collections.reverseOrder());
		allRowIndexes.stream().forEach(rowIndex -> deleteRowAndResizeRemainingRows(rowIndex));
	}

	/**
	 * Deletes a column at the given index<br/>
	 * <b>In case there are multiple selections present, deletes all columns where a cell
	 * is selected</b>
	 * @param column Column index
	 */
	public void deleteColumnAt(int column) {
		if (!selectionController.hasSelection()
				|| selectionController.isOnlyColumnSelected(column)) {
			deleteColumnAndResizeRemainingColumns(column);
		} else {
			deleteColumnsForMulticellSelection();
		}
		storeUndoInfo();
	}

	/**
	 * <b>Important note - only delete columns in a descending order (right to left)</b><br/>
	 * Deletes a column at given index and resizes the remaining columns in ascending order
	 * @param column Column index
	 */
	private void deleteColumnAndResizeRemainingColumns(int column) {
		tabularData.deleteColumnAt(column);
		if (layout != null) {
			layout.resizeRemainingColumnsAscending(column, tabularData.numberOfColumns());
		}
	}

	private void deleteColumnsForMulticellSelection() {
		List<Integer> allColumnIndexes = selectionController.getAllColumnIndexes();
		allColumnIndexes.sort(Collections.reverseOrder());
		allColumnIndexes.stream().forEach(
				columnIndex -> deleteColumnAndResizeRemainingColumns(columnIndex));
	}

	/**
	 * Inserts a column at a given index
	 * @param column Index of where to insert the column
	 * @param right Whether the column is being inserted right of the currently selected column
	 */
	public void insertColumnAt(int column, boolean right) {
		tabularData.insertColumnAt(column);
		Selection lastSelection = selectionController.getLastSelection();
		if (right && lastSelection != null) {
			selectionController.setSelection(lastSelection.getRight(
					tabularData.numberOfColumns(), false));
		}
		if (layout != null) {
			layout.resizeRemainingColumnsDescending(right ? column - 1 : column,
					tabularData.numberOfColumns());
		}
		storeUndoInfo();
	}

	/**
	 * Inserts a row at a given index
	 * @param row Index of where to insert the row
	 * @param below Whether the row is being inserted below the currently selected row
	 */
	public void insertRowAt(int row, boolean below) {
		tabularData.insertRowAt(row);
		Selection lastSelection = selectionController.getLastSelection();
		if (below && lastSelection != null) {
			selectionController.setSelection(lastSelection.getBottom(
					tabularData.numberOfRows(), false));
		}
		if (layout != null) {
			layout.resizeRemainingRowsDescending(below ? row - 1 : row, tabularData.numberOfRows());
		}
		storeUndoInfo();
	}

	boolean isOnlyCellSelected(int row, int column) {
		return selectionController.isOnlyCellSelected(row, column);
	}

	boolean areAllCellsSelected() {
		return selectionController.areAllCellsSelected();
	}

	/**
	 * If there are multiple selections present, the current selection should stay as it was if
	 * <li>Multiple Rows <b>only</b> are selected</li>
	 * <li>Multiple Columns <b>only</b> are selected</li>
	 * <li>Only single or multiple cells are selected (no whole rows / columns)</li>
	 * <li>All cells are selected</li>
	 * @return Whether the selection should be kept for showing the context menu
	 */
	private boolean shouldKeepSelectionForContextMenu() {
		return selectionController.isSingleSelectionType();
	}

	private CopyPasteCutTabularData getCopyPasteCut() {
		return controlsDelegate != null
				? new CopyPasteCutTabularDataImpl<>(tabularData, controlsDelegate.getClipboard())
				: null;
	}

	/**
	 * @return whether editor is currently visible
	 */
	public boolean isEditorActive() {
		return controlsDelegate != null && controlsDelegate.getCellEditor().isVisible();
	}

	private void storeUndoInfo() {
		if (undoProvider != null) {
			undoProvider.storeUndoInfo();
		}
	}

}
