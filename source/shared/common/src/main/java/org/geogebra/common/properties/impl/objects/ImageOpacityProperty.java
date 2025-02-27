package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.objects.delegate.ImageDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Image opacity
 */
public class ImageOpacityProperty extends OpacityProperty {

	/**
	 * image opacity property
	 * @param localization - localization
	 * @param element - element
	 */
	public ImageOpacityProperty(Localization localization, GeoElement element) throws
			NotApplicablePropertyException {
		super(localization, new ImageDelegate(element));
	}
}
