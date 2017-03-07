/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.desktop.gui.toolbar;

import javax.swing.ButtonGroup;

import org.geogebra.desktop.javax.swing.GPopupMenuD;

/**
 * Group of buttons for mode groups
 */
public class ModeToggleButtonGroup extends ButtonGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GPopupMenuD activePopMenu;

	/**
	 * Activates given mode group
	 * 
	 * @param popMenu
	 *            popup menu that should become active
	 */
	public void setActivePopupMenu(GPopupMenuD popMenu) {
		activePopMenu = popMenu;
	}

	/**
	 * Returns active mode group
	 * 
	 * @return active popup menu
	 */
	public GPopupMenuD getActivePopupMenu() {
		return activePopMenu;
	}

}
