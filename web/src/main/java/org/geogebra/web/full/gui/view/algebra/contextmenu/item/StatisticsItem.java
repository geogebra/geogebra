package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.StatisticsAction;

/**
 * Shows statistics for a list of numbers
 */
public class StatisticsItem extends MenuItem<GeoElement> {

	/**
	 * New statistics action
	 */
	public StatisticsItem() {
		super("Statistics",
				MaterialDesignResources.INSTANCE.statistics(),
				new StatisticsAction());
	}
}
