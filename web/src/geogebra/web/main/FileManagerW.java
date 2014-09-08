package geogebra.web.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManager;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.SaveCallback;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.storage.client.Storage;

public class FileManagerW implements FileManager {
	private static final String FILE_PREFIX = "file#";
	ArrayList<Material> materialsToDelete = new ArrayList<Material>();
	Material mat;
	protected AppW app;
	Storage stockStore = Storage.getLocalStorageIfSupported();
	
	public FileManagerW(AppW app) {
		this.app = app;
	}

	@Override
    public void delete(Material mat) {
		this.stockStore.removeItem(FILE_PREFIX + mat.getTitle());
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
    public void saveFile(final SaveCallback cb) {
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				final String fileName = app.getKernel().getConstruction().getTitle();
				
				final Material mat = new Material(0, MaterialType.ggb);
				
				//TODO check if we need to set timestamp / modified
				mat.setTimestamp(System.currentTimeMillis() / 1000);
				
				if (app.getUniqueId() != null) {
					mat.setId(Integer.parseInt(app.getUniqueId()));
				}
				
				mat.setBase64(s);
				mat.setTitle(app.getKernel().getConstruction().getTitle());
				mat.setDescription(app.getKernel().getConstruction().getWorksheetText(0));
				mat.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());
				mat.setAuthor(app.getLoginOperation().getUserName());

				stockStore.setItem(FILE_PREFIX + fileName, mat.toJson().toString());

				cb.onSaved();
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
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).removeMaterial(mat);
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
				Material mat = JSONparserGGT.parseMaterial(this.stockStore.getItem(key));
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
	
	
	/**
	 * IN PROGRESS
	 */
	public void uploadUsersMaterials() {		
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}
		
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			System.out.println("in da for");
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				mat = JSONparserGGT.parseMaterial(this.stockStore.getItem(key));
				if (mat.getAuthor().equals(this.app.getLoginOperation().getUserName())) {
					System.out.println("in da if nach check mat");
					((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadLocalMaterial(app, mat, new MaterialCallback() {

						@Override
						public void onLoaded(List<Material> parseResponse) {
							System.out.println("uploadLocalMaterial on success");
					        materialsToDelete.add(mat);
						}

						@Override
						public void onError(Throwable exception) {
							System.out.println("uploadLocalMaterial onError");
							//TODO
						}
					});
				}
			}
			for (Material material : materialsToDelete) {
				delete(material);
			}
		}	
	}

}
