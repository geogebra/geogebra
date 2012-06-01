package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class LanguageMenu extends MenuBar {
	
	private AbstractApplication app;
	
	private MenuBar atoDMenuBar;
	private MenuBar etoIMenuBar;
	private MenuBar jtoQMenuBar;
	private MenuBar rtoZMenuBar;
	private LanguageCommand langCmd;
	

	
	public LanguageMenu(AbstractApplication app) {
		
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
	    initActions();		
	}
	
//	Command cmd = new Command() {
//
//		private String newLocale;
//
//		public void setNewLocale(String aLocale) {
//			newLocale = aLocale;
//		}
//		public void execute() {
//			Window.alert("Soon! Language support...");
//		}
//	};
	
	private void initActions() {
		//add the sub-sub language menu list

		//add here sub-sub menu for language from A-D
		atoDMenuBar = new MenuBar(true);
		atoDMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.getMenu("A - D"), atoDMenuBar);
		
		//add here sub-sub menu for language from E-I
		etoIMenuBar = new MenuBar(true);
		etoIMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.getMenu("E - I"), etoIMenuBar);
		
		
		//add here sub-sub menu for language from J-Q
		jtoQMenuBar = new MenuBar(true);
		jtoQMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.getMenu("J - Q"), jtoQMenuBar);

		//add here sub-sub menu for language from R-Z
		rtoZMenuBar = new MenuBar(true);
		rtoZMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.getMenu("R - Z"), rtoZMenuBar);
		
		langCmd = new LanguageCommand();
		
		addItems();
	}
	
	
	
	private void addItems() {
		
		for(int i=0; i < Application.supportedLanguages.size(); i++) {			
			String languageCode = Application.supportedLanguages.get(i);
			
			String languageName = Application.specialLanguageNames.get(Application.languageCodeVariationCrossReferencing(languageCode.replace(Application.AN_UNDERSCORE, "")));
			
			if(languageName != null) {
				
				char ch = languageName.toUpperCase().charAt(0);
				
				langCmd.setLocaleCode(languageCode);
				
				if(ch <= 'D') {
					
					atoDMenuBar.addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,langCmd);
				} else if(ch <= 'I') {
					etoIMenuBar.addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,langCmd);
				} else if(ch <= 'Q') {
					jtoQMenuBar.addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,langCmd);
				} else {
					rtoZMenuBar.addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,langCmd);
				}
				
			}
		}

	}
	
}
