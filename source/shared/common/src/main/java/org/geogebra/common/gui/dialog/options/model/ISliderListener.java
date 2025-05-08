package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;

public interface ISliderListener extends PropertyListener {
	@MissingDoc
	void setValue(int value);

	default void setSliderMin(int min) {
		// only needed for angles
	}
}
