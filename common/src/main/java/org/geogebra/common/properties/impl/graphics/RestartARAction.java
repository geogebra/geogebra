package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class RestartARAction extends AbstractActionableProperty implements ActionableIconProperty {

	final EuclidianView3D euclidianView;

	/**
	 * Creates a RestartARAction property.
	 * @param localization localization
	 * @param euclidianView euclidean view
	 */
	public RestartARAction(Localization localization, EuclidianView3D euclidianView) {
		super(localization, "ar.restart");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doPerformAction() {
		euclidianView.getRenderer().setARShouldRestart();
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_RELOAD_AR;
	}
}
