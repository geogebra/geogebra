package org.geogebra.common.gui.menubar;

import org.geogebra.common.main.Localization;

/**
 * Provides lists and lookup for rounding menus.
 * Can be used for global rounding, per-object rounding (GeoText), components with own rounding.
 */
public class RoundingOptions {

	private final Localization localization;

	/**
	 * @param localization
	 *            localization
	 */
	public RoundingOptions(Localization localization) {
		this.localization = localization;
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
}
