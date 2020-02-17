package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;

/**
 * Converts a GeoElement to string that can be used in the Algebra view, while
 * hiding some values.
 */
public class ProtectiveGeoElementValueConverter implements ToStringConverter<GeoElement> {

	private ToStringConverter<GeoElement> defaultConverter = new GeoElementValueConverter();

	@Override
	public String convert(GeoElement element) {
		if (AlgebraItem.isFunctionOrEquationFromToolOrCommand(element)) {
			return "(" + element.getDefinition(StringTemplate.algebraTemplate) + ")";
		} else {
			return defaultConverter.convert(element);
		}
	}
}
