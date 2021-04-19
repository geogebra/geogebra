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

	private HashMap<String, String> stockStore = new HashMap<>();
	private App app;

	public TestMaterialsManager(App app) {
		this.app = app;
	}

	@Override
	public void openMaterial(Material material) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(Material material, boolean permanent,
			Runnable onSuccess) {
		// TODO Auto-generated method stub
	}

	@Override
	public void getUsersMaterials() {
		// TODO Auto-generated method stub

	}

	@Override
	public void search(String query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String newTitle, Material mat) {
		rename(newTitle, mat, null);
	}

	@Override
	public void rename(String newTitle, Material mat, Runnable callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFileProvider(Provider google) {
		// TODO Auto-generated method stub

	}

	@Override
	public Provider getFileProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void autoSave(int counter) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAutosaveJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restoreAutoSavedFile(String json) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAutoSavedFile() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean save(App app) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveLoggedOut(App app) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldKeep(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void export(App app) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exportImage(String url, String string, String extension) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasBase64(Material material) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void nativeShare(String s, String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App app) {
		// TODO Auto-generated method stub

	}

	@Override
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

	@Override
	public void open(String url, String features) {
		// TODO: implement this?
	}

	@Override
	public void open(String url) {
		// TODO: implement this?
	}

}
