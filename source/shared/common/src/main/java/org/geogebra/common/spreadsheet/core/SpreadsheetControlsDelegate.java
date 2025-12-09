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

import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Shows and hides spreadsheet controls (editor, context menu)
 */
public interface SpreadsheetControlsDelegate {

	/**
	 * @return A cell editor instance.
	 */
	@Nonnull SpreadsheetCellEditor getCellEditor();

	/**
	 * Show context menu and take over focus (for keyboard navigation).
	 * @param items list of context menu items
	 * @param point preferred top left coordinates (lower right corner of the selected cell range)
	 */
	void showContextMenu(@Nonnull List<ContextMenuItem> items, @Nonnull Point point);

	/**
	 * Hide context menu (and potential submenus), move focus to spreadsheet.
	 */
	void hideContextMenu();

	/**
	 * @return Interface to system clipboard.
	 */
	@CheckForNull ClipboardInterface getClipboard();

	/**
	 * Show completion suggestions for the cell currently being edited. If the suggestions UI is not
	 * already visible, it should be shown at this point.
	 * @param input The current text of the cell being edited.
	 * @param editorBounds The bounds of the cell being edited (in viewport-relative coordinates).
	 * Use this to position the suggestions UI.
	 */
	void showAutoCompleteSuggestions(@Nonnull String input, @Nonnull Rectangle editorBounds);

	/**
	 * Hide the command suggestions UI (if currently visible).
	 */
	void hideAutoCompleteSuggestions();

	/**
	 * @return True if the suggestions UI is currently visible.
	 */
	boolean isAutoCompleteSuggestionsVisible();

	/**
	 * Give the `SpreadsheetControlsDelegate` an option to handle key presses during autocomplete.
	 * @param keyCode The pressed key.
	 * @return True if the `SpreadsheetControlsDelegate` did handle the key press (which means it
	 * shouldn't be processed any further), false otherwise.
	 */
	boolean handleKeyPressForAutoComplete(int keyCode);

	/**
	 * Show snackbar with given message.
	 * @param messageKey Message key found in the menu localization bundle.
	 */
	void showSnackbar(@Nonnull String messageKey);
}
