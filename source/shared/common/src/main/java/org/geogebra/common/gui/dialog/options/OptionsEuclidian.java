package org.geogebra.common.gui.dialog.options;

import org.geogebra.common.annotation.MissingDoc;

public abstract class OptionsEuclidian {

	@MissingDoc
	abstract public void updateGUI();

	/**
	 * updates x/y min/max/scale
	 */
	abstract public void updateBounds();
}
