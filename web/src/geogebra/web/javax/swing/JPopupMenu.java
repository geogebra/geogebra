package geogebra.web.javax.swing;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Wrapper for com.google.gwt.user.client.ui.MenuBar
 * @author Judit Elias
 */
public class JPopupMenu extends geogebra.common.javax.swing.JPopupMenu{

	com.google.gwt.user.client.ui.MenuBar impl;


	public JPopupMenu(){
		impl = new com.google.gwt.user.client.ui.MenuBar();
	}
	
	public void add(MenuItem mi) {
	    impl.addItem(mi);
	    
    }
}
