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

package org.geogebra.common.gui.view.table.dialog;

import java.util.List;

import org.geogebra.common.util.AttributedString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Related statistic rows displayed together, with an optional heading.
 * @param heading the heading of the group, or {@code null} if no heading should be shown.
 * @param rows the rows of the group to display
 */
public record StatisticGroup(
		@Nullable AttributedString heading, @NonNull List<Row> rows) {
	/**
	 * A labeled statistic value with an optional value for copying to the clipboard.
	 * @param label label shown in the first half of the row
	 * @param value value shown in the second half of the row
	 * @param isLaTeX whether the row uses LaTeX rendering
	 * @param clipboardValue copyable value, or {@code null} when the row is not copyable
	 */
	public record Row(
			@NonNull String label,
			@NonNull String value,
			boolean isLaTeX,
			@Nullable String clipboardValue) {}
}
