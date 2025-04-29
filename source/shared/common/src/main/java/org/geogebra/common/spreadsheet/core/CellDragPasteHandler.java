package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;

/**
 * Utility class designed to handle dragging a selection in order to copy its content to adjacent
 * cells
 */
public interface CellDragPasteHandler {

	/**
	 * Specifies the range that should be copied
	 * @param rangeToCopy {@link TabularRange}
	 */
	void setRangeToCopy(@CheckForNull TabularRange rangeToCopy);

	/**
	 * @return The selected range used to copy a selection to.
	 */
	@CheckForNull TabularRange getDragPasteDestinationRange();

	/**
	 * Pastes the selected range to the chosen destination.
	 */
	void pasteToDestination();

	/**
	 * Sets the destination where the original selection should be copied to
	 * @param destinationRow Row index
	 * @param destinationColumn Column index
	 */
	void setDestinationForPaste(int destinationRow, int destinationColumn);

	/**
	 * @param destinationRow Destination row index
	 * @return True if the destination paste range should be extended vertically, false else
	 * (horizontally)
	 */
	boolean destinationShouldExtendVertically(int destinationRow);
}
