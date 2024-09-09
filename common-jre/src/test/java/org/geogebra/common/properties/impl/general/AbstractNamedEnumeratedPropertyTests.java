package org.geogebra.common.properties.impl.general;

import static java.util.Map.entry;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ValueFilter;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.junit.Test;

public class AbstractNamedEnumeratedPropertyTests extends BaseUnitTest {
	private AbstractNamedEnumeratedProperty<String> namedEnumeratedProperty;

	@Override
	public void setup() {
		super.setup();

		namedEnumeratedProperty = new TestNamedEnumeratedProperty(
				getLocalization(),
				"Test property",
				List.of(entry("value1", "Value One"),
						entry("value2", "Value Two"),
						entry("value3", "Value Three")));
	}

	@Test
	public void testInitialNamedValues() {
		assertEquals(List.of("value1", "value2", "value3"),
				namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One", "Value Two", "Value Three" },
				namedEnumeratedProperty.getValueNames());
	}

	@Test
	public void testNamedValuesWithValueFilters() {
		TestValueFilter valueFilter1 = new TestValueFilter(List.of("value2", "invalid value"));
		TestValueFilter valueFilter2 = new TestValueFilter(List.of("value3"));
		TestValueFilter valueFilter3 = new TestValueFilter(List.of("value2"));

		namedEnumeratedProperty.addValueFilter(valueFilter1);
		assertEquals(List.of("value1", "value3"), namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One", "Value Three" },
				namedEnumeratedProperty.getValueNames());

		namedEnumeratedProperty.addValueFilter(valueFilter2);
		assertEquals(List.of("value1"), namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One" }, namedEnumeratedProperty.getValueNames());

		namedEnumeratedProperty.addValueFilter(valueFilter3);
		assertEquals(List.of("value1"), namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One" }, namedEnumeratedProperty.getValueNames());

		namedEnumeratedProperty.removeValueFilter(valueFilter1);
		assertEquals(List.of("value1"), namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One" }, namedEnumeratedProperty.getValueNames());

		namedEnumeratedProperty.removeValueFilter(valueFilter2);
		assertEquals(List.of("value1", "value3"), namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One", "Value Three" },
				namedEnumeratedProperty.getValueNames());

		namedEnumeratedProperty.removeValueFilter(valueFilter3);
		assertEquals(List.of("value1", "value2", "value3"),
				namedEnumeratedProperty.getValues());
		assertArrayEquals(new String[]{ "Value One", "Value Two", "Value Three" },
				namedEnumeratedProperty.getValueNames());
	}

	@Test
	public void testValueChangesWithValueFilter() {
		TestValueFilter valueFilter1 = new TestValueFilter(List.of("value1"));

		namedEnumeratedProperty.addValueFilter(valueFilter1);
		assertEquals(-1, namedEnumeratedProperty.getIndex());
		assertEquals("value1", namedEnumeratedProperty.getValue());

		namedEnumeratedProperty.setIndex(0);
		assertEquals(0, namedEnumeratedProperty.getIndex());
		assertEquals("value2", namedEnumeratedProperty.getValue());

		namedEnumeratedProperty.setIndex(1);
		assertEquals(1, namedEnumeratedProperty.getIndex());
		assertEquals("value3", namedEnumeratedProperty.getValue());

		namedEnumeratedProperty.removeValueFilter(valueFilter1);
		assertEquals(2, namedEnumeratedProperty.getIndex());
		assertEquals("value3", namedEnumeratedProperty.getValue());
	}

	private static final class TestNamedEnumeratedProperty
			extends AbstractNamedEnumeratedProperty<String> {
		private String value;

		public TestNamedEnumeratedProperty(
				Localization localization,
				String name,
				List<Map.Entry<String, String>> namedValues) {
			super(localization, name);
			value = namedValues.get(0).getKey();
			setNamedValues(namedValues);
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

	private static final class TestValueFilter implements ValueFilter {
		private final List<String> filteredValues;

		public TestValueFilter(List<String> filteredValues) {
			this.filteredValues = filteredValues;
		}

		@Override
		public boolean isValueAllowed(Object value) {
			return !filteredValues.contains(value);
		}
	}
}
