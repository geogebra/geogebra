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

package org.geogebra.common.gui.view.probcalculator;

public abstract class ChiSquareCell {

	private StatisticsCollection statsCollection;

	private boolean isMarginCell = false;
	private boolean isHeaderCell = false;

	private int row;
	private int column;

	/**
	 * @param statsCollection
	 *            statistics collection
	 */
	public ChiSquareCell(StatisticsCollection statsCollection) {
		this.statsCollection = statsCollection;
	}

	/**
	 * @param isMarginCell
	 *            whether this is a margin cell
	 */
	public final void setMarginCell(boolean isMarginCell) {
		this.isMarginCell = isMarginCell;
		setVisualStyle();
	}

	protected abstract void setVisualStyle();

	/**
	 * @param isHeaderCell
	 *            whether this is a header cell
	 */
	public final void setHeaderCell(boolean isHeaderCell) {
		this.isHeaderCell = isHeaderCell;
		setVisualStyle();
	}

	/**
	 * @param row1
	 *            row
	 * @param column1
	 *            column
	 */
	protected void init(int row1, int column1) {
		this.row = row1;
		this.column = column1;
	}

	/**
	 * Update text content of the cell.
	 * @param text new text content
	 */
	public void updateCellData(String text) {
		statsCollection.chiSquareData[row][column] = text;
	}

	protected boolean isHeaderCell() {
		return isHeaderCell;
	}

	protected boolean isMarginCell() {
		return isMarginCell;
	}

	/**
	 * @param i
	 *            sub-row
	 * @param show
	 *            whether to show info on given sub-row
	 */
	public abstract void setLabelVisible(int i, boolean show);

	/**
	 * @param i
	 *            sub-row
	 * @param label
	 *            text of sub-row
	 */
	public abstract void setLabelText(int i, String label);

	/**
	 * Change value of input
	 * 
	 * @param value
	 *            new value
	 */
	public abstract void setValue(String value);
}
