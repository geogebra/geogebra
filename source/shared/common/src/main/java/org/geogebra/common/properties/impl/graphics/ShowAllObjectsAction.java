/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class ShowAllObjectsAction extends AbstractActionableProperty
		implements ActionableIconProperty {

	private final AppConfig appConfig;
	private final EuclidianViewInterfaceCommon euclidianView;

	/**
	 * Creates a ShowAllObjectsAction property.
	 * @param localization localization
	 * @param appConfig app config
	 * @param euclidianView euclidean view
	 */
	public ShowAllObjectsAction(Localization localization, AppConfig appConfig,
			EuclidianViewInterfaceCommon euclidianView) {
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