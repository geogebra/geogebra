package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;

public interface GeoComboListener extends IComboListener {

	void addItem(GeoElement geo);

	void setSelectedItem(String item);

}
