package org.geogebra.common.properties.factory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;
import org.junit.Test;

public class GeoElementPropertiesFactoryTest extends BaseUnitTest {

	@Test
	public void testPoint() {
		GeoPoint zeroPoint = addAvInput("(0,0)");
		GeoPoint onePoint = addAvInput("(1,1)");
		List<GeoElement> points = new ArrayList<>();
		points.add(zeroPoint);
		points.add(onePoint);
		PropertiesArray propertiesArray = new GeoElementPropertiesFactory()
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

	@Test
	public void testEquationFormProperty() {
		GeoElementPropertiesFactory propertiesFactory = new GeoElementPropertiesFactory();

		GeoLine line = addAvInput("Line((-1,-1),(1,2))");
		PropertiesArray lineProperties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getLocalization(), List.of(line));
		assertTrue(containsLinearEquationFormProperty(lineProperties));
		assertFalse(containsQuadraticEquationFormProperty(lineProperties));

		GeoConic circle = addAvInput("xx+yy=1");
		PropertiesArray circleProperties = propertiesFactory.createGeoElementProperties(
				getAlgebraProcessor(), getLocalization(), List.of(circle));
		assertFalse(containsLinearEquationFormProperty(circleProperties));
		assertTrue(containsQuadraticEquationFormProperty(circleProperties));
	}

	private boolean containsLinearEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.filter(property ->
						property instanceof NamedEnumeratedPropertyCollection<?, ?>
								&& ((NamedEnumeratedPropertyCollection<?, ?>) property)
								.getProperties()[0] instanceof LinearEquationFormProperty)
				.findAny().isPresent();
	}

	private boolean containsQuadraticEquationFormProperty(PropertiesArray array) {
		return Arrays.stream(array.getProperties())
				.filter(property ->
						property instanceof NamedEnumeratedPropertyCollection<?, ?>
								&& ((NamedEnumeratedPropertyCollection<?, ?>) property)
								.getProperties()[0] instanceof QuadraticEquationFormProperty)
				.findAny().isPresent();
	}
}