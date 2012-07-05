package geogebra.common.gui.menubar;

import geogebra.common.factories.Factory;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

public abstract class OptionsMenuStatic {
	
	private static RadioButtonMenuBar menuDecimalPlaces;

	//TODO: this will be void, if all cases will processed here
	public static boolean processActionPerformed(String cmd,
			App app, Kernel kernel) {
		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

				kernel.setPrintDecimals(decimals);
				kernel.updateConstruction();
				app.refreshViews();
				
				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				// Application.debug("figures " + figures);

				kernel.setPrintFigures(figures);
				kernel.updateConstruction();
				app.refreshViews();
				
				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		} else return false;
		
		return true;
			
    }
	
	/**
	 * Update the menu with all decimal places.
	 */
	private static void updateMenuDecimalPlaces(Kernel kernel) {
		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < App.figuresLookup.length)
				pos = App.figuresLookup[figures];
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals > 0 && decimals < App.decimalsLookup.length)
				pos = App.decimalsLookup[decimals];

		}

		try {
			menuDecimalPlaces.setSelected(pos);
		} catch (Exception e) {
			//
		}

	}
	
	public static void addDecimalPlacesMenu(MenuInterface menu, App app){
		menuDecimalPlaces = Factory.prototype.newRadioButtonMenuBar();

		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getRoundingMenu();

		menuDecimalPlaces.addRadioButtonMenuItems(menu,
				strDecimalSpaces, App.strDecimalSpacesAC, 0);
		
		app.addMenuItem(menu, "empty.png", app.getMenu("Rounding"), true, menuDecimalPlaces);
		
		updateMenuDecimalPlaces(app.getKernel());		
	}
	
	
}
