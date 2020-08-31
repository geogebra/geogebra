package org.geogebra.common.properties.factory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.Property;
import org.junit.Test;

public class GeoElementPropertiesFactoryTest extends BaseUnitTest {

	@Test
	public void testPoint() {
		GeoPoint zeroPoint = addAvInput("(0,0)");
		GeoPoint onePoint = addAvInput("(1,1)");
		List<GeoElement> points = new ArrayList<>();
		points.add(zeroPoint);
		points.add(onePoint);
		PropertiesArray propertiesArray = GeoElementPropertiesFactory
				.createGeoElementProperties(getKernel().getAlgebraProcessor(),
						getApp().getLocalization(), points);
		Property[] pointProperties = propertiesArray.getProperties();

		assertThat(pointProperties[0].getName(), equalTo("Name"));
		assertThat(pointProperties[1].getName(), equalTo("Show"));
		assertThat(pointProperties[2].getName(), equalTo("Set color"));
		assertThat(pointProperties[3].getName(), equalTo("Style"));
		assertThat(pointProperties[4].getName(), equalTo("Size"));
		assertThat(pointProperties[5].getName(), equalTo("Set caption style"));
		assertThat(pointProperties[6].getName(), equalTo("Show trace"));
		assertThat(pointProperties[7].getName(), equalTo("Fixed"));
		assertThat(pointProperties[8].getName(), equalTo("Show in Algebra View"));
	}
}