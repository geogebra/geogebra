package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;
import org.geogebra.common.properties.impl.objects.delegate.FixObjectDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Fix object
 */
public class FixObjectProperty extends AbstractProperty implements BooleanProperty {

	private final GeoElementDelegate delegate;

	/***/
	public FixObjectProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "fixed");
		delegate = new FixObjectDelegate(element);
	}

	@Override
	public boolean getValue() {
		return delegate.getElement().isLocked();
	}

	@Override
	public void setValue(boolean fixObject) {
		GeoElement element = delegate.getElement();
		element.setFixed(fixObject);
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
