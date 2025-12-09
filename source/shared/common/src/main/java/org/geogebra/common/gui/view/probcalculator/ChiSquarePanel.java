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

import org.geogebra.common.main.Localization;

/**
 * Common superclass for ChiSquarePanel
 *
 * @author gabor
 */
public abstract class ChiSquarePanel {

	protected Localization loc;
	protected StatisticsCalculator statCalc;
	private StatisticsCalculatorProcessor statProcessor;
	private StatisticsCollection sc;
	private boolean showColumnMargin;

	/**
	 * @param loc
	 *            Localization
	 * @param statCalc
	 *            statistics calculator
	 */
	public ChiSquarePanel(Localization loc, StatisticsCalculator statCalc) {
		this.loc = loc;
		this.statCalc = statCalc;
		this.statProcessor = statCalc.getStatProcessor();
		this.sc = statCalc.getStatisticsCollection();
		if (sc.chiSquareData == null) {
			sc.setChiSqData(3, 3);
		} else {
			sc.initComputation(sc.chiSquareData.length - 2,
					sc.chiSquareData.length > 0 ? sc.chiSquareData[0].length - 2
							: 0);
		}
	}

	/**
	 * Update visibility of output values in cells
	 */
	public final void updateVisibility() {
		for (int i = 1; i < getSc().rows + 1; i++) {
			for (int j = 1; j < getSc().columns + 1; j++) {
				getCell(i, j).setLabelVisible(1, getSc().showExpected);
				getCell(i, j).setLabelVisible(2, getSc().showDiff);
				getCell(i, j).setLabelVisible(3, getSc().showRowPercent);
				getCell(i, j).setLabelVisible(4, getSc().showColPercent);
			}
		}

		// column percent for bottom margin
		for (int r = 0; r < getSc().rows; r++) {
			getCell(r + 1, getSc().columns + 1).setLabelVisible(3,
					getSc().showColPercent);
		}

		// row percent for right margin
		for (int c = 0; c < getSc().columns; c++) {
			getCell(getSc().rows + 1, c + 1).setLabelVisible(4,
					getSc().showRowPercent);
		}

		updateCellContent();
	}

	protected abstract ChiSquareCell getCell(int i, int j);

	/**
	 * Update input and output values in cells
	 */
	public void updateCellContent() {
		getStatProcessor().doCalculate();
		for (int r = 0; r < getSc().rows + 1; r++) {
			for (int c = 0; c < getSc().columns + 1; c++) {
				getCell(r, c).setValue(getSc().chiSquareData[r][c]);
			}
		}
		for (int r = 0; r < getSc().rows; r++) {
			for (int c = 0; c < getSc().columns; c++) {
				if (getSc().showExpected) {
					getCell(r + 1, c + 1).setLabelText(1,
							getStatCalc().format(getSc().expected[r][c]));
				}
				if (getSc().showDiff) {
					getCell(r + 1, c + 1).setLabelText(2,
							getStatCalc().format(getSc().diff[r][c]));
				}
				if (getSc().showRowPercent) {
					getCell(r + 1, c + 1).setLabelText(3, getStatCalc().format(
							100 * getSc().observed[r][c] / getSc().rowSum[r])
							+ "%");
				}
				if (getSc().showColPercent) {
					getCell(r + 1, c + 1).setLabelText(4, getStatCalc().format(
							100 * getSc().observed[r][c] / getSc().columnSum[c])
							+ "%");
				}
			}
		}

		// column margin
		if (showColumnMargin) {
			for (int r = 0; r < getSc().rows; r++) {
				getCell(r + 1, getSc().columns + 1).setLabelText(0,
						getStatCalc().format(getSc().rowSum[r]));
				if (getSc().showRowPercent) {
					getCell(r + 1, getSc().columns + 1).setLabelText(3,
							getStatCalc().format(
									100 * getSc().rowSum[r] / getSc().total)
									+ "%");
				}
			}
		}

		// bottom margin
		for (int c = 0; c < getSc().columns; c++) {
			getCell(getSc().rows + 1, c + 1).setLabelText(0,
					getStatCalc().format(getSc().columnSum[c]));

			if (getSc().showColPercent) {
				getCell(getSc().rows + 1, c + 1).setLabelText(4,
						getStatCalc().format(
								100 * getSc().columnSum[c] / getSc().total)
								+ "%");
			}

		}

		// bottom right corner
		if (showColumnMargin) {
			getCell(getSc().rows + 1, getSc().columns + 1).setLabelText(0,
					getStatCalc().format(getSc().total));
		}

	}

	public StatisticsCalculator getStatCalc() {
		return statCalc;
	}

	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getSc() {
		return sc;
	}

	/**
	 * added partly to kill findbugs warning about loc being unused
	 * 
	 * @param s
	 *            key
	 * @return translation
	 */
	protected String getMenu(String s) {
		return loc.getMenu(s);
	}

}
