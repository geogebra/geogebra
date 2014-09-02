package geogebra.web.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManager;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import com.google.gwt.core.client.Callback;
import com.google.gwt.storage.client.Storage;

public class FileManagerW implements FileManager {
	private static final String FILE_PREFIX = "file#";
	private static final String META_PREFIX = "meta#";
	protected AppW app;
	Storage stockStore = Storage.getLocalStorageIfSupported();
	
	public FileManagerW(AppW app) {
		this.app = app;
	}

	@Override
    public void delete(Material mat) {
		this.stockStore.removeItem(FILE_PREFIX + mat.getTitle());
		this.stockStore.removeItem(META_PREFIX + mat.getTitle());
		removeFile(mat);
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).setMaterialsDefaultStyle();
    }

	@Override
    public void getAllFiles() {
		getFiles(MaterialFilter.getUniversalFilter());
    }

	@Override
    public void openMaterial(Material material) {
		try {
			final String base64 = this.stockStore.getItem(FILE_PREFIX + material.getURL());
			if (base64 == null) {
				return;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			app.showError("LoadFileFailed");
			t.printStackTrace();
		}
    }

	@Override
    public void saveFile(final Callback<String, Throwable> cb) {
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				final String fileName = app.getKernel().getConstruction().getTitle();
				
				final Material mat = new Material(0, MaterialType.ggb);
				mat.setTimestamp(System.currentTimeMillis() / 1000);
				if (app.getUniqueId() != null) {
					mat.setId(Integer.parseInt(app.getUniqueId()));
				}
				mat.setTitle(app.getKernel().getConstruction().getTitle());
				mat.setDescription(app.getKernel().getConstruction().getWorksheetText(0));
				mat.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());

				stockStore.setItem(FILE_PREFIX + fileName, s);
				stockStore.setItem(META_PREFIX + fileName, mat.toJson().toString());

				cb.onSuccess("Success");
			}
		};

		app.getGgbApi().getBase64(true, base64saver);
    }

	@Override
    public void search(String query) {
		getFiles(MaterialFilter.getSearchFilter(query));
    }

	@Override
    public void removeFile(Material mat) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).removeFromLocalList(mat);
    }

	@Override
    public void addFile(Material mat) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).addMaterial(mat);
    }
	
	private void getFiles(final MaterialFilter filter) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				final String keyStem = key.substring(FILE_PREFIX.length());
				Material mat = JSONparserGGT.parseMaterial(this.stockStore
						.getItem(META_PREFIX + keyStem));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(keyStem);
				}
				if (filter.check(mat)) {
					mat.setURL(keyStem);
					addFile(mat);
				}
			}
		}
	}
//	
//	private void getFile(final String title) {
//		try {
//			final String base64 = this.stockStore.getItem(FILE_PREFIX + title);
//			if (base64 == null) {
//				return;
//			}
//			app.getGgbApi().setBase64(base64);
//		} catch (final Throwable t) {
//			app.showError("LoadFileFailed");
//			t.printStackTrace();
//		}
//	}
}
