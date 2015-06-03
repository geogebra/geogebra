package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.InequalityProperties;

public class IneqStyleModel extends BooleanOptionModel {

	public interface IIneqStyleListener extends IBooleanOptionListener {
		void enableFilling(boolean value);
	}

	public IneqStyleModel() {
		super(null);
	}
	
	private InequalityProperties getInequalityPropertiesAt(int index) {
		return (InequalityProperties) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		
		if (!(getObjectAt(0) instanceof InequalityProperties)) {
			return;
		}

		InequalityProperties temp, geo0 = getInequalityPropertiesAt(0);
		boolean equalFix = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getObjectAt(i) instanceof InequalityProperties)) {
				return;
			}
			temp = getInequalityPropertiesAt(i);

			if (geo0.showOnAxis() != temp.showOnAxis())
				equalFix = false;
		}

		if (equalFix) {
			getListener().updateCheckbox(geo0.showOnAxis());
			if (geo0.showOnAxis()) {
				((IIneqStyleListener)getListener()).enableFilling(false);
			}
		} else
			getListener().updateCheckbox(false);

	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index).getGeoElementForPropertiesDialog();
		if (!(geo instanceof GeoFunction)) {
			return false;
		}

		GeoFunction gfun = (GeoFunction) geo;
		if (!gfun.isBooleanFunction()
				|| gfun.getVarString(StringTemplate.defaultTemplate)
				.equals("y")) {
			return false;
		}

		return true;
	}

	@Override
	public boolean getValueAt(int index) {
		// not used as updateProperties is overridden.
		return false;
	}

	@Override
	public void apply(int index, boolean value) {
		InequalityProperties geo = (InequalityProperties) getObjectAt(index);
		geo.setShowOnAxis(value);
		geo.updateRepaint();

	}

}
