package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

public class StandardViewAction extends AbstractProperty implements ActionableIconProperty {

	final EuclidianView euclidianView;

	/**
	 * Creates a StandardViewAction property.
	 * @param localization localization
	 * @param euclidianView euclidean view
	 */
	public StandardViewAction(Localization localization, EuclidianView euclidianView) {
		super(localization, "StandardView");
		this.euclidianView = euclidianView;
	}

	@Override
	public void performAction() {
		euclidianView.setStandardView(true);
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_STANDARD_VIEW;
	}
}