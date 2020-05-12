package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public class SolveAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item, AppWFull app) {
		getSuggestion(item).execute(item);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return getSuggestion(item) != null;
	}

	private static Suggestion getSuggestion(GeoElement geo) {
		return SuggestionSolveForSymbolic.isValid(geo)
				? SuggestionSolveForSymbolic.get(geo)
				: SuggestionSolve.get(geo);
	}
}
