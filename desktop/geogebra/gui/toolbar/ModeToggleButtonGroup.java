/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui.toolbar;

import geogebra.common.javax.swing.GPopupMenu;

import javax.swing.ButtonGroup;

/**
 * Group of buttons for mode groups
 */
public class ModeToggleButtonGroup extends ButtonGroup {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GPopupMenu activePopMenu;
	
	
	/**
	 * Activates given mode group
	 * @param popMenu popup menu that should become active
	 */
	public void setActivePopupMenu(GPopupMenu popMenu) {
		activePopMenu = popMenu;			
	}	
	
	/**
	 * Returns active mode group
	 * @return active popup menu
	 */
	public GPopupMenu getActivePopupMenu() {
		return activePopMenu;
	}
	
	
}
