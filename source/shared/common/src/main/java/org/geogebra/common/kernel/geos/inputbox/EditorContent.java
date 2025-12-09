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

package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class EditorContent {
	private final String inputText;
	private final String[] entries;
	private final int rows;
	private final String latex;

	/**
	 * @param inputText text (may be null if entries present)
	 * @param entries matrix entries
	 * @param rows number of matrix rows
	 */
	public EditorContent(String inputText, String latex, String[] entries, int rows) {
		this.inputText = inputText;
		this.entries = entries;
		this.rows = rows;
		this.latex = latex;
	}

	protected String getEditorInput() {
		return entries.length > 0 ? buildMatrixText() : inputText;
	}

	protected void removeCommas(Localization loc) {
		for (int i = 0; i < entries.length; i++) {
			entries[i] = StringUtil.preprocessForParser(entries[i],
					loc.isUsingDecimalComma());
		}
	}

	protected String buildMatrixText() {
		StringBuilder sb = new StringBuilder("{{");
		int cells = entries.length;
		int columns = cells / rows;
		for (int cell = 0; cell < cells; cell++) {
			sb.append(entries[cell]);
			// ensure the row dimension does not change; for >1 column already ensured by commas
			if (columns == 1 && entries[cell].isEmpty()) {
				sb.append('?');
			}
			if (cell == cells - 1) {
				sb.append("}}");
			} else if ((cell + 1) % columns == 0) {
				sb.append("},{");
			} else {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	protected String buildVectorText() {
		return "(" + StringUtil.join(",", entries) + ")";
	}

	protected boolean hasEntries() {
		return entries.length > 0;
	}

	public String getLaTeX() {
		return latex;
	}

	/**
	 * Whether the content should be considered empty; for most objects that included a single ?,
	 * for list only empty string is considered empty.
	 * @param forList whether editor is for a list
	 * @return whether editor is empty
	 */
	public boolean isEmpty(boolean forList) {
		if (entries.length > 0) {
			return false;
		}
		return StringUtil.empty(inputText) || (!forList && "?".equals(inputText));
	}
}
