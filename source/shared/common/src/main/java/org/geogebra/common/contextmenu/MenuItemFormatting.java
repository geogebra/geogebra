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

package org.geogebra.common.contextmenu;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.util.AttributedString;
import org.geogebra.common.util.Range;

final class MenuItemFormatting {

	/**
	 * Parses a string that may contain subscript markup into an {@code AttributedString}.
	 * The method identifies subscript sequences (formatted as "_{...}") and converts them into
	 * subscript attributes within the returned {@code AttributedString}.
	 *
	 * @param str The input string potentially containing subscript markup.
	 * @return An {@code AttributedString} with subscript attributes applied as needed.
	 */
	static @Nonnull AttributedString parse(String str) {
		StringBuilder parsedString = new StringBuilder(str);
		List<Range> subscriptRanges = new ArrayList<>();

		Range subscriptRange = findRawSubscript(str, 0);
		while (subscriptRange != null) {
			String subscript = parsedString.substring(subscriptRange.getStart() + 2,
					subscriptRange.getEnd() - 1);
			parsedString.replace(subscriptRange.getStart(), subscriptRange.getEnd(), subscript);
			subscriptRanges.add(new Range(subscriptRange.getStart(), subscriptRange.getEnd() - 3));
			subscriptRange = findRawSubscript(parsedString.toString(),
					subscriptRange.getEnd() - 3);
		}

		AttributedString attributedString = new AttributedString(parsedString.toString());
		subscriptRanges.forEach(r -> attributedString.add(AttributedString.Attribute.Subscript, r));

		return attributedString;
	}

	private static @CheckForNull Range findRawSubscript(String rawText, int fromIndex) {
		int start = rawText.indexOf("_{", fromIndex);
		if (start != -1) {
			int end = rawText.indexOf("}", fromIndex + 2);
			if (end != -1) {
				return new Range(start, end + 1);
			}
		}
		return null;
	}
}
