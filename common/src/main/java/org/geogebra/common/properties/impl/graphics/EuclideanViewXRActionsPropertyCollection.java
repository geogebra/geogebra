package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
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
			EuclidianView3D view3D) {
		super(localization, "");
		ArrayList<ActionableIconProperty> properties = new ArrayList<>();
		properties.add(new RestartARAction(localization, view3D));
		properties.add(new ARFitThicknessAction(localization, view3D));
		setProperties(properties.toArray(new ActionableIconProperty[0]));
	}
}
