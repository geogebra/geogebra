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
	 * @apiNote if empty cells are pasted, target cells are unchanged (rather than deleted)
	 * @return whether paste changed the table content
	 */
	boolean pasteToDestination();

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
