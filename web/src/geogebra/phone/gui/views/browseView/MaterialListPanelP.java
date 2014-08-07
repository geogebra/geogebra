package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.browser.MaterialListPanel;
import geogebra.html5.main.AppWeb;

public class MaterialListPanelP extends MaterialListPanel {

	public MaterialListPanelP(final AppWeb app) {
	    super(app);
    }

	@Override
	public void addMaterial(final Material mat) {
		final MaterialListElement preview = new MaterialListElementP(mat, this.app);
		this.materials.add(preview);
		this.add(preview);
	}
}
