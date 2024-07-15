package org.geogebra.common.spreadsheet.core;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * A container for tabular data, with support for selecting and editing the data.
 *
 * @apiNote This type is not designed to be thread-safe.
 */
public final class SpreadsheetController {

	private final ContextMenuItems contextMenuItems;
	private final SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	final private TabularData<?> tabularData;

	//@NonOwning
	private @CheckForNull SpreadsheetControlsDelegate controlsDelegate;
	private Editor editor;
	private final TableLayout layout;

	private final SpreadsheetStyle style;
	private DragState dragState;
	private Rectangle viewport;
	private @CheckForNull ViewportAdjuster viewportAdjuster;
	private @CheckForNull UndoProvider undoProvider;
	private CellDragPasteHandler cellDragPasteHandler;
	private int lastPointerPositionX = -1;
	private int lastPointerPositionY = -1;

	/**
	 * @param tabularData underlying data for the spreadsheet
	 */
	public SpreadsheetController(TabularData<?> tabularData) {
		this.tabularData = tabularData;
		this.viewport = new Rectangle(0, 0, 0, 0);
		this.cellDragPasteHandler = tabularData.getCellDragPasteHandler();
		resetDragAction();
		style = new SpreadsheetStyle(tabularData.getFormat());
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), TableLayout.DEFAUL_CELL_HEIGHT,
				TableLayout.DEFAULT_CELL_WIDTH);
		contextMenuItems = new ContextMenuItems(this, selectionController, getCopyPasteCut());
	}

	/**
	 * @param controlsDelegate The controls delegate.
	 */
	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		this.controlsDelegate = controlsDelegate;
		editor = null;
	}

	/**
	 * @param viewportAdjusterDelegate The viewport adjuster delegate.
	 */
	public void setViewportAdjustmentHandler(ViewportAdjusterDelegate viewportAdjusterDelegate) {
		this.viewportAdjuster = new ViewportAdjuster(getLayout(), viewportAdjusterDelegate);
	}

	/**
	 * @param undoProvider The undo provider.
	 */
	public void setUndoProvider(UndoProvider undoProvider) {
		this.undoProvider = undoProvider;
	}

	void setViewport(Rectangle viewport) {
		this.viewport = viewport;
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

	Object contentAt(int row, int column) {
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
	 * @param tabularRange Range that is to be selected
	 * @param extend Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add the selection to the current selection (CTRL)
	 */
	public void select(TabularRange tabularRange, boolean extend, boolean addSelection) {
		selectionController.select(new Selection(tabularRange),
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

	private boolean showCellEditor(int row, int column) {
		if (controlsDelegate == null) {
			return false; // cell editor not shown
		}
		if (editor == null) {
			editor = new Editor(controlsDelegate.getCellEditor());
		}
		editor.showAt(row, column);
		resetDragAction();
		return true;
	}

	private void hideCellEditor() {
		if (isEditorActive()) {
			editor.hide();
		}
	}

	/**
	 * @return true if the cell editor is currently visible.
	 */
	public boolean isEditorActive() {
		return editor != null && editor.isVisible;
	}

	private void saveContentAndHideCellEditor() {
		if (editor.isVisible) {
			editor.commit();
			editor.hide();
		}
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 */
	// TODO change to double (APPS-5637)
	// TODO group all handleXxx methods together
	public void handlePointerDown(int x, int y, Modifiers modifiers) {
		if (isEditorActive()) {
			saveContentAndHideCellEditor();
		}
		if (controlsDelegate != null) {
			controlsDelegate.hideContextMenu();
		}
		dragState = getDragAction(x, y);
		if (modifiers.shift) {
			setDragStartLocationFromSelection();
		}
		if (dragState.cursor == MouseCursor.DRAG_DOT) {
			Selection lastSelection = getLastSelection();
			if (lastSelection != null) {
				cellDragPasteHandler.setRangeToCopy(lastSelection.getRange());
			}
		}
		if (dragState.isModifyingOperation()) {
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
		return x < layout.getRowHeaderWidth() ? -1
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
		dragState = new DragState(MouseCursor.DEFAULT,
				lastRange.getMinRow(), lastRange.getMinColumn());
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
		switch (dragState.cursor) {
		case RESIZE_X:
			if (isSelected(-1, dragState.startColumn)) {
				resizeAllSelectedColumns(x);
			}
			notifyDataDimensionsChanged();
			break;
		case RESIZE_Y:
			if (isSelected(dragState.startRow, -1)) {
				resizeAllSelectedRows(y);
			}
			notifyDataDimensionsChanged();
			break;
		case DEFAULT:
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd);
			break;
		case DRAG_DOT:
			pasteDragSelectionToDestination();
			notifyDataDimensionsChanged();
		}
		resetDragAction();
	}

	private void resizeAllSelectedColumns(int x) {
		Stream<Selection> selections = getSelections();
		double width = layout.getWidthForColumnResize(dragState.startColumn,
				x + viewport.getMinX());
		selections.forEach(selection -> {
			if (selection.getType() == SelectionType.COLUMNS) {
				layout.setWidthForColumns(width, selection.getRange().getMinColumn(),
						selection.getRange().getMaxColumn());
			}
		});
	}

	private void resizeAllSelectedRows(int y) {
		Stream<Selection> selections = getSelections();
		double height = layout.getHeightForRowResize(dragState.startRow,
				y + viewport.getMinY());
		selections.forEach(selection -> {
			if (selection.getType() == SelectionType.ROWS) {
				layout.setHeightForRows(height, selection.getRange().getMinRow(),
						selection.getRange().getMaxRow());
			}
		});
	}

	private void pasteDragSelectionToDestination() {
		Selection lastSelection = getLastSelection();
		TabularRange destinationRange = cellDragPasteHandler.getDragPasteDestinationRange();
		if (lastSelection == null || destinationRange == null) {
			return;
		}
		cellDragPasteHandler.pasteToDestination();
		cellDragPasteHandler.setRangeToCopy(null);
		TabularRange mergedRange = lastSelection.getRange().getRectangularUnion(destinationRange);
		if (mergedRange != null) {
			select(mergedRange, false, true);
		} else {
			select(destinationRange, false, false);
		}
	}

	private void resetDragAction() {
		dragState = new DragState(MouseCursor.DEFAULT, -1, -1);
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
			case JavaKeyCodes.VK_DELETE:
			case JavaKeyCodes.VK_BACK_SPACE:
			case JavaKeyCodes.VK_CLEAR:
				deleteSelectedCells();
				break;
			default:
				startTyping(key, modifiers);
			}
		}
		if (cellSelectionChanged) {
			adjustViewportIfNeeded();
		}
	}

	private void startTyping(String key, Modifiers modifiers) {
		if (!modifiers.ctrlOrCmd && !modifiers.alt && !StringUtil.empty(key)) {
			showCellEditorAtSelection();
			if (editor != null) {
				editor.clearInput();
				editor.type(key);
			}
		}
	}

	private void showCellEditorAtSelection() {
		Selection last = getLastSelection();
		TabularRange range = last == null ? null : last.getRange();
		if (range != null) {
			showCellEditor(range.getFromRow(), range.getFromColumn());
		}
	}

	private void deleteSelectedCells() {
		// TODO implement single cell deletion (delete key)
	}

	/**
	 * Hides the cell editor if active, moves input focus down by one cell, and adjusts the
	 * viewport if necessary.
	 */
	void onEnter() {
		hideCellEditor();
		moveDown(false);
		adjustViewportIfNeeded();
	}

	/**
	 * Hides the cell editor if acgive, moves input focus right by one cell, and adjusts the
	 * viewport if necessary.
	 */
	void onTab() {
		hideCellEditor();
		moveRight(false);
		adjustViewportIfNeeded();
	}

	/**
	 * Hides the cell editor if active.
	 */
	void onEsc() {
		hideCellEditor();
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	void moveLeft(boolean extendingCurrentSelection) {
		selectionController.moveLeft(extendingCurrentSelection);
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	void moveRight(boolean extendingCurrentSelection) {
		selectionController.moveRight(extendingCurrentSelection, layout.numberOfColumns());
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	void moveUp(boolean extendingCurrentSelection) {
		selectionController.moveUp(extendingCurrentSelection);
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	void moveDown(boolean extendingCurrentSelection) {
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

	@CheckForNull
	Selection getLastSelection() {
		return selectionController.getLastSelection();
	}

	/**
	 * @param x event x-coordinate in pixels
	 * @param y event y-coordinate in pixels
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerMove(int x, int y, Modifiers modifiers) {
		lastPointerPositionX = x;
		lastPointerPositionY = y;
		switch (dragState.cursor) {
		case RESIZE_X:
			// only handle the dragged column here, the rest of selection on pointer up
			// otherwise left border of dragged column could move, causing feedback loop
			resizeColumn(x);
			return;
		case RESIZE_Y:
			resizeRow(y);
			return;
		case DRAG_DOT:
			setDestinationForDragPaste(x, y);
			return;
		default:
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd);
		}
	}

	private void setDestinationForDragPaste(int x, int y) {
		int row = findRowOrHeader(y);
		int column = findColumnOrHeader(x);
		cellDragPasteHandler.setDestinationForPaste(row, column);
	}

	private void resizeColumn(int x) {
		double width = layout.getWidthForColumnResize(dragState.startColumn,
				x + viewport.getMinX());
		layout.setWidthForColumns(width, dragState.startColumn, dragState.startColumn);
	}

	private void resizeRow(int y) {
		double height = layout.getHeightForRowResize(dragState.startRow,
				y + viewport.getMinY());
		layout.setHeightForRows(height, dragState.startRow, dragState.startRow);
	}

	/**
	 * @return selections limited to data size
	 */
	List<TabularRange> getVisibleSelections() {
		return getSelections().map(this::intersectWithDataRange)
				.collect(Collectors.toList());
	}

	private void extendSelectionByDrag(int x, int y, boolean addSelection) {
		if (dragState.startColumn >= 0 || dragState.startRow >= 0) {
			int row = findRowOrHeader(y);
			int column = findColumnOrHeader(x);

			TabularRange range =
					new TabularRange(dragState.startRow, dragState.startColumn, row, column);
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
	boolean isSelectionIntersectingColumn(int column) {
		return selectionController.getSelections()
				.anyMatch(sel -> sel.getRange().intersectsColumn(column));
	}

	/**
	 * @param row row index
	 * @return whether selection contains at least one cell in given row
	 */
	boolean isSelectionIntersectingRow(int row) {
		return selectionController.getSelections()
				.anyMatch(sel -> sel.getRange().intersectsRow(row));
	}

	@CheckForNull
	GPoint2D getDraggingDot() {
		if (isEditorActive()) {
			return null;
		}
		List<TabularRange> visibleSelections = getVisibleSelections();
		if (!visibleSelections.isEmpty()) {
			TabularRange lastSelection = visibleSelections.get(visibleSelections.size() - 1);
			Rectangle bounds = layout.getBounds(lastSelection, viewport);
			if (bounds != null && bounds.getMaxX() > layout.getRowHeaderWidth()
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
	void deleteRowAt(int row) {
		if (!selectionController.hasSelection() || selectionController.isOnlyRowSelected(row)) {
			deleteRowAndResizeRemainingRows(row);
		} else {
			deleteRowsForMultiCellSelection();
		}
		layout.setNumberOfRows(tabularData.numberOfRows());
		notifyDataDimensionsChanged();
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
	void deleteColumnAt(int column) {
		if (!selectionController.hasSelection()
				|| selectionController.isOnlyColumnSelected(column)) {
			deleteColumnAndResizeRemainingColumns(column);
		} else {
			deleteColumnsForMulticellSelection();
		}
		layout.setNumberOfColumns(tabularData.numberOfColumns());
		notifyDataDimensionsChanged();
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
	void insertColumnAt(int column, boolean right) {
		tabularData.insertColumnAt(column);
		Selection lastSelection = selectionController.getLastSelection();
		if (right && lastSelection != null) {
			selectionController.setSelection(lastSelection.getNextCellForMoveRight(
					tabularData.numberOfColumns()));
		}
		if (layout != null) {
			layout.setNumberOfColumns(tabularData.numberOfColumns());
			layout.resizeRemainingColumnsDescending(right ? column - 1 : column,
					tabularData.numberOfColumns());
		}
		notifyDataDimensionsChanged();
	}

	private void notifyDataDimensionsChanged() {
		notifyViewportAdjuster();
		adjustViewportIfNeeded();
		storeUndoInfo();
	}

	/**
	 * Inserts a row at a given index
	 * @param row Index of where to insert the row
	 * @param below Whether the row is being inserted below the currently selected row
	 */
	void insertRowAt(int row, boolean below) {
		tabularData.insertRowAt(row);
		Selection lastSelection = selectionController.getLastSelection();
		if (below && lastSelection != null) {
			selectionController.setSelection(lastSelection.getNextCellForMoveDown(
					tabularData.numberOfRows()));
		}
		if (layout != null) {
			layout.setNumberOfRows(tabularData.numberOfRows());
			layout.resizeRemainingRowsDescending(below ? row - 1 : row, tabularData.numberOfRows());
		}
		notifyDataDimensionsChanged();
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
				? new CopyPasteCutTabularDataImpl<>(tabularData,
				controlsDelegate.getClipboard(), layout) : null;
	}

	private void storeUndoInfo() {
		if (undoProvider != null) {
			undoProvider.storeUndoInfo();
		}
	}

	void tabularDataSizeDidChange(SpreadsheetDimensions dimensions) {
		getLayout().dimensionsDidChange(dimensions);
		notifyViewportAdjuster();
	}

	private void notifyViewportAdjuster() {
		if (viewportAdjuster != null) {
			viewportAdjuster.updateScrollPaneSize(new Size(layout.getTotalWidth(),
					layout.getTotalHeight()));
		}
	}

	/**
	 * @return The {@link TabularRange} that indicates the destination for the drag paste
	 */
	public @CheckForNull TabularRange getDragPasteSelection() {
		if (cellDragPasteHandler == null) {
			return null;
		}
		return cellDragPasteHandler.getDragPasteDestinationRange();
	}

	/**
	 * If the pointer is at the top / right / bottom / left corner while dragging a paste
	 * selection, starts scrolling the viewport
	 */
	public void scrollForPasteSelectionIfNeeded() {
		if (cellDragPasteHandler != null && viewportAdjuster != null
				&& cellDragPasteHandler.getDragPasteDestinationRange() != null) {
			viewportAdjuster.scrollForPasteSelectionIfNeeded(
					lastPointerPositionX, lastPointerPositionY, viewport,
					cellDragPasteHandler.destinationShouldExtendVertically(
							findRowOrHeader(lastPointerPositionY)),
					this::setDestinationForDragPaste);
		}
	}

	private final class Editor {
		private final @Nonnull SpreadsheetCellEditor cellEditor;
		private @CheckForNull SpreadsheetMathFieldAdapter mathFieldAdapter;
		boolean isVisible;

		Editor(@Nonnull SpreadsheetCellEditor cellEditor) {
			this.cellEditor = cellEditor;
		}

		void showAt(int row, int column) {
			Object content = tabularData.contentAt(row, column);

			MathFieldInternal mathField = cellEditor.getMathField();
			mathField.parse(cellEditor.getCellDataSerializer().getStringForEditor(content));

			mathFieldAdapter = new SpreadsheetMathFieldAdapter(mathField, row, column,
					cellEditor.getCellProcessor(), SpreadsheetController.this);
			mathField.addMathFieldListener(mathFieldAdapter);
			mathField.setUnhandledArrowListener(mathFieldAdapter);

			Rectangle editorBounds = layout.getBounds(row, column)
					.insetBy(1, 1) // don't overdraw thick selection border
					.translatedBy(-viewport.getMinX() + layout.getRowHeaderWidth(),
							-viewport.getMinY() + layout.getColumnHeaderHeight());
			cellEditor.show(editorBounds, viewport, tabularData.getAlignment(row, column));
			isVisible = true;
		}

		void hide() {
			cellEditor.getMathField().removeMathFieldListener(mathFieldAdapter);
			cellEditor.hide();
			isVisible = false;
		}

		void clearInput() {
			cellEditor.getMathField().parse("");
		}

		void type(String key) {
			KeyboardInputAdapter.type(cellEditor.getMathField(), key);
		}

		void commit() {
			if (mathFieldAdapter != null) {
				mathFieldAdapter.commitInput();
			}
		}
	}
}