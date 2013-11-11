package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.AngleProperties;
import geogebra.common.kernel.geos.GeoElement;

public class AngleArcSizeModel extends OptionsModel {
	private ISliderListener listener;
	public AngleArcSizeModel(ISliderListener listener) {
		this.listener = listener;
	}

	public void applyChanges(int size) {
		AngleProperties angle;
		Object[] geos = getGeos();
		for (int i = 0; i < getGeosLength(); i++) {
			angle = (AngleProperties) geos[i];
			// addded by Loic BEGIN
			// check if decoration could be drawn
			if (size < 20
					&& (angle.getDecorationType() == GeoElement.DECORATION_ANGLE_THREE_ARCS || angle.getDecorationType() == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
				angle.setArcSize(20);
				int selected = ((AngleProperties) geos[0]).getDecorationType();
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
		AngleProperties geo0 = (AngleProperties)getGeos()[0];
		listener.setValue(geo0.getArcSize());

	}

	@Override
	protected boolean isValidAt(int index){
		boolean isValid = true;
		if (getObjectAt(index) instanceof AngleProperties) {
			AngleProperties angle = (AngleProperties) getObjectAt(index);
			if (angle.isIndependent() || !angle.isDrawable()) {
				isValid = false;
			}
		} else {
			isValid = false;
		}
		
		return isValid;
	};
}
