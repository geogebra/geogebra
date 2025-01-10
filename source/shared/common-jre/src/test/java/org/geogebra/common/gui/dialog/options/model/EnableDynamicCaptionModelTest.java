package org.geogebra.common.gui.dialog.options.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.EventAccumulator;
import org.junit.Before;
import org.junit.Test;

public class EnableDynamicCaptionModelTest extends BaseUnitTest {

	private EnableDynamicCaptionModel model;
	private GeoElement point;

	@Before
	public void setupModel() {
		model = new EnableDynamicCaptionModel(null, getApp());
		point = add("A=(1,1)");
		GeoElement[] geos = new GeoElement[]{point};
		model.setGeos(geos);
	}

	@Test
	public void shouldUpdateProperties() {
		EventAccumulator listener = new EventAccumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		model.apply(0, true);
		assertEquals(Collections.singletonList("UPDATE_STYLE A"), listener.getEvents());
	}

	@Test
	public void checkShouldSetEmptyString() {
		model.apply(0, true);
		assertThat(point.getDynamicCaption(), notNullValue());
		assertThat(point.getDynamicCaption(), hasValue(""));
	}

	@Test
	public void uncheckShouldSetNull() {
		model.apply(0, false);
		assertThat(point.getDynamicCaption(), nullValue());
	}
}
