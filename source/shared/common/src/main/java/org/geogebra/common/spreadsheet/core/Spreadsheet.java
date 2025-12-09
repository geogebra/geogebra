/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.spreadsheet.core;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.MulticastEvent;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A spreadsheet (of arbitrary size). This class provides public API  for both rendering
 * and event handling, using {@link SpreadsheetRenderer} and {@link SpreadsheetController}.
 *
 * @apiNote This type is not designed to be thread-safe.
 */
public final class Spreadsheet implements TabularDataChangeListener {

	public final MulticastEvent<String> cellFormatXmlChanged = new MulticastEvent<>();
	public final MulticastEvent<CellSizes> cellSizesChanged;

	public static final int MAX_COLUMNS = 9999;
	public static final int MAX_ROWS = 9999;
	private final SpreadsheetController controller;
	private final SpreadsheetStyling styling;
	private final SpreadsheetStyleBarModel styleBarModel;
	private final SpreadsheetRenderer renderer;
	private @CheckForNull SpreadsheetDelegate spreadsheetDelegate;

	/**
	 * Get the column name for a column index.
	 * @param columnIndex A column index. Must be >= 0.
	 * @return The column name (with uppercase letters).
	 * @implNote This duplicates a method from GeoElementSpreadsheet (which we don't want to reuse
	 * here).
	 */
	public static @Nonnull String getColumnName(int columnIndex) {
		int i = columnIndex + 1;
		String col = "";
		while (i > 0) {
			col = (char) ('A' + (i - 1) % 26) + col;
			i = (i - 1) / 26;
		}
		return col;
	}

	/**
	 * @param tabularData data source
	 * @param rendererFactory converts custom data type to renderable objects
	 * @param undoProvider undo provider, may be null
	 */
	public Spreadsheet(@Nonnull TabularData<?> tabularData,
			@Nonnull CellRenderableFactory rendererFactory,
			@CheckForNull UndoProvider undoProvider) {

		styling = new SpreadsheetStyling();
		styling.stylingChanged.addListener(this::stylingChanged);
		styling.stylingXmlChanged.addListener(cellFormatXmlChanged::notifyListeners);

		controller = new SpreadsheetController(tabularData, styling);
		controller.setUndoProvider(undoProvider);
		controller.selectionController.selectionsChanged.addListener(this::selectionsChanged);
		controller.referencesChanged.addListener(this::referencesChanged);
		cellSizesChanged = controller.cellSizesChanged;

		// get notified when number or size of rows/columns changes
		tabularData.addChangeListener(this);

		styleBarModel = new SpreadsheetStyleBarModel(controller, controller.selectionController,
				styling);

		renderer = new SpreadsheetRenderer(controller.getLayout(), rendererFactory,
				styling, tabularData);

		setViewport(new Rectangle(0, 0, 0, 0));
	}

	// Delegates

	/**
	 * @param controlsDelegate delegate for controls
	 */
	public void setControlsDelegate(@CheckForNull SpreadsheetControlsDelegate controlsDelegate) {
		controller.setControlsDelegate(controlsDelegate);
	}

	/**
	 * @param spreadsheetDelegate delegate for repaint notifications
	 */
	public void setSpreadsheetDelegate(@CheckForNull SpreadsheetDelegate spreadsheetDelegate) {
		this.spreadsheetDelegate = spreadsheetDelegate;
	}

	/**
	 * @param constructionDelegate delegate for creating objects in the construction
	 */
	public void setSpreadsheetConstructionDelegate(
			@CheckForNull SpreadsheetConstructionDelegate constructionDelegate) {
		controller.setSpreadsheetConstructionDelegate(constructionDelegate);
	}

	/**
	 * @param viewportAdjusterDelegate delegate for scrollable container hosting the spreadsheet
	 */
	public void setViewportAdjustmentHandler(
			@CheckForNull ViewportAdjusterDelegate viewportAdjusterDelegate) {
		controller.setViewportAdjustmentHandler(viewportAdjusterDelegate);
	}

	// Layout

