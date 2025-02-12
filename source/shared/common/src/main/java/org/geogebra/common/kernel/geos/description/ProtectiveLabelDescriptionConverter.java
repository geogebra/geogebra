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
	public String convert(GeoElement element, StringTemplate template) {
		if (shouldFilterCaption(element)) {
			return convertProtective(element, template);
		} else {
			return defaultConverter.convert(element, template);
		}
	}

	private boolean shouldFilterCaption(GeoElement element) {
		return !functionAndEquationFilter.isAllowed(element);
	}

	private String convertProtective(GeoElement element, StringTemplate template) {
		String caption;
		switch (element.getLabelMode()) {
			case LABEL_NAME:
			case LABEL_CAPTION:
				caption = element.getLabel(template);
				break;
			case LABEL_VALUE:
				caption = element.getDefinition(template);
				break;
			default:
				caption = element.getNameAndDefinition(template);
		}
		return caption.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : caption;
	}
}
