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
