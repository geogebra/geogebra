package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public class SpecialPointsAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item, AppWFull app) {
		SuggestionRootExtremum.get(item).execute(item);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return SuggestionRootExtremum.get(item) != null;
	}
}
