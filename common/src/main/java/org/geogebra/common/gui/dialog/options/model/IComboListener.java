package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;

public interface IComboListener extends PropertyListener {
	void setSelectedIndex(int index);

	void setSelectedItem(String item);

	Object updatePanel(Object[] geos);

	void addItem(GeoElement item);

	void addItem(String plain);

	void clearItems();

}
