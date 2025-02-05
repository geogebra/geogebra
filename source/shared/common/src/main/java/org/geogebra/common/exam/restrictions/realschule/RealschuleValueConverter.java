package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;

/**
 * GeoElement value converter for the Realschule exam.
 * @see org.geogebra.common.exam.restrictions.cvte.CvteValueConverter
 */
public class RealschuleValueConverter implements ToStringConverter<GeoElement> {

	private final @Nullable ToStringConverter<GeoElement> wrappedConverter;

	public RealschuleValueConverter(@Nullable ToStringConverter<GeoElement> wrappedConverter) {
		this.wrappedConverter = wrappedConverter;
	}

	@Override
	public String convert(GeoElement element, StringTemplate template) {
		if (element == null) {
			return null;
		}
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return element.getDefinition(template);
		}
		if (wrappedConverter != null) {
			return wrappedConverter.convert(element, template);
		}
		return null;
	}
}