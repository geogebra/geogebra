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

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

/**
 * A tabular clipboard to copy to and paste to.
 * @param <T> type of the content.
 */
public final class TabularClipboard<T> implements HasTabularValues<T> {
	private List<List<T>> data = new ArrayList<>();
	private TabularRange sourceRange;
	private SelectionType type;

	public TabularRange getSourceRange() {
		return sourceRange;
	}

	public SelectionType getType() {
		return type;
	}

	@Override
	public T contentAt(int row, int column) {
		List<T> rowList = data.get(row);
		return rowList != null ? rowList.get(column) : null;
	}

	@Override
	public int numberOfRows() {
		return data.size();
	}

	@Override
	public int numberOfColumns() {
		return isEmpty() ? 0 : data.get(0).size();
	}

	/**
	 *
	 * @return true if clipboard is empty.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Clear buffer
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Copy from tabular data to the buffer.
	 * @param tabularData to copy from.
	 * @param range to copy.
	 * @param type selection type
	 */
	public void copy(TabularData tabularData, TabularRange range, SelectionType type) {
		this.sourceRange = range;
		this.type = type;
		clear();
		for (int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
			List<T> rowData = new ArrayList<>();
			for (int column = range.getFromColumn(); column
					< range.getToColumn() + 1; column++) {
				rowData.add((T) tabularData.contentAt(row, column));
			}
			data.add(rowData);
		}
	}
}