package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.browser.MaterialListPanel;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppWeb;

import com.google.gwt.user.client.Window;

public class MaterialListPanelP extends MaterialListPanel {

	public MaterialListPanelP(final AppWeb app) {
	    super(app);
	    this.setHeight(Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT - GLookAndFeel.PHONE_SEARCH_PANEL_HEIGHT + "px");
    }

	@Override
	public void addMaterial(final Material mat) {
		final MaterialListElement preview = new MaterialListElementP(mat, this.app);
		this.materials.add(preview);
		this.add(preview);
	}
	
	@Override
	public void onResize() {
		this.setHeight(Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT - GLookAndFeel.PHONE_SEARCH_PANEL_HEIGHT + "px");
		for (final MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}
}
