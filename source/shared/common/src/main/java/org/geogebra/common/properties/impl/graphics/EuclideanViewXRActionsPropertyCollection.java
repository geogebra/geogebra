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

import java.util.ArrayList;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.aliases.ActionableIconPropertyCollection;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class EuclideanViewXRActionsPropertyCollection
		extends AbstractPropertyCollection<ActionableIconProperty>
		implements ActionableIconPropertyCollection {

	/**
	 * Constructs an EuclideanActionsPropertyCollection.
	 * @param localization localization
	 * @param view3D euclidean view 3d
	 */
	public EuclideanViewXRActionsPropertyCollection(Localization localization,
			EuclidianView3DInterface view3D) {
		super(localization, "");
		ArrayList<ActionableIconProperty> properties = new ArrayList<>();
		properties.add(new RestartARAction(localization, view3D));
		properties.add(new ARFitThicknessAction(localization, view3D));
		setProperties(properties.toArray(new ActionableIconProperty[0]));
	}
}
