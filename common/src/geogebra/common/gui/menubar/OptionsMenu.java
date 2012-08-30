package geogebra.common.gui.menubar;

import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

/**
 * This class is not a superclass of OptionsMenu, only  common method stack
 */
public abstract class OptionsMenu {

	private static RadioButtonMenuBar menuAlgebraStyle;
	private static RadioButtonMenuBar menuDecimalPlaces;
	private static RadioButtonMenuBar menuLabeling;
	private static RadioButtonMenuBar menuPointCapturing;
	private static App app;
	static Kernel kernel;
	
	/**
	 * Sets the value of app field.
	 * @param application
	 */
	public static void init(App application){
		app = application;
		kernel = app.getKernel();
	}
	

	public static void processActionPerformed(String cmd) {
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
				e.printStackTrace();
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
    }

	/**
	 * Adds the "Algebra description" menu for the menu given in parameter 
	 * @param menu "Algebra description menu will be added for this
	 */
	public static void addAlgebraDescriptionMenu(MenuInterface menu){	
		menuAlgebraStyle = app.getFactory().newRadioButtonMenuBar(app);
		
		String[] strDescription = { app.getPlain("Value"), 
				app.getPlain("Definition"), 
				app.getPlain("Command")};
		String[] strDescriptionAC = { "0", "1", "2" };
		
		menuAlgebraStyle.addRadioButtonMenuItems(new MyActionListener() {
			public void actionPerformed(String command) {
				int desc = Integer.parseInt(command);
				kernel.setAlgebraStyle(desc);
				kernel.updateConstruction();
			}
		}, strDescription, strDescriptionAC, 0, false);
		app.addMenuItem(menu, app.getEmptyIconFileName(), app.getMenu("AlgebraDescriptions"), true,
				menuAlgebraStyle);
		
		updateMenuViewDescription();	
	}
	
	/**
	 * Update algebra style description (switch between value / definition / command).
	 */
	public static void updateMenuViewDescription() {
		if (menuAlgebraStyle != null) {
			menuAlgebraStyle.setSelected(kernel.getAlgebraStyle());
		}
	}
	
	/**
	 * Update the menu with all decimal places.
	 */
	public static void updateMenuDecimalPlaces() {
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
	
	public static void addDecimalPlacesMenu(MenuInterface menu){
		menuDecimalPlaces = app.getFactory().newRadioButtonMenuBar(app);

		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getRoundingMenu();

		menuDecimalPlaces.addRadioButtonMenuItems((MyActionListener)menu,
				strDecimalSpaces, App.strDecimalSpacesAC, 0, false);
		
		app.addMenuItem(menu, app.getEmptyIconFileName(), app.getMenu("Rounding"), true, menuDecimalPlaces);
		
		updateMenuDecimalPlaces();		
	}
	
	
	public static void addLabelingMenu(MenuInterface menu){	
		menuLabeling = app.getFactory().newRadioButtonMenuBar(app);
		
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off",
				"Labeling.pointsOnly" };
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling",
				"3_labeling" };
		menuLabeling.addRadioButtonMenuItems((MyActionListener)menu, lstr,
				lastr, 0, true);
		
		app.addMenuItem(menu, "mode_showhidelabel_16.gif", app.getMenu("Labeling"), true, menuLabeling);
		
		updateMenuLabeling();
	}
	
	/**
	 * Update the selected item in the labeling capturing menu.
	 */
	private static void updateMenuLabeling() {
		if (menuLabeling == null) return;
		
		int pos = app.getLabelingStyle();
		menuLabeling.setSelected(pos);
	}
	
	public static void addPointCapturingMenu(MenuInterface menu){		
		menuPointCapturing = app.getFactory().newRadioButtonMenuBar(app);
		String[] strPointCapturing = { app.getMenu("Labeling.automatic"), app.getMenu("SnapToGrid"),
				app.getMenu("FixedToGrid"), app.getMenu("off") };
		String[] strPointCapturingAC = { "3 PointCapturing",
				"1 PointCapturing", "2 PointCapturing", "0 PointCapturing" };
		menuPointCapturing.addRadioButtonMenuItems((MyActionListener)menu,
				strPointCapturing, strPointCapturingAC, 0, false);
		app.addMenuItem(menu, "magnet2.gif", app.getMenu("PointCapturing"), true, menuPointCapturing);
		
		updateMenuPointCapturing();
	}
	
	/**
	 * Update the point capturing menu.
	 */
	public static void updateMenuPointCapturing() {	
		if (menuPointCapturing == null)
			return;

		int pos = app.getActiveEuclidianView().getPointCapturingMode();
		menuPointCapturing.setSelected(pos);
	}
	
	public static void addFontSizeMenu(MenuInterface menu){
		RadioButtonMenuBar submenu = app.getFactory().newRadioButtonMenuBar(app);
		
		//String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt", "24 pt",
		//		"28 pt", "32 pt" };
		String[] fsfi = new String[MyXMLHandler.menuFontSizes.length];
		String[] fontActionCommands = new String[MyXMLHandler.menuFontSizes.length];

		// find current pos
		int fontSize = app.getFontSize();
		int pos = 0;
		for (int i = 0; i < MyXMLHandler.menuFontSizes.length; i++) {
			if (fontSize == MyXMLHandler.menuFontSizes[i]) {
				pos = i;
			}
			fsfi[i] = app.getPlain("Apt",MyXMLHandler.menuFontSizes[i]+"");
			fontActionCommands[i]=MyXMLHandler.menuFontSizes[i] + " pt";
		}

		submenu.addRadioButtonMenuItems((MyActionListener)menu, fsfi, fontActionCommands, pos, true);
		app.addMenuItem(menu, "font.png", app.getMenu("FontSize"), true, submenu);
	}

	public static void update() {
		updateMenuDecimalPlaces();
		updateMenuPointCapturing();
		updateMenuViewDescription();
	}
}
