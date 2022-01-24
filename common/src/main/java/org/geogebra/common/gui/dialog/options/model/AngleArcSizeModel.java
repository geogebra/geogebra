package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

public class AngleArcSizeModel extends OptionsModel {
	public static final Integer MIN_VALUE = 20;
	private ISliderListener listener;

	public AngleArcSizeModel(App app) {
		super(app);
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
			if (size < 20 && (angle
					.getDecorationType() == GeoElementND.DECORATION_ANGLE_THREE_ARCS
					|| angle.getDecorationType() == GeoElementND.DECORATION_ANGLE_TWO_ARCS)) {
				angle.setArcSize(20);
				int selected = getAngleAt(0).getDecorationType();
				if (selected == GeoElementND.DECORATION_ANGLE_THREE_ARCS
						|| selected == GeoElementND.DECORATION_ANGLE_TWO_ARCS) {
					listener.setValue(20);
				}
			}
			// END
			else {
				angle.setArcSize(size);
			}
			angle.updateVisualStyleRepaint(GProperty.ANGLE_STYLE);
		}
		storeUndoInfo();
	}

	@Override
	public void updateProperties() {
		listener.setValue(getAngleAt(0).getArcSize());
	}

	@Override
	public boolean isValidAt(int index) {
		return match(getGeoAt(index));
	}

	/**
	 * @param geo
	 *            The geo to math returns true if geo meets the requirements of
	 *            this model
	 */
	public static boolean match(GeoElement geo) {
		if (geo instanceof AngleProperties) {
			AngleProperties angle = (AngleProperties) geo;
			if (angle.isIndependent() || !angle.isDrawable()) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
