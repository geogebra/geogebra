package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.main.AppWeb;
import geogebra.phone.Phone;
import geogebra.phone.gui.views.ViewsContainer.View;

public class MaterialListElementP extends MaterialListElement {

	public MaterialListElementP(final Material m, final AppWeb app) {
		super(m, app);
	}

	@Override
	protected void closeBrowseView() {
		Phone.getGUI().scrollTo(View.Graphics);
	}

	@Override
	protected void markSelected() {
		this.isSelected = true;
		Phone.getGUI().getMaterialListPanel().disableMaterials();
		Phone.getGUI().getMaterialListPanel().rememberSelected(this);

		this.addStyleName("selected");
	}
}
