package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.models.SyncEvent;

public class TestMaterialsManager extends MaterialsManager {

	private HashMap<String, String> stockStore = new HashMap<String, String>();
	private App app;

	public TestMaterialsManager(App app) {
		this.app = app;
	}

	public void openMaterial(Material material) {
		// TODO Auto-generated method stub

	}

	public void delete(Material material, boolean permanent,
			Runnable onSuccess) {
		// TODO Auto-generated method stub

	}

	public void uploadUsersMaterials(ArrayList<SyncEvent> events) {
		System.out.println("" + stockStore.size());
		if (this.stockStore == null || this.stockStore.size() <= 0) {
			return;
		}
		ArrayList<String> keys = new ArrayList<String>();
		for (String key : stockStore.keySet()) {
			keys.add(key);
		}

		setNotSyncedFileCount(keys.size(), events);
		for (int i = 0; i < keys.size(); i++) {

			final String key = keys.get(i);
			System.out.println("" + key);
			if (key.startsWith(FILE_PREFIX)) {
				final Material mat = JSONParserGGT
						.parseMaterial(this.stockStore.get(key));
				if (getApp().getLoginOperation().owns(mat)) {
					sync(mat, events);

				} else {
					ignoreNotSyncedFile(events);
				}
			} else {
				ignoreNotSyncedFile(events);
			}
		}
	}

	public void getUsersMaterials() {
		// TODO Auto-generated method stub

	}

	public void search(String query) {
		// TODO Auto-generated method stub

	}

	public void rename(String newTitle, Material mat, Runnable callback) {
		// TODO Auto-generated method stub

	}

	public void setFileProvider(Provider google) {
		// TODO Auto-generated method stub

	}

	public Provider getFileProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public void autoSave(int counter) {
		// TODO Auto-generated method stub

	}

	public String getAutosaveJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public void restoreAutoSavedFile(String json) {
		// TODO Auto-generated method stub

	}

	public void deleteAutoSavedFile() {
		// TODO Auto-generated method stub

	}

	public boolean save(App app) {
		// TODO Auto-generated method stub
		return false;
	}

	public void saveLoggedOut(App app) {
		// TODO Auto-generated method stub

	}

	public boolean shouldKeep(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	public void export(App app) {
		// TODO Auto-generated method stub

	}

	public void exportImage(String url, String string) {
		// TODO Auto-generated method stub

	}

	public boolean hasBase64(Material material) {
		// TODO Auto-generated method stub
		return false;
	}

	public void nativeShare(String s, String string) {
		// TODO Auto-generated method stub

	}

	public void showExportAsPictureDialog(String url, String filename,
			App app) {
		// TODO Auto-generated method stub

	}

	public void refreshAutosaveTimestamp() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateFile(String title, long modified, Material material) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void showTooltip(Material mat) {
		// TODO Auto-generated method stub

	}

	@Override
	protected App getApp() {
		return app;
	}

	@Override
	protected void refreshMaterial(Material newMat) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setTubeID(String localKey, Material newMat) {
		// TODO Auto-generated method stub

	}

	protected void insertFile(Material material) {

		String key = MaterialsManager.createKeyString(this.createID(),
				material.getTitle());
		
		this.stockStore.put(key, material.toJson().toString());
	}

	int createID() {
		int nextFreeID = 1;
		for (String key : stockStore.keySet()) {
			if (key.startsWith(MaterialsManager.FILE_PREFIX)) {
				int fileID = MaterialsManager.getIDFromKey(key);
				if (fileID >= nextFreeID) {
					nextFreeID = MaterialsManager.getIDFromKey(key) + 1;
				}
			}
		}
		return nextFreeID;
	}

}
