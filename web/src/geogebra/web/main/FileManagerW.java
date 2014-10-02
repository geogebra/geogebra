package geogebra.web.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.main.AppW;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.util.SaveCallback;

import com.google.gwt.storage.client.Storage;

public class FileManagerW extends FileManager {
	private static final String FILE_PREFIX = "file#";
	Storage stockStore = Storage.getLocalStorageIfSupported();
	
	public FileManagerW(final AppW app) {
		super(app);
	}

	@Override
    public void delete(final Material mat) {
		this.stockStore.removeItem(mat.getTitle());
		removeFile(mat);
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).setMaterialsDefaultStyle();
    }
	
	@Override
    public void openMaterial(final Material material) {
		try {
			final String base64 = material.getBase64();
			if (base64 == null) {
				return;
			}
			app.getGgbApi().setBase64(base64);
			app.setLocalID(getID(material.getTitle()));
		} catch (final Throwable t) {
			app.showError("LoadFileFailed");
			t.printStackTrace();
		}
    }

	@Override
    public void saveFile(final SaveCallback cb) {
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				final Material mat = createMaterial(s);
				int id;

				if (app.getLocalID() == -1) {
					id = createID();
					app.setLocalID(id);
				} else {
					id = app.getLocalID();
				}
				String key = createKeyString(id, app.getKernel().getConstruction().getTitle());
				mat.setTitle(key);
				stockStore.setItem(key, mat.toJson().toString());
				cb.onSaved(mat, true);
			}
		};

		app.getGgbApi().getBase64(true, base64saver);
    }

	/**
	 * creates a new ID
	 * @return int ID
	 */
	int createID() {
		int nextFreeID = 1;
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				int fileID = getID(key);
				if (fileID >= nextFreeID) {
					nextFreeID = getID(key) + 1;
				}
			}
		}
		return nextFreeID;
    }
	
	/**
	 * returns the ID from the given key.
	 * (key is of form "file#ID#fileName")
	 * @param key String
	 * @return int ID
	 */
	private int getID(String key) {
		return Integer.parseInt(key.substring(FILE_PREFIX.length(), key.indexOf("#", FILE_PREFIX.length())));
	}
	
	@Override
    public void removeFile(final Material material) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).removeMaterial(material);
    }

	@Override
    public void addMaterial(final Material material) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).addMaterial(material);
    }
	
	@Override
    protected void getFiles(final MaterialFilter filter) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				Material mat = JSONparserGGT.parseMaterial(this.stockStore.getItem(key));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(getTitle(key));
				}
				if (filter.check(mat)) {
					addMaterial(mat);
				}
			}
		}
	}
	

	@Override
	public void uploadUsersMaterials() {		
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}
		
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				final Material mat = JSONparserGGT.parseMaterial(this.stockStore.getItem(key));
				if (mat.getAuthor().equals(this.app.getLoginOperation().getUserName())) {
					if (mat.getId() == 0) {
						upload(mat);
					} else {
						sync(mat);
					}
				}
			}
		}	
	}

	@Override
    public void rename(String newTitle, Material mat) {
		this.stockStore.removeItem(mat.getTitle());
		
		String newKey = createKeyString(createID(), newTitle);
		mat.setTitle(newKey);
		this.stockStore.setItem(newKey, mat.toJson().toString());
	}

	/**
	 * @param matID local ID of material
	 * @param title of material
	 * @return creates a key (String) for the stockStore
	 */
	String createKeyString(int matID, String title) {
		return FILE_PREFIX + matID + "#" + title;
	}
}
