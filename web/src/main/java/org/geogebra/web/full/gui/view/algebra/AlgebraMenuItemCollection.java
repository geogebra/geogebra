package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DeleteAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DuplicateAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.SettingsAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.SpecialPointsAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.TableOfValuesAction;
import org.geogebra.web.html5.main.AppW;

/**
 * AV context menu actions for graphing / classic / geometry
 *
 * @author Zbynek
 */
public class AlgebraMenuItemCollection extends MenuActionCollection<GeoElement> {

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public AlgebraMenuItemCollection(AlgebraViewW algebraView) {
		AppW app = algebraView.getApp();
		if (app.getConfig().hasTableView(app)) {
			addActions(new TableOfValuesAction());
		}
		addActions(new SpecialPointsAction());
		addActions(new DuplicateAction(algebraView), new DeleteAction(), new SettingsAction());
	}
}
