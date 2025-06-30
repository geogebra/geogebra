package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.MulticastEvent;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * A container for tabular data, with support for selecting and editing the data.
 *
 * @apiNote This type is not designed to be thread-safe.
 */
public final class SpreadsheetController {

	public final MulticastEvent<CellSizes> cellSizesChanged = new MulticastEvent<>();

	/**
	 * Fired when the list of cell references (in the currently editing cell), or the current
	 * cell reference (the one under the cursor) changes.
	 */
	public final MulticastEvent<MulticastEvent.Void> referencesChanged = new MulticastEvent<>();

	final SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	private final @Nonnull TabularData<?> tabularData;
	private final @CheckForNull SpreadsheetStyling spreadsheetStyling;

	private @CheckForNull SpreadsheetControlsDelegate controlsDelegate;
	private @CheckForNull SpreadsheetConstructionDelegate constructionDelegate;
	private final @Nonnull TableLayout layout;
	private final @Nonnull ContextMenuBuilder contextMenuBuilder;

	private Editor editor;
	private SpreadsheetReferences currentReferences;

	private @Nonnull DragState dragState;
	private Rectangle viewport;
	private @CheckForNull ViewportAdjuster viewportAdjuster;
	private @CheckForNull UndoProvider undoProvider;
	private final @CheckForNull CellDragPasteHandler cellDragPasteHandler;
	private double lastPointerPositionX = -1;
	private double lastPointerPositionY = -1;
	private @CheckForNull CopyPasteCutTabularData copyPasteCut;
	private boolean autoscrollRow;
	private boolean autoscrollColumn;
	private boolean didScrollWhileEditorActive = false;

	private static final int DOT_CATCH_RADIUS = 18;

	/**
	 * @param tabularData underlying data for the spreadsheet
	 */
	public SpreadsheetController(@Nonnull TabularData<?> tabularData,
			@CheckForNull SpreadsheetStyling spreadsheetStyling) {
		this.tabularData = tabularData;
		this.spreadsheetStyling = spreadsheetStyling;
		this.viewport = new Rectangle(0, 0, 0, 0);
		this.cellDragPasteHandler = tabularData.getCellDragPasteHandler();
		this.dragState = new DragState(MouseCursor.DEFAULT, -1, -1);
		layout = new TableLayout(tabularData.numberOfRows(), tabularData.numberOfColumns());
		contextMenuBuilder = new ContextMenuBuilder(this);
	}

	// Delegates

	/**
	 * @param controlsDelegate The controls delegate.
	 */
	public void setControlsDelegate(@CheckForNull SpreadsheetControlsDelegate controlsDelegate) {
		this.controlsDelegate = controlsDelegate;
		editor = null;
		initCopyPasteCut();
	}

