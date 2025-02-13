package org.geogebra.common.exam.restrictions.realschule;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;

/**
 * GeoElement value converter for the Realschule exam.
 * @see org.geogebra.common.exam.restrictions.cvte.CvteValueConverter
 */
public class RealschuleValueConverter implements ToStringConverter {

	private final @Nonnull ToStringConverter wrappedConverter;

	public RealschuleValueConverter(@Nonnull ToStringConverter wrappedConverter) {
		this.wrappedConverter = wrappedConverter;
	}

	@Override
	public @Nonnull String toOutputValueString(GeoElement element, StringTemplate template) {
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return element.getDefinition(template);
		}
		return wrappedConverter.toOutputValueString(element, template);
	}

	@Override
	public @Nonnull String toValueString(GeoElement element, StringTemplate template) {
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return element.getDefinition(template);
		}
		return wrappedConverter.toValueString(element, template);
	}

	@Override
	public @Nonnull String toLabelAndDescription(GeoElement element, StringTemplate template) {
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return ToStringConverter.getRestrictedLabelDescription(
					element, template, wrappedConverter);
		}
		return wrappedConverter.toLabelAndDescription(element, template);
	}
}