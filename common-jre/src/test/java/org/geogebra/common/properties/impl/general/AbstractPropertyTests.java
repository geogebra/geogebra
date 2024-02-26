package org.geogebra.common.properties.impl.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractProperty;
import org.junit.Test;

public class AbstractPropertyTests extends BaseUnitTest {

	@Test
	public void testFreezing() {
		DummyAbstractProperty property = new DummyAbstractProperty(getLocalization(), "Dummy");
		property.freeze();
		assertTrue(property.isFrozen());
		property.unfreeze();
		assertFalse(property.isFrozen());
	}

	private static class DummyAbstractProperty extends AbstractProperty {

		public DummyAbstractProperty(Localization localization, String name) {
			super(localization, name);
		}
	}
}
