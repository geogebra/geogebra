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
		property.setFrozen(true);
		assertTrue(property.isFrozen());
		property.setFrozen(false);
		assertFalse(property.isFrozen());
	}

	private static class DummyAbstractProperty extends AbstractProperty {

		public DummyAbstractProperty(Localization localization, String name) {
			super(localization, name);
		}
	}
}
