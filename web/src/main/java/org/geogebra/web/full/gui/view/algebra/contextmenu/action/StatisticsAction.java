package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;

public class StatisticsAction implements MenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item) {
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
