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

package org.geogebra.common.gui.dialog.options.model;

import static org.geogebra.common.kernel.geos.GeoInputBox.isGeoLinkable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class ButtonDialogModelTest extends BaseUnitTest {

	@Test
	public void testLinkableGeos() {
		shouldBeLinkable("2");
		shouldBeLinkable("2x+3/6+sin(x)");
		shouldBeLinkable("(1,2)");
	}

	@Test
	public void testNonLinkableGeos() {
		shouldNotBeLinkable("Sequence[500]");
		shouldNotBeLinkable("Point[{1,2}]");
		shouldNotBeLinkable("Line[(1,2), (3,4)]");
		shouldNotBeLinkable("Mean[{1,2,3,4,5}]");
	}

	private void shouldBeLinkable(String definition) {
		assertTrue(isGeoLinkable(add(definition)));
	}

	private void shouldNotBeLinkable(String definition) {
		assertFalse(isGeoLinkable(add(definition)));
	}
}
