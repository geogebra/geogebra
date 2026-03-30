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

package org.geogebra.common.properties.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.junit.Test;

public class PropertyArrayValueObservingTest extends BaseUnitTest {
	private final List<Object> observedValueChanges = new ArrayList<>();
	private final PropertyValueObserver<?> propertyValueObserver = property ->
			observedValueChanges.add(property.getValue());

	@Test
	public void testObserverRegisteredForNestedProperties() {
		TestValuedProperty testValuedProperty = new TestValuedProperty(getLocalization());
		TestPropertyCollection testPropertyCollection =
				new TestPropertyCollection(getLocalization(), testValuedProperty);
		PropertyArrayValueObserving.addObserver(
				new Property[]{testPropertyCollection}, propertyValueObserver);

		testValuedProperty.setValue(3);
		testValuedProperty.setValue(4);
		testValuedProperty.setValue(5);

		assertEquals(List.of(3, 4, 5), observedValueChanges);
	}

	@Test
	public void testObserverRegisteredForDoubleNestedProperties() {
		TestValuedProperty testValuedProperty = new TestValuedProperty(getLocalization());
		TestPropertyCollection innerCollection =
				new TestPropertyCollection(getLocalization(), testValuedProperty);
		TestPropertyCollection outerCollection =
				new TestPropertyCollection(getLocalization(), innerCollection);
		PropertyArrayValueObserving.addObserver(
				new Property[]{outerCollection}, propertyValueObserver);

		testValuedProperty.setValue(5);
		testValuedProperty.setValue(12);
		testValuedProperty.setValue(13);

		assertEquals(List.of(5, 12, 13), observedValueChanges);
	}

	@Test
	public void testObserverRegisteredForDirectProperties() {
		TestValuedProperty testValuedProperty = new TestValuedProperty(getLocalization());
		PropertyArrayValueObserving.addObserver(
				new Property[]{ testValuedProperty }, propertyValueObserver);

		testValuedProperty.setValue(34);
		testValuedProperty.setValue(55);
		testValuedProperty.setValue(89);

		assertEquals(List.of(34, 55, 89), observedValueChanges);
	}

	private static class TestValuedProperty extends AbstractValuedProperty<Integer> {
		private int value;

		TestValuedProperty(Localization localization) {
			super(localization, "");
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		protected void doSetValue(Integer value) {
			this.value = value;
		}
	}

	private static class TestPropertyCollection extends AbstractPropertyCollection<Property> {
		TestPropertyCollection(Localization localization, Property... children) {
			super(localization, "");
			setProperties(children);
		}
	}
}
