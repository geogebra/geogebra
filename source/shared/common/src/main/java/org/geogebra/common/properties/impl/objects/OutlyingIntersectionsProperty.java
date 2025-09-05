package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LimitedPath;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class OutlyingIntersectionsProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final LimitedPath element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public OutlyingIntersectionsProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "allowOutlyingIntersections");
		if (!(element instanceof LimitedPath)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (LimitedPath) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setAllowOutlyingIntersections(value);
		element.updateRepaint();
	}

	@Override
	public Boolean getValue() {
		return element.allowOutlyingIntersections();
	}
}

