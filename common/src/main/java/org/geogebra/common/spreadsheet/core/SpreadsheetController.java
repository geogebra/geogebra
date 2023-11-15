package org.geogebra.common.spreadsheet.core;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularSelection {

	private final ContextMenuItems contextMenuItems;
	private SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	final private TabularData<?> tabularData;

	private SpreadsheetControlsDelegate controlsDelegate;
	private final TableLayout layout;

	private final SpreadsheetStyle style;
	private MouseCursor activeCursor = MouseCursor.DEFAULT;
	private final GPoint lastPointerDown = new GPoint(-1, -1);

	/**
	 * @param tabularData underlying data for the spreadsheet
	 */
	public SpreadsheetController(TabularData<?> tabularData) {
		this.tabularData = tabularData;
		style = new SpreadsheetStyle(tabularData.getFormat());
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), TableLayout.DEFAUL_CELL_HEIGHT,
				TableLayout.DEFAULT_CELL_WIDTH);
		contextMenuItems = new ContextMenuItems(tabularData, selectionController,
				getCopyPasteCut());
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
	public void select(Selection selection, boolean extend, boolean addSelection) {
		selectionController.select(selection, extend, addSelection);
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

	boolean showCellEditor(int row, int column, Rectangle viewport) {
		if (controlsDelegate != null) {
			Rectangle editorBounds = layout.getBounds(row, column)
					.translatedBy(-viewport.getMinX() + layout.getRowHeaderWidth(),
							-viewport.getMinY() + layout.getColumnHeaderHeight());
			SpreadsheetCellEditor editor = controlsDelegate.getCellEditor();
			editor.setBounds(editorBounds);

			editor.setContent(tabularData.contentAt(row, column));
			editor.setTargetCell(row, column);
			return true;
		}
		return false;
	}

	private void hideCellEditor() {
		if (controlsDelegate != null) {
			controlsDelegate.hideCellEditor();
		}
	}

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		this.controlsDelegate = controlsDelegate;
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 * @param viewport visible area
	 * @return whether the event caused changes in spreadsheet requiring repaint
	 */
	public boolean handlePointerDown(int x, int y, Modifiers modifiers, Rectangle viewport) {
		hideCellEditor();
		activeCursor = layout.getCursor(x + viewport.getMinX(), y + viewport.getMinY(),
				lastPointerDown);
		int column = layout.findColumn(x + viewport.getMinX());
		int row = layout.findRow(y + viewport.getMinY());
		if (modifiers.rightButton) {
			GPoint coords = new GPoint(x, y);
			controlsDelegate.showContextMenu(contextMenuItems.get(row, column), coords);
			return true;
		}
		if (row >= 0 && column >= 0 && isSelected(row, column)) {
			return showCellEditor(row, column, viewport);
		}
		return false;
	}

	public MouseCursor getCursor(int x, int y, Rectangle viewport) {
		return layout.getCursor(x + viewport.getMinX(), y + viewport.getMinY(), new GPoint());
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 * @param viewport visible area
	 */
	public void handlePointerUp(int x, int y, Modifiers modifiers, Rectangle viewport) {
		if (finishDrag(x, y, modifiers)) {
			return;
		}
		int row = layout.findRow(y + viewport.getMinY());
		int column = layout.findColumn(x + viewport.getMinX());

		if (column < 0) { // Select row
			selectRow(row, modifiers.shift, modifiers.ctrl);
		} else if (row < 0) { // Select column
			selectColumn(column, modifiers.shift, modifiers.ctrl);
		} else { // Select cell
			select(new Selection(SelectionType.CELLS, TabularRange.range(row,
					row, column, column)), modifiers.shift, modifiers.ctrl);
		}
	}

	private boolean finishDrag(int x, int y, Modifiers modifiers) {
		List<Selection> sel = getSelections();
		boolean handled = false;
		switch (activeCursor) {
		case RESIZE_X:
			if (isSelected(-1, lastPointerDown.x)) {
				double width = layout.resizeColumn(lastPointerDown.x, x);
				for (Selection selection : sel) {
					if (selection.getType() == SelectionType.COLUMNS) {
						layout.setWidthForColumns(width, selection.getRange().getMinColumn(),
								selection.getRange().getMaxColumn());
					}
				}
			}
			handled = true;
			break;
		case RESIZE_Y:
			if (isSelected(lastPointerDown.y, -1)) {
				double height = layout.resizeRow(lastPointerDown.y, y);
				for (Selection selection : sel) {
					if (selection.getType() == SelectionType.ROWS) {
						layout.setHeightForRows(height, selection.getRange().getMinRow(),
								selection.getRange().getMaxRow());
					}
				}
			}
			handled = true;
			break;
		case DEFAULT:
			handled = extendSelectionByDrag(x, y, modifiers.ctrl);
		}
		activeCursor = MouseCursor.DEFAULT;
		lastPointerDown.setLocation(-1, -1);
		return handled;
	}

	/**
	 * Handles keys being pressed
	 * @param keyCode Key Code
	 * @param modifiers Modifiers
	 * @return Whether the event caused changes in the spreadsheet requiring repaint
	 */
	public boolean handleKeyPressed(int keyCode, Modifiers modifiers) {
		if (selectionController.hasSelection()) {
			switch (keyCode) {
			case JavaKeyCodes.VK_LEFT:
				moveLeft(modifiers.shift);
				return true;
			case JavaKeyCodes.VK_RIGHT:
				moveRight(modifiers.shift);
				return true;
			case JavaKeyCodes.VK_UP:
				moveUp(modifiers.shift);
				return true;
			case JavaKeyCodes.VK_DOWN:
				moveDown(modifiers.shift);
				return true;
			case JavaKeyCodes.VK_A:
				if (modifiers.ctrl) {
					selectionController.selectAll(layout.numberOfRows(), layout.numberOfColumns());
					return true;
				}
			default:
				return false;
			}
		}
		return false;
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

	public Selection getLastSelection() {
		return selectionController.getLastSelection();
	}

	/**
	 * @param x event x-coordinate in pixels
	 * @param y event y-coordinate in pixels
	 * @param modifiers alt/ctrl/shift
	 * @return whether something changed and repaint is needed
	 */
	public boolean handlePointerMove(int x, int y, Modifiers modifiers) {
		switch (activeCursor) {
		case RESIZE_X:
			// only handle the dragged column here, the rest of selection on pointer up
			// otherwise left border of dragged column could move, causing feedback loop
			double width = layout.resizeColumn(lastPointerDown.x, x);
			layout.setWidthForColumns(width, lastPointerDown.x, lastPointerDown.x);
			return true;
		case RESIZE_Y:
			double height = layout.resizeRow(lastPointerDown.y, y);
			layout.setHeightForRows(height, lastPointerDown.y, lastPointerDown.y);
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
		// TODO drag selection for columns and rows
		if (lastPointerDown.x >= 0 && lastPointerDown.y >= 0) {
			int row = getLayout().findRow(y);
			int column = getLayout().findColumn(x);
			if (row != lastPointerDown.getY() || column != lastPointerDown.getX()) {
				TabularRange range =
						new TabularRange(lastPointerDown.y, lastPointerDown.x, row, column);
				selectionController.select(new Selection(SelectionType.CELLS,
								range), false, addSelection);
				return true;
			}
		}
		return false;
	}

	private TabularRange intersectWithDataRange(Selection selection) {
		return selection.getRange().restrictTo(tabularData.numberOfRows(),
				tabularData.numberOfColumns());
	}

}
