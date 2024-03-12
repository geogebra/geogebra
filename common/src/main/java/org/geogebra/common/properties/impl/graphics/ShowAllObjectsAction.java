package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class ShowAllObjectsAction extends AbstractActionableProperty
		implements ActionableIconProperty {

	private final AppConfig appConfig;
	private final EuclidianView euclidianView;

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
	protected void doPerformAction() {
		boolean keepRatio = appConfig.shouldKeepRatioEuclidian();
		euclidianView.setViewShowAllObjects(true, keepRatio);
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_ZOOM_TO_FIT;
	}
}