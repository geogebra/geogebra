package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.properties.ActionableProperty;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.impl.algebra.AlgebraDescriptionProperty;
import org.geogebra.common.properties.impl.algebra.ShowAuxiliaryProperty;
import org.geogebra.common.properties.impl.collections.ActionablePropertyCollection;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RestoreSettingsAction;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.common.properties.impl.general.SaveSettingsAction;
import org.geogebra.common.properties.impl.graphics.AdvancedApps2DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.AdvancedApps3DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.AdvancedClassic2DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.AdvancedClassic3DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.AxesBoldProperty;
import org.geogebra.common.properties.impl.graphics.AxesColorProperty;
import org.geogebra.common.properties.impl.graphics.AxesColoredProperty;
import org.geogebra.common.properties.impl.graphics.AxesLineStyleProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.AxisCrossPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisLabelProperty;
import org.geogebra.common.properties.impl.graphics.AxisPositiveDirectionProperty;
import org.geogebra.common.properties.impl.graphics.AxisSelectionAllowedProperty;
import org.geogebra.common.properties.impl.graphics.AxisTickProperty;
import org.geogebra.common.properties.impl.graphics.AxisUnitPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.Dimension2DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsActionsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridStyleIconProperty;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PointCapturingProperty;
import org.geogebra.common.properties.impl.graphics.ProjectionPropertyCollection;
import org.geogebra.common.properties.impl.graphics.RulingGridBoldProperty;
import org.geogebra.common.properties.impl.graphics.RulingGridColorProperty;
import org.geogebra.common.properties.impl.graphics.RulingGridLineStyleProperty;
import org.geogebra.common.properties.impl.graphics.VerticalYAxis;

/**
 * Creates properties for the GeoGebra application.
 */
