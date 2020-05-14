package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public class SettingsAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item, AppWFull app) {
		ArrayList<GeoElement> list = new ArrayList<>();
		list.add(item);
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, list);
	}
}
