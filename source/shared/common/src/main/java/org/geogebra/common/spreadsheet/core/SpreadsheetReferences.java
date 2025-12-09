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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Container to group a list of cell references and a current reference into a single value.
 */
final class SpreadsheetReferences {

	final @Nonnull List<SpreadsheetReference> cellReferences;
	final @CheckForNull SpreadsheetReference currentCellReference;

	SpreadsheetReferences(@CheckForNull List<SpreadsheetReference> cellReferences,
			@CheckForNull SpreadsheetReference currentCellReference) {
		this.cellReferences = cellReferences != null ? cellReferences : List.of();
		this.currentCellReference = currentCellReference;
	}

	SpreadsheetReferences removingDuplicates() {
		List<SpreadsheetReference> deduplicatedCellReferences = new ArrayList<>();
		for (SpreadsheetReference reference : cellReferences) {
			if (deduplicatedCellReferences.stream()
					.noneMatch(ref -> ref.equalsIgnoringAbsolute(reference))) {
				deduplicatedCellReferences.add(reference);
			}
		}
		return new SpreadsheetReferences(deduplicatedCellReferences, currentCellReference);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SpreadsheetReferences)) {
			return false;
		}
		SpreadsheetReferences that = (SpreadsheetReferences) object;
		return Objects.equals(cellReferences, that.cellReferences)
				&& Objects.equals(currentCellReference, that.currentCellReference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cellReferences, currentCellReference);
	}
}
