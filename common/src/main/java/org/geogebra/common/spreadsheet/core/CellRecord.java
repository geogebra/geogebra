package org.geogebra.common.spreadsheet.core;

import java.util.Comparator;

class CellRecord {
	private static Comparator<CellRecord> comparator;
	int id;
	int sourceRow;
	int sourceCol;
	int destRow;
	int destCol;

	public CellRecord(int id, int sourceRow, int sourceCol, int destRow, int destCol) {
		this.id = id;
		this.sourceRow = sourceRow;
		this.destRow = destRow;
		this.sourceCol = sourceCol;
		this.destCol = destCol;
	}

	public int getSourceRow() {
		return sourceRow;
	}

	public int getDestRow() {
		return destRow;
	}

	public int getSourceCol() {
		return sourceCol;
	}

	public int getDestCol() {
		return destCol;
	}

	/**
	 * used to sort Records based on the id (which is the construction index)
	 *
	 * @return comparator
	 */
	public static Comparator<CellRecord> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<CellRecord>() {
				@Override
				public int compare(CellRecord a, CellRecord b) {
					return a.id - b.id;
				}

			};
		}
		return comparator;
	}
}
