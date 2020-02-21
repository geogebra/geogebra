package org.geogebra.web.full.gui;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler;

/**
 * Class to create group related menu items.
 *
 * @author laszlo
 */
public class GroupItems {
	private final Localization loc;
	private Construction construction;
	private ArrayList<GeoElement> geos;
	private App app;

	/**
	 *
	 * @param app the Application
	 */
	GroupItems(App app) {
		this.loc = app.getLocalization();
		this.construction = app.getKernel().getConstruction();
		this.geos = app.getSelectionManager().getSelectedGeos();
		this.app = app;
	}

	/**
	 * Add items that are available to currently selected geos.
	 * @param popup the menu to add items to.
	 */
	void addAvailable(GPopupMenuW popup) {
		addGroupItemIfNeeded(popup);
	}

	private void addGroupItemIfNeeded(GPopupMenuW popup) {
		if (geos.size() < 2) {
			return;
		}
		popup.addItem(createGroupItem());
	}

	private AriaMenuItem createGroupItem() {
		return new AriaMenuItem(loc.getMenu("Group"), false, new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				createGroup();
			}
		});
	}

	private void createGroup() {
		construction.createGroup(geos);
		unfixAll();
		app.storeUndoInfo();
	}

	private void unfixAll() {
		for (GeoElement geo: geos) {
			geo.setFixed(false);
		}
	}
}