	/**
	 * @param constructionDelegate {@link SpreadsheetConstructionDelegate}
	 */
	public void setSpreadsheetConstructionDelegate(@CheckForNull SpreadsheetConstructionDelegate
			constructionDelegate) {
		this.constructionDelegate = constructionDelegate;
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
	public void setUndoProvider(@CheckForNull UndoProvider undoProvider) {
		this.undoProvider = undoProvider;
	}

	// Tabular data

	Object contentAt(int row, int column) {
		return tabularData.contentAt(row, column);
	}

	/**
	 * Inserts a row at a given index
	 * @param row Index of where to insert the row
	 * @param below Whether the row is being inserted below the currently selected row
	 */
	void insertRowAt(int row, boolean below) {
		tabularData.insertRowAt(row); // this also updates the SpreadsheetSettings
		Selection lastSelection = selectionController.getLastSelection();
		if (below && lastSelection != null) {
			selectionController.setSelection(lastSelection.getNextCellForMoveDown(
					tabularData.numberOfRows()));
		}
		layout.setNumberOfRows(tabularData.numberOfRows());
		layout.resizeRemainingRowsDescending(below ? row - 1 : row, tabularData.numberOfRows());
		onLayoutChange();
	}

	private void insertRowBottom() {
		tabularData.insertRowAt(tabularData.numberOfRows());
		updateLayout(tabularData.numberOfColumns(), tabularData.numberOfRows() - 1);
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
		layout.setNumberOfColumns(tabularData.numberOfColumns());
		layout.resizeRemainingColumnsDescending(right ? column - 1 : column,
				tabularData.numberOfColumns());
		onLayoutChange();
	}

	private void insertColumnRight() {
		tabularData.insertColumnAt(tabularData.numberOfColumns());
		updateLayout(tabularData.numberOfColumns() - 1, tabularData.numberOfRows());
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
		onLayoutChange();
	}

	/**
	 * <b>Important note - only delete rows in a descending order (bottom to top)</b><br/>
	 * Deletes a row at given index and resizes the remaining rows in ascending order
	 * @param row Row index
	 */
	private void deleteRowAndResizeRemainingRows(int row) {
		tabularData.deleteRowAt(row);
		layout.resizeRemainingRowsAscending(row, tabularData.numberOfRows());
	}

	private void deleteRowsForMultiCellSelection() {
		List<Integer> allRowIndexes = selectionController.getAllRowIndexes();
		allRowIndexes.sort(Collections.reverseOrder());
		allRowIndexes.forEach(this::deleteRowAndResizeRemainingRows);
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
		onLayoutChange();
	}

	/**
	 * <b>Important note - only delete columns in a descending order (right to left)</b><br/>
	 * Deletes a column at given index and resizes the remaining columns in ascending order
	 * @param column Column index
	 */
	private void deleteColumnAndResizeRemainingColumns(int column) {
		tabularData.deleteColumnAt(column);
		layout.resizeRemainingColumnsAscending(column, tabularData.numberOfColumns());
	}

	private void deleteColumnsForMulticellSelection() {
		List<Integer> allColumnIndexes = selectionController.getAllColumnIndexes();
		allColumnIndexes.sort(Collections.reverseOrder());
		allColumnIndexes.stream().forEach(
				this::deleteColumnAndResizeRemainingColumns);
	}

	private void deleteSelectedCells() {
		int fromRow = selectionController.getUppermostSelectedRowIndex();
		int fromCol = selectionController.getLeftmostSelectedColumnIndex();
		getSelections().forEach(s -> {
			TabularRange validRange = s.getRange().restrictTo(tabularData.numberOfRows(),
					tabularData.numberOfColumns());
			validRange.forEach(tabularData::removeContentAt);
		});
		updateLayout(fromCol, fromRow);
	}

	// Call chain:
	// KernelTabularDataAdapter (on settings change)
	//   -> Spreadsheet.tabularDataDimensionsDidChange()
	//     -> SpreadsheetController.tabularDataDimensionsDidChange()
	void tabularDataDimensionsDidChange(SpreadsheetDimensions dimensions) {
		layout.dimensionsDidChange(dimensions);
        selectionController.trimSelectionToSize(layout.numberOfRows(), layout.numberOfColumns());
		notifyViewportAdjusterAboutSizeChange();
	}

	private void updateLayout(int fromColumn, int fromRow) {
		layout.setNumberOfColumns(tabularData.numberOfColumns());
		layout.setNumberOfRows(tabularData.numberOfRows());
		int clampedFromColumn = Math.max(0, fromColumn - 1);
		int clampedFromRow = Math.max(0, fromRow - 1);
		double w = layout.getWidth(clampedFromColumn);
		double h = layout.getHeight(clampedFromRow);
		layout.setWidthForColumns(w, clampedFromColumn,
				tabularData.numberOfColumns() - 1);
		layout.setHeightForRows(h, clampedFromRow,
				tabularData.numberOfRows() - 1);
		onLayoutChange();
	}

	private void onLayoutChange() {
		// sync TableLayout -> SpreadsheetSettings
		cellSizesChanged.notifyListeners(
				new CellSizes(layout.getCustomColumnWidths(), layout.getCustomRowHeights()));
		storeUndoInfo();
		notifyViewportAdjusterAboutSizeChange();
		adjustViewportIfNeeded();
	}

	void storeUndoInfo() {
		if (undoProvider != null) {
			undoProvider.storeUndoInfo();
		}
	}

	boolean hasError(int row, int column) {
		return tabularData.hasError(row, column);
	}

	/**
	 * Get the name of a column.
	 * @param column column index
	 * @return column name
	 */
	public @Nonnull String getColumnName(int column) {
		return tabularData.getColumnName(column);
	}

	/**
	 * Get the name of a row.
	 * @param row row index
	 * @return row name
	 */
	public @Nonnull String getRowName(int row) {
		return tabularData.getRowName(row);
	}

	// Viewport

	void setViewport(@Nonnull Rectangle viewport) {
		Point oldViewportOrigin = this.viewport != null ? this.viewport.origin : null;
		Point newViewportOrigin = viewport != null ? viewport.origin : null;
		boolean viewportOriginDidChange = !Objects.equals(oldViewportOrigin, newViewportOrigin);
		this.viewport = viewport;
		if (isEditorActive() && viewportOriginDidChange) {
			didScrollWhileEditorActive = true;
		}
	}

	@Nonnull Rectangle getViewport() {
		return viewport;
	}

	/**
	 * Adjusts the viewport if the selected cell or column is not fully visible
	 */
	private void adjustViewportIfNeeded() {
		if (viewportAdjuster == null) {
			return;
		}
		Selection lastSelection = getLastSelection();
		if (lastSelection != null && (cellDragPasteHandler == null
				|| cellDragPasteHandler.getDragPasteDestinationRange() == null)) {
			viewport = viewportAdjuster.adjustViewportIfNeeded(
					lastSelection.getRange().getToRow(),
					lastSelection.getRange().getToColumn(),
					viewport);
		}
	}

	private void notifyViewportAdjusterAboutSizeChange() {
		if (viewportAdjuster == null) {
			return;
		}
		viewportAdjuster.updateScrollPaneSize(
				new Size(layout.getTotalWidth(), layout.getTotalHeight()));
	}

	// Layout

	@Nonnull TableLayout getLayout() {
		return layout;
	}

	// Selection

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
	public void select(@Nonnull TabularRange tabularRange, boolean extend, boolean addSelection) {
		selectionController.select(new Selection(tabularRange), extend, addSelection);
	}

	/**
	 * Select all cells
	 */
	public void selectAll() {
		selectionController.selectAll();
	}

	void selectCell(int rowIndex, int columnIndex, boolean extend, boolean addSelection) {
		selectionController.selectCell(rowIndex, columnIndex, extend, addSelection);
	}

	// default visibility, same as Selection class
	@Nonnull Stream<Selection> getSelections() {
		return selectionController.getSelections();
	}

	@CheckForNull Selection getLastSelection() {
		return selectionController.getLastSelection();
	}

	/**
	 * @return selections limited to data size
	 */
	@Nonnull List<TabularRange> getVisibleSelections() {
		return getSelections().map(this::intersectWithDataRange)
				.collect(Collectors.toList());
	}

	private @Nonnull TabularRange intersectWithDataRange(@Nonnull Selection selection) {
		return selection.getRange().restrictTo(tabularData.numberOfRows(),
				tabularData.numberOfColumns());
	}

	boolean isSelected(int row, int column) {
		return selectionController.isSelected(row, column);
	}

	/**
	 * @return The "first" (upper left) cell in the last selection range, or null if there
	 * is no selection.
	 */
	@CheckForNull SpreadsheetCoords getLastSelectionUpperLeftCell() {
		List<TabularRange> visibleSelections = getVisibleSelections();
		if (visibleSelections.isEmpty()) {
			return null;
		}
		TabularRange range = visibleSelections.get(visibleSelections.size() - 1);
		return new SpreadsheetCoords(range.getFromRow(), range.getFromColumn());
	}

	/**
	 * Deselect all cells.
	 */
	public void clearSelection() {
		selectionController.clearSelections();
	}

	boolean isOnlyCellSelected(int row, int column) {
		return selectionController.isOnlyCellSelected(row, column);
	}

	boolean areAllCellsSelected() {
		return selectionController.areAllCellsSelected();
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

	private int findRowOrHeader(double y) {
		return y < layout.getColumnHeaderHeight() ? -1
				: layout.findRow(y + viewport.getMinY());
	}

	private int findColumnOrHeader(double x) {
		return x < layout.getRowHeaderWidth() ? -1
				: layout.findColumn(x + viewport.getMinX());
	}

	private boolean isInvalidCellReferenceSelection(int row, int column) {
		return row < 0 || column < 0 || tabularData.contentAt(row, column) == null;
	}

	// Cell Editor

	private void showCellEditor(int row, int column, boolean editExistingContent) {
		if (controlsDelegate == null) {
			return; // cell editor not shown
		}
		if (editor == null) {
			editor = new Editor(controlsDelegate.getCellEditor());
		}
		currentReferences = null;
		didScrollWhileEditorActive = false;
		editor.showAt(row, column, editExistingContent);
		resetDragAction();
	}

	private void showCellEditorAtSelection(boolean editExistingContent) {
		Selection last = getLastSelection();
		TabularRange range = last == null ? null : last.getRange();
		if (range != null) {
			showCellEditor(range.getFromRow(), range.getFromColumn(), editExistingContent);
		}
	}

	private void hideCellEditor() {
		if (isEditorActive()) {
			editor.hide();
		}
		currentReferences = null;
		if (controlsDelegate != null) {
			controlsDelegate.hideAutoCompleteSuggestions();
		}
	}

	private void resizeCellEditor() {
		if (!isEditorActive() || didScrollWhileEditorActive) {
			return;
		}
		editor.updatePosition();
	}

	/**
	 * @return true if the cell editor is currently visible.
	 */
	public boolean isEditorActive() {
		return editor != null && editor.isVisible();
	}

	/**
	 * Saves the content of the editor and hides it afterwards.
	 */
	void saveContentAndHideCellEditor() {
		if (editor != null && editor.isVisible()) {
			commitInput();
			hideCellEditor();
		}
	}

	@CheckForNull Rectangle getEditorBounds() {
		return editor != null ? editor.bounds : null;
	}

	void scrollEditorIntoView() {
		if (viewportAdjuster != null && editor != null && editor.isVisible()) {
			viewport = viewportAdjuster.adjustViewportIfNeeded(editor.row, editor.column, viewport);
			editor.updatePosition();
		}
	}

	private void commitInput() {
		if (editor == null || !isEditorActive()) {
			return;
		}
		editor.commitInput();
	}

	private void discardInput() {
		if (editor == null || !isEditorActive()) {
			return;
		}
		editor.discardInput();
	}

	private int getAlignment(int row, int column) {
		if (spreadsheetStyling != null) {
			Integer alignment = spreadsheetStyling.getAlignment(row, column);
			if (alignment != null) {
				return alignment;
			}
		}
		return tabularData.isTextContentAt(row, column)
				? CellFormat.ALIGN_LEFT : CellFormat.ALIGN_RIGHT;
	}

	// Editor cell/range references

	/**
	 * @return A (possibly empty) list of cell or cell range references in the editor, or
	 * {@code null} if the editor is currently not active.
	 */
	@CheckForNull SpreadsheetReferences getCurrentReferences() {
		return currentReferences;
	}

	/**
	 * @return A (possibly empty) list of cell or cell range references in the editor, or
	 * {@code null} if the editor is currently not active.
	 * @apiNote Non-private mostly for testability; use #getCurrentReferences() instead
	 */
	@CheckForNull List<SpreadsheetReference> getEditorCellReferences() {
		if (editor == null || !isEditorActive()) {
			return null;
		}

		MathFieldInternal mathField = editor.cellEditor.getMathField();
		String input = mathField.getText();
		if (input.isEmpty() || !input.startsWith("=")) {
			return null;
		}
		ArrayList<String> characterSequences = new ArrayList<>();
		Predicate<MathCharacter> include = w -> w.isCharacter() || ":".equals(w.getUnicodeString());
		mathField.collectCharacterSequences(include, characterSequences);
		ArrayList<SpreadsheetReference> cellRanges = new ArrayList<>();
		for (String characterSequence : characterSequences) {
			SpreadsheetReference cellRange =
					SpreadsheetReferenceParsing.parseReference(characterSequence);
			if (cellRange != null) {
				cellRanges.add(cellRange);
			}
		}
		return cellRanges;
	}

	/**
	 * Returns the cell or range reference containing the (editor) cursor.
	 * @return A cell or range reference, or {@code null} if the cursor is currently not inside
	 * a cell reference (e.g., "A1") or range reference (e.g., "A1:A10").
	 * @apiNote Non-private mostly for testability; prefer getCachedReferences()
	 */
	@CheckForNull SpreadsheetReference getCurrentEditorCellReference() {
		if (editor == null || !isEditorActive()) {
			return null;
		}
		String candidate = editor.getCurrentCellRangeCandidate();
		if (candidate == null) {
			return null;
		}
		return SpreadsheetReferenceParsing.parseReference(candidate);
	}

	// Mouse events

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 */
	public void handlePointerDown(double x, double y, @Nonnull Modifiers modifiers) {
		if (controlsDelegate != null) {
			controlsDelegate.hideContextMenu();
			controlsDelegate.hideAutoCompleteSuggestions();
		}
		dragState = getDragAction(x, y);
		if (modifiers.shift) {
			setDragStartLocationFromSelection();
		}
		if (dragState.cursor == MouseCursor.DRAG_DOT) {
			Selection lastSelection = getLastSelection();
			if (lastSelection != null && cellDragPasteHandler != null) {
				cellDragPasteHandler.setRangeToCopy(lastSelection.getRange());
			}
		}
		if (dragState.isModifyingOperation()) {
			return;
		}
		int column = findColumnOrHeader(x);
		int row = findRowOrHeader(y);
		if (isEditorActive()
				&& (!editor.isComputedCell() || isInvalidCellReferenceSelection(row, column))) {
			saveContentAndHideCellEditor();
		}
		if (viewportAdjuster != null) {
			viewport = viewportAdjuster.adjustViewportIfNeeded(row, column, viewport);
		}

		if (modifiers.secondaryButton && controlsDelegate != null) {
			if (isSelected(row, column) && shouldKeepSelectionForContextMenu()) {
				// clicked inside selection: don't reset selection and show menu
				showContextMenuForSelection(x, y);
				return;
			}
			// clicked outside of selection: update selection to handle shift
			updateCellSelection(row, column, modifiers);
			showContextMenuForSelection(x, y);
			return;
		}

		if (row >= 0 && column >= 0 && selectionController.isOnlyCellSelected(row, column)) {
			showCellEditor(row, column, true);
			return;
		}
		updateCellSelection(row, column, modifiers);
	}

	private void showContextMenuForSelection(double x, double y) {
		showContextMenu(x, y, selectionController.getUppermostSelectedRowIndex(),
					selectionController.getBottommostSelectedRowIndex(),
					selectionController.getLeftmostSelectedColumnIndex(),
					selectionController.getRightmostSelectedColumnIndex());
	}

	private void updateCellSelection(int row, int column, Modifiers modifiers) {
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

	/**
	 * @param x event x-coordinate in pixels
	 * @param y event y-coordinate in pixels
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerMove(double x, double y, Modifiers modifiers) {
		lastPointerPositionX = x;
		lastPointerPositionY = y;
		autoscrollColumn = autoscrollRow = false;
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
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd, false);
		}
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 */
	public void handlePointerUp(double x, double y, Modifiers modifiers) {
		switch (dragState.cursor) {
		case RESIZE_X:
			if (isSelected(-1, dragState.startColumn)) {
				resizeAllSelectedColumns(x);
			}
			onLayoutChange();
			break;
		case RESIZE_Y:
			if (isSelected(dragState.startRow, -1)) {
				resizeAllSelectedRows(y);
			}
			onLayoutChange();
			break;
		case DEFAULT:
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd, true);
			break;
		case DRAG_DOT:
			if (pasteDragSelectionToDestination()) {
				onLayoutChange();
			}
		}
		autoscrollColumn = autoscrollRow = false;
		resetDragAction();
	}

	/**
	 * If there are multiple selections present, the current selection should stay as it was if
	 * <ul>
	 * <li>Multiple Rows <b>only</b> are selected</li>
	 * <li>Multiple Columns <b>only</b> are selected</li>
	 * <li>Only single or multiple cells are selected (no whole rows / columns)</li>
	 * <li>All cells are selected</li>
	 * </ul>
	 * @return Whether the selection should be kept for showing the context menu
	 */
	private boolean shouldKeepSelectionForContextMenu() {
		return selectionController.isSingleSelectionType();
	}

	// Mouse drag

	private void setDragStartLocationFromSelection() {
		Selection lastSelection = selectionController.getLastSelection();
		if (lastSelection == null) {
			return;
		}
		TabularRange lastRange = lastSelection.getRange();
		dragState = new DragState(MouseCursor.DEFAULT,
				lastRange.getMinRow(), lastRange.getMinColumn());
	}

	@Nonnull DragState getDragAction(double x, double y) {
		Point draggingDot = getDraggingDotLocation();
		if (draggingDot != null && draggingDot.distanceTo(x, y) < DOT_CATCH_RADIUS) {
			return new DragState(MouseCursor.DRAG_DOT, layout.findRow(y + viewport.getMinY()),
					layout.findColumn(x + viewport.getMinX()));
		}
		return layout.getResizeAction(x, y, viewport);
	}

	private void resetDragAction() {
		dragState = new DragState(MouseCursor.DEFAULT, -1, -1);
	}

	@CheckForNull Point getDraggingDotLocation() {
		if (isEditorActive()) {
			return null;
		}
		List<TabularRange> visibleSelections = getVisibleSelections();
		if (!visibleSelections.isEmpty()) {
			TabularRange lastSelection = visibleSelections.get(visibleSelections.size() - 1);
			Rectangle bounds = layout.getBounds(lastSelection, viewport);
			if (bounds != null && bounds.getMaxX() > layout.getRowHeaderWidth()
					&& bounds.getMaxY() > layout.getColumnHeaderHeight()) {
				return new Point(bounds.getMaxX(), bounds.getMaxY());
			}
			return null;
		}
		return null;
	}

	/**
	 * @see Spreadsheet#scrollForDragIfNeeded()
	 */
	void scrollForDragIfNeeded() {
		if (viewportAdjuster == null) {
			return;
		}
		if (cellDragPasteHandler != null
				&& cellDragPasteHandler.getDragPasteDestinationRange() != null) {
			double oldViewportX = viewport.getMinX();
			double oldViewportY = viewport.getMinY();
			adjustDataDimensionsForDrag();
			if (viewportAdjuster != null) {
				viewport = viewportAdjuster.scrollForDrag(
						lastPointerPositionX, lastPointerPositionY, viewport,
						cellDragPasteHandler.destinationShouldExtendVertically(
								findRowOrHeader(lastPointerPositionY)));
			}
			setDestinationForDragPaste(lastPointerPositionX + viewport.getMinX() - oldViewportX,
					lastPointerPositionY + viewport.getMinY() - oldViewportY);
		} else if (autoscrollRow  || autoscrollColumn) {
			viewport = viewportAdjuster.scrollForDrag(
					lastPointerPositionX, lastPointerPositionY, viewport,
					autoscrollRow);
			extendSelectionByDrag(Math.max(lastPointerPositionX, layout.getRowHeaderWidth()),
					Math.max(lastPointerPositionY, layout.getColumnHeaderHeight()), false, false);
		}
	}

	private void adjustDataDimensionsForDrag() {
		while (lastPointerPositionX + viewport.getMinX()
				> layout.getTotalWidth() - layout.getRowHeaderWidth()) {
			insertColumnRight();
		}
		while (lastPointerPositionY + viewport.getMinY()
				> layout.getTotalHeight() - layout.getColumnHeaderHeight()) {
			insertRowBottom();
		}
	}

	/**
	 * @return The {@link TabularRange} that indicates the destination for the drag paste
	 */
	@CheckForNull TabularRange getDragPasteSelection() {
		if (cellDragPasteHandler == null) {
			return null;
		}
		return cellDragPasteHandler.getDragPasteDestinationRange();
	}

	private void setDestinationForDragPaste(double x, double y) {
		if (cellDragPasteHandler == null) {
			return;
		}
		int row = findRowOrHeader(y);
		int column = findColumnOrHeader(x);
		cellDragPasteHandler.setDestinationForPaste(row, column);
	}

	private boolean pasteDragSelectionToDestination() {
		if (cellDragPasteHandler == null) {
			return false;
		}
		Selection lastSelection = getLastSelection();
		TabularRange destinationRange = cellDragPasteHandler.getDragPasteDestinationRange();
		if (lastSelection == null || destinationRange == null) {
			return false;
		}
		boolean success = cellDragPasteHandler.pasteToDestination();
		cellDragPasteHandler.setRangeToCopy(null);
		TabularRange mergedRange = lastSelection.getRange().getRectangularUnion(destinationRange);
		if (mergedRange != null) {
			select(mergedRange, false, true);
		} else {
			select(destinationRange, false, false);
		}
		return success;
	}

	private void extendSelectionByDrag(double x, double y,
			boolean addSelection, boolean pointerUp) {
		if (dragState.startColumn >= 0 || dragState.startRow >= 0) {
			int row = Math.min(findRowOrHeader(y), tabularData.numberOfRows() - 1);
			int column = Math.min(findColumnOrHeader(x), tabularData.numberOfColumns() - 1);
			if (row == -1 && dragState.startRow != -1) {
				autoscrollRow = true;
				return;
			}
			if (column == -1 && dragState.startColumn != -1) {
				autoscrollColumn = true;
				return;
			}
			TabularRange range =
					new TabularRange(dragState.startRow, dragState.startColumn, row, column);
			selectionController.select(new Selection(range), false, addSelection);
			if (column >= layout.findColumn(viewport.getMaxX())) {
				autoscrollColumn = true;
			}
			if (row >= layout.findRow(viewport.getMaxY())) {
				autoscrollRow = true;
			}
			if (pointerUp && viewportAdjuster != null) {
				viewport = viewportAdjuster.adjustViewportIfNeeded(row, column, viewport);
			}
		}
		handleCellReferenceInsertion();
	}

	private void handleCellReferenceInsertion() {
		Optional<Selection> first = selectionController.getSelections().findFirst();
		if (isEditorActive() && editor.isComputedCell() && first.isPresent()
				&& first.get().getType() == SelectionType.CELLS
				&& !first.get().getRange().contains(editor.row, editor.column)) {
			editor.updateReference(first.get().getName(tabularData));
			TabularRange editorRect = new TabularRange(editor.row, editor.column);
			selectionController.select(new Selection(editorRect), false, false);
		}
	}

	private void resizeRow(double y) {
		double height = layout.getHeightForRowResize(dragState.startRow,
				y + viewport.getMinY());
		layout.setHeightForRows(height, dragState.startRow, dragState.startRow);
		resizeCellEditor();
	}

	private void resizeAllSelectedRows(double y) {
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

	private void resizeColumn(double x) {
		double width = layout.getWidthForColumnResize(dragState.startColumn,
				x + viewport.getMinX());
		layout.setWidthForColumns(width, dragState.startColumn, dragState.startColumn);
		resizeCellEditor();
	}

	private void resizeAllSelectedColumns(double x) {
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

	// Key events

	/**
	 * Handles keys being pressed
	 * @param keyCode Key Code
	 * @param key unicode value
	 * @param modifiers Modifiers
	 */
	public void handleKeyPressed(int keyCode, @CheckForNull String key, @Nonnull Modifiers modifiers) {
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
			case JavaKeyCodes.VK_CONTEXT_MENU:
				if (controlsDelegate != null) {
					selectionController.getSelections().findFirst().ifPresent(sel -> {
						int fromRow = sel.getRange().getMinRow();
						int toRow = sel.getRange().getMaxRow();
						int fromCol = sel.getRange().getMinColumn();
						int toCol = sel.getRange().getMaxColumn();
						Rectangle bounds = layout.getBounds(toRow, toCol);
						showContextMenu(bounds.getMaxX(), bounds.getMaxY(), fromRow,
								toRow, fromCol, toCol);
					});
				}
				break;
			case JavaKeyCodes.VK_ENTER:
				showCellEditorAtSelection(true);
				return;
			case JavaKeyCodes.VK_DELETE:
			case JavaKeyCodes.VK_BACK_SPACE:
			case JavaKeyCodes.VK_CLEAR:
				deleteSelectedCells();
				break;
			case JavaKeyCodes.VK_X:
				if (modifiers.ctrlOrCmd) {
					cutSelections();
					return;
				}
				startTyping(key, modifiers);
				break;
			case JavaKeyCodes.VK_C:
				if (modifiers.ctrlOrCmd) {
					copySelections();
					return;
				}
				startTyping(key, modifiers);
				break;
			case JavaKeyCodes.VK_V:
				if (modifiers.ctrlOrCmd) {
					pasteToSelections(selectionController.getSelections()
							.map(Selection::getRange));
					return;
				}
				startTyping(key, modifiers);
				break;
			default:
				startTyping(key, modifiers);
			}
		}
		if (cellSelectionChanged) {
			adjustViewportIfNeeded();
		}
	}

	/**
	 * Hides the cell editor if active, moves input focus down by one cell, and adjusts the
	 * viewport if necessary.
	 */
	void onEnter() {
		commitInput();
		moveDown(false);
		adjustViewportIfNeeded();
		showCellEditorAtSelection(true);
	}

	/**
	 * Hides the cell editor if acgive, moves input focus right by one cell, and adjusts the
	 * viewport if necessary.
	 */
	void onTab() {
		commitInput();
		hideCellEditor();
		moveRight(false);
		adjustViewportIfNeeded();
	}

	/**
	 * Hides the cell editor if active.
	 */
	void onEsc() {
		discardInput();
		hideCellEditor();
	}

	void onEditorTextOrCursorPositionChanged() {
		SpreadsheetReferences editorReferences = new SpreadsheetReferences(
				getEditorCellReferences(), getCurrentEditorCellReference());
		SpreadsheetReferences previousReferences = currentReferences;
		currentReferences = editorReferences;
		if (!Objects.equals(previousReferences, editorReferences)) {
			referencesChanged.notifyListeners(MulticastEvent.VOID);
		}
	}

	private void startTyping(@CheckForNull String key, @Nonnull Modifiers modifiers) {
		if (modifiers.ctrlOrCmd || modifiers.alt || StringUtil.empty(key)) {
			return;
		}
		if (editor == null || !editor.isVisible()) {
			showCellEditorAtSelection(false);
		}
		if (editor != null) {
			editor.type(key);
		}
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
		Selection lastSelection = selectionController.getLastSelection();
		if (lastSelection != null
				&& lastSelection.getRange().getMaxRow() == tabularData.numberOfRows() - 1) {
			insertRowBottom();
		}
		selectionController.moveDown(extendingCurrentSelection, layout.numberOfRows());
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
		Selection lastSelection = selectionController.getLastSelection();
		if (lastSelection != null
				&& lastSelection.getRange().getMaxColumn() == tabularData.numberOfColumns() - 1) {
			insertColumnRight();
		}
		selectionController.moveRight(extendingCurrentSelection, layout.numberOfColumns());
	}

	// Context menu

	private void showContextMenu(double x, double y, int fromRow, int toRow,
			int fromCol, int toCol) {
		if (controlsDelegate != null) {
			controlsDelegate.showContextMenu(
					contextMenuBuilder.build(fromRow, toRow, fromCol, toCol),
					new Point(x, y));
		}
		resetDragAction();
	}

	// Copy / Paste

	private void initCopyPasteCut() {
		if (copyPasteCut == null && controlsDelegate != null) {
			copyPasteCut = new CopyPasteCutTabularDataImpl<>(tabularData,
					controlsDelegate.getClipboard(), layout, selectionController);
		}
	}

	void setCopyPasteCut(@CheckForNull CopyPasteCutTabularData copyPasteCut) {
		this.copyPasteCut = copyPasteCut;
	}

	void pasteCells(int row, int column) {
		if (copyPasteCut == null) {
			return;
		}
		if (!selectionController.hasSelection()) {
			pasteToSelections(Stream.of(new TabularRange(row, column)));
		} else {
			pasteToSelections(selectionController.getSelections().map(Selection::getRange));
		}
	}

	void copyCells(int row, int column) {
		if (copyPasteCut == null) {
			return;
		}
		if (!selectionController.hasSelection()) {
			copyPasteCut.copyDeep(new TabularRange(row, row, column, column));
		} else {
			selectionController.getSelections().forEach(
					selection -> copyPasteCut.copyDeep(selection.getRange()));
		}
	}

	void cutCells(int row, int column) {
		if (copyPasteCut == null) {
			return;
		}
		if (!selectionController.hasSelection()) {
			copyPasteCut.cut(new TabularRange(row, row, column, column));
			updateLayout(column, row);
		} else {
			selectionController.getSelections().forEach(
					selection -> copyPasteCut.cut(selection.getRange()));
			updateLayout(selectionController.getLeftmostSelectedColumnIndex(),
					selectionController.getUppermostSelectedRowIndex());
		}
	}

	private void cutSelections() {
		if (copyPasteCut == null) {
			return;
		}
		getSelections().forEach(selection -> copyPasteCut.cut(selection.getRange()));
		updateLayout(selectionController.getLeftmostSelectedColumnIndex(),
				selectionController.getUppermostSelectedRowIndex());
	}

	private void copySelections() {
		if (copyPasteCut == null) {
			return;
		}
		getSelections().forEach(selection -> copyPasteCut.copyDeep(selection.getRange()));
	}

	private void pasteToSelections(@Nonnull Stream<TabularRange> destinations) {
		if (copyPasteCut == null) {
			return;
		}
		copyPasteCut.readExternalClipboard(externalContent -> {
			int oldColumns = getLayout().numberOfColumns();
			int oldRows = getLayout().numberOfRows();
			String[][] data = externalContent == null ? null
					: DataImport.parseExternalData(null, externalContent, true);
			destinations.forEach(
					destination -> copyPasteCut.paste(destination, data));
			if (copyPasteCut != null) {
				copyPasteCut.selectPastedContent();
			}
			updateLayout(oldColumns - 1, oldRows - 1);
		});
	}

	// Calculations

	void calculate(SpreadsheetCommand command) {
		Selection last = getLastSelection();
		TabularRange range = last == null ? null : last.getRange();

		if (range == null) {
			return;
		}

		if (range.isSingleCell()) {
			processCalculate(command, -1, -1, -1, -1, range.getMinRow(), range.getMinColumn(),
					true);
		} else if (range.isEntireColumn()) {
			processCalculate(command, 0, range.getMinColumn(), getLayout().numberOfRows() - 2,
					range.getMaxColumn(), getLayout().numberOfRows() - 1,
					range.getMaxColumn(), false);
		} else if (range.isEntireRow()) {
			processCalculate(command, range.getMinRow(), 0, range.getMaxRow(),
					getLayout().numberOfColumns() - 2, range.getMaxRow(),
					getLayout().numberOfColumns() - 1, false);
		} else if (range.isPartialColumn()) {
			processCalculate(command, range.getFromRow(), range.getFromColumn(),
					range.getToRow(), range.getToColumn(), range.getToRow() + 1,
					range.getFromColumn(), false);
		} else if (range.isPartialRow()) {
			processCalculate(command, range.getFromRow(), range.getFromColumn(),
					range.getToRow(), range.getToColumn(), range.getFromRow(),
					range.getToColumn() + 1, false);
		} else {
			// multiple part of columns and rows
			processCalculate(command, range.getFromRow(), range.getMinColumn(),
					range.getToRow(), range.getMaxColumn(), range.getMaxRow() + 1,
					range.getMaxColumn(), false);
		}
	}

	private void processCalculate(SpreadsheetCommand command, int fromRow, int fromCol, int toRow,
			int toCol, int destRow, int destCol, boolean showEditor) {
		String curCommand = getCalculateString(command, fromRow, fromCol, toRow, toCol);
		tabularData.getCellProcessor().process(curCommand, destRow, destCol);
		updateSelectionAndScroll(destRow, destCol);
		if (showEditor) {
			showCellEditor(destRow, destCol, true);
			editor.cellEditor.getMathField().onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT));
		}
	}

