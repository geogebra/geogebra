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
