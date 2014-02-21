package geogebra.web.gui.menubar;

import com.google.gwt.user.client.ui.MenuItem;


public interface GeoGebraMenuW {
	
	public void updateSelection();
	
	public void updateMenubar();

	public MenuItem getSignIn();
}
