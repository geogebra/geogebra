package org.geogebra.web.web.gui.app;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.menubar.MainMenu;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWMenuBar extends Composite {
	
	private HorizontalPanel ggbmenubarwrapper;
	private MainMenu menubar;

	// added shift-click to toggle new menubar for testing
	// TODO: remove temporary code
	
	public GGWMenuBar() {
		ggbmenubarwrapper = new HorizontalPanel();
		ggbmenubarwrapper.addStyleName("ggbmenubarwrapper");
		initWidget(ggbmenubarwrapper);
		
	}
	
	public void init(AppW app) {
		menubar = (MainMenu) app.getLAF().getMenuBar(app);
		ggbmenubarwrapper.add((Widget) menubar);
	}
	
	public MainMenu getMenubar() {
		return menubar;
	}

	public void removeMenus(){
		ggbmenubarwrapper.clear();
	}

	public void focus() {
	    menubar.focus();
	    
    }

	public void updateHeight(int height) {
		menubar.updateHeight(height);
	}
}
