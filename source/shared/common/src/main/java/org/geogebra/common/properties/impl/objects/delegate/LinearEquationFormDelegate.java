package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class LinearEquationFormDelegate extends AbstractGeoElementDelegate {

	public LinearEquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		if (element instanceof LinearEquationRepresentable) {
			// see e.g., APPS-1691
			Kernel kernel = element.getKernel();
			if (kernel != null) {
				EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
				if (equationBehaviour != null) {
					return equationBehaviour.allowsChangingEquationFormsByUser();
				}
			}
			return true;
		}
		return false;
	}
}
