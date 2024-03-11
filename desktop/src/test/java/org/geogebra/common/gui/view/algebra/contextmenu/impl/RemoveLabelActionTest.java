package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class RemoveLabelActionTest extends BaseSymbolicTest {

	@Test
	public void shouldForceLabelsForSliders() {
		GeoElement integral = add("Integral(sin(x))");
		GeoNumeric constant = (GeoNumeric) lookup("c_{1}");
		constant.initAlgebraSlider();
		RemoveLabelAction removeLabel = new RemoveLabelAction();
		assertFalse("Remove label should not be available for sliders in CAS",
				removeLabel.isAvailable(constant));
		assertTrue("Remove label should be available for integral in CAS",
				removeLabel.isAvailable(integral));
	}
}
