package geogebra.touch;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.ListBox;

public class FileManagerM {
	private static final String FILE_PREFIX = "file#";
	private static final String THUMB_PREFIX = "img#";
	private static final String META_PREFIX = "meta#";
	protected Storage stockStore = Storage.getLocalStorageIfSupported();

	public FileManagerM() {
		if (this.stockStore != null) {
			ensureKeyPrefixes();
		}
	}

	private void ensureKeyPrefixes() {
		if (this.stockStore.getLength() > 0) {
			for (int i = 0; i < this.stockStore.getLength(); i++) {
				String oldKey = this.stockStore.key(i);
				if (!oldKey.contains("#")) {
					this.stockStore.removeItem(oldKey);
				}
			}
		}
	}

	public void toList(ListBox fileList) {
		fileList.clear();

		if (this.stockStore == null) {
			return;
		}

		if (this.stockStore.getLength() > 0) {
			for (int i = 0; i < this.stockStore.getLength(); i++) {
				String key = this.stockStore.key(i);
				if (key.startsWith(FILE_PREFIX)) {
					fileList.addItem(key.substring(FILE_PREFIX.length()));
				}
			}
		}

	}

	public void delete(String text) {
		this.stockStore.removeItem(FILE_PREFIX + text);
		this.stockStore.removeItem(THUMB_PREFIX + text);
		TouchEntryPoint.browseGUI.reloadLocalFiles();
	}

	public void saveFile(final App app) {
		final String consTitle = app.getKernel().getConstruction().getTitle();
		StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(String s) {
				FileManagerM.this.stockStore.setItem(FILE_PREFIX + consTitle,
						s);
				TouchEntryPoint.browseGUI.reloadLocalFiles();
			}
		};

		((geogebra.html5.main.GgbAPI) app.getGgbApi()).getBase64(base64saver);

		// extract metadata
		Material mat = new Material(0, MaterialType.ggb);
		mat.setTimestamp(System.currentTimeMillis() / 1000);
		mat.setTitle(consTitle);
		mat.setDescription(app.getKernel().getConstruction()
				.getWorksheetText(0));

		this.stockStore.setItem(META_PREFIX + consTitle, mat.toJson()
				.toString());
		this.stockStore.setItem(THUMB_PREFIX + consTitle,
				((EuclidianViewWeb) app.getEuclidianView1())
						.getCanvasBase64WithTypeString());		
		app.setSaved();
		((TouchApp)app).approveFileName();
	}

	public boolean getFile(String title, App app) {
		boolean success = true;
		try{
			String base64 = this.stockStore.getItem(FILE_PREFIX + title);
			if(base64==null){
				return false;
			}
			app.getGgbApi().setBase64(base64);
		}catch(Throwable t){
			success = false;
			t.printStackTrace();
		}
		return success;
	}

	public String getThumbnailDataUrl(String title) {
		return this.stockStore.getItem(THUMB_PREFIX + title);
	}

	public List<Material> search(String query) {
		return getFiles(MaterialFilter.getSearchFilter(query));
	}

	public List<Material> getAllFiles() {
		return getFiles(MaterialFilter.getUniversalFilter());
	}

	public List<Material> getFiles(MaterialFilter filter) {
		List<Material> ret = new ArrayList<Material>();
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return ret;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				String keyStem = key.substring(FILE_PREFIX.length());
				Material mat = JSONparserGGT.parseMaterial(this.stockStore
						.getItem(META_PREFIX + keyStem));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(keyStem);
				}
				if (filter.check(mat)) {
					mat.setURL(keyStem);
					ret.add(mat);
				}
			}
		}

		return ret;
	}

	public String getDefaultConstructionTitle(Localization loc) {
		int i = 1;
		String filename;
		do {
			filename = loc.getPlain("UntitledA", i + "");
			i++;
		} while (hasFile(filename));
		return filename;
	}

	public boolean hasFile(String filename) {
		return this.stockStore != null
				&& this.stockStore.getItem(FILE_PREFIX + filename) != null;
	}
}
