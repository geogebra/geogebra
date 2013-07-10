package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;

public class SaveMaterialPanel extends VerticalMaterialPanel {

	public SaveMaterialPanel(AppWeb app, FileManagerM fm) {
		super(app, fm);
	}

	@Override
	protected MaterialListElement buildListElement(Material m, AppWeb app2,
			FileManagerM fm2) {
		MaterialListElement mle = new SaveMaterialListElement(m, app2, fm2,
				this);
		mle.initButtons();
		return mle;
	}

}
