package geogebra.web.gui.menubar;

import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.gui.menubar.OptionsMenu;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Options" menu.
 */
public class OptionsMenuW extends MenuBar implements MenuInterface, MyActionListener{
	
	private static App app;
	static Kernel kernel;
	
	private LanguageMenuW languageMenu;
	private MenuBar menuAlgebraStyle;
	private RadioButtonMenuBarW menuPointCapturing;
	private RadioButtonMenuBarW menuDecimalPlaces;
	
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenuW(App app) {

		super(true);
	    this.app = app;
	    kernel = app.getKernel();
	    OptionsMenu.init(app);
	    addStyleName("GeoGebraMenuBar");
	    initItems();
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		OptionsMenu.addAlgebraDescriptionMenu(this);
		
		addPointCapturingMenu();
		OptionsMenu.addDecimalPlacesMenu(this, app);
		
		addSeparator();
		
		OptionsMenu.addLabelingMenu(this, app);
		addSeparator();
		
		//language menu
		addLanguageMenu();
	}
	
	private void addLanguageMenu() {
		languageMenu = new LanguageMenuW(app);
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Language")), true, languageMenu);
	}
	

	
	
	
	

	private void addPointCapturingMenu(){		
		menuPointCapturing = new RadioButtonMenuBarW(app);
		String[] strPointCapturing = { app.getMenu("Labeling.automatic"), app.getMenu("SnapToGrid"),
				app.getMenu("FixedToGrid"), app.getMenu("off") };
		String[] strPointCapturingAC = { "3 PointCapturing",
				"1 PointCapturing", "2 PointCapturing", "0 PointCapturing" };
		menuPointCapturing.addRadioButtonMenuItems(this,
				strPointCapturing, strPointCapturingAC, 0, false);
		app.addMenuItem(this, "magnet2.gif", app.getMenu("PointCapturing"), true, menuPointCapturing);
		
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



	

	public void actionPerformed(String cmd){
		OptionsMenu.processActionPerformed(cmd, app, kernel);
	}


}
