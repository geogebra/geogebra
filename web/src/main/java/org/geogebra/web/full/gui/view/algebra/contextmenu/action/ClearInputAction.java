package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.AppWFull;

/**
 * Clears input row
 * @author laszlo
 *
 */
public class ClearInputAction extends DeleteAction {
	private RadioTreeItem inputItem;

	/**
	 * @param inputItem
	 *            input item
	 */
	public ClearInputAction(RadioTreeItem inputItem) {
		this.inputItem = inputItem;
	}
	
	@Override
	public void execute(GeoElement geo, AppWFull app) {
		inputItem.setText("");
	}
}
