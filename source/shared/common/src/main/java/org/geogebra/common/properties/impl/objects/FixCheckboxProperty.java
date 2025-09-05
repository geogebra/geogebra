package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class FixCheckboxProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoBoolean element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public FixCheckboxProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "FixCheckbox");
		if (!(element instanceof GeoBoolean)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoBoolean) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setCheckboxFixed(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return element.isLockedPosition();
	}
}

