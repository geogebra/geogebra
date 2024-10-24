package org.geogebra.common.gui.dialog.options.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class AbsoluteScreenPositionModelTest extends BaseUnitTest {

	@Test
	public void testDynamicPosition() {
		AbsoluteScreenPositionModel model = new AbsoluteScreenPositionModel.ForX(getApp());
		GeoElement[] geos = prepareGeos();

		model.setGeos(geos);
		model.applyChanges("posx");
		for (GeoElement geo: geos) {
			geo.updateRepaint();
			assertThat(geo + " x-coordinate ",
					((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX(), is(200));
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
		assertAllHaveXCoord(geos, 300);

		add("SetValue(posx,400)");
		assertAllHaveXCoord(geos, 400);
	}

	private void assertAllHaveXCoord(GeoElement[] geos, int i) {
		for (GeoElement geo: geos) {
			geo.updateRepaint();
			assertThat(geo + " x-coordinate ",
					getScreenLocX((AbsoluteScreenLocateable) geo), is(i));
		}
	}

	@Test
	public void switchingToAbsShouldRemoveListeners() throws CircularDefinitionException {
		add("ZoomIn(0,0,16,12)");
		AbsoluteScreenPositionModel model = new AbsoluteScreenPositionModel.ForX(getApp());
		GeoElement[] geos = Arrays.stream(prepareGeos())
				.filter(g->!g.isGeoBoolean() && !g.isGeoList()).toArray(GeoElement[]::new);
		model.setGeos(geos);
		model.applyChanges("posx");
		AbsoluteScreenLocationModel absLocModel = new AbsoluteScreenLocationModel(getApp());
		absLocModel.setGeos(geos);
		assertAllHaveXCoord(geos, 200);
		absLocModel.applyChanges(false);
		add("SetValue(posx,300)");
		// pos change should no longer move the geos
		assertAllHaveXCoord(geos, 200);
		GeoPoint pt = add("(3,1)");
		for (GeoElement geo: geos) {
			((AbsoluteScreenLocateable) geo).setStartPoint(pt);
		}
		pt.updateRepaint();
		assertAllHaveXCoord(geos, 150);
		absLocModel.applyChanges(true);
		pt.setCoords(9, 1, 1);
		pt.updateRepaint();
		// should not change
		assertAllHaveXCoord(geos, 150);
	}

	@Test
	public void shouldSwitchFromDynamicToStatic() {
		GeoText txt = add("\"move me\"");
		add("a=42");
		txt.setAbsoluteScreenLocActive(true);
		AbsoluteScreenPositionModel model = new AbsoluteScreenPositionModel.ForX(getApp());
		model.setGeos(new GeoElement[]{txt});
		model.applyChanges("1+a");
		assertThat(txt.getStartPoint().getDefinition(StringTemplate.defaultTemplate),
				is("(1 + a, 0)"));
		model.applyChanges("50");
		assertThat(txt.getStartPoint(), nullValue());
		assertThat(txt.getAbsoluteScreenLocX(), is(50));
	}

	private Integer getScreenLocX(AbsoluteScreenLocateable geo) {
		return geo.isAbsoluteScreenLocActive() ? geo.getAbsoluteScreenLocX()
				: getApp().getActiveEuclidianView().toScreenCoordX(geo.getRealWorldLocX());
	}

	private GeoElement[] prepareGeos() {
		add("posx=200");
		GeoList drop = add("drop={1,2,3}");
		add("pic=ToolImage(42)");
		drop.setDrawAsComboBox(true);
		String[] def = new String[]{"Slider(-5,5,1)", "Checkbox()", "Button()", "InputBox()",
				"drop", "pic", "\"GeoGebra rocks\""};
		GeoElement[] geos = Arrays.stream(def).map(this::<GeoElement>add)
				.toArray(GeoElement[]::new);
		((GeoText) lookup("text1")).setAbsoluteScreenLocActive(true);
		((GeoImage) lookup("pic")).setAbsoluteScreenLocActive(true);
		return geos;
	}

}
