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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.objects.LocationPropertyCollection;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.util.ImageManagerCommon;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyViewFactoryTest extends BaseAppTestSetup {
	private GeoElement element;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().setImageManager(new ImageManagerCommon());
		element = evaluateGeoElement("A = (1, 2)");
	}

	@Test
	void reconstructionKeepsSelectedTabWhenTabPositionsChange() {
		PropertyView.TabbedPageSelector original =
				properties(evaluateGeoElement("BarChart({1, 2}, {3, 4})"));
		String advanced = getLocalization().getMenu("Advanced");
		original.setSelectedTabIndex(original.getTabTitles().indexOf(advanced));
		PropertyView.TabbedPageSelector replacement =
				properties(evaluateGeoElement("Circle((0, 0), 2)"));
		PropertyViewFactory.restorePresentation(original, replacement);

		assertNotEquals(original.getSelectedTabIndex(), replacement.getSelectedTabIndex());
		assertEquals(advanced, replacement.getTabTitles().get(replacement.getSelectedTabIndex()));
	}

	@Test
	void reconstructionFallsBackWhenSelectedTabDisappears() {
		PropertyView.TabbedPageSelector original =
				properties(evaluateGeoElement("BarChart({1, 2}, {3, 4})"));
		original.setSelectedTabIndex(
				original.getTabTitles().indexOf(getLocalization().getMenu("Data")));
		PropertyView.TabbedPageSelector replacement = properties(evaluateGeoElement("true"));
		PropertyViewFactory.restorePresentation(original, replacement);

		assertEquals(0, replacement.getSelectedTabIndex());
	}

	@Test
	void restoresExpandableListsInReconstructedTree() {
		Property inner = new Group("Axes", new ShowObjectProperty(getLocalization(), element));
		Property outer = new Group("Grid", inner);
		PropertyView.TabbedPageSelector original = properties(element);
		PropertyView.ExpandableList oldOuter = (PropertyView.ExpandableList) PropertyView.of(outer);
		original.getPageContents(0).add(oldOuter);
		oldOuter.setExpanded(true);
		((PropertyView.ExpandableList) oldOuter.getItems().get(0)).setExpanded(true);
		PropertyView.TabbedPageSelector replacement = properties(element);
		PropertyView.ExpandableList newOuter = (PropertyView.ExpandableList) PropertyView.of(outer);
		replacement.getPageContents(0).add(newOuter);
		PropertyViewFactory.restorePresentation(original, replacement);

		assertTrue(newOuter.isExpanded());
		assertTrue(((PropertyView.ExpandableList) newOuter.getItems().get(0)).isExpanded());
	}

	@Test
	void restoresExpandableListsByTitleWhenOrderChanges() {
		Property first = new Group("Grid", new ShowObjectProperty(getLocalization(), element));
		Property second = new Group("Axes", new ShowObjectProperty(getLocalization(), element));
		PropertyView.TabbedPageSelector original = properties(element);
		PropertyView.ExpandableList oldFirst = (PropertyView.ExpandableList) PropertyView.of(first);
		original.getPageContents(0).add(oldFirst);
		original.getPageContents(0).add(PropertyView.of(second));
		oldFirst.setExpanded(true);
		PropertyView.TabbedPageSelector replacement = properties(element);
		PropertyView.ExpandableList newFirst = (PropertyView.ExpandableList) PropertyView.of(first);
		PropertyView.ExpandableList newSecond = (PropertyView.ExpandableList) PropertyView.of(second);
		newSecond.setExpanded(true);
		replacement.getPageContents(0).addAll(List.of(newSecond, newFirst));
		PropertyViewFactory.restorePresentation(original, replacement);

		assertTrue(newFirst.isExpanded());
		assertFalse(newSecond.isExpanded());
	}

	@Test
	void restoresExpandableListsInsideRelatedPropertyCollections() {
		Property related = new LocationPropertyCollection(
				new GeoElementPropertiesFactory(), getLocalization(), List.of(element));
		Property nested = new Group("Grid", new ShowObjectProperty(getLocalization(), element));
		PropertyView.TabbedPageSelector original = properties(element);
		PropertyView.RelatedPropertyViewCollection oldCollection =
				(PropertyView.RelatedPropertyViewCollection) PropertyView.of(related);
		PropertyView.ExpandableList oldGroup = (PropertyView.ExpandableList) PropertyView.of(nested);
		oldCollection.getPropertyViews().add(oldGroup);
		original.getPageContents(0).add(oldCollection);
		oldGroup.setExpanded(true);
		PropertyView.TabbedPageSelector replacement = properties(element);
		PropertyView.RelatedPropertyViewCollection newCollection =
				(PropertyView.RelatedPropertyViewCollection) PropertyView.of(related);
		PropertyView.ExpandableList newGroup = (PropertyView.ExpandableList) PropertyView.of(nested);
		newCollection.getPropertyViews().add(newGroup);
		replacement.getPageContents(0).add(newCollection);
		PropertyViewFactory.restorePresentation(original, replacement);

		assertTrue(newGroup.isExpanded());
	}

	private PropertyView.TabbedPageSelector properties(GeoElement element) {
		return PropertyViewFactory.propertyViewOfObjectSettings(
				getApp(), List.of(element), new ObjectSettingsSession.Context());
	}

	private class Group extends AbstractPropertyCollection<Property> {
		Group(String name, Property... children) {
			super(PropertyViewFactoryTest.this.getLocalization(), name);
			setProperties(children);
		}
	}
}
