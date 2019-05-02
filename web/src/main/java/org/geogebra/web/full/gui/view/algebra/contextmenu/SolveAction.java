package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class SolveAction extends MenuAction<GeoElement> {

	public SolveAction() {
		super("Solve", null);
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		getSuggestion(geo).execute(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return getSuggestion(geo) != null;
	}

	private Suggestion getSuggestion(GeoElement geo) {
		Log.error(geo + "SOLVE");
		return SuggestionSolveForSymbolic.isValid(geo)
				? SuggestionSolveForSymbolic.get(geo)
				: SuggestionSolve.get(geo);
	}

}
