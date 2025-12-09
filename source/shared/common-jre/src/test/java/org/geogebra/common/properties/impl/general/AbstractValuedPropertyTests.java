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

package org.geogebra.common.properties.impl.general;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.junit.Test;

public class AbstractValuedPropertyTests extends BaseUnitTest {

	@Test
	public void testValueChangeNotification() {
		DummyAbstractValuedProperty property =
				new DummyAbstractValuedProperty(getLocalization(), "Dummy");
		List<Object> observedValues = new ArrayList<>();
		property.addValueObserver(new PropertyValueObserver() {
			@Override
			public void onDidSetValue(ValuedProperty property) {
				observedValues.add(property.getValue());
			}

			@Override
			public void onBeginSetValue(ValuedProperty property) {
			}

			@Override
			public void onEndSetValue(ValuedProperty property) {
			}
		});

		property.setValue(1);
		property.setValue(2);
		assertEquals(Arrays.asList(1, 2), observedValues);
	}

	private static class DummyAbstractValuedProperty extends AbstractValuedProperty {

		private Object value;

		public DummyAbstractValuedProperty(Localization localization, String name) {
			super(localization, name);
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		protected void doSetValue(Object value) {
			this.value = value;
		}
	}
}
