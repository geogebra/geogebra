package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.graphics.ARRatioPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxesColoredProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsPositionProperty;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PlaneVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.PointCapturingProperty;
import org.geogebra.common.properties.impl.graphics.ProjectionsProperty;

/**
 * List of properties for graphics views
 */
public class GraphicsPropertiesList extends PropertiesArray {

	private final App app;
	private final Localization localization;
	private Property[] arViewProperties;

	/**
	 * @param app app
	 * @param localization localization
	 */
	public GraphicsPropertiesList(App app, Localization localization) {
		super(localization.getMenu("DrawingPad"),
				getProperties(app, localization).toArray(new Property[0]));
		this.app = app;
		this.localization = localization;
	}

	private static List<Property> getProperties(App app, Localization localization) {
		EuclidianView activeView = app.getActiveEuclidianView();
		EuclidianSettings euclidianSettings = activeView.getSettings();
		ArrayList<Property> propertyList = new ArrayList<>();

		propertyList.add(new GraphicsPositionProperty(app));
		propertyList.add(new AxesVisibilityProperty(localization, euclidianSettings));

		if (activeView.isEuclidianView3D()) {
			propertyList.add(new PlaneVisibilityProperty(localization,
					(EuclidianSettings3D) euclidianSettings));
		}

		propertyList.add(new GridVisibilityProperty(localization, euclidianSettings));

		if (activeView.isEuclidianView3D()) {
			propertyList.add(new ProjectionsProperty(localization, activeView,
					(EuclidianSettings3D) euclidianSettings));
		}

		if (!activeView.isEuclidianView3D()) {
			propertyList.add(new GridStyleProperty(localization, euclidianSettings));
		}

		propertyList.add(new PointCapturingProperty(app, localization));
		propertyList.add(new DistancePropertyCollection(app, localization, euclidianSettings));
		propertyList.add(new LabelsPropertyCollection(localization, euclidianSettings));

		if (activeView.isEuclidianView3D()) {
			propertyList.add(new AxesColoredProperty(localization,
					(EuclidianSettings3D) euclidianSettings));
		}

		return propertyList;
	}

	@Override
	public Property[] getProperties() {
		if (app.getActiveEuclidianView().isAREnabled()) {
			ensureArViewPropertiesInitialized();
			return arViewProperties;
		}
		return super.getProperties();
	}

	private void ensureArViewPropertiesInitialized() {
		if (arViewProperties == null) {
			List<Property> propertiesListARView =
					new ArrayList<>(Arrays.asList(super.getProperties()));
			propertiesListARView.add(1, new ARRatioPropertyCollection(app, localization));
			propertiesListARView.add(2, new BackgroundProperty(app, localization));
			arViewProperties = propertiesListARView.toArray(new Property[0]);
		}
	}
}
