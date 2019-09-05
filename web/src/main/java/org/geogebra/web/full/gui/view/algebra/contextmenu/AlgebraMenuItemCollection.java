package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.html5.main.AppW;

/**
 * AV context menu actions for graphing / classic / geometry
 *
 * @author Zbynek
 */
public class AlgebraMenuItemCollection extends GeoElementMenuItemCollection {

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public AlgebraMenuItemCollection(AlgebraViewW algebraView) {
		AppW app = algebraView.getApp();
		if (app.getConfig().hasTableView()) {
			addActions(new TableOfValuesAction());
		}
		addActions(new SpecialPointsAction());
		if (!app.getConfig().hasAutomaticLabels()) {
			addLabelingActions();
		}
		// For some reason it is shown only once. That is, if Discover(P) already exists for a given P,
		// the action is not shown. This may be not the expected behavior.
		addActions(new DiscoverAction());
		addActions(new DuplicateAction(algebraView), new DeleteAction(), new SettingsAction());
	}

}
