package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.menubar.MenuFactory;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.main.GeoGebraPreferencesW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Options" menu.
 */
public class OptionsMenuW extends GMenuBar implements MenuInterface, MyActionListener{
	
	private AppW app;
	
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenuW(AppW app) {
		super(true, "options", new MenuResources(), app);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    initItems();
	}

	/**
	 * @return app
	 */
	protected AppW getApp() {
		return app;
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		// getOptionsMenu().addAlgebraDescriptionMenu(this);
		getOptionsMenu().addDecimalPlacesMenu(this);

		// in exam mode only "Rounding" menu needed
		if (app.isExam()) {
			return;
		}

		if (!app.has(Feature.NEW_TOOLBAR)) {
			addSeparator();
		}
		getOptionsMenu().addLabelingMenu(this);
		if (!app.has(Feature.NEW_TOOLBAR)) {
			addSeparator();
		}
		getOptionsMenu().addFontSizeMenu(this);
		//language menu
		addLanguageMenu();
		if (!getApp().isApplet() && getApp().enableFileFeatures()) {
			if (!app.has(Feature.NEW_TOOLBAR)) {
				addSeparator();
			}
			addSaveSettingsMenu();
			addRestoreDefaultSettingsMenu();
		}
		
		/*
		addSeparator();
		addZoomMenu();
		addGlobalFontSizeMenu();
		*/
	}

	private void addLanguageMenu() {

		if (!app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					app.has(Feature.NEW_TOOLBAR)
							? MaterialDesignResources.INSTANCE.language_black()
									.getSafeUri().asString()
							: GuiResources.INSTANCE
		        .menu_icon_options_language().getSafeUri().asString(),
					getApp().getLocalization()
				.getMenu("Language"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
				getApp().showLanguageGUI();
			}
		});
		}
	}
	
	private void addRestoreDefaultSettingsMenu(){
		
		addItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty()
				.getSafeUri().asString(),
				getApp().getLocalization().getMenu("Settings.ResetDefault"),
				true),
		        true, new MenuCommand(app) {
			
			        @Override
			        public void doExecute() {
						resetDefault();

					}
				});
	}

	/**
	 * Reset defaults
	 */
	protected void resetDefault() {
		GeoGebraPreferencesW.getPref().clearPreferences();

		// reset defaults for GUI, views etc
		// this has to be called before load XML preferences,
		// in order to avoid overwrite
		app.getSettings().resetSettings(app);

		// for geoelement defaults, this will do nothing, so it is
		// OK here
		GeoGebraPreferencesW.getPref().resetPreferences(app);

		// reset default line thickness etc
		app.getKernel().getConstruction().getConstructionDefaults()
				.resetDefaults();

		// reset defaults for geoelements; this will create brand
		// new objects
		// so the options defaults dialog should be reset later
		app.getKernel().getConstruction().getConstructionDefaults()
				.createDefaultGeoElements();

		// reset the stylebar defaultGeo
		if (app.getEuclidianView1().hasStyleBar()) {
			app.getEuclidianView1().getStyleBar().restoreDefaultGeo();
		}
		if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1).hasStyleBar()) {
			app.getEuclidianView2(1).getStyleBar().restoreDefaultGeo();
		}
		// TODO needed to eg. update rounding, possibly too heavy
		app.getKernel().updateConstruction();
	}
	
	private void addSaveSettingsMenu(){
		if (!app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					app.has(Feature.NEW_TOOLBAR)
							? MaterialDesignResources.INSTANCE.save_black()
									.getSafeUri().asString()
							: GuiResources.INSTANCE
					.menu_icon_file_save().getSafeUri().asString(),
					getApp().getLocalization()
				.getMenu("Settings.Save"), true),
		        true, new MenuCommand(app) {
			
					@Override
					public void doExecute() {
				GeoGebraPreferencesW.getPref().saveXMLPreferences(getApp());
			        }
		        });
		}
	}
	
	@Override
	public void actionPerformed(String cmd){
		getOptionsMenu().processActionPerformed(cmd);
	}

	private OptionsMenu getOptionsMenu() {
		return getApp().getOptionsMenu(new MenuFactory() {

			@Override
			public RadioButtonMenuBar newSubmenu() {
				return new RadioButtonMenuBarW(getApp(), true);
			}

			@Override
			public void addMenuItem(MenuInterface parentMenu, String key,
					boolean asHtml, MenuInterface subMenu) {
				addMenuItem((MenuBar) parentMenu, key, subMenu);
			}

			private void addMenuItem(MenuBar parentMenu, String key,
					MenuInterface subMenu) {

				if (subMenu instanceof MenuBar) {
					((MenuBar) subMenu).addStyleName("GeoGebraMenuBar");
				}
				ImageResource imgRes = AppResources.INSTANCE.empty();

				if ("Labeling".equals(key)) {
					imgRes = app.has(Feature.NEW_TOOLBAR)
							? MaterialDesignResources.INSTANCE.label_black()
							: AppResources.INSTANCE.mode_showhidelabel_16();
				}
				if ("FontSize".equals(key)) {
					imgRes = app.has(Feature.NEW_TOOLBAR)
							? MaterialDesignResources.INSTANCE.font_size_black()
							: GuiResources.INSTANCE
							.menu_icon_options_font_size();
				}
				((GMenuBar) parentMenu).addItem(
						getApp().getGuiManager().getMenuBarHtml(imgRes,
								getApp().getLocalization().getMenu(key), true),
						true, (MenuBar) subMenu, !app.isWhiteboardActive());
			}
		});
	}
	
//	private class ZoomMenu extends MenuBar implements MenuInterface{
//	
//	public ZoomMenu(){
//		super(true);
//	}
//}
//
//private void addZoomMenu(){
//	MenuBar zoomMenu = new ZoomMenu();
//	
//	zoomMenu.addItem(getZoomMenuItem("1"));
//	zoomMenu.addItem(getZoomMenuItem("1.2"));
//	zoomMenu.addItem(getZoomMenuItem("1.4"));
//	zoomMenu.addItem(getZoomMenuItem("1.6"));
	// getApp().addMenuItem((MenuInterface)this, "font.png",
	// /*getApp().getMenu("FontSize")*/ "Zoom", true, (MenuInterface)zoomMenu);
//}
//
//public MenuItem getZoomMenuItem(final String name){
//	ScheduledCommand cmd = new ScheduledCommand(){
//		@Override
//        public void execute() {
//           zoom(name);      
//        }
//	};
//	return new MenuItem(name, cmd);
//	
//}
//
//public void zoom(String d){
	// getApp().getFrameElement().getStyle().setProperty("zoom", d);
//	
//}
//
//private void addGlobalFontSizeMenu(){
//	addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE
//			.empty().getSafeUri().asString(), "font size: 32px", true),
//	        true, new MenuCommand(app) {
//		
//		@Override
//        public void doExecute() {
//	        //Remove the style element(s) if already exist
//					NodeList<Element> fontsizeElements = Dom.getElementsByClassName("GGWFontsize");
//					for(int i=0; i<fontsizeElements.getLength(); i++){
//						fontsizeElements.getItem(i).removeFromParent();
//					}
//					
	// getApp().getFrameElement().getStyle().setFontSize(32, Unit.PX);
//		        }
//	        });	
//}
//
}
