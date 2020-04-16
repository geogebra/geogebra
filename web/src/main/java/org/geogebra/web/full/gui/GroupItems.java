package org.geogebra.web.full.gui;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.main.App;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler;

/**
 * Class to create group related menu items.
 */
public class GroupItems {
	private ArrayList<GeoElement> geos;
	private App app;

	/**
	 * Constructor for adding Group/Ungroup menu items
	 */
	GroupItems(App app) {
		this.app = app;
		this.geos = app.getSelectionManager().getSelectedGeos();
	}

	/**
	 * Add items that are available to currently selected geos.
	 * @param popup the menu to add items to.
	 * @return if any groups item was added
	 */
	boolean addAvailableItems(GPopupMenuW popup) {
		boolean groupAdded = addGroupItem(popup);
		boolean ungroupAdded = addUngroupItem(popup);

		return groupAdded || ungroupAdded;
	}

	private boolean addGroupItem(GPopupMenuW popup) {
		if (geos.size() >= 2 && !Group.isInSameGroup(geos)) {
			popup.addItem(createGroupItem());
			return true;
		}
		return false;
	}

	private boolean addUngroupItem(GPopupMenuW popup) {
		if (!app.getSelectionManager().getSelectedGroups().isEmpty()) {
			popup.addItem(createUngroupItem());
			return true;
		}
		return false;
	}

	private AriaMenuItem createUngroupItem() {
		return new AriaMenuItem(app.getLocalization().getMenu("ContextMenu.Ungroup"), false,
				new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						ungroupGroups();
						app.storeUndoInfo();
					}
				});
	}

	private void ungroupGroups() {
		for (GeoElement geo : geos) {
			Group groupOfGeo = geo.getParentGroup();
			if (groupOfGeo != null) {
				app.getKernel().getConstruction().removeGroupFromGroupList(groupOfGeo);
				geo.setParentGroup(null);
			}
		}
	}

	private AriaMenuItem createGroupItem() {
		return new AriaMenuItem(app.getLocalization().getMenu("ContextMenu.Group"), false,
				new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				createGroup();
				app.storeUndoInfo();
			}
		});
	}

	private void createGroup() {
		ungroupGroups();
		app.getKernel().getConstruction().createGroup(geos);
		unfixAll();
		app.getKernel().getConstruction().getLayerManager().groupObjects(geos);
		app.getActiveEuclidianView().invalidateDrawableList();
	}

	private void unfixAll() {
		for (GeoElement geo: geos) {
			geo.setFixed(false);
		}
	}
}