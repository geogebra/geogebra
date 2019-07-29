package org.geogebra.common.kernel.commands;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpecialPointsTest {

	private static AppCommon app;

	@BeforeClass
	public static void setup() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		app.setConfig(new AppConfigGraphing());
	}

	@Before
	public void clear() {
		app.getKernel().clearConstruction(true);
	}

	@Test
	public void specialPointsForPolynomials() {
		t("f(x)=x^3-x");
		updateSpecialPoints("f");
		Assert.assertEquals(6, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsForSegment() {
		t("s:Segment((-1,-1),(1,1))");
		updateSpecialPoints("s");
		Assert.assertEquals(0, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsForTrig() {
		t("ZoomIn(-4pi-1,-2,4pi+1,2)");
		t("f(x)=sin(x)");
		updateSpecialPoints("f");
		Assert.assertEquals(18, numberOfSpecialPoints());
	}

	@Test
	public void specialPointForLines() {
		t("f:x=2+y");
		t("g:x=2-y");
		t("c:xx+yy=10");
		updateSpecialPoints("f");
		Assert.assertEquals(5, numberOfSpecialPoints());
		updateSpecialPoints("g");
		Assert.assertEquals(5, numberOfSpecialPoints());
	}

	@Test
	public void specialPointForConics() {
		t("f:y=x^2-6x+8");
		updateSpecialPoints("f");
		// 4 visible, 1 undefined
		Assert.assertEquals(5, numberOfSpecialPoints());
	}

	@Test
	public void specialPointsRedefine() {
		t("f(x)=x^2");
		updateSpecialPoints("f");
		t("a=1");
		t("f(x)=x^2+a");
		updateSpecialPoints("f");
		Assert.assertEquals(3, numberOfSpecialPoints());
	}

	private static int numberOfSpecialPoints() {
		if (app.getSpecialPointsManager().getSelectedPreviewPoints() == null) {
			return 0;
		}
		return app.getSpecialPointsManager().getSelectedPreviewPoints().size();
	}

	private static void updateSpecialPoints(String string) {
		app.getSpecialPointsManager()
				.updateSpecialPoints(app.getKernel().lookupLabel(string));
	}

	private static void t(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				true);
	}
}
