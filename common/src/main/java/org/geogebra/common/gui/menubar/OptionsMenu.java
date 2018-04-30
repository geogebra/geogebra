package org.geogebra.common.gui.menubar;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;

/**
 * This class is not a superclass of OptionsMenu, only common method stack
 */
public class OptionsMenu {
	final private static int[] ROUNDING_MENU_LOOKUP = { 0, 1, 2, 3, 4, 5, 10,
			15, -1, 3, 5, 10, 15 };
	final private static int[] DECIMALS_LOOKUP = { 0, 1, 2, 3, 4, 5, -1, -1, -1,
			-1, 6, -1, -1, -1, -1, 7 };
	final private static int[] FIGURES_LOOKUP = { -1, -1, -1, 9, -1, 10, -1, -1,
			-1, -1, 11, -1, -1, -1, -1, 12 };

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param skipSeparator
	 *            whether to skip the separator between DP and SF
	 * @return position in rounding menu regarding current kernel settings
	 */
	static final public int getMenuDecimalPosition(Kernel kernel,
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

	public static int figuresLookup(int i) {
		return FIGURES_LOOKUP[i];
	}

	public static int figuresLookupLength() {
		return FIGURES_LOOKUP.length;
	}

	public static int decimalsLookup(int i) {
		return DECIMALS_LOOKUP[i];
	}

	public static int decimalsLookupLength() {
		return DECIMALS_LOOKUP.length;
	}

	public static int roundingMenuLookup(int i) {
		return ROUNDING_MENU_LOOKUP[i];
	}

	/**
	 * @param app
	 *            app
	 * @param id
	 *            index of rounding option
	 * @param figures
	 *            whether to use significant figures
	 */
	public static void setRounding(App app, int id, boolean figures) {
		Kernel kernel = app.getKernel();
		if (figures) {
			kernel.setPrintFigures(OptionsMenu.roundingMenuLookup(id));
		} else {
			kernel.setPrintDecimals(OptionsMenu.roundingMenuLookup(id));
		}

		kernel.updateConstruction(false);
		app.refreshViews();

		// see ticket 79
		kernel.updateConstruction(false);

		app.setUnsaved();
	}
}
