package org.geogebra.common.gui.dialog.options.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class ScriptInputModelTest extends BaseUnitTest {

	@Test
	public void availabilityTest() {
		List<String> standardOptionsWithDrag =
				Arrays.asList("OnClick", "OnUpdate", "OnDragEnd", "GlobalJavaScript");

		GeoElement pt = add("Pt=(1,1)");
		assertEquals(standardOptionsWithDrag, getAvailable(pt));

		GeoElement poly = add("Polygon(Pt,(1,2),(2,1))");
		assertEquals(standardOptionsWithDrag, getAvailable(poly));

		GeoElement polyRegular = add("Polygon((1,2),(2,1),5)");
		assertEquals(standardOptionsWithDrag, getAvailable(polyRegular));

		GeoElement stroke = add("PenStroke((1,2),(2,1))");
		assertEquals(standardOptionsWithDrag, getAvailable(stroke));

		GeoElement circle = add("Circle((0,1),(1,2))");
		assertEquals(standardOptionsWithDrag, getAvailable(circle));

		GeoElement circleF = add("x^2+y^2=1");
		assertEquals(Arrays.asList("OnClick", "OnUpdate", "GlobalJavaScript"),
				getAvailable(circleF));

		GeoElement input = add("InputBox(Pt)");
		input.setFixed(true);
		assertEquals(Arrays.asList("OnClick", "OnUpdate", "OnChange", "GlobalJavaScript"),
				getAvailable(input));
	}

	private List<String> getAvailable(GeoElement pt) {
		ScriptInputModel[] models = ScriptInputModel.getModels(getApp());
		return Arrays.stream(models).filter(m ->{
					m.setGeos(new GeoElement[]{pt});
					return m.checkGeos();
				}).map(ScriptInputModel::getTitle)
				.collect(Collectors.toList());
	}
}