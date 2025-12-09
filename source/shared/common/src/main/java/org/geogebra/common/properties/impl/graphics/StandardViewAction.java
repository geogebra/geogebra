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