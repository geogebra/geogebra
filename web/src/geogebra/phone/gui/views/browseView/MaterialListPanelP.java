package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.browser.MaterialListPanel;
import geogebra.html5.main.AppWeb;
import geogebra.phone.gui.elements.MaterialListElementP;

public class MaterialListPanelP extends MaterialListPanel {

	public MaterialListPanelP(AppWeb app) {
	    super(app);
	    // TODO Auto-generated constructor stub
    }

	@Override
	public void addMaterial(Material mat) {
		final MaterialListElement preview = new MaterialListElementP(mat, this.app);
		this.materials.add(preview);
		this.add(preview);
	}
}
