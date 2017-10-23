package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.menubar.MenuFactory;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
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
	
	/**
	 * Constructs the "Option" menu
	 * @param app Application instance
	 */
	public OptionsMenuW(AppW app) {
		super(true, "options", new MenuResources(), app);
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initItems();
	}
	
	private void initItems(){
		//"Algebra Descriptions" menu
		// getOptionsMenu().addAlgebraDescriptionMenu(this);
		if (!getApp().isWhiteboardActive()) {
			getOptionsMenu().addDecimalPlacesMenu(this);

		// in exam mode only "Rounding" menu needed
			if (getApp().isExam()) {
			return;
		}
			addSeparator();
		getOptionsMenu().addLabelingMenu(this);
			addSeparator();
		getOptionsMenu().addFontSizeMenu(this);
		}
		//language menu
		addLanguageMenu();
		if (!getApp().isApplet() && getApp().enableFileFeatures()) {
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

	private void addLanguageMenu() {
		if (!getApp().isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					getApp().isUnbundledOrWhiteboard()
							? MaterialDesignResources.INSTANCE.language_black()
									.getSafeUri().asString()
							: GuiResources.INSTANCE
		        .menu_icon_options_language().getSafeUri().asString(),
					getApp().getLocalization()
							.getMenu("Language"),
					true), true, new MenuCommand(getApp()) {

			@Override
			public void doExecute() {
				getApp().showLanguageGUI();
			}
		});
		}
	}
	
	private void addRestoreDefaultSettingsMenu(){
		addItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(),
				getApp().getLocalization().getMenu("Settings.ResetDefault"),
				true), true, new MenuCommand(getApp()) {

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
		getApp().getSettings().resetSettings(getApp());
		// for geoelement defaults, this will do nothing, so it is
		// OK here
		GeoGebraPreferencesW.getPref().resetPreferences(getApp());
		// reset default line thickness etc
		getApp().getKernel().getConstruction().getConstructionDefaults()
				.resetDefaults();
		// reset defaults for geoelements; this will create brand
		// new objects
		// so the options defaults dialog should be reset later
		getApp().getKernel().getConstruction().getConstructionDefaults()
				.createDefaultGeoElements();
		// reset the stylebar defaultGeo
		if (getApp().getEuclidianView1().hasStyleBar()) {
			getApp().getEuclidianView1().getStyleBar().restoreDefaultGeo();
		}
		if (getApp().hasEuclidianView2EitherShowingOrNot(1)
				&& getApp().getEuclidianView2(1).hasStyleBar()) {
			getApp().getEuclidianView2(1).getStyleBar().restoreDefaultGeo();
		}
		// TODO needed to eg. update rounding, possibly too heavy
		getApp().getKernel().updateConstruction();
	}
	
	private void addSaveSettingsMenu(){
		if (!getApp().isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					getApp().isUnbundled()
							? MaterialDesignResources.INSTANCE.save_black()
									.getSafeUri().asString()
							: GuiResources.INSTANCE
					.menu_icon_file_save().getSafeUri().asString(),
					getApp().getLocalization()
				.getMenu("Settings.Save"), true),
					true, new MenuCommand(getApp()) {
			
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
					if (getApp().isUnbundled()) {
						((MenuBar) subMenu).addStyleName("matMenuBar");
					} else {
						((MenuBar) subMenu).addStyleName("GeoGebraMenuBar");
					}
				}
				ImageResource imgRes = AppResources.INSTANCE.empty();

				if ("Labeling".equals(key)) {
					imgRes = AppResources.INSTANCE.mode_showhidelabel_16();
				}
				if ("FontSize".equals(key)) {
					imgRes = GuiResources.INSTANCE
							.menu_icon_options_font_size();
				}

				((GMenuBar) parentMenu).addItem(
						getApp().getGuiManager().getMenuBarHtml(imgRes,
								getApp().getLocalization().getMenu(key), true),
						true, (MenuBar) subMenu,
						!getApp().isWhiteboardActive());
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
