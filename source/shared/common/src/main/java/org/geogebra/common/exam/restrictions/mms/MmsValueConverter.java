package org.geogebra.common.exam.restrictions.mms;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;

public final class MmsValueConverter implements ToStringConverter {

	private final @Nonnull ToStringConverter wrappedConverter;

	public MmsValueConverter(@Nonnull ToStringConverter wrappedConverter) {
		this.wrappedConverter = wrappedConverter;
	}

	@Override
	public @Nonnull String toOutputValueString(GeoElement element, StringTemplate template) {
		return Mms.isOutputAllowed(element)
				? wrappedConverter.toOutputValueString(element, template)
				: element.getDefinition(template);
	}

	@Override
	public @Nonnull String toValueString(GeoElement element, StringTemplate template) {
		return Mms.isOutputAllowed(element)
				? wrappedConverter.toValueString(element, template)
				: element.getDefinition(template);
	}

	@Override
	public @Nonnull String toLabelAndDescription(GeoElement element, StringTemplate template) {
		return Mms.isOutputAllowed(element)
				? wrappedConverter.toLabelAndDescription(element, template)
				: ToStringConverter.getRestrictedLabelDescription(element, template,
						wrappedConverter);
	}
}