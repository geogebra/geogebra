package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ShowTracePropertyDelegate;

/**
 * Show trace
 */
public class ShowTraceProperty extends AbstractProperty implements BooleanProperty {

	private final GeoElementDelegate delegate;

	/***/
	public ShowTraceProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "ShowTrace");
		delegate = new ShowTracePropertyDelegate(element);
	}

	@Override
	public boolean getValue() {
		return delegate.getElement().getTrace();
	}

	@Override
	public void setValue(boolean trace) {
		GeoElement element = delegate.getElement();
		if (element.isTraceable()) {
			((Traceable) element).setTrace(trace);
		}
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