	private void updateSelectionAndScroll(int destinationRow, int destinationCol) {
		Selection selection = new Selection(destinationRow, destinationCol);
		selectionController.select(selection, false, false);
		if (viewport != null && viewportAdjuster != null) {
			viewport = viewportAdjuster.adjustViewportIfNeeded(destinationRow,
					destinationCol, viewport);
		}
	}

	private String getCalculateString(SpreadsheetCommand command, int fromRow, int fromCol,
			int toRow, int toCol) {
		StringBuilder sb = new StringBuilder();
		sb.append("=");
		sb.append(command.getCommand());
		sb.append("(");
		if (fromRow > -1 && fromCol > -1 && toRow > -1 && toCol > -1) {
			sb.append(tabularData.getCellName(fromRow, fromCol)).append(":")
					.append(tabularData.getCellName(toRow, toCol));
		}
		sb.append(")");

		return sb.toString();
	}

	// Charts

	void createChart(ContextMenuItem.Identifier chartType) {
		Selection last = getLastSelection();
		TabularRange range = last == null ? null : last.getRange();

		if (range == null) {
			return;
		}

		switch (chartType) {
		case PIE_CHART:
			createPieChart(range);
			break;
		case BAR_CHART:
		case HISTOGRAM:
			createChartWithTwoParameter(range, chartType);
			break;
		case LINE_CHART:
			createLineChart(range);
			break;
		default:
		}
	}

