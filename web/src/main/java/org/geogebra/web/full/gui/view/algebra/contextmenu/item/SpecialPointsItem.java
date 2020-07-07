package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.SpecialPointsAction;

/**
 * Shows special points for a functionable geo
 */
public class SpecialPointsItem extends MenuItem<GeoElement> {

	/**
	 * New special points action
	 */
	public SpecialPointsItem() {
		super("Suggestion.SpecialPoints",
				MaterialDesignResources.INSTANCE.special_points(),
				new SpecialPointsAction());
	}
}