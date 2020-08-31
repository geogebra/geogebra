package org.geogebra.common.properties.impl.objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class ShowInAVPropertyTest extends BaseUnitTest {

	@Test
	public void testIsEnabledWhenHiddenInGraphics() {
		GeoElement element = getElementFactory().createGeoLine();
		element.setEuclidianVisible(false);

		ShowInAVProperty property = new ShowInAVProperty(getLocalization(), element);
		assertThat(property.isEnabled(), is(true));
	}
}