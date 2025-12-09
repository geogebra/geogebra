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

package org.geogebra.web.full.gui.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class MaterialVisibilityPropertyTest {

	private AppWFull app;
	private MaterialVisibilityProperty property;

	@Before
	public void setUp() {
		app = AppMocker.mockGraphing();
		property = new MaterialVisibilityProperty(app.getLocalization());
	}

	@Test
	public void testUpdateVisibility() {
		checkVisibilityIndex(MaterialVisibility.Private, 0);
		checkVisibilityIndex(MaterialVisibility.Shared, 1);
		checkVisibilityIndex(MaterialVisibility.Public, 2);
	}

	private void checkVisibilityIndex(MaterialVisibility visibility, int index) {
		property.update(visibility);
		assertEquals(index, property.getIndex());
	}

	@Test
	public void testValuesPrivate() {
		property.update(MaterialVisibility.Private);
		assertArrayEquals(new String[]{"Private", "Shared"}, property.getValueNames());
	}

	@Test
	public void testValuesShared() {
		property.update(MaterialVisibility.Shared);
		assertArrayEquals(new String[]{"Private", "Shared"}, property.getValueNames());
	}

	@Test
	public void testValuesPublic() {
		property.update(MaterialVisibility.Public);
		assertArrayEquals(new String[]{"Private", "Shared", "Public"}, property.getValueNames());
	}
}
