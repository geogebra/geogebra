package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class SolveAction extends MenuAction<GeoElement> {

	public SolveAction() {
		super("Solve", null);
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		SuggestionSolveForSymbolic.get(geo).execute(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return SuggestionSolveForSymbolic.get(geo) != null;
	}

}
