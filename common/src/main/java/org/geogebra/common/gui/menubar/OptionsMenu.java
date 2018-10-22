package org.geogebra.common.gui.menubar;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * This class is not a superclass of OptionsMenu, only common method stack
 */
public class OptionsMenu {

	private Localization localization;

	/**
	 * @param localization
	 *            localization
	 */
	public OptionsMenu(Localization localization) {
		this.localization = localization;
	}

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param skipSeparator
	 *            whether to skip the separator between DP and SF
	 * @return position in rounding menu regarding current kernel settings
	 */
	final public int getMenuDecimalPosition(Kernel kernel,
			boolean skipSeparator) {
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < figuresLookupLength()) {
				pos = figuresLookup(figures) - (skipSeparator ? 1 : 0);
			}
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals >= 0 && decimals < decimalsLookupLength()) {
				pos = decimalsLookup(decimals);
			}
		}

		return pos;
	}

	/**
	 * @param i
	 *            number of figures
	 * @return menu index
	 */
	public int figuresLookup(int i) {
		int[] significantFigures = localization.getSignificantFigures();
		int[] decimalPlaces = localization.getDecimalPlaces();
		for (int index = 0; index < significantFigures.length; index++) {
			if (significantFigures[index] == i) {
				return index + decimalPlaces.length + 1;
			}
		}
		return -1;
	}

	/**
	 * @return number of "significant figures" items
	 */
	public int figuresLookupLength() {
		int[] significantFigures = localization.getSignificantFigures();
		return significantFigures[significantFigures.length - 1] + 1;
	}

	/**
	 * @param i
	 *            number of decimals
	 * @return menu item
	 */
	public int decimalsLookup(int i) {
		int[] decimalPlaces = localization.getDecimalPlaces();
		for (int index = 0; index < decimalPlaces.length; index++) {
			if (decimalPlaces[index] == i) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * @return number of "decimal places" items
	 */
	public int decimalsLookupLength() {
		int[] decimalPlaces = localization.getDecimalPlaces();
		return decimalPlaces[decimalPlaces.length - 1] + 1;
	}

	/**
	 * @param i
	 *            menu item index
	 * @return decimal places or significant figures
	 */
	public int roundingMenuLookup(int i) {
		int[] decimals = localization.getDecimalPlaces();
		int[] significant = localization.getSignificantFigures();
		if (i < decimals.length) {
			return decimals[i];
		}
		return significant[i - decimals.length - 1];
	}

	/**
	 * @param app
	 *            app
	 * @param id
	 *            index of rounding option
	 * @param figures
	 *            whether to use significant figures
	 */
	public void setRounding(App app, int id, boolean figures) {
		Kernel kernel = app.getKernel();
		int rounding = roundingMenuLookup(id);
		if (figures) {
			kernel.setPrintFigures(rounding);
		} else {
			kernel.setPrintDecimals(rounding);
		}

		kernel.updateConstruction(false);
		app.refreshViews();

		// see ticket 79
		kernel.updateConstruction(false);

		app.setUnsaved();
	}
}