	/**
	 * Sets the width for a range of columns.
	 * @param width double
	 * @param minColumn Index from where to start setting the width
	 * @param maxColumn Index of where to stop setting the width (inclusive)
	 */
	public void setWidthForColumns(double width, int minColumn, int maxColumn) {
		controller.getLayout().setWidthForColumns(width, minColumn, maxColumn);
	}

	/**
	 * Sets the height for a range of rows.
	 * @param height double
	 * @param minRow Index from where to start setting the height
	 * @param maxRow Index of where to stop setting the width (inclusive)
	 */
	public void setHeightForRows(double height, int minRow, int maxRow) {
		controller.getLayout().setHeightForRows(height, minRow, maxRow);
	}

	public double getTotalWidth() {
		return controller.getLayout().getTotalWidth();
	}

	public double getTotalHeight() {
		return controller.getLayout().getTotalHeight();
	}

	// Styling

	public @Nonnull SpreadsheetStyleBarModel getStyleBarModel() {
		return styleBarModel;
	}

	private void stylingChanged(@CheckForNull List<TabularRange> ranges) {
		if (ranges == null) {
			return;
		}
		// style bar -> SpreadsheetSettings
		cellFormatXmlChanged.notifyListeners(styling.getCellFormatXml());

		controller.storeUndoInfo();

		ranges.forEach(range ->
			range.forEach(renderer::invalidate)
		);
		notifyRepaintNeeded();
    }

	/**
	 * SpreadsheetSettings -> style bar
	 * @param cellFormatXml the cell format XML
	 */
	public void setCellFormatXml(String cellFormatXml) {
		styling.setCellFormatXml(cellFormatXml);
	}

	// Drawing

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
		drawCells(graphics, viewport); // on top of selections
		for (TabularRange range: visibleSelections) {
			renderer.drawSelectionBorder(range, graphics, viewport, false, false);
		}
		SpreadsheetCoords selectedCell = controller.getLastSelectionUpperLeftCell();
		if (selectedCell != null) {
			renderer.drawSelectionBorder(new TabularRange(selectedCell.row, selectedCell.column),
					graphics, viewport, true, false);
		}

		SpreadsheetReferences references = controller.getCurrentReferences();
		if (references != null) {
			drawReferences(graphics, viewport, references);
		}

