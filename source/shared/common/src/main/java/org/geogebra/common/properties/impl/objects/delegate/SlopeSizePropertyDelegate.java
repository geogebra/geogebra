package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class SlopeSizePropertyDelegate extends AbstractGeoElementDelegate {

	public SlopeSizePropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element instanceof GeoNumeric && Algos.isUsedFor(Commands.Slope, element);
	}

	@Override
	public GeoNumeric getElement() {
		return (GeoNumeric) super.getElement();
	}
}
