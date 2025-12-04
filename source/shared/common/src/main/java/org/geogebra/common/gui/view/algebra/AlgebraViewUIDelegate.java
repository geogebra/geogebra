package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;

public interface AlgebraViewUIDelegate {

	/**
	 * Reset/clear the (Algebra View UI) input field.
	 */
	void clearInputField();

	/**
	 * See {@link org.geogebra.common.kernel.View}
	 */
	void updatePreviewFromInputBar(GeoElement[] geos);
}
