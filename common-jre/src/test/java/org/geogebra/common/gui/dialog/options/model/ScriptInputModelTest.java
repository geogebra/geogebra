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
		GeoElement pt = add("Pt=(1,1)");
		List<String> available = getAvailable(pt);
		assertEquals(Arrays.asList("OnClick", "OnUpdate", "OnDragEnd", "GlobalJavaScript"),
				available);
		GeoElement input = add("InputBox(Pt)");
		available = getAvailable(input);
		assertEquals(Arrays.asList("OnClick", "OnUpdate", "OnChange", "GlobalJavaScript"),
				available);
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