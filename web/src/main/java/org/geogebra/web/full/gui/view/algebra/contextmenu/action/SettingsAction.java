package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;

public class SettingsAction extends DefaultMenuAction<GeoElement> {

	private DialogManager dialogManager;

	public SettingsAction(DialogManager dialogManager) {
		this.dialogManager = dialogManager;
	}

	@Override
	public void execute(GeoElement item) {
		ArrayList<GeoElement> list = new ArrayList<>();
		list.add(item);
		dialogManager.showPropertiesDialog(OptionType.OBJECTS, list);
	}
}
