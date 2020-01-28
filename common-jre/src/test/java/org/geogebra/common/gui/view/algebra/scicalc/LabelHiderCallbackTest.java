package org.geogebra.common.gui.view.algebra.scicalc;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LabelHiderCallbackTest extends BaseUnitTest {

	private LabelHiderCallback callback;
	private LabelController labelController;

	@Before
	public void setUp() {
		callback = new LabelHiderCallback();
		labelController = new LabelController();
	}

	@Test
	public void testCallbackHidesLabels() {
		String[] inputs = {"1", "x^2", "Cross((1,2), (3,4))", "y=1", "x^2 + y^2 = 5"};
		for (String input: inputs) {
			GeoElement element = (GeoElement) getElementFactory().create(input);
			Assert.assertTrue(labelController.hasLabel(element));
			callback.callback(new GeoElement[] { element });
			Assert.assertFalse(labelController.hasLabel(element));
		}
	}

	@Test
	public void testCallbackDoesNotHideSliderLabels() {
		GeoElement element = (GeoElement) getElementFactory().create("Slider(2, 20)");
		callback.callback(new GeoElement[] { element });
		Assert.assertTrue(labelController.hasLabel(element));
	}
}
