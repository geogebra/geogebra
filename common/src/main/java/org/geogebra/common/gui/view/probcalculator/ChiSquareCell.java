package org.geogebra.common.gui.view.probcalculator;

public abstract class ChiSquareCell {

	protected StatisticsCollection sc;

	protected boolean isMarginCell = false;
	protected boolean isHeaderCell = false;

	protected int row, column;

	public final void setMarginCell(boolean isMarginCell) {
		this.isMarginCell = isMarginCell;
		setVisualStyle();
	}

	protected abstract void setVisualStyle();

	public final void setHeaderCell(boolean isHeaderCell) {
		this.isHeaderCell = isHeaderCell;
		setVisualStyle();
	}
}
