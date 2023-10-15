package org.geogebra.common.spreadsheet.core;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A container for tabular data, with support for selecting parts of the data.
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class SpreadsheetController implements TabularSelection {

	private SpreadsheetSelectionController selectionController
			= new SpreadsheetSelectionController();
	final private TabularData<?> tabularData;

	private SpreadsheetControlsDelegate controlsDelegate;
	private final TableLayout layout;

	private final SpreadsheetStyle style = new SpreadsheetStyle();

	/**
	 * @param tabularData underlying data for the spreadsheet
	 */
	public SpreadsheetController(TabularData<?> tabularData) {
		this.tabularData = tabularData;
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), 20, 40);
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
		selectionController.selectRow(row, layout.numberOfColumns(), extend, addSelection);
	}

	@Override
	public void selectColumn(int column, boolean extend, boolean addSelection) {
		selectionController.selectColumn(column, layout.numberOfRows(), extend, addSelection);
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

	/**
	 * @param column column number
	 * @return actions for the column (map action's ggbtrans key -> action)
	 */
	public Map<String, Runnable> getContextMenu(int column) {
		HashMap<String, Runnable> actions = new HashMap<>();
		actions.put("InsertColumn", () -> tabularData.insertColumnAt(column));
		actions.put("DeleteColumn", () -> tabularData.deleteColumnAt(column));
		return actions;
	}

	boolean showCellEditor(int row, int column, Rectangle viewport) {
		if (controlsDelegate != null) {
			Rectangle editorBounds = layout.getBounds(row, column)
					.translatedBy(-viewport.getMinX() + layout.getRowHeaderWidth(),
							-viewport.getMinY() + layout.getColumnHeaderHeight());
			SpreadsheetCellEditor editor = controlsDelegate.getCellEditor();
			editor.setBounds(editorBounds);

			editor.setContent(tabularData.getEditableString(row, column));
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
		int column = layout.findColumn(x + viewport.getMinX());
		int row = layout.findRow(y + viewport.getMinY());
		if (modifiers.rightButton) {
			controlsDelegate.showContextMenu(getContextMenu(column), new GPoint(x, y));

			return true;
		}
		if (isSelected(row, column)) {
			return showCellEditor(row, column, viewport);
		}
		return false;
	}

	/**
	 * @param x x-coordinate relative to viewport
	 * @param y y-coordinate relative to viewport
	 * @param modifiers event modifiers
	 * @param viewport visible area
	 */
	public void handlePointerUp(int x, int y, Modifiers modifiers, Rectangle viewport) {
		int row = layout.findRow(y + viewport.getMinY());
		int column = layout.findColumn(x + viewport.getMinX());

		if (column < 0) { // Select row
			selectRow(row, modifiers.shift, modifiers.ctrl);
		} else if (row < 0) { // Select column
			selectColumn(column, modifiers.shift, modifiers.ctrl);
		} else { // Select cell
			select(new Selection(SelectionType.CELLS, new TabularRange(row,
					row, column, column)), modifiers.shift, modifiers.ctrl);
		}
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
			case KeyEvent.VK_LEFT:
				moveLeft(modifiers.shift);
				return true;
			case KeyEvent.VK_RIGHT:
				moveRight(modifiers.shift);
				return true;
			case KeyEvent.VK_UP:
				moveUp(modifiers.shift);
				return true;
			case KeyEvent.VK_DOWN:
				moveDown(modifiers.shift);
				return true;
			case KeyEvent.VK_A:
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
}
