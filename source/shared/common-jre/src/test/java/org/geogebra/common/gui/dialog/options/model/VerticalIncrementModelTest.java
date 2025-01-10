package org.geogebra.common.gui.dialog.options.model;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class VerticalIncrementModelTest extends BaseUnitTest {

	@Test
	public void shouldChangeStep() {
		VerticalIncrementModel model = new VerticalIncrementModel(getApp());
		GeoPoint pt = add("A=(1,2)");
		model.setGeos(new GeoElement[]{pt});
		model.applyChanges("3+4");
		assertThat(pt.getVerticalIncrement(), hasValue("7"));
		reload();
		assertThat(((GeoPoint) lookup("A")).getVerticalIncrement(), hasValue("7"));
	}
}
