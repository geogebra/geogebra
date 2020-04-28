package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.settings.AppConfigCas;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeoNumericTest extends BaseUnitTest {

	@Test
	public void euclidianShowabilityOfOperationResult() {
		GeoNumeric numeric = addAvInput("4+6");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}

	@Test
	public void testNumericIsNotDrawableInCas() {
		getApp().setConfig(new AppConfigCas());
		GeoNumeric numeric = addAvInput("2");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}

	@Test
	public void testSliderIsVisibleInEv() {
		GeoNumeric numeric = new GeoNumeric(getConstruction());
		GeoNumeric.setSliderFromDefault(numeric, false);
		assertThat(numeric.isEuclidianShowable(), is(true));
	}
}