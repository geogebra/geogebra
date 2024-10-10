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
