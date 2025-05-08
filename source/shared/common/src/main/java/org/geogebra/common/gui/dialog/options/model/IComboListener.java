package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;

public interface IComboListener extends PropertyListener {
	@MissingDoc
	void setSelectedIndex(int index);

	@Override
	Object updatePanel(Object[] geos);

	@MissingDoc
	void addItem(String plain);

	@MissingDoc
	void clearItems();

}
