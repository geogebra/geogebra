package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;

public class SpecialPointsAction implements MenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item) {
		SuggestionRootExtremum.get(item).execute(item);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return SuggestionRootExtremum.get(item) != null;
	}
}
