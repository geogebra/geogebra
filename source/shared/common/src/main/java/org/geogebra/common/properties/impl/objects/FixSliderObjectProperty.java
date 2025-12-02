package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for fixing sliders position. Is analog to the {@link FixObjectProperty}
 * but specialized to sliders.
 */
public class FixSliderObjectProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, GeoElementDependentProperty {

	private final GeoNumeric numeric;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public FixSliderObjectProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "FixObject");
		if (!(element instanceof GeoNumeric)) {
			throw new NotApplicablePropertyException(element);
		}
		this.numeric = (GeoNumeric) element;
	}

	@Override
	public Boolean getValue() {
		return numeric.isLockedPosition();
	}

	@Override
	public void doSetValue(Boolean fixObject) {
		numeric.setSliderFixed(fixObject);
	}

	@Override
	public boolean isEnabled() {
		return numeric.isSlider();
	}

	@Override
	public GeoElement getGeoElement() {
		return numeric;
	}
}

