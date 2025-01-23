package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.ToStringConverter;

/**
 * Converts a GeoElement such that it can be used in an algebra input.
 */
public class GeoElementValueConverter implements ToStringConverter<GeoElement> {

	@Override
	public String convert(GeoElement element) {
		return element instanceof GeoText
				? ((GeoText) element).getTextString()
				: element.toOutputValueString(StringTemplate.algebraTemplate);
	}
}
