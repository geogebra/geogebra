package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.ggtapi.models.SyncEvent;

import java.util.ArrayList;

public interface FileManagerI {

	void openMaterial(Material material);

	void delete(Material material, boolean permanent);

	void uploadUsersMaterials(ArrayList<SyncEvent> events);

	void getUsersMaterials();

	void search(String query);

	void rename(String newTitle, Material mat, Runnable callback);

	void setFileProvider(Provider google);

	Provider getFileProvider();

	void autoSave();

	boolean isAutoSavedFileAvailable();

	public void restoreAutoSavedFile();

	public void deleteAutoSavedFile();

	boolean save(AppW app);

	void saveLoggedOut(AppW app);

	boolean shouldKeep(int i);

	void getFromTube(int id);
}
