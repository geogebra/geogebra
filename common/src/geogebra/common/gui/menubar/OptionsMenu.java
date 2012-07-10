package geogebra.common.gui.menubar;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import geogebra.common.factories.Factory;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

/**
 * This class is not a superclass of OptionsMenu, only  common method stack
 */
public abstract class OptionsMenu {
	
	private static RadioButtonMenuBar menuDecimalPlaces;
	private static RadioButtonMenuBar menuLabeling;

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
		} 
		
		

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)));
				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView1().setPointCapturing(mode);
			if (app.hasEuclidianView2EitherShowingOrNot()) {
				app.getEuclidianView2().setPointCapturing(mode);
			}
			app.setUnsaved();
		}

		// Labeling
		else if (cmd.endsWith("labeling")) {
			int style = Integer.parseInt(cmd.substring(0, 1));
			app.setLabelingStyle(style);
			app.setUnsaved();
		}	
		else return false;
		
		return true;
			
    }
	
	/**
	 * Update the menu with all decimal places.
	 */
	public static void updateMenuDecimalPlaces(Kernel kernel) {
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
		menuDecimalPlaces = Factory.prototype.newRadioButtonMenuBar(app);

		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getRoundingMenu();

		menuDecimalPlaces.addRadioButtonMenuItems((MyActionListener)menu,
				strDecimalSpaces, App.strDecimalSpacesAC, 0, false);
		
		app.addMenuItem(menu, "empty.png", app.getMenu("Rounding"), true, menuDecimalPlaces);
		
		updateMenuDecimalPlaces(app.getKernel());		
	}
	
	
	public static void addLabelingMenu(MenuInterface menu, App app){	
		menuLabeling = Factory.prototype.newRadioButtonMenuBar(app);
		
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off",
				"Labeling.pointsOnly" };
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling",
				"3_labeling" };
		menuLabeling.addRadioButtonMenuItems((MyActionListener)menu, lstr,
				lastr, 0, true);
		
		app.addMenuItem(menu, "mode_showhidelabel_16.gif", app.getMenu("Labeling"), true, menuLabeling);
		
		updateMenuLabeling(app);
	}
	
	/**
	 * Update the selected item in the labeling capturing menu.
	 */
	private static void updateMenuLabeling(App app) {
		if (menuLabeling == null) return;
		
		int pos = app.getLabelingStyle();
		menuLabeling.setSelected(pos);
	}
}
