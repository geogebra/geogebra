package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.InequalityProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class InequalityOnAxisProperty  extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final InequalityProperties inequality;

	/**
	 * @param localization this is used to localize the name
	 * @param geo the construction element
	 */
	public InequalityOnAxisProperty(Localization localization, GeoElement geo)
			throws NotApplicablePropertyException {
		super(localization, "OnAxis");
		if (!(geo instanceof InequalityProperties)) {
			throw new NotApplicablePropertyException(geo);
		}
		this.inequality = (InequalityProperties) geo;
	}

	@Override
	protected void doSetValue(Boolean value) {
		inequality.setShowOnAxis(value);
		inequality.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return inequality.showOnAxis();
	}
}
