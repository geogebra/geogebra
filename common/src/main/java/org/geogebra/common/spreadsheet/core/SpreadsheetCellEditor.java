package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.common.spreadsheet.kernel.SpreadsheetCellProcessor;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

/**
 * An interface abstracting away the platform-specific parts of spreadsheet cell editing.
 */
public interface SpreadsheetCellEditor {

	/**
	 * Show the spreadsheet cell editor.
	 * @param editorBounds The editor (=cell) bounds in viewport-relative coordinates.
	 * @param viewport The current visible viewport.
	 * @param textAlignment The text alignment of the editor.
	 */
	// TODO introduce enum for alignment
	void show(Rectangle editorBounds, Rectangle viewport, int textAlignment);

	/**
	 * Hide the spreadsheet cell editor.
	 */
	void hide();

	/**
	 * Scroll the cell editor if necessary to bring the cursor into view.
	 */
	void scrollCursorVisible();

	/**
	 * @return The underlying `MathFieldInternal` of the (platform-specific) cell editor.
	 */
	@Nonnull MathFieldInternal getMathField();

	/**
	 * @return A {@link SpreadsheetCellProcessor} (which basically abstracts the
	 * {@link org.geogebra.common.kernel.Kernel} and
	 * {@link org.geogebra.common.kernel.commands.AlgebraProcessor} away from the spreadsheet code).
	 */
	@Nonnull SpreadsheetCellProcessor getCellProcessor();
}