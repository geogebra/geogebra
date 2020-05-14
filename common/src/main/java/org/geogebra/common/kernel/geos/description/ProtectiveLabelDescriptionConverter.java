package org.geogebra.common.kernel.geos.description;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import org.geogebra.common.gui.view.algebra.fiter.FunctionAndEquationFilter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.ToStringConverter;

/**
 * Filters the label of
 */
public class ProtectiveLabelDescriptionConverter implements ToStringConverter<GeoElement> {

	private ToStringConverter<GeoElement> defaultConverter =
			new DefaultLabelDescriptionConverter();
	private FunctionAndEquationFilter functionAndEquationFilter = new FunctionAndEquationFilter();

	@Override
	public String convert(GeoElement element) {
		if (shouldFilterCaption(element)) {
			return convertProtective(element);
		} else {
			return defaultConverter.convert(element);
		}
	}

	private boolean shouldFilterCaption(GeoElement element) {
		return !functionAndEquationFilter.isAllowed(element);
	}

	private String convertProtective(GeoElement element) {
		String caption;
		switch (element.getLabelMode()) {
			case LABEL_NAME:
			case LABEL_CAPTION:
				caption = element.getLabel(element.getLabelStringTemplate());
				break;
			case LABEL_VALUE:
				caption = element.getDefinition(element.getLabelStringTemplate());
				break;
			default:
				caption = element.getNameAndDefinition(StringTemplate.editTemplate);
		}
		return caption.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : caption;
	}
}
