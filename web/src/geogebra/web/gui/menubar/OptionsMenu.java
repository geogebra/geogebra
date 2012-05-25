package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

public class OptionsMenu extends MenuBar {
	
	private AbstractApplication app;
	
	private LanguageMenu languageMenu;
	
	public OptionsMenu(AbstractApplication app) {

		super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    createLanguageMenu();		
	}
	
	private void createLanguageMenu() {
		languageMenu = new LanguageMenu(app);
		addItem(app.getMenu("Language"), languageMenu);
	}

}
