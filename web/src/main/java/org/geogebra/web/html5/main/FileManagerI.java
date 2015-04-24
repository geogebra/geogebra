package org.geogebra.web.html5.main;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.models.Material.Provider;

public interface FileManagerI {

	void openMaterial(Material material);

	void delete(Material material, boolean permanent, Runnable onSuccess);

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

	void getFromTube(int id, boolean fromAnotherDevice);

	boolean isSyncing();

	void export(AppW app);

	void exportPng(String url, String string);
}
