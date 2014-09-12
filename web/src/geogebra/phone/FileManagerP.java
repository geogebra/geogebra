package geogebra.phone;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.FileManagerT;

public class FileManagerP extends FileManagerT {
	
	public FileManagerP(final AppP app) {
		super(app);
	}
	
	@Override
	public void addMaterial(final Material mat) {
		Phone.getGUI().getMaterialListPanel().addMaterial(mat, false, true);
	}
	
	@Override
	public void removeFile(final Material mat) {
		Phone.getGUI().getMaterialListPanel().removeMaterial(mat);
	}
}
