package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.common.util.Language;
import geogebra.common.util.Unicode;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.MenuBar;

public class LanguageMenuW extends MenuBar {
	
	private App app;
	
	private MenuBar atoDMenuBar;
	private MenuBar etoIMenuBar;
	private MenuBar jtoQMenuBar;
	private MenuBar rtoZMenuBar;
	

	
	public LanguageMenuW(App app) {
		
		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
	    initActions();		
	}
	
	private void initActions() {
		//add the sub-sub language menu list

		//add here sub-sub menu for language from A-D
		atoDMenuBar = new MenuBar(true);
		atoDMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.isRightToLeftReadingOrder() ? "D - A" : "A - D", atoDMenuBar);
		
		//add here sub-sub menu for language from E-I
		etoIMenuBar = new MenuBar(true);
		etoIMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.isRightToLeftReadingOrder() ? "I - E" : "E - I", etoIMenuBar);
		
		
		//add here sub-sub menu for language from J-Q
		jtoQMenuBar = new MenuBar(true);
		jtoQMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.isRightToLeftReadingOrder() ? "Q - J" : "J - Q", jtoQMenuBar);

		//add here sub-sub menu for language from R-Z
		rtoZMenuBar = new MenuBar(true);
		rtoZMenuBar.addStyleName("GeoGebraMenuBar");
		addItem(app.isRightToLeftReadingOrder() ? "Z - R" : "R - Z", rtoZMenuBar);
		
		addItems();
	}
	
	
	
	private void addItems() {

		for (Language l : Language.values()) {
			
			StringBuilder sb = new StringBuilder();

			if (l.enableInGWT) {

				String text = l.name;

				if(text != null) {

					char ch = text.toUpperCase().charAt(0);
					if (ch == Unicode.LeftToRightMark || ch == Unicode.RightToLeftMark) {
						ch = text.charAt(1);
					} else {			
						// make sure brackets are correct in Arabic, ie not )US)
						sb.setLength(0);
						sb.append(Unicode.LeftToRightMark);
						sb.append(text);
						sb.append(Unicode.LeftToRightMark);
						text = sb.toString();
					}	
					
					App.debug("Language Menu includes language: " + l.localeGWT);										

					String menuBarHTML = GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),text);
					LanguageCommand lc = new LanguageCommand(l, (AppW) app);

					if(ch <= 'D') {
						atoDMenuBar.addItem(menuBarHTML ,true, lc);
					} else if(ch <= 'I') {
						etoIMenuBar.addItem(menuBarHTML ,true, lc);
					} else if(ch <= 'Q') {
						jtoQMenuBar.addItem(menuBarHTML ,true, lc);
					} else {
						rtoZMenuBar.addItem(menuBarHTML ,true, lc);
					}

				}
			}
		}

	}
}
