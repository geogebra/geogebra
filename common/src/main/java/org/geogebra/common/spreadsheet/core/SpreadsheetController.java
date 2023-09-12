package org.geogebra.common.spreadsheet.core;

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
	public void selectRow(int row) {
		selectionController.selectRow(row, false);
	}

	@Override
	public void selectColumn(int column) {
		selectionController.selectColumn(column, false);
	}

	@Override
	public void select(Selection selection, boolean extend) {
		selectionController.select(selection, extend);
	}

	@Override
	public void selectAll() {
		selectionController.selectAll();
	}

	// default visibility, same as Selection class
	List<Selection> getSelection() {
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

	public void handlePointerUp(int x, int y, Modifiers modifiers, Rectangle viewport) {
		int row = layout.findRow(y + viewport.getMinY());
		int column = layout.findColumn(x + viewport.getMinX());
		select(new Selection(SelectionType.CELLS, new TabularRange(row,
				row, column, column)), modifiers.ctrl);
	}
}
