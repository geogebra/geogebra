package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.IsFixedObjectDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for fixing objects. Counterpart of {@link IsFixedObjectProperty}
 * with changes to meet new settings view requirements without altering the original.
 */
public class FixObjectProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {
	private final IsFixedObjectDelegate delegate;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public FixObjectProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "FixObject");
		this.delegate = new IsFixedObjectDelegate(geoElement);
	}

	@Override
	public Boolean getValue() {
		return delegate.getElement().isLocked();
	}

	@Override
	public void doSetValue(Boolean fixObject) {
		delegate.getElement().setFixed(fixObject);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
