package org.geogebra.common.gui.dialog.options.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class AbsoluteScreenPositionModelTest extends BaseUnitTest {

	@Test
	public void testDynamicPosition() {
		AbsoluteScreenPositionModel model = new AbsoluteScreenPositionModel.ForX(getApp());
		add("posx=200");
		GeoList drop = add("drop={1,2,3}");
		add("pic=ToolImage(42)");
		drop.setDrawAsComboBox(true);
		String[] def = new String[] {"Slider(-5,5,1)", "Checkbox()", "Button()", "InputBox()",
				"drop", "pic", "\"GeoGebra rocks\""};
		GeoElement[] geos = Arrays.stream(def).map(this::<GeoElement>add).toArray(GeoElement[]::new);
		((GeoText) lookup("text1")).setAbsoluteScreenLocActive(true);
		((GeoImage) lookup("pic")).setAbsoluteScreenLocActive(true);
		model.setGeos(geos);
		model.applyChanges("posx");
		for (GeoElement geo: geos) {
			geo.updateRepaint();
			assertThat(geo + " x-coordinate ", ((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX(), is(200));
		}
		add("SetValue(posx,300)");
		for (GeoElement geo: geos) {
			assertThat(((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX(), is(300));
		}
		reload();
		geos = getApp().getKernel().getConstruction().getGeoSetConstructionOrder()
				.stream().filter(geo -> !"posx".equals(geo.getLabelSimple()))
				.toArray(GeoElement[]::new);
		model.setGeos(geos);
		for (GeoElement geo: geos) {
			geo.updateRepaint();
			assertThat(geo + " x-coordinate ",
					((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX(), is(300));
		}
		add("SetValue(posx,400)");
		for (GeoElement geo: geos) {
			assertThat(geo + " x-coordinate ",
					((AbsoluteScreenLocateable)geo).getAbsoluteScreenLocX(), is(400));
		}
	}
}
