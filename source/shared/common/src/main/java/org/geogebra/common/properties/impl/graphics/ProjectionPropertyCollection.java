package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class ProjectionPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs a projection property collection.
	 * @param app application
	 * @param localization localization for the title
	 * @param euclidianSettings EV settings
	 */
	public ProjectionPropertyCollection(App app, Localization localization, EuclidianSettings3D
			euclidianSettings) {
		super(localization, "Projection");

		EuclidianView euclidianView = app.getActiveEuclidianView();
		ArrayList<Property> properties = new ArrayList<>();

		properties.add(new ProjectionsProperty(localization, app.getActiveEuclidianView(),
				euclidianSettings));
		properties.add(new DistanceFromScreenProperty(localization, euclidianSettings));
		properties.add(new DistanceBetweenEyesProperty(localization, euclidianSettings));
		properties.add(new GrayScaleProperty(localization, (EuclidianView3D) euclidianView));
		properties.add(new OmitGreenChannelProperty(localization,
				(EuclidianView3D) euclidianView));
		properties.add(new ObliqueAngleProperty(localization, (EuclidianView3D) euclidianView));
		properties.add(new ObliqueFactorProperty(localization, (EuclidianView3D) euclidianView));
		setProperties(properties.toArray(new Property[0]));
	}
}
