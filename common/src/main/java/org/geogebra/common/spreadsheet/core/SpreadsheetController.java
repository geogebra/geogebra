package org.geogebra.common.spreadsheet.core;

import java.util.List;
import java.util.stream.Collectors;

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
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularSelection {

	private final ContextMenuItems contextMenuItems;
	private final SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	final private TabularData<?> tabularData;

	private @CheckForNull SpreadsheetControlsDelegate controlsDelegate;
	private final TableLayout layout;

	private final SpreadsheetStyle style;
	private DragAction dragAction;
	private Rectangle viewport;
	private @CheckForNull ViewportAdjuster viewportAdjuster;

	/**
	 * @param tabularData underlying data for the spreadsheet
	 * @param viewport Visible area
	 */
	public SpreadsheetController(TabularData<?> tabularData, Rectangle viewport) {
		this.tabularData = tabularData;
		initViewport(viewport);
		resetDragAction();
		style = new SpreadsheetStyle(tabularData.getFormat());
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), TableLayout.DEFAUL_CELL_HEIGHT,
				TableLayout.DEFAULT_CELL_WIDTH);
		contextMenuItems = new ContextMenuItems(tabularData, selectionController,
				getCopyPasteCut());
	}

	private void initViewport(Rectangle viewport) {
		if (viewport == null) {
			this.viewport = new Rectangle(0, 0, 0, 0);
		} else {
			this.viewport = viewport;
		}
	}

	private CopyPasteCutTabularData getCopyPasteCut() {
		return controlsDelegate != null
				? new CopyPasteCutTabularDataImpl<>(tabularData, controlsDelegate.getClipboard())
				: null;
	}

	TableLayout getLayout() {
		return layout;
	}

	SpreadsheetStyle getStyle() {
		return style;
	}

	// - TabularData

	public Object contentAt(int row, int column) {
		return tabularData.contentAt(row, column);
	}

	// - TabularSelection

	@Override
	public void clearSelection() {
		selectionController.clearSelection();
	}

	@Override
	public void selectRow(int row, boolean extend, boolean addSelection) {
		selectionController.selectRow(row, extend, addSelection);
	}

	@Override
	public void selectColumn(int column, boolean extend, boolean addSelection) {
		selectionController.selectColumn(column, extend, addSelection);
	}

	/**
	 * @param selection Selection that is to be selected
	 * @param extend Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add the selection to the current selection (CTRL)
	 */
	@Override
	public boolean select(TabularRange selection, boolean extend, boolean addSelection) {
		return selectionController.select(new Selection(SelectionType.CELLS, selection),
				extend, addSelection);
	}

	@Override
	public void selectAll() {
		selectionController.selectAll(layout.numberOfRows(), layout.numberOfColumns());
	}

	public void selectCell(int rowIndex, int columnIndex, boolean extend, boolean addSelection) {
		selectionController.selectCell(rowIndex, columnIndex, extend, addSelection);
	}

	// default visibility, same as Selection class
	List<Selection> getSelections() {
		return selectionController.selections();
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
		if (controlsDelegate != null) {
			Rectangle editorBounds = layout.getBounds(row, column)
					.translatedBy(-viewport.getMinX() + layout.getRowHeaderWidth(),
							-viewport.getMinY() + layout.getColumnHeaderHeight());
			SpreadsheetCellEditor editor = controlsDelegate.getCellEditor();
			editor.setBounds(editorBounds);

			editor.setContent(tabularData.contentAt(row, column));
			editor.setAlign(tabularData.getAlignment(row, column));
			editor.setTargetCell(row, column);
			resetDragAction();
			return true;
		}
		return false;
	}

	/**
	 * Process the editor input, update corresponding cell and hide the editor
	 */
	public void saveContentAndHideCellEditor() {
		if (controlsDelegate != null && controlsDelegate.getCellEditor() != null) {
			controlsDelegate.getCellEditor().onEnter();
			controlsDelegate.getCellEditor().hide();
		}
	}

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		this.controlsDelegate = controlsDelegate;
	}

	public void setViewportAdjuster(ViewportAdjuster viewportAdjuster) {
		this.viewportAdjuster = viewportAdjuster;
	}

	public void createViewportAdjuster(Scrollable scrollable) {
		this.viewportAdjuster = new ViewportAdjuster(getLayout(), scrollable);
	}

	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 * @return whether the event caused changes in spreadsheet requiring repaint
	 */
	public boolean handlePointerDown(int x, int y, Modifiers modifiers) {
		saveContentAndHideCellEditor();
		if (controlsDelegate != null) {
			controlsDelegate.hideContextMenu();
		}
		dragAction = getDragAction(x, y);
		if (modifiers.shift) {
			setDragStartLocationFromSelection();
		}
		if (dragAction.activeCursor != MouseCursor.DEFAULT) {
			return true;
		}
		int column = findColumnOrHeader(x);
		int row = findRowOrHeader(y);

		boolean changed = false;
		if (viewportAdjuster != null) {
			changed = viewportAdjuster.adjustViewportHorizontallyIfNeeded(column, viewport)
					| viewportAdjuster.adjustViewportVerticallyIfNeeded(row, viewport);
		}

		if (modifiers.rightButton && controlsDelegate != null) {
			selectionController.clearSelection();
			GPoint coords = new GPoint(x, y);
			controlsDelegate.showContextMenu(contextMenuItems.get(row, column), coords);
			resetDragAction();
		}
		if (row >= 0 && column >= 0 && isSelected(row, column)) {
			return showCellEditor(row, column);
		}
		if (!modifiers.ctrl && !modifiers.shift && selectionController.hasSelection()) {
			selectionController.clearSelection();
			changed = true;
		}
		if (column < 0) { // Select row
			selectRow(row, modifiers.shift, modifiers.ctrl);
			changed = true;
		} else if (row < 0) { // Select column
			selectColumn(column, modifiers.shift, modifiers.ctrl);
			changed = true;
		} else { // Select cell
			changed = select(TabularRange.range(row, row, column, column),
					modifiers.shift, modifiers.ctrl) || changed;
		}
		return changed;
	}

	private int findRowOrHeader(int y) {
		return y < layout.getColumnHeaderHeight() ? -1
				: layout.findRow(y + viewport.getMinY());
	}

	private int findColumnOrHeader(int x) {
		return x < layout.getRowHeaderWidth() ? - 1
				: layout.findColumn(x + viewport.getMinX());
	}

	private void setDragStartLocationFromSelection() {
		Selection lastSelection = selectionController.getLastSelection();
		if (lastSelection != null) {
			TabularRange lastRange = lastSelection.getRange();
			dragAction = new DragAction(MouseCursor.DEFAULT,
					lastRange.getMinColumn(), lastRange.getMinRow());
		}
	}

	DragAction getDragAction(int x, int y) {
		GPoint2D draggingDot = getDraggingDot();
		if (draggingDot != null && draggingDot.distance(x, y) < 18) {
			return new DragAction(MouseCursor.DRAG_DOT, layout.findRow(y + viewport.getMinY()),
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
		List<Selection> sel = getSelections();
		switch (dragAction.activeCursor) {
		case RESIZE_X:
			if (isSelected(-1, dragAction.column)) {
				double width = layout.getWidthForColumnResize(dragAction.column,
						x + viewport.getMinX());
				for (Selection selection : sel) {
					if (selection.getType() == SelectionType.COLUMNS) {
						layout.setWidthForColumns(width, selection.getRange().getMinColumn(),
								selection.getRange().getMaxColumn());
					}
				}
			}
			break;
		case RESIZE_Y:
			if (isSelected(dragAction.row, -1)) {
				double height = layout.getHeightForRowResize(dragAction.row,
						y + viewport.getMinY());
				for (Selection selection : sel) {
					if (selection.getType() == SelectionType.ROWS) {
						layout.setHeightForRows(height, selection.getRange().getMinRow(),
								selection.getRange().getMaxRow());
					}
				}
			}
			break;
		case DEFAULT:
		default:
			extendSelectionByDrag(x, y, modifiers.ctrl);
		// TODO implement formula propagation with DRAG_DOT
		}
		resetDragAction();
	}

	private void resetDragAction() {
		dragAction = new DragAction(MouseCursor.DEFAULT, -1, -1);
	}

	/**
	 * Handles keys being pressed
	 * @param keyCode Key Code
	 * @param key unicode value
	 * @param modifiers Modifiers
	 * @return Whether the event caused changes in the spreadsheet requiring repaint
	 */
	public boolean handleKeyPressed(int keyCode, String key, Modifiers modifiers) {
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
				if (modifiers.ctrl) {
					selectionController.selectAll(layout.numberOfRows(), layout.numberOfColumns());
					return true;
				}
			case JavaKeyCodes.VK_ENTER:
				showCellEditorAtSelection();
				return true;
			default:
				SpreadsheetControlsDelegate controls = controlsDelegate;
				if (!modifiers.ctrl && !modifiers.alt && !StringUtil.empty(key)
					&& controls != null) {
					showCellEditorAtSelection();
					controls.getCellEditor().setContent(key);
				}
				return false;
			}
		}
		if (cellSelectionChanged) {
			adjustViewportHorizontallyIfNeeded();
			adjustViewportVerticallyIfNeeded();
			return true;
		}
		return false;
	}

	private void showCellEditorAtSelection() {
		Selection last = getLastSelection();
		TabularRange range = last == null ? null : last.getRange();
		if (range != null) {
			showCellEditor(range.getFromRow(), range.getFromColumn());
		}
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
	 * Adjusts the viewport horizontally if the selected cell or column is not fully visible
	 */
	private void adjustViewportHorizontallyIfNeeded() {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null && viewportAdjuster != null) {
			viewportAdjuster.adjustViewportHorizontallyIfNeeded(
					lastSelection.getRange().getToColumn(), viewport);
		}
	}

	/**
	 * Adjusts the viewport vertically if the selected cell or row is not fully visible
	 */
	private void adjustViewportVerticallyIfNeeded() {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null && viewportAdjuster != null) {
			viewportAdjuster.adjustViewportVerticallyIfNeeded(
					lastSelection.getRange().getToRow(), viewport);
		}
	}

	@CheckForNull Selection getLastSelection() {
		return selectionController.getLastSelection();
	}

	/**
	 * @param x event x-coordinate in pixels
	 * @param y event y-coordinate in pixels
	 * @param modifiers alt/ctrl/shift
	 * @return whether something changed and repaint is needed
	 */
	public boolean handlePointerMove(int x, int y, Modifiers modifiers) {
		switch (dragAction.activeCursor) {
		case RESIZE_X:
			// only handle the dragged column here, the rest of selection on pointer up
			// otherwise left border of dragged column could move, causing feedback loop
			double width = layout.getWidthForColumnResize(dragAction.column,
					x + viewport.getMinX());
			layout.setWidthForColumns(width, dragAction.column, dragAction.column);
			return true;
		case RESIZE_Y:
			double height = layout.getHeightForRowResize(dragAction.row,
					y + viewport.getMinY());
			layout.setHeightForRows(height, dragAction.row, dragAction.row);
			return true;
		default:
		case DEFAULT:
			return extendSelectionByDrag(x, y, modifiers.ctrl);
		}
	}

	/**
	 * @return selections limited to data size
	 */
	public List<TabularRange> getVisibleSelections() {
		return getSelections().stream().map(this::intersectWithDataRange)
				.collect(Collectors.toList());
	}

	private boolean extendSelectionByDrag(int x, int y, boolean addSelection) {
		if (dragAction.column >= 0 || dragAction.row >= 0) {
			int row = findRowOrHeader(y);
			int column = findColumnOrHeader(x);

			TabularRange range =
					new TabularRange(dragAction.row, dragAction.column, row, column);
			SelectionType type = getDragSelectionType();
			return selectionController.select(new Selection(type,
							range), false, addSelection);

		}
		return false;
	}

	private SelectionType getDragSelectionType() {
		if (dragAction.column < 0) {
			return SelectionType.ROWS;
		}
		if (dragAction.row < 0) {
			return SelectionType.COLUMNS;
		}
		return SelectionType.CELLS;
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
		return selectionController.selections().stream()
				.anyMatch(sel -> sel.getRange().intersectsColumn(column));
	}

	/**
	 * @param row row index
	 * @return whether selection contains at least one cell in given row
	 */
	public boolean isSelectionIntersectingRow(int row) {
		return selectionController.selections().stream()
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
	 * @return whether editor is currently visible
	 */
	public boolean isEditorActive() {
		return controlsDelegate != null && controlsDelegate.getCellEditor().isVisible();
	}
}
