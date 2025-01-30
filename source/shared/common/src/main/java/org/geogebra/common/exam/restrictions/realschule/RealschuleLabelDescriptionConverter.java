package org.geogebra.common.exam.restrictions.realschule;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
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
	public String convert(GeoElement element) {
		if (element == null) {
			return null;
		}
		if (!Realschule.isCalculatedEquationAllowed(element)) {
			return getRestrictedLabelDescription(element);
		}
		if (wrappedConverter != null) {
			return wrappedConverter.convert(element);
		}
		return null;
	}

	private String getRestrictedLabelDescription(GeoElement element) {
		String label;
		switch (element.getLabelMode()) {
		case LABEL_VALUE:
		case LABEL_NAME_VALUE:
			label = element.getDefinition(element.getLabelStringTemplate());
			break;
		default:
			label = wrappedConverter.convert(element);
		}
		return label.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : label;
	}
}