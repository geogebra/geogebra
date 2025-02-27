package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.objects.delegate.FillingStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class NotesOpacityColorProperty extends OpacityProperty {

	/**
	 * image opacity property
	 * @param localization - localization
	 * @param element - element
	 */
	public NotesOpacityColorProperty(Localization localization, GeoElement element) throws
			NotApplicablePropertyException {
		super(localization, new FillingStylePropertyDelegate(element));
	}
}
