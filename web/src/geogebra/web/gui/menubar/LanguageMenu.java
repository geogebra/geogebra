package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.user.client.ui.MenuBar;

public class LanguageMenu extends MenuBar {
	
	private AbstractApplication app;
	
	private LanguagesADMenu atoDMenuBar;
	private LanguagesEHMenu etoHMenuBar;
	
	public LanguageMenu(AbstractApplication app) {
		
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
	    initActions();		
	}
	
	private void initActions() {
		//add the sub-sub language menu list

		//add here sub-sub menu for language from A-D
		createAtoDLanguageMenu();
		//add here sub-sub menu for language from E-H
		createEtoHLanguageMenu();
		//add here sub-sub menu for language from I-Q
		//add here sub-sub menu for language from R-Z
	}
	
	private void createAtoDLanguageMenu() {
		atoDMenuBar = new LanguagesADMenu(app);
		addItem(app.getMenu("A-D"), atoDMenuBar);
	}
	
	private void createEtoHLanguageMenu() {
		etoHMenuBar = new LanguagesEHMenu(app);
		addItem(app.getMenu("E-H"), etoHMenuBar);
	}
	
}
