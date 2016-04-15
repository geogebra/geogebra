package org.geogebra.desktop.javax.swing;

import javax.swing.JPopupMenu;

import org.geogebra.common.javax.swing.GPopupMenu;
import org.geogebra.common.util.debug.Log;

/**
 * Wrapper for javax.swing.JPopupMenu
 * 
 * @author Judit Elias
 */
public class GPopupMenuD extends GPopupMenu {

	private JPopupMenu impl = null;

	public GPopupMenuD() {
		impl = new JPopupMenu();
	}

	public GPopupMenuD(JPopupMenu popMenu) {
		impl = popMenu;
	}

	public static JPopupMenu getImpl(GPopupMenu menu) {

		if (menu == null) {
			return null;
		}

		if (menu instanceof GPopupMenuD) {
			return ((GPopupMenuD) menu).impl;
		}

		Log.warn("The function was called not with the right type.");
		return null;
	}
}
