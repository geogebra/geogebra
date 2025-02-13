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

	@Nonnull
	@Override
	public String toValueString(GeoElement object, StringTemplate tpl) {
		return object.toValueString(tpl);
	}

	@Nonnull
	@Override
	public String toLabelAndDescription(GeoElement element, StringTemplate template) {
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
