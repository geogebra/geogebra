package org.geogebra.desktop.javax.swing;

import javax.swing.JPopupMenu;

/**
 * Wrapper for javax.swing.JPopupMenu
 * 
 * @author Judit Elias
 */
public class GPopupMenuD {

	private JPopupMenu impl = null;

	public GPopupMenuD() {
		impl = new JPopupMenu();
	}

	public GPopupMenuD(JPopupMenu popMenu) {
		impl = popMenu;
	}

	public static JPopupMenu getImpl(GPopupMenuD menu) {

		if (menu == null) {
			return null;
		}

		return menu.impl;
	}
}
