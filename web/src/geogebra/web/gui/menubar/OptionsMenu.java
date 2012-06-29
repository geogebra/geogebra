package geogebra.web.gui.menubar;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The "Options" menu.
 */
public class OptionsMenu extends MenuBar{
	
	private static AbstractApplication app;
	static Kernel kernel;
	
	private LanguageMenu languageMenu;
	private RadioButtonMenuBar menuPointCapturing;
	private RadioButtonMenuBar menuDecimalPlaces;
	
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenu(AbstractApplication app) {

		super(true);
	    this.app = app;
	    kernel = app.getKernel();
	    addStyleName("GeoGebraMenuBar");
	    initItems();
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		addAlgebraDescriptionMenu();
		
		//language menu
		addLanguageMenu();
		
		addPointCapturingMenu();
		addDecimalPlacesMenu();
	}
	
	private void addLanguageMenu() {
		languageMenu = new LanguageMenu(app);
		addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Language")), true, languageMenu);
	}
	
	private void addAlgebraDescriptionMenu(){
		RadioButtonMenuBar submenu = new RadioButtonMenuBar();
		
		submenu.addItem(app.getMenu("Value"), new RadioButtonCommand(submenu, 0) {
			

			@Override
			public void exec() {
				kernel.setAlgebraStyle(0);
				kernel.updateConstruction();
			}
		});
		submenu.addItem(app.getMenu("Definition"), new RadioButtonCommand(submenu, 1) {
			@Override
			public void exec() {
				kernel.setAlgebraStyle(1);
				kernel.updateConstruction();
			}
		});
		submenu.addItem(app.getMenu("Command"), new RadioButtonCommand(submenu, 2) {
			@Override
			public void exec() {
				kernel.setAlgebraStyle(2);
				kernel.updateConstruction();
			}
		});
				
		MenuItem algebraDescription = new MenuItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("AlgebraDescriptions")),
		        true, submenu);		
		
		addItem(algebraDescription);		
	}

	private void addPointCapturingMenu(){
//		RadioButtonMenuBar submenu = new RadioButtonMenuBar();
//		
//		submenu.addItem(app.getMenu("Labeling.automatic"), new RadioButtonCommand(submenu, 0) {
//			@Override
//			public void exec() {
//				setPointCapturing(3);
//			}
//		});
//		submenu.addItem(app.getMenu("SnapToGrid"), new RadioButtonCommand(submenu, 1) {
//			@Override
//			public void exec() {
//				setPointCapturing(1);
//			}
//		});
//		submenu.addItem(app.getMenu("FixedToGrid"), new RadioButtonCommand(submenu, 2) {
//			@Override
//			public void exec() {
//				setPointCapturing(2);
//			}
//		});
//	
//		submenu.addItem(app.getMenu("off"), new RadioButtonCommand(submenu, 3) {
//			@Override
//			public void exec() {
//				setPointCapturing(0);
//			}
//		});
//		
//		menuPointCapturing = new MenuItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
//		        .magnet2().getSafeUri().asString(), app.getMenu("PointCapturing")),
//		        true, submenu);		
//		
//		addItem(menuPointCapturing);
		
		menuPointCapturing = new RadioButtonMenuBar();
		String[] strPointCapturing = { app.getMenu("Labeling.automatic"), app.getMenu("SnapToGrid"),
				app.getMenu("FixedToGrid"), app.getMenu("off") };
		String[] strPointCapturingAC = { "3 PointCapturing",
				"1 PointCapturing", "2 PointCapturing", "0 PointCapturing" };
		menuPointCapturing.addRadioButtonMenuItems(this,
				strPointCapturing, strPointCapturingAC, 0);		
		addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .magnet2().getSafeUri().asString(), app.getMenu("PointCapturing")),
		        true, menuPointCapturing);
		updateMenuPointCapturing();
	}
	
	void setPointCapturing(int mode){
		app.getEuclidianView1().setPointCapturing(mode);
		if (app.hasEuclidianView2EitherShowingOrNot()) {
			app.getEuclidianView2().setPointCapturing(mode);
		}
		app.setUnsaved();		
	}
	
	public void update() {
		//updateMenuDecimalPlaces();
		updateMenuPointCapturing();
		//updateMenuViewDescription();
	}

	/**
	 * Update the point capturing menu.
	 */
	private void updateMenuPointCapturing() {
//		if (menuPointCapturing == null)
//			return;
//
//		int mode = app.getActiveEuclidianView().getPointCapturingMode();	
//		((RadioButtonMenuBar) menuPointCapturing.getSubMenu()).setSelected(mode);
		
		if (menuPointCapturing == null)
			return;

		int pos = app.getActiveEuclidianView().getPointCapturingMode();
		menuPointCapturing.setSelected(pos);
	}


	public void addDecimalPlacesMenu(){
		menuDecimalPlaces = new RadioButtonMenuBar();

		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getRoundingMenu();

		menuDecimalPlaces.addRadioButtonMenuItems(this,
				strDecimalSpaces, AbstractApplication.strDecimalSpacesAC, 0);

		
		addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Rounding")),
		        true, menuDecimalPlaces);
		
		updateMenuDecimalPlaces();		
	}
	

	/**
	 * Update the menu with all decimal places.
	 */
	private void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < AbstractApplication.figuresLookup.length)
				pos = AbstractApplication.figuresLookup[figures];
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals > 0 && decimals < AbstractApplication.decimalsLookup.length)
				pos = AbstractApplication.decimalsLookup[decimals];

		}

		try {
			menuDecimalPlaces.setSelected(pos);
		} catch (Exception e) {
			//
		}

	}


	public static void actionPerformed(String cmd) {
		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

				kernel.setPrintDecimals(decimals);
				kernel.updateConstruction();
				((Application)app).refreshViews();
				
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
		

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView1().setPointCapturing(mode);
			if (app.hasEuclidianView2EitherShowingOrNot()) {
				app.getEuclidianView2().setPointCapturing(mode);
			}
			app.setUnsaved();
		}
    }
}
