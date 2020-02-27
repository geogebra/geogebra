package org.geogebra.common.kernel.geos.description;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.ToStringConverter;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

/**
 * Default label description implementation for GeoElement.
 */
public class DefaultLabelDescriptionConverter implements ToStringConverter<GeoElement> {

	@Override
	public String convert(GeoElement element) {
		String labelDescription;
		switch (element.getLabelMode()) {
			case LABEL_CAPTION_VALUE:
				labelDescription = getCaptionAndValue(element);
				break;
			case LABEL_NAME_VALUE:
				labelDescription = element.getAlgebraDescriptionDefault();
				break;
			case LABEL_VALUE:
				labelDescription = element.toDefinedValueString(element.getLabelStringTemplate());
				break;
			case LABEL_CAPTION:
				labelDescription = element.getCaption(element.getLabelStringTemplate());
				break;
			default: // case LABEL_NAME:
				labelDescription = element.getLabel(element.getLabelStringTemplate());
		}
		return labelDescription.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : labelDescription;
	}

	private String getCaptionAndValue(GeoElement element) {
		if ("".equals(element.getRawCaption())) {
			return element.getAlgebraDescriptionDefault();
		}

		String retVal = element.getRawCaption();
		retVal += element.getLabelDelimiterWithSpace();
		retVal += element.toValueString(StringTemplate.defaultTemplate);
		return retVal;
	}
}
