package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ShowTracePropertyDelegate;

/**
 * Show trace
 */
public class ShowTraceProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {

	private final AbstractGeoElementDelegate delegate;

	/***/
	public ShowTraceProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "ShowTrace");
		delegate = new ShowTracePropertyDelegate(element);
	}

	@Override
	public Boolean getValue() {
		return delegate.getElement().getTrace();
	}

	@Override
	public void doSetValue(Boolean trace) {
		GeoElement element = delegate.getElement();
		if (element.isTraceable()) {
			((Traceable) element).setTrace(trace);
		}
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
