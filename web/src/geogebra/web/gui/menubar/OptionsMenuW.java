package geogebra.web.gui.menubar;

import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.gui.menubar.OptionsMenu;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.GeoGebraPreferencesW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Options" menu.
 */
public class OptionsMenuW extends MenuBar implements MenuInterface, MyActionListener{
	
	private static App app;
	static Kernel kernel;
	
	private LanguageMenuW languageMenu;
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenuW(App app) {
		super(true);
	    this.app = app;
	    kernel = app.getKernel();
	    addStyleName("GeoGebraMenuBar");
	    initItems();
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		app.getOptionsMenu().addAlgebraDescriptionMenu(this);
		app.getOptionsMenu().addPointCapturingMenu(this);
		app.getOptionsMenu().addDecimalPlacesMenu(this);	
		addSeparator();
		app.getOptionsMenu().addLabelingMenu(this);
		addSeparator();
		app.getOptionsMenu().addFontSizeMenu(this);
		//language menu
		addLanguageMenu();
		addSeparator();
		addSaveSettingsMenu();
		addRestoreDefaultSettingsMenu();
	}
	
	private void addLanguageMenu() {
		languageMenu = new LanguageMenuW(app);
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Language")), true, languageMenu);
	}
	
	private void addRestoreDefaultSettingsMenu(){
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Settings.ResetDefault")),
		        true, new Command() {
			        public void execute() {
			        	GeoGebraPreferencesW.getPref().clearPreferences();
			        	
						// reset defaults for GUI, views etc
						// this has to be called before load XML preferences,
						// in order to avoid overwrite
						app.getSettings().resetSettings();

						// for geoelement defaults, this will do nothing, so it is
						// OK here
						GeoGebraPreferencesW.getPref().loadXMLPreferences(app);

						
						// reset default line thickness etc
						app.getKernel().getConstruction().getConstructionDefaults()
						.resetDefaults();
						
						// reset defaults for geoelements; this will create brand
						// new objects
						// so the options defaults dialog should be reset later
						app.getKernel().getConstruction().getConstructionDefaults()
						.createDefaultGeoElementsFromScratch();

						// reset the stylebar defaultGeo
						if (app.getEuclidianView1().hasStyleBar())
							app.getEuclidianView1().getStyleBar()
							.restoreDefaultGeo();
						if (app.hasEuclidianView2EitherShowingOrNot())
							if (app.getEuclidianView2().hasStyleBar())
								app.getEuclidianView2().getStyleBar()
								.restoreDefaultGeo();						

			        }
		        });
	}
	
	private void addSaveSettingsMenu(){
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.document_save().getSafeUri().asString(), app.getMenu("Settings.Save")),
		        true, new Command() {
			        public void execute() {
			        	GeoGebraPreferencesW.getPref().saveXMLPreferences(app);
			        }
		        });
	}
	
	public void actionPerformed(String cmd){
		app.getOptionsMenu().processActionPerformed(cmd);
	}


}
