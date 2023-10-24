package org.geogebra.common.spreadsheet.core;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.shape.Rectangle;

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

	private final SpreadsheetStyle style = new SpreadsheetStyle();

	/**
	 * @param tabularData underlying data for the spreadsheet
	 */
	public SpreadsheetController(TabularData<?> tabularData) {
		this.tabularData = tabularData;
		layout = new TableLayout(tabularData.numberOfRows(),
				tabularData.numberOfColumns(), 20, 40);
		contextMenuItems = new ContextMenuItems(tabularData, selectionController,
				getCopyPasteCut());
	}

	private CopyPasteCutTabularData getCopyPasteCut() {
		return controlsDelegate != null
				? new CopyPasteCutTabularDataImpl(tabularData, controlsDelegate.getClipboard())
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
			GPoint coords = new GPoint(x, y);
			controlsDelegate.showContextMenu(contextMenuItems.get(row, column), coords);
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
		select(new Selection(SelectionType.CELLS, new TabularRange(row,
				row, column, column)), modifiers.ctrl);
	}
}
