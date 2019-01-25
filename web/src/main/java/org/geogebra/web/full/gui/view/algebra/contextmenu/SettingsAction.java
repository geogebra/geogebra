package org.geogebra.web.full.gui.view.algebra.contextmenu;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class SettingsAction extends MenuAction {
	public SettingsAction() {
		super("Settings", MaterialDesignResources.INSTANCE.gear());
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		ArrayList<GeoElement> list = new ArrayList<>();
		list.add(geo);
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, list);
	}
}