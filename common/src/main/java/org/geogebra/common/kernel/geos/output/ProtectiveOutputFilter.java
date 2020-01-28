package org.geogebra.common.kernel.geos.output;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;

import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_CAPTION;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_NAME;
import static org.geogebra.common.kernel.kernelND.GeoElementND.LABEL_VALUE;

/**
 * This class should be used by the apps that need output filtering.
 */
public class ProtectiveOutputFilter implements GeoOutputFilter {

	@Override
	public boolean shouldFilterCaption(GeoElement element) {
		return !AlgebraItem.isFunctionOrEquationFromUser(element);
	}

	@Override
	public String filterCaption(GeoElement element) {
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
				caption = element.getNameAndDefinition();
		}
		return caption.startsWith(LabelManager.HIDDEN_PREFIX) ? "" : caption;
	}
}
