package org.geogebra.common.kernel.geos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.test.UndoRedoTester;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class SliderTest extends BaseUnitTest {

	private EvalInfo info;

	@Before
	public void setUp() {
		info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		getApp().setConfig(new AppConfigUnrestrictedGraphing());
		getConstruction().getConstructionDefaults().createDefaultGeoElements();
	}

	@Test
	public void setShowExtendedAV() {
		GeoNumeric slider = add("a = 1", info);
		slider.setAVSliderOrCheckboxVisible(true);
		slider.initAlgebraSlider();
		slider.setAVSliderOrCheckboxVisible(false);
		assertThat(slider.showInEuclidianView(), is(true));
	}

	@Test
	public void testMarbleFunctionalityWithUndoRedo() {
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();

		GeoNumeric slider = add("a = 1", info);
		app.storeUndoInfo();
		slider.setAVSliderOrCheckboxVisible(true);
		slider.setEuclidianVisible(true);
		app.storeUndoInfo();
		slider.setAVSliderOrCheckboxVisible(false);
		app.storeUndoInfo();
		slider.setEuclidianVisible(false);
		app.storeUndoInfo();
		slider.setEuclidianVisible(true);
		app.storeUndoInfo();

		slider = undoRedo.getAfterUndo("a");
		assertThat(slider.isEuclidianVisible(), is(false));

		slider = undoRedo.getAfterUndo("a");
		assertThat(slider.isEuclidianVisible(), is(true));

		slider = undoRedo.getAfterUndo("a");
		assertThat(slider.isAVSliderOrCheckboxVisible(), is(true));

		slider = undoRedo.getAfterRedo("a");
		assertThat(slider.isAVSliderOrCheckboxVisible(), is(false));

		slider = undoRedo.getAfterRedo("a");
		assertThat(slider.isEuclidianVisible(), is(false));

		slider = undoRedo.getAfterRedo("a");
		assertThat(slider.isEuclidianVisible(), is(true));
	}

	@Test
	public void removeSlider() {
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();

		GeoNumeric slider = add("a = 1", info);
		app.storeUndoInfo();
		slider.createSlider();
		app.storeUndoInfo();
		slider.setEuclidianVisible(true);
		app.storeUndoInfo();
		new RemoveSlider(getAlgebraProcessor()).execute(slider);
		app.storeUndoInfo();
		assertThat(slider.isSetEuclidianVisible(), is(false));

		slider = undoRedo.getAfterUndo("a");
		assertThat(slider.isEuclidianVisible(), is(true));
	}

	@Test
	public void autocreateSliderShouldHaveCorrectRangeGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		getConstruction().getConstructionDefaults().createDefaultGeoElements();
		GeoAngle slider = autocreateAngle();
		assertThat(slider.getAngleStyle(), is(GeoAngle.AngleStyle.NOTREFLEX));
		assertThat(slider.getIntervalMax(), is(Math.PI));
	}

	@Test
	public void autocreateSliderShouldHaveCorrectRangeGraphing() {
		GeoAngle slider = autocreateAngle();
		assertThat(slider.getAngleStyle(), is(GeoAngle.AngleStyle.ANTICLOCKWISE));
		assertThat(slider.getIntervalMax(), is(Kernel.PI_2));
	}

	@Test
	public void autocreateSliderShouldWorkForSingleLetterCommands() {
		GeoFunctionNVar f = add("f(x,y)=ax^2+bx+c(x+3)", info);
		assertThat(((GeoNumeric) lookup("a")).isSlider(), equalTo(true));
		assertThat(((GeoNumeric) lookup("b")).isSlider(), equalTo(true));
		assertThat(((GeoNumeric) lookup("c")).isSlider(), equalTo(true));
		assertThat(f, hasValue(unicode("1 x^2 + 1 x + 1 (x + 3)")));
	}

	@Test
	@Issue("APPS-6015")
	public void autocreateSliderShouldNotCreateAnythingOnError() {
		assertThrows(AssertionError.class, () -> add("f(x)=f(x)+1", info));
		assertThrows(AssertionError.class, () -> add("g(x)=g(x)+a", info));
		assertEquals(0, getConstruction().getGeoSetConstructionOrder().size());
	}

	private GeoAngle autocreateAngle() {
		add("A=(0,0)");
		add("B=(1,0)");
		add("Rotate(A,a,B)", info);
		return (GeoAngle) lookup("a");
	}
}