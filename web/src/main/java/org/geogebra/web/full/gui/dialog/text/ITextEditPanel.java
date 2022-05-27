package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

public interface ITextEditPanel {
	void updatePreviewPanel();

	void insertGeoElement(GeoElement geo);

	void insertTextString(String text, boolean isLatex);

	GeoText getEditGeo();

	void ensureLaTeX();

	void updatePreviewPanel(boolean b);

}