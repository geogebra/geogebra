package geogebra.web.gui.app;

import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWMenuBar extends Composite {
	
	HorizontalPanel ggbmenubarwrapper;
	private GeoGebraMenuW menubar;
	private boolean menubar2Showing = false;
	private FocusPanel clickBox;

	// added shift-click to toggle new menubar for testing
	// TODO: remove temporary code
	
	public GGWMenuBar() {
		ggbmenubarwrapper = new HorizontalPanel();
		ggbmenubarwrapper.addStyleName("ggbmenubarwrapper");
		initWidget(ggbmenubarwrapper);
		
	}
	
	public void init(AppW app) {
		menubar = app.getLAF().getMenuBar(app);
		ggbmenubarwrapper.add((Widget) menubar);
	}
	
	public GeoGebraMenuW getMenubar() {
		return menubar;
	}

	public void removeMenus(){
		ggbmenubarwrapper.clear();
	}
	
}
