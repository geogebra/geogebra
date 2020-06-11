package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class ShowObjectPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoNumeric slider = addAvInput("1");
		try {
			new ShowObjectProperty(slider);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetValue() {
		GeoNumeric slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		slider.setEuclidianVisible(false);

		ShowObjectProperty showObjectProperty = null;
		MinProperty minProperty = null;
		ColorProperty colorProperty = null;
		try {
			showObjectProperty = new ShowObjectProperty(slider);
			minProperty = new MinProperty(slider);
			colorProperty = new ColorProperty(slider);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}

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