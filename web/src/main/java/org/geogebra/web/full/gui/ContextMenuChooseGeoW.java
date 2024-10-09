package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.MouseOverEvent;
import org.gwtproject.user.client.ui.InlineHTML;

/**
 * Menu for choosing geos
 */
public class ContextMenuChooseGeoW extends ContextMenuGeoElementW {

	/**
	 * 
	 */
	protected EuclidianView view;

	/**
	 * polygons/polyhedra parents of segments, polygons, ...
	 */
	private TreeSet<GeoElement> metas;

	private ArrayList<GeoElement> selectedGeos;
	private GPoint location;
	private AriaMenuBar selectAnotherMenu;
	private ArrayList<GeoElement> tmpAnotherMenuItemList;

	/**
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            view
	 * @param selectedGeos
	 *            selected geos
	 * @param geos
	 *            geos
	 * @param invokerLocation
	 *            place to show
	 */
	public ContextMenuChooseGeoW(AppW app, EuclidianView view,
								 ArrayList<GeoElement> selectedGeos, ArrayList<GeoElement> geos,
								 GPoint invokerLocation, ContextMenuFactory factory) {
		super(app, selectedGeos, factory);
		// return if just one geo, or if first geos more than one
		if (geos.size() < 2 || selectedGeos.size() > 1) {
			justOneGeo = false;
			addOtherItems();
			return;
		}

		justOneGeo = true;

		// section to choose a geo
		// addSeparator();
		// addSelectAnotherMenu(view.getMode());

		this.location = invokerLocation;
		this.selectedGeos = selectedGeos;
		this.setGeos(geos);
		this.view = view;

		GeoElement geoSelected = selectedGeos.get(0);

		// add geos
		metas = new TreeSet<>();

		// first collect geos in tmp list
		addGeosToTmpAnotherMenuItemList(metas, geos, geoSelected);

		// if tmp list not empty
		if (!tmpAnotherMenuItemList.isEmpty()) {
			// add menu item
			addSelectAnotherMenu(view.getMode());
			for (GeoElement geo : tmpAnotherMenuItemList) {
				// add geos to submenu
				if (!metas.contains(geo)) {
					addGeo(geo);
				}
			}
		}
		addOtherItems();
	}

	private void addGeosToTmpAnotherMenuItemList(TreeSet<GeoElement> metaElements,
			ArrayList<GeoElement> geos, GeoElement geoSelected) {
		tmpAnotherMenuItemList = new ArrayList<>();

		for (GeoElement geo1 : geos) {
			// do not add xoy plane or selected geo
			if (geo1 != geoSelected && geo1 != app.getKernel().getXOYPlane()) {
				tmpAnotherMenuItemList.add(geo1);
				if (geo1.getMetasLength() > 0) {
					addMetas(geo1, geoSelected, metaElements);
				}
			}
		}

	}

	private void addMetas(GeoElement geo1, GeoElement geoSelected,
			TreeSet<GeoElement> metaElements) {
		for (GeoElement meta : ((FromMeta) geo1).getMetas()) {
			if (!metaElements.contains(meta) && (meta != geoSelected
					|| !app.has(Feature.G3D_SELECT_META))) {
				tmpAnotherMenuItemList.add(meta);
			}
		}
	}

	private void addGeo(GeoElement geo) {
		// prevent selection of xOy plane
		if (geo == app.getKernel().getXOYPlane()) {
			return;
		}

		AriaMenuItem mi = new AriaMenuItem(new InlineHTML(getDescription(geo, false)),
				() -> geoActionCmd(geo));
		mi.addDomHandler(evt -> view.getEuclidianController().doSingleHighlighting(geo),
				MouseOverEvent.getType());

		selectAnotherMenu.addItem(mi);
		if (app.isUnbundledOrWhiteboard()) {
			mi.addStyleName("no-image");
		}
		metas.add(geo);
	}

	private void addSelectAnotherMenu(int mode) {
		selectAnotherMenu = new AriaMenuBar();
		AriaMenuItem selectAnotherMenuItem;
		Localization localization = app.getLocalization();
		if (EuclidianConstants.isMoveOrSelectionMode(mode)) {
			selectAnotherMenuItem = new AriaMenuItem(
					localization.getMenu("SelectAnother"), null,
					selectAnotherMenu);
		} else {
			selectAnotherMenuItem = new AriaMenuItem(
					localization.getMenu("PerformToolOn"), null,
					selectAnotherMenu);
		}
		wrappedPopup.addItem(selectAnotherMenuItem);
	}

	/**
	 * @param geo
	 *            single geo
	 */
	public void geoActionCmd(GeoElement geo) {
		geoActionCmd(geo, selectedGeos, getGeos(), view, location);
		wrappedPopup.hideMenu();
	}
}
