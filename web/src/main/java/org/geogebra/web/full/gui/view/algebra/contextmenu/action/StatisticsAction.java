package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public class StatisticsAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item, AppWFull app) {
		Suggestion suggestion = SuggestionStatistics.get(item);
		if (suggestion != null) {
			suggestion.execute(item);
		}
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return SuggestionStatistics.get(item) != null;
	}
}
