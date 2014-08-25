package geogebra.phone;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.FileManager;

public class FileManagerP extends FileManager {
	
	public FileManagerP() {
		super();
	}
	
	@Override
	public void addFile(Material mat) {
		Phone.getGUI().getMaterialListPanel().addMaterial(mat);
	}
	
	@Override
	public void removeFile(Material mat) {
		Phone.getGUI().getMaterialListPanel().removeMaterial(mat);
	}
}
