package geogebra.web.gui.menubar;

import geogebra.common.gui.menubar.MenuFactory;
import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.gui.menubar.OptionsMenu;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.html5.main.AppW;
import geogebra.html5.util.Dom;
import geogebra.web.gui.images.AppResources;
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
public class OptionsMenuW extends GMenuBar implements MenuInterface, MyActionListener{
	
	private AppW app;
	private Kernel kernel;
	private static int currentZoom = 1;
	
	private double origFontSize;
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenuW(AppW app) {
		super(true, new MenuResources());
	    this.app = app;
	    kernel = app.getKernel();
	    addStyleName("GeoGebraMenuBar");
	    initItems();
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		getOptionsMenu().addAlgebraDescriptionMenu(this);
		getOptionsMenu().addDecimalPlacesMenu(this);	
		addSeparator();
		getOptionsMenu().addLabelingMenu(this);
		addSeparator();
		getOptionsMenu().addFontSizeMenu(this);
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
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE
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
		
			App.debug("smart menu");
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_options_language().getSafeUri().asString(), app.getMenu("Language"), true), true, new Command(){

						@Override
	                    public void execute() {
							
							app.showLanguageUI();
		                    
	                    }});
			return;
		
	}
	
	private void addRestoreDefaultSettingsMenu(){
		
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE
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
						if (app.hasEuclidianView2EitherShowingOrNot(1))
							if (app.getEuclidianView2(1).hasStyleBar())
								app.getEuclidianView2(1).getStyleBar()
								.restoreDefaultGeo();						

			        }
		        });
	}
	
	private void addSaveSettingsMenu(){
		
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_save().getSafeUri().asString(), app.getMenu("Settings.Save"), true),
		        true, new Command() {
			        public void execute() {
			        	GeoGebraPreferencesW.getPref().saveXMLPreferences(app);
			        }
		        });
	}
	
	public void actionPerformed(String cmd){
		getOptionsMenu().processActionPerformed(cmd);
	}

	private OptionsMenu getOptionsMenu() {
		return app.getOptionsMenu(new MenuFactory() {

			@Override
			public RadioButtonMenuBar newSubmenu() {
				return new RadioButtonMenuBarW(app, true);
			}
		});
	}
}
