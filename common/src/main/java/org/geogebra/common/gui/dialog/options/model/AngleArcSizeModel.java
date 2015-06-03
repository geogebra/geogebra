package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoElement;

public class AngleArcSizeModel extends OptionsModel {
	public static final Integer MIN_VALUE = 20;
	private ISliderListener listener;

	public AngleArcSizeModel() {
	}

	public void setListener(ISliderListener listener) {
		this.listener = listener;
	}
	private AngleProperties getAngleAt(int index) { 
		return (AngleProperties) getObjectAt(index);
	}

	public void applyChanges(int size) {
		for (int i = 0; i < getGeosLength(); i++) {
			AngleProperties angle = getAngleAt(i);
			// addded by Loic BEGIN
			// check if decoration could be drawn
			if (size < 20
					&& (angle.getDecorationType() == GeoElement.DECORATION_ANGLE_THREE_ARCS || angle.getDecorationType() == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
				angle.setArcSize(20);
				int selected = getAngleAt(0).getDecorationType();
				if (selected == GeoElement.DECORATION_ANGLE_THREE_ARCS
						|| selected == GeoElement.DECORATION_ANGLE_TWO_ARCS) {
					listener.setValue(20);
				}
			}
			// END
			else {
				angle.setArcSize(size);
			}
			angle.updateRepaint();
		}
	};

	@Override
	public void updateProperties() {
		listener.setValue(getAngleAt(0).getArcSize());
	}

	@Override
	public boolean isValidAt(int index) {
		boolean isValid = true;
		if (getObjectAt(index) instanceof AngleProperties) {
			AngleProperties angle = getAngleAt(index);
			if (angle.isIndependent() || !angle.isDrawable()) {
				isValid = false;
			}
		} else {
			isValid = false;
		}

		return isValid;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	};
}
