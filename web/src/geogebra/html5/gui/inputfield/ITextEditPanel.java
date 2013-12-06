package geogebra.html5.gui.inputfield;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;

public interface ITextEditPanel {
	void updatePreviewPanel();
	void insertGeoElement(GeoElement geo);
	void insertTextString(String text, boolean isLatex);
	GeoText getEditGeo();
	
}