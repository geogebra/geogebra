package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;

public class SolveAction implements MenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item) {
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
