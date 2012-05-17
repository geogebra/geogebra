package geogebra.web.gui.toolbar;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ModeToggleButtonGroup extends MenuBar {
	
	public ModeToggleButtonGroup() {
	   super();
	   setFocusOnHoverEnabled(false);
    }
	
	
	public void add(MenuItem mi) {
	    addItem(mi);
    }

}