	private void createPieChart(TabularRange range) {
		if (constructionDelegate == null || controlsDelegate == null) {
			return;
		}

		if (range.isEntireColumn() || range.isPartialColumn() && !range.isPartialRow()
				&& !range.isEntireRow()) {
			constructionDelegate.createPieChart(tabularData, range);
		} else {
			controlsDelegate.showSnackbar(range.isSingleCell() ? "StatsDialog.NoData"
					: "ChartError.OneColumn");
		}
	}

	private void createChartWithTwoParameter(TabularRange range,
			ContextMenuItem.Identifier chartType) {
		if (constructionDelegate == null || controlsDelegate == null) {
			return;
		}

		if (range.getWidth() == 2) {
			switch (chartType) {
			case BAR_CHART:
				constructionDelegate.createBarChart(tabularData, range);
				break;
			case HISTOGRAM:
				constructionDelegate.createHistogram(tabularData, range);
				break;
			default:
			}
		} else {
			controlsDelegate.showSnackbar(range.isSingleCell() ? "StatsDialog.NoData"
					: "ChartError.TwoColumns");
		}
	}

	private void createLineChart(TabularRange range) {
		if (constructionDelegate == null || controlsDelegate == null) {
			return;
		}

		if (range.getWidth() >= 2) {
			constructionDelegate.createLineGraph(tabularData, range);
		} else {
			controlsDelegate.showSnackbar(range.isSingleCell() ? "StatsDialog.NoData"
					: "ChartError.TwoColumns");
		}
	}

