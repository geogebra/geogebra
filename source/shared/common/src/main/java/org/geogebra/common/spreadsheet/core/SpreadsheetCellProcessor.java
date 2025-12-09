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

import javax.annotation.Nonnull;

/**
 * An abstraction for spreadsheet cell input processing.
 *
 * (This prevents direct dependencies on AlgebraProcessor and other classes from the kernel
 * package.)
 */
public interface SpreadsheetCellProcessor {

	/**
	 * Process spreadsheet cell input.
	 * @param input The input from the cell editor.
	 * @param row The row identifying the cell being edited.
	 * @param column The row identifying the cell being edited.
	 */
	void process(@Nonnull String input, int row, int column);

	/**
	 * Mark error for cell input.
	 */
	void markError();

	/**
	 * Whether string is too short for checking autocompletions.
	 * Overridden for CJK support.
	 * @param searchPrefix prefix for autocompletion lookup
	 * @return whether string is too short
	 */
	default boolean isTooShortForAutocomplete(@Nonnull String searchPrefix) {
		return searchPrefix.length() < 3;
	}
}
