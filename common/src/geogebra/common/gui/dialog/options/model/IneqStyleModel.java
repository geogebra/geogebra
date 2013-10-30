package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.InequalityProperties;

public class IneqStyleModel extends BooleanOptionModel {

	public interface IIneqStyleListener extends IBooleanOptionListener {
		void enableFilling(boolean value);
	}
	public IneqStyleModel(IIneqStyleListener listener) {
		super(listener);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyChanges(boolean value) {
		Object[] geos = getGeos();
		for (int i = 0; i < geos.length; i++) {
			InequalityProperties geo = (InequalityProperties) geos[i];
			geo.setShowOnAxis(value);
			geo.updateRepaint();

		}
	}


	@Override
	public void updateProperties() {
		Object[] geos = getGeos();
		if (!(geos[0] instanceof InequalityProperties)) {
			return;
		}
		
		InequalityProperties temp, geo0 = (InequalityProperties) geos[0];
		boolean equalFix = true;

		for (int i = 0; i < geos.length; i++) {
			if (!(geos[i] instanceof InequalityProperties)) {
				return;
			}
			temp = (InequalityProperties) geos[i];

			if (geo0.showOnAxis() != temp.showOnAxis())
				equalFix = false;
		}

		// set trace visible checkbox
		if (equalFix) {
			getListener().updateCheckbox(geo0.showOnAxis());
			if (geo0.showOnAxis()) {
				((IIneqStyleListener)getListener()).enableFilling(false);
			}
		} else
			getListener().updateCheckbox(false);

	}

	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i).getGeoElementForPropertiesDialog();
			if (!(geo instanceof GeoFunction))
				return false;
			GeoFunction gfun = (GeoFunction) geo;
			if (!gfun.isBooleanFunction()
					|| gfun.getVarString(StringTemplate.defaultTemplate)
							.equals("y"))
				return false;
		}
		return true;
	}

}
