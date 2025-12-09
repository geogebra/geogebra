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

package org.geogebra.common.gui.view.table.dimensions;

import org.geogebra.common.gui.view.table.TableValuesCell;
import org.geogebra.common.gui.view.table.TableValuesDimensions;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Implementation of TableValuesDimensions.
 */
public class TableValuesViewDimensions implements TableValuesDimensions, TableValuesListener {
	private DimensionCache columnCache;
	private TextSizeMeasurer measurer;

	/** Table values model */
	TableValuesModel tableModel;

	/**
	 * Construct a new TableValuesViewDimensions object.
	 * @param model table values model
	 * @param measurer text size measurer
	 */
	public TableValuesViewDimensions(TableValuesModel model, TextSizeMeasurer measurer) {
		this.tableModel = model;
		this.columnCache = new DimensionCache(this);
		this.measurer = measurer;
	}

	@Override
	public int getColumnWidth(int column) {
		return columnCache.getWidth(column);
	}

	/**
	 * @param column to get the width.
	 * @return the calculated width.
	 */
	int calculateExactColumnWidth(int column) {
		int maxWidth = MIN_COLUMN_WIDTH;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			TableValuesCell cell = tableModel.getCellAt(i, column);
			int width = getWidth(cell.getInput());
			maxWidth = Math.max(maxWidth, width);
		}

		return maxWidth;
	}

	private int getWidth(String text) {
		int cellWidth = measurer.getWidth(text);
		cellWidth += CELL_LEFT_MARGIN + CELL_RIGHT_MARGIN;
		return Math.max(Math.min(MAX_COLUMN_WIDTH, cellWidth), MIN_COLUMN_WIDTH);
	}

	private void resetCache() {
		columnCache.resetCache();
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		columnCache.removeColumn(column);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		columnCache.updateColumn(column);
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		columnCache.addColumn(column);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// Ignore
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		notifyColumnChanged(model, evaluatable, column);
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		resetCache();
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		resetCache();
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		resetCache();
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		resetCache();
	}

	@Override
	public int getColumnWidth(int column, int exceptRow) {
		int maxWidth = MIN_COLUMN_WIDTH;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (i != exceptRow && column < tableModel.getColumnCount()) {
				TableValuesCell cell = tableModel.getCellAt(i, column);
				int width = getWidth(cell.getInput());
				maxWidth = Math.max(maxWidth, width);
			}
		}

		return maxWidth;
	}
}
