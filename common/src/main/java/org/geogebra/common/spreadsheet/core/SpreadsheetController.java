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

	//@NonOwning
	private SpreadsheetControlsDelegate controlsDelegate;
	private final TableLayout layout;

	private final SpreadsheetStyle style;
	private DragAction dragAction;
	private Rectangle viewport = new Rectangle(0, 0, 0, 0);
	private @CheckForNull ViewportAdjuster viewportAdjuster;

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
		contextMenuItems = new ContextMenuItems(tabularData, selectionController,
				getCopyPasteCut());
	}

	// TODO move out of the way (further down), group with related methods if possible
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
	public void select(TabularRange selection, boolean extend, boolean addSelection) {
		return selectionController.select(new Selection(selection),
				extend, addSelection);
	}

	@Override
	public void selectAll() {
		// TODO I think the selectionController should know the number of rows/columns,
		//  so I'd add a field and setter (call when the data shape changes)
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
		if (controlsDelegate == null) {
			return false;
		}
		// TODO maybe this even warrants a method getEditorBounds(row, column)
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

	public void setViewportAdjustmentHandler(ViewportAdjustmentHandler viewportAdjustmentHandler) {
		this.viewportAdjuster = new ViewportAdjuster(getLayout(), viewportAdjustmentHandler);
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
		// TODO encapsulate this low-level check into a method with a meaningful name, maybe
		// if (dragAction.isModifyingOperation())
		if (dragAction.cursor != MouseCursor.DEFAULT) {
			return;
		}
		int column = findColumnOrHeader(x);
		int row = findRowOrHeader(y);

		if (viewportAdjuster != null) {

			viewportAdjuster.adjustViewportIfNeeded(row, column, viewport);
		}

		if (modifiers.secondaryButton && controlsDelegate != null) {
			selectionController.clearSelection();
			if (controlsDelegate != null) {
				GPoint point = new GPoint(x, y);
				controlsDelegate.showContextMenu(contextMenuItems.get(row, column), point);
			}
			resetDragAction();
			return;
		}

		if (row >= 0 && column >= 0 && selectionController.isOnlyCellSelected(row, column)) {
			showCellEditor(row, column);
			return;
		}
		if (!modifiers.ctrlOrCmd && !modifiers.shift && selectionController.hasSelection()) {
			selectionController.clearSelection();
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
		// TODO fold this logic into TableLayout? (since it knows about column header heights anyway?)
		return y < layout.getColumnHeaderHeight() ? -1
				: layout.findRow(y + viewport.getMinY());
	}

	private int findColumnOrHeader(int x) {
		return x < layout.getRowHeaderWidth() ? - 1
				: layout.findColumn(x + viewport.getMinX());
	}

	private void setDragStartLocationFromSelection() {
		Selection lastSelection = selectionController.getLastSelection();
		if (lastSelection == null) {
			return;
		}
		TabularRange lastRange = lastSelection.getRange();
		dragAction = new DragAction(MouseCursor.DEFAULT,
				lastRange.getMinColumn(), lastRange.getMinRow());
	}

	DragAction getDragAction(int x, int y) {
		GPoint2D draggingDot = getDraggingDot();
		if (draggingDot != null && draggingDot.distance(x, y) < 18) {
			return new DragAction(MouseCursor.DRAG_DOT, layout.findRow(y + viewport.getMinY()),
					layout.findColumn(x + viewport.getMinX()));
		}
		return layout.getResizeAction(x, y, viewport);
	}

	// TODO this should go next to handlePointerDown() (group related methods together)
	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 */
	public void handlePointerUp(int x, int y, Modifiers modifiers) {
		List<Selection> selections = getSelections();
		switch (dragAction.cursor) {
		case RESIZE_X:
			if (isSelected(-1, dragAction.column)) {
				double width = layout.getWidthForColumnResize(dragAction.column,
						x + viewport.getMinX());
				for (Selection selection : selections) {
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
				for (Selection selection : selections) {
					if (selection.getType() == SelectionType.ROWS) {
						layout.setHeightForRows(height, selection.getRange().getMinRow(),
								selection.getRange().getMaxRow());
					}
				}
			}
			break;
		case DEFAULT:
		default:
			extendSelectionByDrag(x, y, modifiers.ctrlOrCmd);
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
					selectionController.selectAll(layout.numberOfRows(), layout.numberOfColumns());
					return;
				}
			case JavaKeyCodes.VK_ENTER:
				showCellEditorAtSelection();
				return;
			default:
				SpreadsheetControlsDelegate controls = controlsDelegate;
				if (!modifiers.ctrlOrCmd && !modifiers.alt && !StringUtil.empty(key)
					&& controls != null) {
					showCellEditorAtSelection();
					controls.getCellEditor().setContent(key);
				}
				return;
			}
		}
		if (cellSelectionChanged) {
			adjustViewportIfNeeded();
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
	 * @return whether something changed and repaint is needed
	 */
	public void handlePointerMove(int x, int y, Modifiers modifiers) {
		switch (dragAction.cursor) {
		case RESIZE_X:
			// only handle the dragged column here, the rest of selection on pointer up
			// otherwise left border of dragged column could move, causing feedback loop
			double width = layout.getWidthForColumnResize(dragAction.column,
					x + viewport.getMinX());
			layout.setWidthForColumns(width, dragAction.column, dragAction.column);
			break;
		case RESIZE_Y:
			double height = layout.getHeightForRowResize(dragAction.row,
					y + viewport.getMinY());
			layout.setHeightForRows(height, dragAction.row, dragAction.row);
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
		return getSelections().stream().map(this::intersectWithDataRange)
				.collect(Collectors.toList());
	}

	private void extendSelectionByDrag(int x, int y, boolean addSelection) {
		if (dragAction.column >= 0 || dragAction.row >= 0) {
			int row = findRowOrHeader(y);
			int column = findColumnOrHeader(x);

			TabularRange range =
					new TabularRange(dragAction.row, dragAction.column, row, column);
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
