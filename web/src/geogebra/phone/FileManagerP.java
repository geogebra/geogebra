package geogebra.phone;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.FileManagerT;

public class FileManagerP extends FileManagerT {
	
	public FileManagerP(AppP app) {
		super(app);
	}
	
	@Override
	public void addMaterial(Material mat) {
		Phone.getGUI().getMaterialListPanel().addMaterial(mat, true);
	}
	
	@Override
	public void removeFile(Material mat) {
		Phone.getGUI().getMaterialListPanel().removeMaterial(mat);
	}
}
