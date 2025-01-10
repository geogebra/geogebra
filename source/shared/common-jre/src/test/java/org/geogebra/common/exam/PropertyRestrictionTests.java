package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.exam.restrictions.PropertyRestriction;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.junit.Test;

public class PropertyRestrictionTests extends BaseUnitTest {
    private TestEnumeratedProperty enumeratedProperty;

    @Override
    public void setup() {
        super.setup();
        enumeratedProperty = new TestEnumeratedProperty(getLocalization(),
                "Test property",
                List.of("value1", "value2", "value3"));
    }

    @Test
    public void testRestrictionWithPropertyFreeze() {
        PropertyRestriction propertyRestriction = new PropertyRestriction(true, null);

        propertyRestriction.applyTo(enumeratedProperty);
        assertTrue(enumeratedProperty.isFrozen());

        propertyRestriction.removeFrom(enumeratedProperty);
        assertFalse(enumeratedProperty.isFrozen());
    }

    @Test
    public void testRestrictionWithValueFilter() {
        PropertyRestriction propertyRestriction1 =
                new PropertyRestriction(false, value -> value != "value1");
        PropertyRestriction propertyRestriction2 =
                new PropertyRestriction(false, value -> value != "value2");

        propertyRestriction1.applyTo(enumeratedProperty);
        assertEquals(List.of("value2", "value3"), enumeratedProperty.getValues());

        propertyRestriction2.applyTo(enumeratedProperty);
        assertEquals(List.of("value3"), enumeratedProperty.getValues());

        propertyRestriction1.removeFrom(enumeratedProperty);
        assertEquals(List.of("value1", "value3"), enumeratedProperty.getValues());

        propertyRestriction2.removeFrom(enumeratedProperty);
        assertEquals(List.of("value1", "value2", "value3"), enumeratedProperty.getValues());
    }

    private static final class TestEnumeratedProperty extends AbstractEnumeratedProperty<String> {
        public TestEnumeratedProperty(Localization localization, String name, List<String> values) {
            super(localization, name);
            setValues(values);
        }

        @Override
        protected void doSetValue(String value) {
        }

        @Override
        public String getValue() {
            return "";
        }
    }
}
