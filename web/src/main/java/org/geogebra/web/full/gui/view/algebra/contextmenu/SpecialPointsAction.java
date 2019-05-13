package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows special points for a functionable geo
 */
public class SpecialPointsAction extends MenuAction<GeoElement> {
	private LabelController labelController;

	/**
	 * New special points action
	 */
	public SpecialPointsAction() {
		super("Suggestion.SpecialPoints", MaterialDesignResources.INSTANCE.special_points());
		labelController = new LabelController();
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		labelController.ensureHasLabel(geo);
		SuggestionRootExtremum.get(geo).execute(geo);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return SuggestionRootExtremum.get(geo) != null;
	}
}