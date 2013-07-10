package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;

public class SaveMaterialListElement extends MaterialListElement {

	public SaveMaterialListElement(Material m, AppWeb app, FileManagerM fm,
			VerticalMaterialPanel vmp) {
		super(m, app, fm, vmp);
	}

	@Override
	protected void initButtons() {
		initDeleteButton();
	}

}
