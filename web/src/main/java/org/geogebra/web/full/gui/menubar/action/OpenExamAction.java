package org.geogebra.web.full.gui.menubar.action;

import java.util.Collection;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.menubar.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class OpenExamAction implements MenuAction<Void> {
	@Override
	public boolean isAvailable(Void item) {
		return true;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		Collection<Material> materials = app.getExam().getTempStorage().collectTempMaterials();
		for (Material m: materials) {
			Log.debug(m.getTitle());
		}
	}
}
