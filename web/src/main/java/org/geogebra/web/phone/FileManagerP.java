package org.geogebra.web.phone;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.FileManagerT;

public class FileManagerP extends FileManagerT {

	public FileManagerP(final AppW app) {
		super(app);
	}

	@Override
	public void addMaterial(final Material mat) {
		// Phone.getGUI().getMaterialListPanel().addMaterial(mat, false, true);
	}

	@Override
	public void removeFile(final Material mat) {
		// Phone.getGUI().getMaterialListPanel().removeMaterial(mat);
	}
}
