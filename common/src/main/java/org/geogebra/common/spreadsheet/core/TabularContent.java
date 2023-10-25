package org.geogebra.common.spreadsheet.core;

public class TabularContent {
	private final TabularData tabularData;

	public TabularContent(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	public String toString(TabularRange range) {
		StringBuilder sb = new StringBuilder();
		for (int row = range.fromRow; row < range.toRow + 1; row++) {
			for (int column = range.fromCol; column < range.toCol + 1; column++) {
				Object value = tabularData.contentAt(row, column);
				sb.append(value);
				if (column != range.toCol) {
					sb.append('\t');
				}
			}
			if (row != range.toRow) {
				sb.append('\t');
			}
		}
		return sb.toString();
	}
}
