package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionableProperty;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractProperty;

public class StandardViewAction extends AbstractProperty implements ActionableProperty,
		IconAssociatedProperty {

	final EuclidianView euclidianView;

	public StandardViewAction(Localization localization, EuclidianView euclidianView) {
		super(localization, "StandardView");
		this.euclidianView = euclidianView;
	}

	@Override
	public Runnable getAction() {
		return new Runnable() {
			@Override
			public void run() {
				euclidianView.setStandardView(true);
			}
		};
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_STANDARD_VIEW;
	}
}