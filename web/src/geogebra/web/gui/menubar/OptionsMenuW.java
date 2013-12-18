package geogebra.web.gui.menubar;

import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;
import geogebra.web.main.GeoGebraPreferencesW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The "Options" menu.
 */
public class OptionsMenuW extends MenuBar implements MenuInterface, MyActionListener{
	
	private static App app;
	static Kernel kernel;
	private static int currentZoom = 1;
	
	private LanguageMenuW languageMenu;
	private double origFontSize;
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
		if (!app.isApplet()){
			addSeparator();
			addSaveSettingsMenu();
			addRestoreDefaultSettingsMenu();
		}
		
		/*
		addSeparator();
		addZoomMenu();
		addGlobalFontSizeMenu();
		*/
	}
	
	private class ZoomMenu extends MenuBar implements MenuInterface{
		
		public ZoomMenu(){
			super(true);
		}
	}
	
	private void addZoomMenu(){
		MenuBar zoomMenu = new ZoomMenu();
		
		zoomMenu.addItem(getZoomMenuItem("1"));
		zoomMenu.addItem(getZoomMenuItem("1.2"));
		zoomMenu.addItem(getZoomMenuItem("1.4"));
		zoomMenu.addItem(getZoomMenuItem("1.6"));
		app.addMenuItem((MenuInterface)this, "font.png", /*app.getMenu("FontSize")*/ "Zoom", true, (MenuInterface)zoomMenu);
	}
	
	public MenuItem getZoomMenuItem(final String name){
		ScheduledCommand cmd = new ScheduledCommand(){
			@Override
            public void execute() {
	           zoom(name);      
            }
		};
		return new MenuItem(name, cmd);
		
	}
	
	public void zoom(String d){
		((AppW) app).getFrameElement().getStyle().setProperty("zoom", d);
		
	}
	
	private void addGlobalFontSizeMenu(){
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), "font size: 32px", true),
		        true, new Command() {
			        public void execute() {
						//Remove the style element(s) if already exist
						NodeList<Element> fontsizeElements = Dom.getElementsByClassName("GGWFontsize");
						for(int i=0; i<fontsizeElements.getLength(); i++){
							fontsizeElements.getItem(i).removeFromParent();
						}
						
			        	((AppW) app).getFrameElement().getStyle().setFontSize(32, Unit.PT);
			        }
		        });	
	}
	
	private void addLanguageMenu() {
		languageMenu = new LanguageMenuW(app);
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Language"), true), true, languageMenu);
	}
	
	private void addRestoreDefaultSettingsMenu(){
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
		        .empty().getSafeUri().asString(), app.getMenu("Settings.ResetDefault"), true),
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
				.document_save().getSafeUri().asString(), app.getMenu("Settings.Save"), true),
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
