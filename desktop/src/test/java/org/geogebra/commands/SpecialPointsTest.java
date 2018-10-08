package org.geogebra.commands;

import org.geogebra.desktop.main.AppDNoGui;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpecialPointsTest {

	private static AppDNoGui app;

	@BeforeClass
	public static void setup() {
		app = CommandsTest.createApp();
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
