package geogebra.tablet;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.tablet.gui.browser.TabletBrowseGUI;
import geogebra.tablet.main.TabletApp;
import geogebra.touch.FileManagerT;

public class TabletFileManager extends FileManagerT {
	
	public TabletFileManager(TabletApp tabletApp) {
		super(tabletApp);
	}
	
	@Override()
	public void addMaterial(final Material mat) {
		((TabletBrowseGUI) app.getGuiManager().getBrowseGUI()).addMaterial(mat);
	}
	
	@Override()
	public void removeFile(final Material mat) {
		((TabletBrowseGUI) app.getGuiManager().getBrowseGUI()).removeMaterial(mat);
	}
}
