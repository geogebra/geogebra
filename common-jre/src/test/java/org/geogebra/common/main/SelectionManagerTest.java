package org.geogebra.common.main;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Assert;
import org.junit.Test;

public class SelectionManagerTest extends BaseUnitTest {

	@Test
	public void hasNextShouldSkipInvisibleGeos() {
		getApp().getGgbApi().setPerspective("G");
		GeoElement first = add("A:(1,1)");
		GeoElement second = add("B:(2,1)");
		GeoElement hidden = add("C:(3,1)");
		hidden.setEuclidianVisible(false);
		GeoElement notSelectable = add("D:(4,1)");
		notSelectable.setSelectionAllowed(false);

		Assert.assertTrue(getSelection().hasNext(first));
		Assert.assertFalse(getSelection().hasNext(second));
	}

	@Test
	public void selectNextShouldSkipInvisibleGeos() {
		getApp().getGgbApi().setPerspective("G");
		GeoElement first = add("A:(1,1)");
		GeoElement second = add("B:(2,1)");
		GeoElement hidden = add("C:(3,1)");
		hidden.setEuclidianVisible(false);
		GeoElement notSelectable = add("D:(4,1)");
		notSelectable.setSelectionAllowed(false);

		getSelection().setSelectedGeos(null);
		getSelection().addSelectedGeo(first);
		// next jumps to second
		getSelection().selectNextGeo(getApp().getEuclidianView1());
		Assert.assertTrue(second.isSelected());
		// next jumps bacck to first
		getSelection().selectNextGeo(getApp().getEuclidianView1());
		Assert.assertTrue(first.isSelected());
	}

	private SelectionManager getSelection() {
		return getApp().getSelectionManager();
	}

	private GeoElement add(String string) {
		GeoElementND[] ret = getApp().getKernel().getAlgebraProcessor()
				.processAlgebraCommand(string, false);
		return ret[0].toGeoElement();
	}
}
