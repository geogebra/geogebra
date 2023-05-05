package org.geogebra.common.gui.dialog.options.model;

public interface ISliderListener extends PropertyListener {
	void setValue(int value);

	default void setSliderMin(int min) {
		// only needed for angles
	}
}
