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

package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.restrictions.PropertyRestriction;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyRestrictionTests extends BaseAppTestSetup {
    private TestEnumeratedProperty enumeratedProperty;

	@BeforeEach
	void setup() {
        setupApp(SuiteSubApp.GRAPHING);
        enumeratedProperty = new TestEnumeratedProperty(getLocalization(),
                "Test property",
                List.of("value1", "value2", "value3"));
    }

	@Test
	void testRestrictionWithPropertyFreeze() {
        PropertyRestriction propertyRestriction = new PropertyRestriction(true, null);

        propertyRestriction.applyTo(enumeratedProperty);
        assertTrue(enumeratedProperty.isFrozen());

        propertyRestriction.removeFrom(enumeratedProperty);
        assertFalse(enumeratedProperty.isFrozen());
    }

	@Test
	void testRestrictionWithValueFilter() {
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
        TestEnumeratedProperty(Localization localization, String name, List<String> values) {
            super(localization, name);
            setValues(values);
        }

        @Override
        protected void doSetValue(String value) {
            // always ""
        }

        @Override
        public String getValue() {
            return "";
        }
    }
}
