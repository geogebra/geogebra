package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.AngleProperties;

public class RightAngleModel extends BooleanOptionModel {

	public RightAngleModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public void updateProperties() {
		AngleProperties geo0 = (AngleProperties) getGeoAt(0);
		getListener().updateCheckbox(geo0.isEmphasizeRightAngle());
	}


	@Override
	public boolean isValidAt(int index) {
		return getObjectAt(index) instanceof AngleProperties;
	}
	
	@Override
	public boolean getValueAt(int index) {
		// not used here, as updateProperties is overridden.
		return false;
	}

	@Override
	public void apply(int index, boolean value) {
		AngleProperties geo = (AngleProperties) getObjectAt(index);
		geo.setEmphasizeRightAngle(value);
		geo.updateRepaint();
	}

}
