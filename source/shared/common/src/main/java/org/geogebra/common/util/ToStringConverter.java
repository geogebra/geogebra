/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;

/**
 * Provides methods for serializing GeoElement to String.
 * Calling serialization methods of GeoElement directly may "leak" information not
 * acceptable in some settings, this class provides a filtered view of these methods.
 */
public interface ToStringConverter {

	/**
	 * Filters output of {@link GeoElement#toOutputValueString(StringTemplate)}
	 * @param object converted element
	 * @param tpl string template
	 * @return value string, local variables printed as labels
	 */
	@Nonnull String toOutputValueString(GeoElement object, StringTemplate tpl);

	/**
	 * Filters output of {@link GeoElement#toValueString(StringTemplate)}
	 * @param object converted element
	 * @param tpl string template
	 * @return value string, local variables printed as values
	 */
	@Nonnull String toValueString(GeoElement object, StringTemplate tpl);

	/**
	 * Filters output of {@link GeoElement#getLabelDescription()}
	 * @param object converted element
	 * @param tpl string template
	 * @return string containing label, filtered value, or both, bases on element's label setting.
	 */
	@Nonnull String toLabelAndDescription(GeoElement object, StringTemplate tpl);

	/**
	 * @param object construction element
	 * @return output value string, using default template
	 */
	default @Nonnull String convert(GeoElement object) {
		return toOutputValueString(object, StringTemplate.defaultTemplate);
	}

	/**
	 * @param object construction element
	 * @return string containing label and description, using default template
	 */
	default @Nonnull String toLabelAndDescription(GeoElement object) {
		return toLabelAndDescription(object, StringTemplate.defaultTemplate);
	}

	/**
	 * @param element Element
	 * @param template StringTemplate
	 * @param wrappedConverter ToStringConverter used by default
	 * @return The restricted label description for the GeoElement
	 */
	static String getRestrictedLabelDescription(GeoElement element, StringTemplate template,
			ToStringConverter wrappedConverter) {
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
			label = wrappedConverter.toLabelAndDescription(element, template);
		}
		return label.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : label;
	}

	/**
	 * @param element construction element
	 * @param value value
	 * @return assignment string; caption: value, caption = value
	 */
	static String getCaptionAndValue(GeoElement element, String value,
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
