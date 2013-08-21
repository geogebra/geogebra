package geogebra.javax.swing;

import geogebra.common.util.debug.Log;


/**
 * Wrapper for javax.swing.JPopupMenu
 * @author Judit Elias
 */
public class GPopupMenuD extends geogebra.common.javax.swing.GPopupMenu{

	private javax.swing.JPopupMenu impl = null;
	
	public GPopupMenuD() {
		impl = new javax.swing.JPopupMenu();
	}
	
	
	public GPopupMenuD(javax.swing.JPopupMenu popMenu) {
		impl = popMenu;
	}

	public static javax.swing.JPopupMenu getImpl(geogebra.common.javax.swing.GPopupMenu menu){
		if (menu==null) return null;
		if (menu instanceof GPopupMenuD) return ((GPopupMenuD) menu).impl;
		Log.warn("The function was called not with the right type.");
		return null;
	}
}
