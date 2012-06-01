package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

public class LanguagesADMenu extends MenuBar {
	
	public static final String language_1 = "Language";
	
	private AbstractApplication app;
	
	public LanguagesADMenu(AbstractApplication app) {
		
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
	    initActions();		
	}
	
	private void initActions() {
		//add the menu items of the different languages

		for(int i=0; i < Application.supportedLanguages.size(); i++) {
			String languageCode = Application.supportedLanguages.get(i);
			
			String languageName = Application.specialLanguageNames.get(Application.languageCodeVariationCrossReferencing(languageCode.replace(Application.AN_UNDERSCORE, "")));
			
			if(languageName != null) {
				
				char ch = languageName.toUpperCase().charAt(0);
				
				if(ch <= 'D') {
					addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),languageName),true,new Command() {

						public void execute() {
							Window.alert("Soon! Language support...");
						}
					});
				}
				
			}
		}

	}
	

}
