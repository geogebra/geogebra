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

package org.geogebra.common.gui.view.algebra;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME_VALUE;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.ToStringConverter;

/**
 * Converts GeoElement to String without any restrictions.
 */
public class GeoElementValueConverter implements ToStringConverter {

	@Override
	public @Nonnull String toOutputValueString(GeoElement element, StringTemplate template) {
		return element instanceof GeoText
				? ((GeoText) element).getTextStringSafe()
				: element.toOutputValueString(template);
	}

	@Override
	public @Nonnull String toValueString(GeoElement object, StringTemplate tpl) {
		return object.toValueString(tpl);
	}

	@Override
	public @Nonnull String toLabelAndDescription(GeoElement element, StringTemplate template) {
		String labelDescription;
		switch (element.getLabelMode()) {
		case LABEL_CAPTION_VALUE:
			labelDescription = ToStringConverter.getCaptionAndValue(element,
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
}
