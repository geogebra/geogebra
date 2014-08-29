package geogebra.tablet.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppWeb;
import geogebra.touch.gui.browser.MaterialListPanelT;

import com.google.gwt.user.client.Window;

public class TabletMaterialListPanel extends MaterialListPanelT {

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
	public void addMaterial(final Material mat) {
		final MaterialListElement preview = new TabletMaterialElement(mat, this.app);
		this.materials.add(preview);
		this.insert(preview, 0);
	}
}
