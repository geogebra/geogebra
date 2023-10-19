package org.geogebra.common.spreadsheet.core;

import java.util.Comparator;

class CellRecord {
	private static Comparator<CellRecord> comparator;
	int id;
	int x1;
	int y1;
	int x2;
	int y2;

	public CellRecord(int id, int x1, int y1, int x2, int y2) {
		this.id = id;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public int getx1() {
		return x1;
	}

	public int getx2() {
		return x2;
	}

	public int gety1() {
		return y1;
	}

	public int gety2() {
		return y2;
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
	}}
