package org.geogebra.common.properties.impl.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.junit.Test;

public final class AbstractEnumeratedPropertyTests extends BaseUnitTest {
	private AbstractEnumeratedProperty<String> enumeratedProperty;

	@Override
	public void setup() {
		super.setup();
		enumeratedProperty = new TestEnumeratedProperty(
				getLocalization(),
				"Test property",
				List.of("value1", "value2", "value3"));
	}

	@Test
	public void testInitialValues() {
		assertEquals(List.of("value1", "value2", "value3"), enumeratedProperty.getValues());
		assertEquals(0, enumeratedProperty.getIndex());
		assertEquals("value1", enumeratedProperty.getValue());
	}

	@Test
	public void testValueChanges() {
		enumeratedProperty.setValue("value2");
		assertEquals(1, enumeratedProperty.getIndex());
		assertEquals("value2", enumeratedProperty.getValue());

		enumeratedProperty.setIndex(2);
		assertEquals(2, enumeratedProperty.getIndex());
		assertEquals("value3", enumeratedProperty.getValue());

		assertThrows(RuntimeException.class, () -> enumeratedProperty.setIndex(3));
	}

	private static final class TestEnumeratedProperty extends AbstractEnumeratedProperty<String> {
		private String value;

		public TestEnumeratedProperty(Localization localization, String name, List<String> values) {
			super(localization, name);
			value = values.get(0);
			setValues(values);
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		protected void doSetValue(String value) {
			this.value = value;
		}
	}
}
