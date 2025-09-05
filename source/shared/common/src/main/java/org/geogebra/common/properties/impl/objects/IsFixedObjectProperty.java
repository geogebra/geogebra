package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.IsFixedObjectDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Corresponds to the {@link GeoElement#setFixed(boolean)} / {@link GeoElement#isLocked()} property.
 */
public class IsFixedObjectProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private final AbstractGeoElementDelegate delegate;

	/***/
	public IsFixedObjectProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "fixed");
		delegate = new IsFixedObjectDelegate(element);
	}

	@Override
	public Boolean getValue() {
		return delegate.getElement().isLocked();
	}

	@Override
	public void doSetValue(Boolean fixObject) {
		GeoElement element = delegate.getElement();
		element.setFixed(fixObject);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
