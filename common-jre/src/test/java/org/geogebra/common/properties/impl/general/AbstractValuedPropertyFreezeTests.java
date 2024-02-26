package org.geogebra.common.properties.impl.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.junit.Test;

public class AbstractValuedPropertyFreezeTests extends BaseUnitTest {

	@Test
	public void testFreezing() {
		DummyAbstractValuedProperty property = new DummyAbstractValuedProperty(getLocalization(), "Dummy");
		property.setValue(1);
		property.freezeValue(2);
		assertTrue(property.isFrozen());
		property.setValue(3);
		assertEquals(2, property.getValue());
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
