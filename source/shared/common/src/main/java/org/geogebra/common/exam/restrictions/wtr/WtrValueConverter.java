package org.geogebra.common.exam.restrictions.wtr;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;

/**
 * Value converter for WTR exam, prevents serializing
 * angles in radians if they are defined in degrees.
 */
public final class WtrValueConverter implements ToStringConverter {

	private final @Nonnull ToStringConverter wrappedConverter;
	private final AlgebraConversionFilter filter;

	/**
	 * @param wrappedConverter wrapped converter
	 */
	public WtrValueConverter(@Nonnull ToStringConverter wrappedConverter) {
		this.wrappedConverter = wrappedConverter;
		this.filter = new AlgebraConversionFilter();
	}

	@Override
	public @Nonnull String toOutputValueString(GeoElement element, StringTemplate template) {
		return filter.isAllowed(element)
				? wrappedConverter.toOutputValueString(element, template)
				: element.getDefinition(template);
	}

	@Override
	public @Nonnull String toValueString(GeoElement element, StringTemplate template) {
		return filter.isAllowed(element)
				? wrappedConverter.toValueString(element, template)
				: element.getDefinition(template);
	}

	@Override
	public @Nonnull String toLabelAndDescription(GeoElement element, StringTemplate template) {
		return filter.isAllowed(element)
				? wrappedConverter.toLabelAndDescription(element, template)
				: ToStringConverter.getRestrictedLabelDescription(element, template,
				wrappedConverter);
	}
}