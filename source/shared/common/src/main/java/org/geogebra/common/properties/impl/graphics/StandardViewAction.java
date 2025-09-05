package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class StandardViewAction extends AbstractActionableProperty
		implements ActionableIconProperty {

	final EuclidianViewInterfaceCommon euclidianView;

	/**
	 * Creates a StandardViewAction property.
	 * @param localization localization
	 * @param euclidianView euclidean view
	 */
	public StandardViewAction(Localization localization,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "StandardView");
		this.euclidianView = euclidianView;
	}

	@Override
	public void doPerformAction() {
		euclidianView.setStandardView(true);
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_STANDARD_VIEW;
	}
}