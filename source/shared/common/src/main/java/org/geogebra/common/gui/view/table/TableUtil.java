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

package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.Comparator;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;
import org.geogebra.common.util.Range;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Table values related utility method collection.
 */
@SuppressWarnings({"checkstyle:UnicodeRange"})
public final class TableUtil {

	/**
	 * Builds an {@link AttributedString} for a raw column header with subscript ranges.
	 * <p> Examples: </p>
	 * <ul>
	 *     <li>For the first column it returns {@code x}</li>
	 *     <li>For the second column it returns {@code y₁}</li>
	 *     <li>For the tenth column it returns {@code y₁₀}</li>
	 * </ul>
	 * @param model table values model
	 * @param columnIndex index of column
	 * @return attributed string with subscript ranges
	 */
	public static AttributedString getColumnHeader(TableValuesModel model, int columnIndex) {
		String content = model.getHeaderAt(columnIndex);
		return parseSubscripts(content == null ? "-" : content);
	}

	/**
	 * Builds an {@link AttributedString} for a labeled column header ("Column y₁") with
	 * subscript ranges.
	 * <p> Examples: </p>
	 * <ul>
	 *     <li>For the second column without two variables it returns {@code Column y₁}</li>
	 *     <li>For the second column with two variables it returns {@code Column x y₁}</li>
	 *     <li>For the third column without two variables it returns {@code Column y₂}</li>
	 *     <li>For the third column with two variables it returns {@code Column x y₂}</li>
	 * </ul>
	 * @param model table values model
	 * @param columnIndex index of column
	 * @param hasTwoVariable whether to prepend "x" prefix for two-variable context
	 * @param localization localization for the "Column %0" pattern
	 * @return attributed string with subscript ranges
	 */
	public static AttributedString getLabeledColumnHeader(TableValuesModel model, int columnIndex,
			boolean hasTwoVariable, Localization localization) {
		String header = model.getHeaderAt(columnIndex);
		if (header == null) {
			header = "-";
		}
		if (hasTwoVariable) {
			header = "x " + header;
		}
		String wrapped = localization.getPlainDefault("ColumnA", "Column %0", header);
		return parseSubscripts(wrapped);
	}

	/**
	 * Converts an {@link AttributedString} to an HTML string, wrapping subscript ranges
	 * in {@code <sub>} tags.
	 * <p> Examples: </p>
	 * <ul>
	 *     <li>{@code Column y₁} => {@code Column y<sub>1</sub>}</li>
	 *     <li>{@code Column x y₂} => {@code Column x y<sub>2</sub>}</li>
	 *     <li>{@code 2H₂ + O₂ → 2H₂O} => {@code 2H<sub>2</sub> + O<sub>2</sub> → 2H<sub>2</sub>O}</li>
	 * </ul>
	 * @param attributedString the attributed string to convert
	 * @return HTML string with subscript ranges enclosed in {@code <sub>} tags
	 */
	public static String toHtml(AttributedString attributedString) {
		String raw = attributedString.getRawValue();
		StringBuilder html = new StringBuilder();
		int pos = 0;
		ArrayList<Range> ranges = new ArrayList<>(
				attributedString.getAttribute(AttributedString.Attribute.Subscript));
		ranges.sort(Comparator.comparingInt(Range::getStart));
		for (Range range : ranges) {
			html.append(raw, pos, range.getStart())
					.append("<sub>")
					.append(raw, range.getStart(), range.getEnd())
					.append("</sub>");
			pos = range.getEnd();
		}
		html.append(raw.substring(pos));
		return html.toString();
	}

	@SuppressFBWarnings("IM_BAD_CHECK_FOR_ODD")
	private static AttributedString parseSubscripts(String text) {
		String[] parts = splitByIndices(text);
		StringBuilder raw = new StringBuilder();
		for (String part : parts) {
			raw.append(part);
		}
		AttributedString result = new AttributedString(raw.toString());
		int index = 0;
		for (int i = 0; i < parts.length; i++) {
			int length = parts[i].length();
			if (i % 2 == 1 && length > 0) {
				result.add(AttributedString.Attribute.Subscript, new Range(index, index + length));
			}
			index += length;
		}
		return result;
	}

	private static String[] splitByIndices(String content) {
		String[] labelParts = content.split("_");
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add(labelParts[0]);
		for (int i = 1; i < labelParts.length; i++) {
			String part = labelParts[i];
			int index = part.indexOf('}');
			if (part.startsWith("{") && index > -1) {
				retVal.add(part.substring(1, index));
				retVal.add(part.substring(index + 1));
			} else {
				retVal.add(part.substring(0, 1));
				retVal.add(part.substring(1));
			}
		}
		return retVal.toArray(new String[0]);
	}
}
