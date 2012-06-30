package geogebra.web.javax.swing;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Wrapper for com.google.gwt.user.client.ui.MenuBar
 * @author Judit Elias
 */
public class GPopupMenuW extends geogebra.common.javax.swing.GPopupMenu{

	com.google.gwt.user.client.ui.MenuBar impl;


	public GPopupMenuW(){
		impl = new com.google.gwt.user.client.ui.MenuBar();
	}
	
	public void add(MenuItem mi) {
	    impl.addItem(mi);
	    
    }
}
