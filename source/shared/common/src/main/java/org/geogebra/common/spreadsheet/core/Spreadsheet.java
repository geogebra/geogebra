package org.geogebra.common.spreadsheet.core;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size). This class provides public API  for both rendering
 * and event handling, using {@link SpreadsheetRenderer} and {@link SpreadsheetController}.
 *
 * @apiNote This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularDataChangeListener {

	public static final int MAX_COLUMNS = 9999;
	public static final int MAX_ROWS = 9999;
	private final SpreadsheetController controller;
	private final SpreadsheetStyle style;
	private final SpreadsheetStyleBarModel styleBarModel;
	private final SpreadsheetRenderer renderer;
	private @CheckForNull SpreadsheetDelegate spreadsheetDelegate;

	/**
	 * @param tabularData data source
	 * @param rendererFactory converts custom data type to rendable objects
	 * @param undoProvider undo provider, may be null
	 */
	public Spreadsheet(@Nonnull TabularData<?> tabularData,
			@Nonnull CellRenderableFactory rendererFactory, @CheckForNull UndoProvider undoProvider) {
		controller = new SpreadsheetController(tabularData);
		style = new SpreadsheetStyle(tabularData.getFormat());
		styleBarModel = new SpreadsheetStyleBarModel(controller, controller.selectionController,
				style);
		style.stylingApplied.addListener(this::stylingApplied);
		renderer = new SpreadsheetRenderer(controller.getLayout(), rendererFactory,
				style, tabularData);
		setViewport(new Rectangle(0, 0, 0, 0));
		tabularData.addChangeListener(this);
		if (undoProvider != null) {
			controller.setUndoProvider(undoProvider);
		}
		tabularData.setCustomRowAndColumnSizeProvider(controller.getLayout());
	}

	// layout

	// styling

	public @Nonnull SpreadsheetStyleBarModel getStyleBarModel() {
		return styleBarModel;
	}

	private void stylingApplied(@Nonnull List<TabularRange> ranges) {
		ranges.forEach(range ->
			range.forEach((row, column) -> renderer.invalidate(row, column))
		);
		notifyRepaintNeeded();
    }

	// drawing

	/**
	 * Draws current viewport of the spreadsheet
	 * @param graphics graphics to draw to
	 */
	public void draw(GGraphics2D graphics) {
		graphics.setPaint(GColor.WHITE);
		Rectangle viewport = controller.getViewport();
		renderer.fillRect(graphics, 0, 0, viewport.getWidth(), viewport.getHeight());
		List<TabularRange> visibleSelections = controller.getVisibleSelections();
		for (TabularRange range: visibleSelections) {
			renderer.drawSelection(range, graphics, viewport);
		}
		drawCells(graphics, viewport);
		for (TabularRange range: visibleSelections) {
			renderer.drawSelectionBorder(range, graphics, viewport, false, false);
		}
		SpreadsheetCoords selectedCell = controller.getLastSelectionUpperLeftCell();
		if (selectedCell != null) {
			renderer.drawSelectionBorder(new TabularRange(selectedCell.row, selectedCell.column),
					graphics, viewport, true, false);
		}
		GPoint2D draggingDot = controller.getDraggingDot();
		if (draggingDot != null) {
			renderer.drawDraggingDot(draggingDot, graphics);
		}
		TabularRange dragPasteSelection = controller.getDragPasteSelection();
		if (dragPasteSelection != null) {
			renderer.drawSelectionBorder(dragPasteSelection, graphics, viewport, false, true);
		}

		Rectangle editorBounds = controller.getEditorBounds();
		if (editorBounds != null) {
			renderer.drawEditorBorder(editorBounds, graphics);
		}
	}

	void drawCells(GGraphics2D graphics, Rectangle viewport) {
		TableLayout layout = controller.getLayout();
		TableLayout.Portion portion =
				layout.getLayoutIntersecting(viewport);
		double offsetX = viewport.getMinX() - layout.getRowHeaderWidth();
		double offsetY = viewport.getMinY() - layout.getColumnHeaderHeight();
		drawContentCells(graphics, portion, offsetX, offsetY);
		renderer.drawHeaderBackgroundAndOutline(graphics, viewport);
		controller.getSelections().forEach(selection ->
			renderer.drawSelectionHeader(selection, graphics, controller.getViewport())
		);
		graphics.translate(-offsetX, 0);
		graphics.setColor(style.getGridColor());
		for (int column = portion.fromColumn + 1; column <= portion.toColumn; column++) {
			renderer.drawColumnBorder(column, graphics);
		}

		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			setHeaderColor(graphics, controller.isSelected(-1, column));
			renderer.drawColumnHeader(column, graphics, controller::getColumnName);
		}

		graphics.translate(offsetX, -offsetY);
		graphics.setColor(style.getGridColor());
		for (int row = portion.fromRow + 1; row <= portion.toRow; row++) {
			renderer.drawRowBorder(row, graphics);
		}
		for (int row = portion.fromRow; row <= portion.toRow; row++) {
			setHeaderColor(graphics, controller.isSelected(row, -1));
			renderer.drawRowHeader(row, graphics, controller::getRowName);
		}
		graphics.translate(0, offsetY);
		graphics.setColor(style.getHeaderBackgroundColor());
		renderer.fillRect(graphics, 0, 0,
				layout.getRowHeaderWidth(), layout.getColumnHeaderHeight());

		drawErrorCells(graphics, portion, viewport, offsetX, offsetY);
	}

	private void drawErrorCells(GGraphics2D graphics, TableLayout.Portion portion,
			Rectangle viewport, double offsetX, double offsetY) {
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			for (int row = portion.fromRow; row <= portion.toRow; row++) {
				if (controller.hasError(row, column)) {
					renderer.drawErrorCell(row, column, graphics, viewport, offsetX, offsetY);
				}
			}
		}
	}

	private void drawContentCells(GGraphics2D graphics, TableLayout.Portion portion,
			double offsetX, double offsetY) {
		graphics.translate(-offsetX, -offsetY);
		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			for (int row = portion.fromRow; row <= portion.toRow; row++) {
				renderer.drawCell(row, column, graphics,
						controller.contentAt(row, column), controller.hasError(row, column));
			}
		}
		graphics.translate(offsetX, offsetY);
	}

	private void setHeaderColor(GGraphics2D graphics, boolean isSelected) {
		if (isSelected) {
			graphics.setColor(style.getSelectedTextColor());
		} else {
			graphics.setColor(style.getDefaultTextColor());
		}
	}

	// keyboard (use com.himamis.retex.editor.share.event.KeyListener?)

	// touch

	// - TabularSelection

	/**
	 * @param viewport The viewport (visible rectangle) relative to the table, in points.
	 */
	public void setViewport(Rectangle viewport) {
		this.controller.setViewport(viewport);
	}

	public void setControlsDelegate(SpreadsheetControlsDelegate controlsDelegate) {
		controller.setControlsDelegate(controlsDelegate);
	}

	/**
	 * @param spreadsheetDelegate {@link SpreadsheetDelegate}
	 */
	public void setSpreadsheetDelegate(SpreadsheetDelegate spreadsheetDelegate) {
		this.spreadsheetDelegate = spreadsheetDelegate;
	}

	/**
	 * @param constructionDelegate {@link SpreadsheetConstructionDelegate}
	 */
	public void setSpreadsheetConstructionDelegate(
			SpreadsheetConstructionDelegate constructionDelegate) {
		controller.setSpreadsheetConstructionDelegate(constructionDelegate);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerUp(double x, double y, Modifiers modifiers) {
		controller.handlePointerUp(x, y, modifiers);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerDown(double x, double y, Modifiers modifiers) {
		controller.handlePointerDown(x, y, modifiers);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerMove(double x, double y, Modifiers modifiers) {
		controller.handlePointerMove(x, y, modifiers);
	}

	/**
	 * @param keyCode keyboard code, see {@link com.himamis.retex.editor.share.util.JavaKeyCodes}
	 * @param key key typed if printable, empty otherwise (Alt, Ctrl, F1, Backspace)
	 * @param modifiers alt/shift/ctrl modifiers
	 */
	public void handleKeyPressed(int keyCode, String key, Modifiers modifiers) {
		controller.handleKeyPressed(keyCode, key, modifiers);
	}

	@Override
	public void tabularDataDidChange(int row, int column) {
		renderer.invalidate(row, column);
		notifyRepaintNeeded();
	}

	@Override
	public void tabularDataSizeDidChange(SpreadsheetDimensions dimensions) {
		controller.tabularDataSizeDidChange(dimensions);
		notifyRepaintNeeded();
	}

	private void notifyRepaintNeeded() {
		if (spreadsheetDelegate != null) {
			spreadsheetDelegate.notifyRepaintNeeded();
		}
	}

	public void setWidthForColumns(double width, int minColumn, int maxColumn) {
		controller.getLayout().setWidthForColumns(width, minColumn, maxColumn);
	}

	public void setHeightForRows(double height, int minRow, int maxRow) {
		controller.getLayout().setHeightForRows(height, minRow, maxRow);
	}

	/**
	 * @param x mouse position relative to viewport, in logical points.
	 * @param y mouse position relative to viewport, in logical points.
	 * @return the cursor to show at the given point.
	 */
	public MouseCursor getCursor(double x, double y) {
		return controller.getDragAction(x, y).cursor;
	}

	public double getTotalWidth() {
		return controller.getLayout().getTotalWidth();
	}

	public double getTotalHeight() {
		return controller.getLayout().getTotalHeight();
	}

	public boolean isEditorActive() {
		return controller.isEditorActive();
	}

	public void tabPressed() {
		controller.moveRight(false);
	}

	/**
	 * Clears the selection, committing any pending cell edits beforehand.
	 */
	public void clearSelection() {
		controller.saveContentAndHideCellEditor();
		controller.clearSelection();
	}

	/**
	 * Clears the selection only.
	 */
	public void clearSelectionOnly() {
		controller.clearSelection();
	}

	void selectRow(int row, boolean extend, boolean add) {
		controller.selectRow(row, extend, add);
	}

	void selectColumn(int column, boolean extend, boolean add) {
		controller.selectColumn(column, extend, add);
	}

	void selectCell(int row, int column, boolean extend, boolean add) {
		controller.selectCell(row, column, extend, add);
	}

	public void setViewportAdjustmentHandler(ViewportAdjusterDelegate mockForScrollable) {
		controller.setViewportAdjustmentHandler(mockForScrollable);
	}

	/**
	 * If the pointer is at the top / right / bottom / left corner while dragging,
	 * starts scrolling the viewport
	 */
	public void scrollForDragIfNeeded() {
		controller.scrollForDragIfNeeded();
	}

	/**
	 * Scroll editor into view if visible
	 */
	public void scrollEditorIntoView() {
		controller.scrollEditorIntoView();
	}

	public void saveContentAndHideCellEditor() {
		controller.saveContentAndHideCellEditor();
	}

	public SpreadsheetController getController() {
		return controller;
	}

	public Rectangle getViewport() {
		return controller.getViewport();
	}
}
