package org.geogebra.web.full.gui;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.user.client.Command;

public class GroupItems {
	private final Localization loc;
	private Construction construction;
	private ArrayList<GeoElement> geos;
	GroupItems(App app) {
		this.loc = app.getLocalization();
		this.construction = app.getKernel().getConstruction();
		this.geos = app.getSelectionManager().getSelectedGeos();
	}

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
		return new AriaMenuItem(loc.getMenu("Group"), false, newGroupCommand());
	}

	private Command newGroupCommand() {
		return new Command() {
			@Override
			public void execute() {
				construction.createGroup(geos);
			}
		};
	}
}