	// Autocomplete

	void onEditorTextChanged() {
		updateAutoCompleteSearchPrefix();
	}

	boolean handleKeyPress(int keyCode) {
		if (controlsDelegate == null) {
			return false;
		}

		switch (keyCode) {
			case JavaKeyCodes.VK_LEFT:
			case JavaKeyCodes.VK_RIGHT:
			case JavaKeyCodes.VK_UP:
			case JavaKeyCodes.VK_DOWN:
			case JavaKeyCodes.VK_ENTER:
				return controlsDelegate.handleKeyPressForAutoComplete(keyCode);
			case JavaKeyCodes.VK_ESCAPE:
				if (controlsDelegate.isAutoCompleteSuggestionsVisible()) {
					controlsDelegate.hideAutoCompleteSuggestions();
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}

	private void updateAutoCompleteSearchPrefix() {
		Rectangle editorBounds = getEditorBounds();
		if (editor == null || editorBounds == null || controlsDelegate == null) {
			return;
		}
		String searchPrefix = editor.cellEditor.getMathField().getCharactersLeftOfCursor();
		if (tabularData.getCellProcessor().isTooShortForAutocomplete(searchPrefix)
				|| !editor.cellEditor.getMathField().getText().startsWith("=")) {
			controlsDelegate.hideAutoCompleteSuggestions();
			return;
		}
		controlsDelegate.showAutoCompleteSuggestions(searchPrefix, editorBounds);
	}

	private final class Editor {
		private final @Nonnull SpreadsheetCellEditor cellEditor;
		private @CheckForNull SpreadsheetMathFieldAdapter mathFieldAdapter;
		@CheckForNull Rectangle bounds;
		int row;
		int column;
		@CheckForNull Object previousCellContent;

		Editor(@Nonnull SpreadsheetCellEditor cellEditor) {
			this.cellEditor = cellEditor;
		}

		void showAt(int row, int column, boolean editExistingContent) {
			this.row = row;
			this.column = column;
			MathFieldInternal mathField = cellEditor.getMathField();
			previousCellContent = tabularData.contentAt(row, column);
			if (editExistingContent) {
				mathField.parse(cellEditor.getCellDataSerializer()
						.getStringForEditor(previousCellContent));
			} else {
				mathField.parse("");
			}

			// If the cell editor is reused without first hiding it,
			// remove the old listener and add the new one after reinitializing.
			mathField.removeMathFieldListener(mathFieldAdapter);
			if (mathFieldAdapter != null) {
				mathField.unregisterMathFieldInternalListener(mathFieldAdapter);
			}

			mathFieldAdapter = new SpreadsheetMathFieldAdapter(mathField, row, column,
					cellEditor.getCellProcessor(), SpreadsheetController.this);
			mathField.addMathFieldListener(mathFieldAdapter);
			mathField.registerMathFieldInternalListener(mathFieldAdapter);

			mathField.setUnhandledArrowListener(mathFieldAdapter);

			bounds = layout.getBounds(new TabularRange(row, column), viewport);
			if (bounds != null) {
				cellEditor.show(bounds.insetBy(1, 1), viewport, getAlignment(row, column));
			}
		}

		void updatePosition() {
			bounds = layout.getBounds(new TabularRange(row, column), viewport);
			if (bounds != null) {
				cellEditor.updatePosition(bounds.insetBy(1, 1), viewport);
			}
		}

		void hide() {
			cellEditor.getMathField().removeMathFieldListener(mathFieldAdapter);
			bounds = null;
			cellEditor.hide();
		}

		boolean isVisible() {
			return bounds != null;
		}

		void type(@CheckForNull String key) {
			if (key == null) {
				return;
			}
			KeyboardInputAdapter.type(cellEditor.getMathField(), key);
		}

		void commitInput() {
			if (mathFieldAdapter != null) {
				mathFieldAdapter.commitInput();
			}
			previousCellContent = null;
		}

		void discardInput() {
			// restore previous cell content
			tabularData.setContent(row, column, previousCellContent);
			previousCellContent = null;
		}

		boolean isComputedCell() {
			return cellEditor.getMathField().getText().startsWith("=");
		}

		void updateReference(String reference) {
			Predicate<MathCharacter> predicate =
					w -> w.isCharacter() || ":".equals(w.getUnicodeString());
			String[] parts = reference.split(":");
			String startCell = parts[0].trim();
			String endCell = parts.length > 1 ? parts[1].trim() : startCell;
			String currentWord = cellEditor.getMathField()
					.getCharactersLeftOfCursorMatching(predicate);
			boolean spaceNeeded = !currentWord.isEmpty();
			if (currentWord.endsWith(":" + endCell)
					|| currentWord.startsWith(startCell + ":")
					|| currentWord.equals(endCell) || currentWord.equals(startCell)) {
				cellEditor.getMathField().deleteCurrentCharSequence(
						predicate);
				spaceNeeded = false;
			}
			type(spaceNeeded ? " " + reference : reference);
		}

		private @CheckForNull String getCurrentCellRangeCandidate() {
			Predicate<MathCharacter> predicate =
					w -> w.isCharacter() || ":".equals(w.getUnicodeString());
			String candidate = cellEditor.getMathField()
					.getCharactersAroundCursorMatching(predicate);
			return candidate == null || candidate.isEmpty() ? null : candidate;
		}
	}
}
