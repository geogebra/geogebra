package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Solves one or more equations.
 */
public class SolveAction extends MenuAction<GeoElement> {

	/**
	 * New solve action
	 */
	public SolveAction() {
		super("Solve");
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		getSuggestion(geo).execute(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return getSuggestion(geo) != null;
	}

	private static Suggestion getSuggestion(GeoElement geo) {
		return SuggestionSolveForSymbolic.isValid(geo)
				? SuggestionSolveForSymbolic.get(geo)
				: SuggestionSolve.get(geo);
	}

}
