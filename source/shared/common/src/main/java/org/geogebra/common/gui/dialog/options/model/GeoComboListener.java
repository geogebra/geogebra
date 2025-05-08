package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoElement;

public interface GeoComboListener extends IComboListener {

	@MissingDoc
	void addItem(GeoElement geo);

	@MissingDoc
	void setSelectedItem(String item);

}
