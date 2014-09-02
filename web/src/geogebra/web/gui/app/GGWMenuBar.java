package geogebra.web.gui.app;

import geogebra.html5.main.AppW;
import geogebra.web.gui.menubar.MainMenu;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class GGWMenuBar extends Composite implements RequiresResize{
	
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
		menubar = app.getLAF().getMenuBar(app);
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

	@Override
    public void onResize() {
		if(menubar!=null){
			menubar.onResize();
		}
    }
}
