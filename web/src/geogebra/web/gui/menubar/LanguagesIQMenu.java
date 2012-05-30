package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

public class LanguagesIQMenu extends MenuBar {
	
public static final String language_1 = "Language";
	
	private AbstractApplication app;
	
	public LanguagesIQMenu(AbstractApplication app) {
		
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
	    initActions();		
	}
	
	private void initActions() {
		//add the menu items of the different languages
		addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),language_1),true,new Command() {
			
			public void execute() {
				Window.alert("Soon! Language support...");
			}
		});
		
	}
}
