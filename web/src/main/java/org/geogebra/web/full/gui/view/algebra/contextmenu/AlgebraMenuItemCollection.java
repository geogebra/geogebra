package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DeleteAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.SettingsAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.SpecialPointsAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.TableOfValuesAction;
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
		addActions(new DuplicateAction(algebraView), new DeleteAction(), new SettingsAction());
	}

}
