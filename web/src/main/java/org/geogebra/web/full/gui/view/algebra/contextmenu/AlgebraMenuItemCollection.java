package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DeleteItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DuplicateInputItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DuplicateOutputItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.SettingsItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.SpecialPointsItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.StatisticsItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.TableOfValuesItem;
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
		addItems(new StatisticsItem());

		if (app.getConfig().hasTableView()) {
			addItems(new TableOfValuesItem());
		}
		addItems(new SpecialPointsItem());
		if (!app.getConfig().hasAutomaticLabels()) {
			addLabelingActions();
		}
		addItems(new DuplicateInputItem(algebraView), new DuplicateOutputItem(algebraView),
				new DeleteItem(), new SettingsItem());
	}
}
