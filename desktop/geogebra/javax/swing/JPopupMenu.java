package geogebra.javax.swing;

import geogebra.common.main.AbstractApplication;

/**
 * Wrapper for javax.swing.JPopupMenu
 * @author Judit Elias
 */
public class JPopupMenu extends geogebra.common.javax.swing.JPopupMenu{

	private javax.swing.JPopupMenu impl = null;
	
	public JPopupMenu() {
		impl = new javax.swing.JPopupMenu();
	}
	
	
	public JPopupMenu(javax.swing.JPopupMenu popMenu) {
		impl = popMenu;
	}

	public static javax.swing.JPopupMenu getImpl(geogebra.common.javax.swing.JPopupMenu menu){
		if (menu==null) return null;
		if (menu instanceof JPopupMenu) return ((JPopupMenu) menu).impl;
		AbstractApplication.warn("The function was called not with the right type.");
		return null;
	}
}
