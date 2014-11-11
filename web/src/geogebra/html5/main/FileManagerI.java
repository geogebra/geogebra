package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.Provider;

public interface FileManagerI {

	void openMaterial(Material material);

	void delete(Material material);

	void uploadUsersMaterials();

	void getUsersMaterials();

	void search(String query);

	void rename(String newTitle, Material mat);

	void setFileProvider(Provider google);

	Provider getFileProvider();
	
	void autoSave();

	boolean isAutoSavedFileAvailable();
	
	public void restoreAutoSavedFile();
	
	public void deleteAutoSavedFile();

	boolean save(AppW app);
}
