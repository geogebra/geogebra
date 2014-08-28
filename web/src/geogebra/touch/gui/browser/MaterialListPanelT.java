package geogebra.touch.gui.browser;

import geogebra.html5.gui.browser.MaterialListPanel;
import geogebra.html5.main.AppWeb;
import geogebra.touch.main.AppT;

public class MaterialListPanelT extends MaterialListPanel {

	public MaterialListPanelT(final AppWeb app) {
	    super(app);
    }

	@Override
    public void loadFeatured() {
		clearMaterials();
		//local files
		if (((AppT) app).getFileManager() != null) {
			((AppT) app).getFileManager().getAllFiles();
		}
		super.loadggt();
	}
	
	@Override
	public void displaySearchResults(final String query) {
		clearMaterials();
		
		if (query.equals("")) {
			loadFeatured();
			return;
		}
		//search local
		if (((AppT) this.app).getFileManager() != null) {
			((AppT) this.app).getFileManager().search(query);
		}
		searchGgt(query);
	}
}
