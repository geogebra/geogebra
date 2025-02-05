package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.description.DefaultLabelDescriptionConverter;
import org.geogebra.common.util.ToStringConverter;

/**
 * Label description converter for the Realschule exam.
 * @see org.geogebra.common.exam.restrictions.cvte.CvteLabelDescriptionConverter
 */
public class RealschuleLabelDescriptionConverter implements ToStringConverter<GeoElement> {

	private final @Nullable ToStringConverter<GeoElement> wrappedConverter;

	public RealschuleLabelDescriptionConverter(
			@Nullable ToStringConverter<GeoElement> wrappedConverter) {
		this.wrappedConverter = wrappedConverter;
	}

	@Override
	public String convert(GeoElement element, StringTemplate template) {
		if (element == null) {
			return null;
		}
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return DefaultLabelDescriptionConverter.getRestrictedLabelDescription(
					element, template, wrappedConverter);
		}
		if (wrappedConverter != null) {
			return wrappedConverter.convert(element, template);
		}
		return null;
	}
}