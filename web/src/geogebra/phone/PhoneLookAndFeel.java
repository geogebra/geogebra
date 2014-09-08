package geogebra.phone;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.views.browseView.MaterialListElementP;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.laf.GLookAndFeel;


public class PhoneLookAndFeel extends GLookAndFeel {
	
	public MaterialListElement getMaterialElement(Material mat, AppW app, boolean isLocal) {
		return new MaterialListElementP(mat, app, isLocal);
	}
}
