package org.geogebra.common.gui.dialog.options;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.dialog.options.model.VectorHeadStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.EventAccumulator;
import org.junit.Before;
import org.junit.Test;

public class VectorHeadStyleTest extends BaseUnitTest {

	private VectorHeadStyleModel model;
	private GeoElement vector;

	@Before
	public void setupModel() {
		model = new VectorHeadStyleModel(getApp());
		vector = add("v=(1,1)");
		GeoElement[] geos = new GeoElement[]{vector};
		model.setGeos(geos);
	}

	@Test
	public void shouldUpdateProperties() {
		EventAccumulator listener = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		model.apply(0, 1);
		assertEquals(Collections.singletonList("UPDATE_STYLE v"), listener.getEvents());
	}
}
