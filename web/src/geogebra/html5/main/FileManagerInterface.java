package geogebra.html5.main;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;

public interface FileManagerInterface {

	void getAllFiles();

	void search(String query);

	void delete(Material material);

	void openMaterial(Material material, AppWeb app);

	void saveFile(App app);

	void addFile(Material mat);
	
	void removeFile(Material mat);
}
