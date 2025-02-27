package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ThicknessPropertyDelegate;

/**
 * Line thickness
 */
public class NotesThicknessProperty extends ThicknessProperty {
	/***/
	public NotesThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, 60, new ThicknessPropertyDelegate(element));
	}
}
