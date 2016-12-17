package org.geogebra.common.gui.view.probcalculator;

public abstract class ChiSquareCell {

	private StatisticsCollection sc;

	private boolean isMarginCell = false;
	private boolean isHeaderCell = false;

	private int row, column;

	public ChiSquareCell(StatisticsCollection sc1) {
		this.sc = sc1;
	}

	public final void setMarginCell(boolean isMarginCell) {
		this.isMarginCell = isMarginCell;
		setVisualStyle();
	}

	protected abstract void setVisualStyle();

	public final void setHeaderCell(boolean isHeaderCell) {
		this.isHeaderCell = isHeaderCell;
		setVisualStyle();
	}

	protected void init(int row1, int column1) {
		this.row = row1;
		this.column = column1;
	}

	protected void updateCellData(String text) {
		sc.chiSquareData[row][column] = text;
	}

	protected boolean isHeaderCell() {
		return isHeaderCell;
	}

	protected boolean isMarginCell() {
		return isMarginCell;
	}
}
