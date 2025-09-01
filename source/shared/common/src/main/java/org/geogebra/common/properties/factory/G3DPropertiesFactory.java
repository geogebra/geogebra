package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.impl.graphics.ARRatioPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AdvancedPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.AxesColoredProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;
import org.geogebra.common.properties.impl.graphics.DimensionPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.EuclideanViewXRActionsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsActionsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PlaneVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.PointCapturingProperty;
import org.geogebra.common.properties.impl.graphics.ProjectionsProperty;

public class G3DPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(createGeneralProperties(app, localization, propertiesRegistry),
				createAlgebraProperties(app, localization, propertiesRegistry),
				createStructuredGraphics3DProperties(app, localization, propertiesRegistry));
	}

	@Override
	protected PropertiesArray createGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		EuclidianSettings euclidianSettings = app.getActiveEuclidianView().getSettings();
		List<Property> propertyList = new ArrayList<>();

		if (app.getActiveEuclidianView().isXREnabled()) {
			EuclidianView3D view3D = (EuclidianView3D) app.getActiveEuclidianView();
			propertyList.add(new EuclideanViewXRActionsPropertyCollection(localization, view3D));
			propertyList.add(new ARRatioPropertyCollection(app, localization));
			propertyList.add(new BackgroundProperty(app, localization));
		} else {
			propertyList.add(new GraphicsActionsPropertyCollection(app, localization));
		}
		propertyList.add(new AxesVisibilityProperty(localization, euclidianSettings));
		propertyList.add(
				new PlaneVisibilityProperty(localization, (EuclidianSettings3D) euclidianSettings));
		propertyList.add(new GridVisibilityProperty(localization, euclidianSettings));
		propertyList.add(
				new ProjectionsProperty(
						localization,
						app.getActiveEuclidianView(),
						(EuclidianSettings3D) euclidianSettings));
		propertyList.add(new PointCapturingProperty(app, localization));
		propertyList.add(new DistancePropertyCollection(app, localization, euclidianSettings));
		propertyList.add(new LabelsPropertyCollection(localization, euclidianSettings));
		propertyList.add(
				new AxesColoredProperty(localization, (EuclidianSettings3D) euclidianSettings));

		return new PropertiesArray("DrawingPad", localization,
				registerProperties(propertiesRegistry, propertyList));
	}

	@Override
	public PropertiesArray createStructuredGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		EuclidianView activeView = app.getActiveEuclidianView();
		EuclidianSettings euclidianSettings = activeView.getSettings();
		return new PropertiesArray("GraphicsView3D", localization,
				registerProperties(propertiesRegistry,
						new PropertyCollectionWithLead(localization, "Grid",
								new GridVisibilityProperty(localization, euclidianSettings),
								new GridDistancePropertyCollection(app, localization,
										euclidianSettings)),
						new PropertyCollectionWithLead(localization, "Axes",
								new AxesVisibilityProperty(localization, euclidianSettings)
						),
						new DimensionPropertiesCollection(app, localization, euclidianSettings),
						axisExpandableProperty(0, "xAxis", app, localization),
						axisExpandableProperty(1, "yAxis", app, localization),
						axisExpandableProperty(2, "zAxis", app, localization),
						new AdvancedPropertiesCollection(localization, euclidianSettings))
		);

	}

}
