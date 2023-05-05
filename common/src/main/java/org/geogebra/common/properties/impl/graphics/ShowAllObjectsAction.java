package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionableProperty;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractProperty;

public class ShowAllObjectsAction extends AbstractProperty implements ActionableProperty,
		IconAssociatedProperty {

	final AppConfig appConfig;
	final EuclidianView euclidianView;

	/**
	 * Creates a ShowAllObjectsAction property.
	 * @param localization localization
	 * @param appConfig app config
	 * @param euclidianView euclidean view
	 */
	public ShowAllObjectsAction(Localization localization, AppConfig appConfig,
			EuclidianView euclidianView) {
		super(localization, "ShowAllObjects");
		this.appConfig = appConfig;
		this.euclidianView = euclidianView;
	}

	@Override
	public Runnable getAction() {
		return new Runnable() {
			@Override
			public void run() {
				boolean keepRatio = appConfig.shouldKeepRatioEuclidian();
				euclidianView.setViewShowAllObjects(true, keepRatio);
			}
		};
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_ZOOM_TO_FIT;
	}
}