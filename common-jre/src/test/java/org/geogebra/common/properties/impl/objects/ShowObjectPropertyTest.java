package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class ShowObjectPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoNumeric slider = addAvInput("1");
		new ShowObjectProperty(slider);
	}

	@Test
	public void testSetValue() {
		GeoNumeric slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		slider.setEuclidianVisible(false);

		ShowObjectProperty showObjectProperty = new ShowObjectProperty(slider);
		MinProperty minProperty = new MinProperty(slider);
		ColorProperty colorProperty = new ColorProperty(slider);

		showObjectProperty.setValue(true);
		assertThat(showObjectProperty.isEnabled(), is(true));
		assertThat(minProperty.isEnabled(), is(true));
		assertThat(colorProperty.isEnabled(), is(true));

		showObjectProperty.setValue(false);
		assertThat(showObjectProperty.isEnabled(), is(true));
		assertThat(minProperty.isEnabled(), is(true));
		assertThat(colorProperty.isEnabled(), is(false));
	}
}