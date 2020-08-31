package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.App;

public class EquationFormDelegate extends AbstractGeoElementDelegate {

	public EquationFormDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (isTextOrInput(element)) {
			return false;
		}
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		return hasEquationModeSetting(element);
	}

	private boolean hasEquationModeSetting(GeoElement element) {
		return !isEnforcedEquationForm(element)
				&& element instanceof GeoLine
				&& !element.isNumberValue()
				&& element.getDefinition() == null;
	}

	private boolean isEnforcedEquationForm(GeoElement element) {
		App app = element.getApp();
		boolean isEnforcedLineEquationForm = element instanceof GeoLine
				&& app.getConfig().getEnforcedLineEquationForm() != -1;
		boolean isEnforcedConicEquationForm = element instanceof GeoConicND
				&& app.getConfig().getEnforcedConicEquationForm() != -1;
		return isEnforcedLineEquationForm || isEnforcedConicEquationForm;
	}
}