public class DefaultPropertiesFactory implements PropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(
				createGeneralProperties(app, localization, propertiesRegistry),
				createAlgebraProperties(app, localization, propertiesRegistry),
				PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
						? createStructuredGraphicsProperties(app, localization, propertiesRegistry)
						: createGraphicsProperties(app, localization, propertiesRegistry));
	}

	/**
	 * Creates general properties.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @param propertiesRegistry properties registry
	 * @return an array of general properties
	 */
	protected PropertiesArray createGeneralProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		Kernel kernel = app.getKernel();
		Settings settings = app.getSettings();
		return new PropertiesArray("General", localization,
				PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
				? registerProperties(propertiesRegistry,
						new LanguageProperty(app, localization),
						new RoundingIndexProperty(app, localization),
						new CoordinatesProperty(kernel, localization),
						new AngleUnitProperty(kernel, localization),
						new FontSizeProperty(
								localization,
								settings.getFontSettings(),
								app.getFontSettingsUpdater()),
						createSaveRestoreSettingsProperties(app, localization))
				: registerProperties(propertiesRegistry,
						new LanguageProperty(app, localization),
						new RoundingIndexProperty(app, localization),
						new CoordinatesProperty(kernel, localization),
						new AngleUnitProperty(kernel, localization),
						new FontSizeProperty(
								localization,
								settings.getFontSettings(),
								app.getFontSettingsUpdater())));
	}

	/**
	 * Creates algebra-specific properties.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @return an array of algebra-specific properties
	 */
	protected PropertiesArray createAlgebraProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return new PropertiesArray("Algebra", localization,
				registerProperties(propertiesRegistry,
						new AlgebraDescriptionProperty(app, localization),
						new ShowAuxiliaryProperty(app, localization)));
	}

	/**
	 * Creates graphics-specific properties.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @return an array of graphics-specific properties
	 */
	protected PropertiesArray createGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		EuclidianView activeView = app.getActiveEuclidianView();
		EuclidianSettings euclidianSettings = activeView.getSettings();
		return new PropertiesArray("DrawingPad", localization,
				registerProperties(propertiesRegistry,
						new GraphicsActionsPropertyCollection(app, localization, activeView),
						new AxesVisibilityProperty(localization, euclidianSettings),
						new GridVisibilityProperty(localization, euclidianSettings),
						new GridStyleProperty(localization, euclidianSettings),
						new PointCapturingProperty(localization, activeView),
						new DistancePropertyCollection(app, localization, euclidianSettings,
								activeView),
						new LabelsPropertyCollection(localization, euclidianSettings))
		);
	}

	protected PropertiesArray createStructuredGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		EuclidianView view1 = app.getEuclidianView1();
		EuclidianSettings euclidianSettings = view1.getSettings();
		return new PropertiesArray("DrawingPad", localization,
				registerProperties(propertiesRegistry,
						new PropertyCollectionWithLead(localization, "Grid",
								new GridVisibilityProperty(localization, euclidianSettings),
								new GridStyleIconProperty(localization, euclidianSettings),
								new RulingGridColorProperty(localization, euclidianSettings),
								new RulingGridLineStyleProperty(localization, euclidianSettings),
								new RulingGridBoldProperty(localization, euclidianSettings),
								new GridDistancePropertyCollection(app, localization,
										euclidianSettings, view1)),
						new PropertyCollectionWithLead(localization, "Axes",
								new AxesVisibilityProperty(localization, euclidianSettings),
								new AxesColorProperty(localization, euclidianSettings),
								new AxesLineStyleProperty(localization, euclidianSettings),
								new AxesBoldProperty(localization, euclidianSettings),
								new LabelStylePropertyCollection(localization, euclidianSettings)
						),
						new Dimension2DPropertiesCollection(app, localization, euclidianSettings,
								view1),
						axisExpandableProperty2D(0, "xAxis", app, localization, view1),
						axisExpandableProperty2D(1, "yAxis", app, localization, view1),
						app.isUnbundled()
								? new AdvancedApps2DPropertiesCollection(app, localization,
								euclidianSettings, view1)
								: new AdvancedClassic2DPropertiesCollection(app, localization,
								euclidianSettings, view1))
		);
	}

	protected PropertiesArray createStructuredGraphics2Properties(App app,
			Localization localization, PropertiesRegistry propertiesRegistry) {
		EuclidianView activeView = app.getEuclidianView2(1);
		EuclidianSettings euclidianSettings = activeView.getSettings();
		return new PropertiesArray("DrawingPad2", localization,
				registerProperties(propertiesRegistry,
						new PropertyCollectionWithLead(localization, "Grid",
								new GridVisibilityProperty(localization, euclidianSettings),
								new GridStyleIconProperty(localization, euclidianSettings),
								new RulingGridColorProperty(localization, euclidianSettings),
								new RulingGridLineStyleProperty(localization, euclidianSettings),
								new RulingGridBoldProperty(localization, euclidianSettings),
								new GridDistancePropertyCollection(app, localization,
										euclidianSettings, activeView)),
						new PropertyCollectionWithLead(localization, "Axes",
								new AxesVisibilityProperty(localization, euclidianSettings),
								new AxesColorProperty(localization, euclidianSettings),
								new AxesLineStyleProperty(localization, euclidianSettings),
								new AxesBoldProperty(localization, euclidianSettings),
								new LabelStylePropertyCollection(localization, euclidianSettings)
						),
						new Dimension2DPropertiesCollection(app, localization,
								euclidianSettings, activeView),
						axisExpandableProperty2D(0, "xAxis", app, localization, activeView),
						axisExpandableProperty2D(1, "yAxis", app, localization, activeView),
						app.isUnbundled()
								? new AdvancedApps2DPropertiesCollection(app, localization,
								euclidianSettings, activeView)
								: new AdvancedClassic2DPropertiesCollection(app, localization,
								euclidianSettings, activeView))
		);
	}

	protected PropertiesArray createStructuredGraphics3DProperties(App app,
			Localization localization, PropertiesRegistry propertiesRegistry) {
		EuclidianSettings euclidianSettings = app.getSettings().getEuclidian(-1);
		EuclidianViewInterfaceCommon view = app.getEuclidianView3D();
		return new PropertiesArray("GraphicsView3D", localization,
				registerProperties(propertiesRegistry,
						new PropertyCollectionWithLead(localization, "Grid",
								new GridVisibilityProperty(localization, euclidianSettings),
								new GridDistancePropertyCollection(app, localization,
										euclidianSettings, view)),
						new PropertyCollectionWithLead(localization, "Axes",
								new AxesVisibilityProperty(localization, euclidianSettings),
								new VerticalYAxis(localization,
										(EuclidianSettings3D) euclidianSettings),
								new AxesColoredProperty(localization,
										(EuclidianSettings3D) euclidianSettings),
								new LabelStylePropertyCollection(localization, euclidianSettings)),
						axisExpandableProperty3D(0, "xAxis", app, localization, view),
						axisExpandableProperty3D(1, "yAxis", app, localization, view),
						axisExpandableProperty3D(2, "zAxis", app, localization, view),
						new ProjectionPropertyCollection(app, localization,
								(EuclidianSettings3D) euclidianSettings),
						app.isUnbundled()
								? new AdvancedApps3DPropertiesCollection(app, localization,
								euclidianSettings, (EuclidianView3D) view)
								: new AdvancedClassic3DPropertiesCollection(app, localization,
								euclidianSettings, (EuclidianView3D) view))
		);
	}

	protected ActionablePropertyCollection<ActionableProperty> createSaveRestoreSettingsProperties(
			App app, Localization localization) {
		return new ActionablePropertyCollection<ActionableProperty>(localization, List.of(
				new SaveSettingsAction(app, localization),
				new RestoreSettingsAction(app, localization)));
	}

	protected Property axisExpandableProperty2D(int axis, String label, App app,
			Localization localization, EuclidianViewInterfaceCommon view) {
		return axisExpandableProperty(axis, label, app, localization, view,
				new AxisCrossPropertyCollection(localization, view.getSettings(), axis, view));
	}

	protected Property axisExpandableProperty3D(int axis, String label, App app,
			Localization localization, EuclidianViewInterfaceCommon view) {
		return axisExpandableProperty(axis, label, app, localization, view, null);
	}

	private Property axisExpandableProperty(int axis, String label, App app,
			Localization localization, EuclidianViewInterfaceCommon view,
			@CheckForNull Property crossProperty) {
		EuclidianSettings euclidianSettings = view.getSettings();
		Property[] properties = Stream.of(
				new AxisLabelProperty(localization, euclidianSettings, "Label", axis),
				new AxisTickProperty(localization, euclidianSettings, axis, view),
				new AxisDistancePropertyCollection(app, localization, euclidianSettings, axis,
						view),
				new AxisUnitPropertyCollection(localization, euclidianSettings, axis, view),
				crossProperty,
				new AxisPositiveDirectionProperty(localization, euclidianSettings, axis, view),
				new AxisSelectionAllowedProperty(localization, euclidianSettings, axis, view)
		).filter(Objects::nonNull).toArray(Property[]::new);
		return new PropertyCollectionWithLead(localization, label,
				new AxisVisibilityProperty(localization, euclidianSettings, axis, label),
				properties);
	}
}