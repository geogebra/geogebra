package geogebra.tablet.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.browser.MaterialListPanel;
import geogebra.web.gui.laf.GLookAndFeel;

import com.google.gwt.user.client.Window;

public class TabletMaterialListPanel extends MaterialListPanel {

	public TabletMaterialListPanel(final AppWeb app) {
	    super(app);
	    this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
    }
	
	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		for (final MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}
	
	@Override
	public void addMaterial(final Material mat, final boolean isLocal) {
		final MaterialListElement preview = new TabletMaterialElement(mat, this.app, isLocal);
		this.materials.add(preview);
		this.insert(preview, 0);
	}
}
