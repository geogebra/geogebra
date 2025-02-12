package org.geogebra.common.kernel.geos.description;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.ToStringConverter;

/**
 * Default label description implementation for GeoElement.
 */
public class DefaultLabelDescriptionConverter implements ToStringConverter<GeoElement> {

	@Override
	public String convert(GeoElement element, StringTemplate template) {
		String labelDescription;
		switch (element.getLabelMode()) {
			case LABEL_CAPTION_VALUE:
				labelDescription = getCaptionAndValue(element,
						element.toValueString(template), template);
				break;
			case LABEL_NAME_VALUE:
				labelDescription = element.getAlgebraDescriptionDefault();
				break;
			case LABEL_VALUE:
				labelDescription = element.toDefinedValueString(template);
				break;
			case LABEL_CAPTION:
				labelDescription = element.getCaption(template);
				break;
			default: // case LABEL_NAME:
				labelDescription = element.getLabel(template);
		}
		return labelDescription.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : labelDescription;
	}

	/**
	 * @param element Element
	 * @param template StringTemplate
	 * @param wrappedConverter ToStringConverter used by default
	 * @return The restricted label description for the GeoElement
	 */
	public static String getRestrictedLabelDescription(GeoElement element, StringTemplate template,
			ToStringConverter<GeoElement> wrappedConverter) {
		String label;
		switch (element.getLabelMode()) {
		case LABEL_VALUE:
			label = element.getDefinition(template);
			break;
		case LABEL_NAME_VALUE:
			label = withLabel(element, element.getDefinition(template), template);
			break;
		case LABEL_CAPTION_VALUE:
			label = getCaptionAndValue(element, element.getDefinition(template), template);
			break;
		default:
			label = wrappedConverter.convert(element, template);
		}
		return label.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : label;
	}

	/**
	 * @param element construction element
	 * @param value value
	 * @return assignment string; caption: value, caption = value
	 */
	private static String getCaptionAndValue(GeoElement element, String value,
			StringTemplate template) {
		String delimiter = element.getLabelDelimiterWithSpace(template);
		if ("".equals(element.getRawCaption())) {
			if (element.hasVisibleLabel()) {
				return element.getAssignmentLHS(template) + delimiter + value;
			}
		} else {
			return element.getCaption(template) + delimiter + value;
		}
		return value;
	}

	private static String withLabel(GeoElement element, String value, StringTemplate template) {
		if (element.hasVisibleLabel()) {
			String delimiter = element.getLabelDelimiterWithSpace(template);
			return element.getAssignmentLHS(template) + delimiter + value;
		}
		return value;
	}
}
