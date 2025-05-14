package org.geogebra.common.gui.view.algebra;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.util.ToStringConverter;

/**
 * Converts a GeoElement to string that can be used in the Algebra view, while
 * hiding some values.
 */
public class ProtectiveGeoElementValueConverter implements ToStringConverter {

	private final ToStringConverter defaultConverter = new GeoElementValueConverter();
	private final AlgebraOutputFilter functionAndEquationFilter;

	public ProtectiveGeoElementValueConverter(
			AlgebraOutputFilter filter) {
		this.functionAndEquationFilter = filter;
	}

	@Override
	public  @Nonnull String toOutputValueString(GeoElement element, StringTemplate template) {
		if (functionAndEquationFilter.isAllowed(element)) {
			return defaultConverter.toOutputValueString(element, template);
		} else {
			return element.getDefinition(template);
		}
	}

	@Override
	public  @Nonnull String toValueString(GeoElement element, StringTemplate template) {
		if (functionAndEquationFilter.isAllowed(element)) {
			return defaultConverter.toValueString(element, template);
		} else {
			return element.getDefinition(template);
		}
	}

	@Override
	public @Nonnull String toLabelAndDescription(GeoElement element, StringTemplate template) {
		if (!functionAndEquationFilter.isAllowed(element)) {
			return convertProtective(element, template);
		} else {
			return defaultConverter.toLabelAndDescription(element, template);
		}
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
