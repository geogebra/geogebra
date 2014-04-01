package geogebra.html5.gui.laf;

import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Window;

public class ModernLookAndFeel extends GLookAndFeel{
	public GeoGebraMenuW getMenuBar(AppW app) {
	    MainMenu menubar = new MainMenu(app);
	    Window.addResizeHandler(menubar);
	    return menubar;
    }
	
	public boolean isModern() {
		return true;
	}
}