		Point draggingDotLocation = controller.getDraggingDotLocation();
		if (draggingDotLocation != null) {
			renderer.drawDraggingDot(draggingDotLocation, graphics);
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

	private void drawCells(GGraphics2D graphics, Rectangle viewport) {
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
		graphics.setColor(styling.getGridColor());
		for (int column = portion.fromColumn + 1; column <= portion.toColumn; column++) {
			renderer.drawColumnBorder(column, graphics);
		}

		for (int column = portion.fromColumn; column <= portion.toColumn; column++) {
			setHeaderColor(graphics, controller.isSelected(-1, column));
			renderer.drawColumnHeader(column, graphics, controller::getColumnName);
		}

		graphics.translate(offsetX, -offsetY);
		graphics.setColor(styling.getGridColor());
		for (int row = portion.fromRow + 1; row <= portion.toRow; row++) {
			renderer.drawRowBorder(row, graphics);
		}
		for (int row = portion.fromRow; row <= portion.toRow; row++) {
			setHeaderColor(graphics, controller.isSelected(row, -1));
			renderer.drawRowHeader(row, graphics, controller::getRowName);
		}
		graphics.translate(0, offsetY);
		graphics.setColor(styling.getHeaderBackgroundColor());
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

	private void drawReferences(GGraphics2D graphics, Rectangle viewport,
			@Nonnull SpreadsheetReferences references) {
		SpreadsheetReferences deduplicatedReferences = references.removingDuplicates();
		for (int index = 0; index < deduplicatedReferences.cellReferences.size(); index++) {
			SpreadsheetReference reference = deduplicatedReferences.cellReferences.get(index);
			boolean filled = reference.equalsIgnoringAbsolute(references.currentCellReference);
			renderer.drawReference(reference, index, filled, graphics, viewport);
		}
	}

	private void setHeaderColor(GGraphics2D graphics, boolean isSelected) {
		if (isSelected) {
			graphics.setColor(styling.getSelectedTextColor());
		} else {
			graphics.setColor(styling.getDefaultTextColor());
		}
	}

	private void notifyRepaintNeeded() {
		if (spreadsheetDelegate != null) {
			spreadsheetDelegate.notifyRepaintNeeded();
		}
	}

	// Viewport & scrolling

	public @Nonnull Rectangle getViewport() {
		return controller.getViewport();
	}

	/**
	 * @param viewport The viewport (visible rectangle) relative to the table, in points.
	 */
	public void setViewport(@Nonnull Rectangle viewport) {
		this.controller.setViewport(viewport);
	}

	/**
	 * If the pointer is at the top / right / bottom / left corner while dragging,
	 * starts scrolling the viewport
	 */
	public void scrollForDragIfNeeded() {
		controller.scrollForDragIfNeeded();
	}

	// Mouse events

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerUp(double x, double y, @Nonnull Modifiers modifiers) {
		controller.handlePointerUp(x, y, modifiers);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerDown(double x, double y, @Nonnull Modifiers modifiers) {
		controller.handlePointerDown(x, y, modifiers);
	}

	/**
	 * @param x screen coordinate of event
	 * @param y screen coordinate of event
	 * @param modifiers alt/ctrl/shift
	 */
	public void handlePointerMove(double x, double y, @Nonnull Modifiers modifiers) {
		controller.handlePointerMove(x, y, modifiers);
	}

	/**
	 * @param x mouse position relative to viewport, in logical points.
	 * @param y mouse position relative to viewport, in logical points.
	 * @return the cursor to show at the given point.
	 */
	public MouseCursor getCursor(double x, double y) {
		return controller.getDragAction(x, y).cursor;
	}

	// Key events

	/**
	 * @param keyCode keyboard code, see {@link org.geogebra.editor.share.util.JavaKeyCodes}
	 * @param key key typed if printable, empty otherwise (Alt, Ctrl, F1, Backspace)
	 * @param modifiers alt/shift/ctrl modifiers
	 */
	public void handleKeyPressed(int keyCode, String key, @Nonnull Modifiers modifiers) {
		controller.handleKeyPressed(keyCode, key, modifiers);
	}

	/**
	 * Handle pressing the Tab key.
	 */
	public void tabPressed() {
		controller.moveRight(false);
	}

	// Selection

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

	private void selectionsChanged(MulticastEvent.Void unused) {
		notifyRepaintNeeded();
	}

	// Editor

	public boolean isEditorActive() {
		return controller.isEditorActive();
	}

	/**
	 * Scroll editor into view if visible
	 */
	public void scrollEditorIntoView() {
		controller.scrollEditorIntoView();
	}

	/**
	 * Saves the content of the editor and hides it afterwards.
	 */
	public void saveContentAndHideCellEditor() {
		controller.saveContentAndHideCellEditor();
	}

	private void referencesChanged(MulticastEvent.Void unused) {
		notifyRepaintNeeded();
	}

	// -- TabularDataChangeListener --

	@Override
	public void tabularDataDidChange(int row, int column) {
		renderer.invalidate(row, column);
		notifyRepaintNeeded();
	}

	// Call chain:
	// KernelTabularDataAdapter (on settings change)
	//   -> Spreadsheet.tabularDataDimensionsDidChange()
	@Override
	public void tabularDataDimensionsDidChange(SpreadsheetDimensions spreadsheetDimensions) {
		controller.tabularDataDimensionsDidChange(spreadsheetDimensions);
		notifyRepaintNeeded();
	}

	// -- Context Menu --

	/**
	 * Returns the context menu items
	 * @param identifier identifier
	 * @return list of context menu items
	 */
	public List<ContextMenuItem> getMenuItems(ContextMenuItem.Identifier identifier) {
		return controller.getMenuItems(identifier);
	}

	// Test support API (DO NOT USE except for tests!)

	public SpreadsheetController getController() {
		return controller;
	}

	SpreadsheetStyling getStyling() {
		return styling;
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
}
