package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A spreadsheet (of arbitrary size).
 *
 * @Note: This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularData, TabularSelection {

	private final CellularDataSource source;
	private final SpreadsheetEventHandler eventDispatcher;
	private final SpreadsheetController controller;
	private final TableLayout layout;

	private boolean needsRedraw = true;

	private SpreadsheetStyle style = new SpreadsheetStyle();
	private final SpreadsheetRenderer renderer;
	private Rectangle viewport;
	private SpreadsheetDelegate delegate;

	public Spreadsheet(int initialNumberOfRows, int initialNumberOfColumns, CellularDataSource source) {
		renderer = new SpreadsheetRenderer(this);
		controller = new SpreadsheetController(initialNumberOfRows, initialNumberOfColumns);
		layout = new TableLayout(initialNumberOfRows, initialNumberOfColumns, 20, 40);
		eventDispatcher = new SpreadsheetEventHandler(this, layout);
		this.source = source;
	}

	// editing & validation

	void beginEditing(int row, int col) {
		// TODO show editor
	}

	// TODO validation

	void endEditing() { }

	// layout

	// styling

	// drawing
	public void draw(GGraphics2D graphics) {
		if (!needsRedraw) {
			return;
		}
		renderer.draw(graphics, viewport);
		for (Selection selection: controller.getSelection()) {
			renderer.drawSelection(selection.getRange(), graphics, viewport);
		}
		needsRedraw = false;
	}

	// keyboard (use com.himamis.retex.editor.share.event.KeyListener?)

	// touch

	// - TabularData

	@Override
	public void reset(int rows, int columns) {
	}

	@Override
	public int numberOfRows() {
		return layout.numberOfRows();
	}

	@Override
	public int numberOfColumns() {
		return layout.numberOfColumns();
	}

	@Override
	public void appendRows(int numberOfRows) {
		controller.appendRows(numberOfRows);
		needsRedraw = true;
	}

	@Override
	public void insertRowAt(int row) {
		controller.insertRowAt(row);
		needsRedraw = true;
	}

	@Override
	public void deleteRowAt(int row) {
		controller.deleteRowAt(row);
		needsRedraw = true;
	}

	@Override
	public void appendColumns(int numberOfColumns) {
		controller.appendColumns(numberOfColumns);
		needsRedraw = true;
	}

	@Override
	public void insertColumnAt(int column) {
		controller.insertColumnAt(column);
		needsRedraw = true;
	}

	@Override
	public void deleteColumnAt(int column) {
		controller.deleteColumnAt(column);
		needsRedraw = true;
	}

	@Override
	public void setContent(int row, int column, Object content) {
		controller.setContent(row, column, content);
		needsRedraw = true;
	}

	@Override
	public Object contentAt(int row, int column) {
		return controller.contentAt(row, column);
	}

	// - TabularSelection

	@Override
	public void clearSelection() {
		controller.clearSelection();
		needsRedraw = true;
	}

	@Override
	public void selectRow(int row) {
		controller.selectRow(row);
		needsRedraw = true;
	}

	@Override
	public void selectColumn(int column) {
		controller.selectColumn(column);
		needsRedraw = true;
	}

	@Override
	public void select(Selection selection, boolean extend) {
		controller.select(selection, extend);
		needsRedraw = true;
	}

	@Override
	public void selectAll() {
		controller.selectAll();
		needsRedraw = true;
	}

	public TableLayout getLayout() {
		return layout;
	}

	public void setViewport(Rectangle viewport) {
		this.viewport = viewport;
		needsRedraw = true;
	}

	public CellRenderer getRenderer(int i, int j) {
		Object value = this.source.getValueAt(i, j);
		if (value instanceof String) {
			// plain text rendered without LaTeX
			return (g, x, y) -> g.drawString((String) value, x, y + 20);
		} else if (value instanceof TeXIcon) {
			return (g, x, y) -> ((TeXIcon) value).paintIcon(() -> null,
					(Graphics2DInterface) g, x, y);
		} else {
			throw new IllegalStateException("Cannot render " + value);
		}
	}

	public SpreadsheetEventHandler getEventDispatcher() {
		return eventDispatcher;
	}

	public boolean isSelected(int x, int y) {
		return controller.isSelected(x, y);
	}

	public void showCellEditor(int x, int y) {
		if (delegate != null) {
			delegate.showCellEditor(layout.getBounds(x, y), source.getValueAt(x, y));
		}
	}

	public void hideCellEditor() {
		if (delegate != null) {
			delegate.hideCellEditor();
		}
	}
}
