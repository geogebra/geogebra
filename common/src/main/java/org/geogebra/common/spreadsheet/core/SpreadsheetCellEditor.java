package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

/**
 * An abstraction for spreadsheet cell editing.
 *
 * (This prevents dependencies on the platform-specifics of cell editors in the spreadsheet.core
 * package.)
 */
public interface SpreadsheetCellEditor {

	/**
	 * Show the spreadsheet cell editor.
	 * @param editorBounds The editor (=cell) bounds in viewport-relative coordinates.
	 * @param viewport The current visible viewport.
	 * @param textAlignment The text alignment of the editor. One of {@link CellFormat}'s
	 * ALIGN_LEFT, ALIGN_CENTER, or ALIGN_RIGHT.
	 */
	void show(Rectangle editorBounds, Rectangle viewport, int textAlignment);

	/**
	 * Move the spreadsheet cell editor to a new position.
	 * @param editorBounds The editor (=cell) bounds in viewport-relative coordinates.
	 * @param viewport The current visible viewport.
	 */
	void updatePosition(Rectangle editorBounds, Rectangle viewport);

	/**
	 * Hide the spreadsheet cell editor.
	 */
	void hide();

	/**
	 * @return The underlying `MathFieldInternal` of the (platform-specific) cell editor.
	 */
	@Nonnull MathFieldInternal getMathField();

	/**
	 * @return A {@link SpreadsheetCellProcessor} (which basically abstracts the kernel code away
	 * from the spreadsheet code). You can simply return an instance of
	 * {@link org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellProcessor} here, it
	 * offers a default implementation.
	 */
	@Nonnull SpreadsheetCellProcessor getCellProcessor();

	/**
	 * @return A {@link SpreadsheetCellDataSerializer} (which basically abstracts the kernel code
	 * away from the spreadsheet code). You can simply return an instance of
	 * {@link org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer} here,
	 * offers a default implementation.
	 */
	@Nonnull SpreadsheetCellDataSerializer getCellDataSerializer();
}