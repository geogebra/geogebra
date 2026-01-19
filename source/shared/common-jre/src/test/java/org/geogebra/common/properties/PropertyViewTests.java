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

package org.geogebra.common.properties;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.graphics.AxisDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.objects.AnimationPropertyCollection;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PropertyViewTests extends BaseAppTestSetup {
	@BeforeAll
	public static void enablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterAll
	public static void disabledPreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	public void testGridTypeDependentLineStyleVisibility() {
		setupApp(SuiteSubApp.GRAPHING);

		PropertiesArray graphicsProperties = getApp().getConfig().createPropertiesFactory()
				.createProperties(getApp(), getLocalization(), GlobalScope.propertiesRegistry)
				.get(2);
		PropertyView.ExpandableList gridPropertyView = (PropertyView.ExpandableList)
				PropertyViewFactory.propertyViewListOf(graphicsProperties).get(1);
		PropertyView.SingleSelectionIconRow gridTypeSelector = (PropertyView.SingleSelectionIconRow)
				gridPropertyView.getItems().get(0);
		PropertyView.SingleSelectionIconRow
				lineStyleSelector = (PropertyView.SingleSelectionIconRow)
				gridPropertyView.getItems().get(2);

		assertNotEquals(PropertyResource.ICON_DOTS,
				gridTypeSelector.getIcons().get(gridTypeSelector.getSelectedIconIndex()));
		assertTrue(lineStyleSelector.isVisible());

		gridTypeSelector.setSelectedIconIndex(4);

		assertEquals(PropertyResource.ICON_DOTS,
				gridTypeSelector.getIcons().get(gridTypeSelector.getSelectedIconIndex()));
		assertFalse(lineStyleSelector.isVisible());
	}

	@Test
	public void testFixedDistanceDependentComboBoxEnabledState() {
		setupApp(SuiteSubApp.GRAPHING);

		GridDistancePropertyCollection gridDistancePropertyCollection =
				new GridDistancePropertyCollection(getApp(), getLocalization(),
						getEuclidianSettings(), getEuclidianView());
		PropertyView.RelatedPropertyViewCollection relatedPropertyViewCollection = (PropertyView
				.RelatedPropertyViewCollection) PropertyView.of(gridDistancePropertyCollection);
		PropertyView.Checkbox fixedDistanceCheckbox = (PropertyView.Checkbox)
				relatedPropertyViewCollection.getPropertyViews().get(0);
		PropertyView.HorizontalSplitView horizontalSplitView = (PropertyView.HorizontalSplitView)
				relatedPropertyViewCollection.getPropertyViews().get(1);
		PropertyView.ComboBox xGridDistanceComboBox =
				(PropertyView.ComboBox) horizontalSplitView.getLeadingPropertyView();
		PropertyView.ComboBox yGridDistanceComboBox =
				(PropertyView.ComboBox) horizontalSplitView.getTrailingPropertyView();

		assertFalse(fixedDistanceCheckbox.isSelected());
		assertFalse(xGridDistanceComboBox.isEnabled());
		assertFalse(yGridDistanceComboBox.isEnabled());

		fixedDistanceCheckbox.setSelected(true);

		assertTrue(fixedDistanceCheckbox.isSelected());
		assertTrue(xGridDistanceComboBox.isEnabled());
		assertTrue(yGridDistanceComboBox.isEnabled());
	}

	@Test
	public void testAxisDistanceTextFieldInputValidation() {
		setupApp(SuiteSubApp.GRAPHING);

		AxisDistanceProperty axisDistanceProperty = new AxisDistanceProperty(getLocalization(),
				getEuclidianSettings(), getEuclidianView(), getKernel(), "xAxis", 0);
		getEuclidianSettings().setAutomaticAxesNumberingDistance(false, 0, true);
		PropertyView.ComboBox axisDistanceComboBox =
				(PropertyView.ComboBox) PropertyView.of(axisDistanceProperty);

		assertNull(axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("");
		assertEquals("Please check your input", axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("1");
		assertNull(axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("1/");
		assertEquals("Please check your input", axisDistanceComboBox.getErrorMessage());
		axisDistanceComboBox.setValue("1/2");
		assertNull(axisDistanceComboBox.getErrorMessage());
	}

	@Test
	public void testOrdinalPositionsOfExpandableListsInGraphicsSettings() {
		setupApp(SuiteSubApp.GRAPHING);

		PropertiesArray graphicsProperties = getApp().getConfig().createPropertiesFactory()
				.createProperties(getApp(), getLocalization(), GlobalScope.propertiesRegistry)
				.get(2);
		List<PropertyView> graphicsPropertyViews =
				PropertyViewFactory.propertyViewListOf(graphicsProperties);

		assertFalse(graphicsPropertyViews.get(0) instanceof PropertyView.ExpandableList);
		assertInstanceOf(PropertyView.ExpandableList.class, graphicsPropertyViews.get(1));
		assertEquals(PropertyView.ExpandableList.OrdinalPosition.First,
				((PropertyView.ExpandableList) graphicsPropertyViews.get(1)).ordinalPosition);
		assertEquals(PropertyView.ExpandableList.OrdinalPosition.InBetween,
				((PropertyView.ExpandableList) graphicsPropertyViews.get(2)).ordinalPosition);
		assertInstanceOf(PropertyView.ExpandableList.class,
				graphicsPropertyViews.get(graphicsPropertyViews.size() - 1));
		assertEquals(PropertyView.ExpandableList.OrdinalPosition.Last,
				((PropertyView.ExpandableList) graphicsPropertyViews.get(
						graphicsPropertyViews.size() - 1)).ordinalPosition);
	}

	@Test
	public void testCheckboxConfigurationUpdate() {
		setupApp(SuiteSubApp.GRAPHING);

		GridVisibilityProperty gridVisibilityProperty = new GridVisibilityProperty(
				getLocalization(), getEuclidianSettings());
		PropertyView.Checkbox gridVisibilityCheckbox = (PropertyView.Checkbox)
				PropertyView.of(gridVisibilityProperty);
		AtomicInteger visibilityUpdatedCount = new AtomicInteger();
		gridVisibilityCheckbox.setConfigurationUpdateDelegate(
				() -> visibilityUpdatedCount.addAndGet(1));

		assertTrue(gridVisibilityCheckbox.isSelected());
		gridVisibilityCheckbox.setSelected(false);
		assertFalse(gridVisibilityCheckbox.isSelected());
		assertEquals(1, visibilityUpdatedCount.get());
		gridVisibilityCheckbox.setSelected(true);
		assertTrue(gridVisibilityCheckbox.isSelected());
		assertEquals(2, visibilityUpdatedCount.get());
	}

	@Test
	public void testHorizontalSplitViewIgnoredVisibilityListenersForChildViews() {
		setupApp(SuiteSubApp.GRAPHING);

		PropertyView.ComboBox leadingComboBox = new PropertyView.ComboBox(new GridDistanceProperty(
				getAlgebraProcessor(), getLocalization(), getEuclidianView(), "x", 0));
		PropertyView.ComboBox trailingComboBox = new PropertyView.ComboBox(new GridDistanceProperty(
				getAlgebraProcessor(), getLocalization(), getEuclidianView(), "y", 0));
		PropertyView.HorizontalSplitView horizontalSplitView = new PropertyView.HorizontalSplitView(
				leadingComboBox, trailingComboBox);

		AtomicBoolean leadingPropertyViewVisibilityListenerCalled = new AtomicBoolean(false);
		horizontalSplitView.getLeadingPropertyView().setVisibilityUpdateDelegate(() ->
				leadingPropertyViewVisibilityListenerCalled.set(true));

		AtomicBoolean trailingPropertyViewVisibilityListenerCalled = new AtomicBoolean(false);
		horizontalSplitView.getTrailingPropertyView().setVisibilityUpdateDelegate(() ->
				trailingPropertyViewVisibilityListenerCalled.set(true));

		AtomicBoolean horizontalSplitViewVisibilityListenerCalled = new AtomicBoolean(false);
		horizontalSplitView.setVisibilityUpdateDelegate(() ->
				horizontalSplitViewVisibilityListenerCalled.set(true));

		getEuclidianSettings().setGridType(EuclidianView.GRID_POLAR);

		assertAll(() -> assertTrue(horizontalSplitViewVisibilityListenerCalled.get()),
				() -> assertFalse(leadingPropertyViewVisibilityListenerCalled.get()),
				() -> assertFalse(trailingPropertyViewVisibilityListenerCalled.get()));
	}

	@Test
	public void testSingleHorizontalSplitViewVisibilityListener() {
		setupApp(SuiteSubApp.GRAPHING);

		PropertyView.HorizontalSplitView horizontalSplitView = new PropertyView.HorizontalSplitView(
				new PropertyView.ComboBox(new GridDistanceProperty(
						getAlgebraProcessor(), getLocalization(), getEuclidianView(), "x", 0)),
				new PropertyView.ComboBox(new GridDistanceProperty(
						getAlgebraProcessor(), getLocalization(), getEuclidianView(), "y", 0)));

		AtomicBoolean horizontalSplitViewVisibilityListenerCalled = new AtomicBoolean(false);
		horizontalSplitView.setVisibilityUpdateDelegate(() ->
				horizontalSplitViewVisibilityListenerCalled.set(true));

		getEuclidianSettings().setGridType(EuclidianView.GRID_POLAR);

		assertTrue(horizontalSplitViewVisibilityListenerCalled.get());
	}

	@Test
	public void testFrozenPropertiesAreHidden() {
		setupApp(SuiteSubApp.GRAPHING);

		GeoElement line = evaluateGeoElement("Line((-1,-1),(1,1))");
		GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();
		PropertiesArray properties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getLocalization(), List.of(line));
		LinearEquationFormProperty linearEquationFormProperty = null;
		for (Property property : properties.getProperties()) {
			if (property instanceof NamedEnumeratedPropertyListFacade) {
				Property firstProperty = ((NamedEnumeratedPropertyListFacade) property)
						.getFirstProperty();
				if (firstProperty instanceof LinearEquationFormProperty) {
					linearEquationFormProperty = (LinearEquationFormProperty) firstProperty;
					break;
				}
			}
		}
		assertNotNull(linearEquationFormProperty);
		PropertyView propertyView = PropertyView.of(linearEquationFormProperty);
		assertTrue(propertyView.isVisible());
		linearEquationFormProperty.setFrozen(true);
		propertyView = PropertyView.of(linearEquationFormProperty);
		assertFalse(propertyView.isVisible());
	}
	
	@Test
	@Issue({"APPS-7088", "APPS-7092"})
	public void testSingleExpandableListIsConvertedToContents()
			throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);

		GeoNumeric animatablePoint = evaluateGeoElement("a = 5");
		animatablePoint.setIntervalMin(0);
		animatablePoint.setIntervalMax(10);

		PropertiesArray array = new PropertiesArray(null, getLocalization(),
				new AnimationPropertyCollection(GlobalScope.geoElementPropertiesFactory,
						getAlgebraProcessor(), getLocalization(), List.of(animatablePoint)));
		List<PropertyView> propertyViews = PropertyViewFactory.propertyViewListOf(array);

		assertAll(() -> assertFalse(propertyViews.get(0) instanceof PropertyView.ExpandableList),
				() -> assertTrue(propertyViews.size() == 2)
		);
	}

	private EuclidianView getEuclidianView() {
		return getApp().getActiveEuclidianView();
	}

	private EuclidianSettings getEuclidianSettings() {
		return getApp().getActiveEuclidianView().getSettings();
	}
}
