package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class EditorContent {
	private final String inputText;
	private final String[] entries;
	private final int rows;

	/**
	 * @param inputText text (may be null if entries present)
	 * @param entries matrix entries
	 * @param rows number of matrix rows
	 */
	public EditorContent(String inputText, String[] entries, int rows) {
		this.inputText = inputText;
		this.entries = entries;
		this.rows = rows;
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
}
