package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class QuadraticEquationFormDelegate extends AbstractGeoElementDelegate {

	/**
	 * Construct a quadratic equation form element delegate.
	 * @param element element representable as a quadratic equation form
	 * @throws NotApplicablePropertyException when the element is not representable
	 * as a quadratic equation form
	 */
	public QuadraticEquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
		checkIsApplicable(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		if (element instanceof QuadraticEquationRepresentable) {
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