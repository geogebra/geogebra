package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class DrawArrowsProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {
	private final GeoLocus locus;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public DrawArrowsProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "DrawArrows");
		if (!(element instanceof GeoLocus)) {
			throw new NotApplicablePropertyException(element);
		}
		this.locus = (GeoLocus) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		locus.drawAsArrows(value);
		locus.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return locus.hasDrawArrows();
	}
}
