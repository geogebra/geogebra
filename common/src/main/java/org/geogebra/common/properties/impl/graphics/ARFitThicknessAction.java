package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class ARFitThicknessAction extends AbstractActionableProperty
		implements ActionableIconProperty {

	final EuclidianView3D euclidianView;

	/**
	 * Creates an ARFitThickenssAction property.
	 * @param localization localization
	 * @param euclidianView euclidean view
	 */
	public ARFitThicknessAction(Localization localization, EuclidianView3D euclidianView) {
		super(localization, "ar.FitThickness");
		this.euclidianView = euclidianView;
	}

	@Override
	public void doPerformAction() {
		euclidianView.getRenderer().fitThicknessInAR();
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_AR_FIT_THICKNESS;
	}
}
