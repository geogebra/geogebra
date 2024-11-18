package org.geogebra.common.kernel.commands;

import org.geogebra.common.main.PreviewFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CmdImplicitSurfaceTest extends BaseCommandTest {

	@BeforeClass
	public static void enablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterClass
	public static void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	public void testCommand() {
		t("ImplicitSurface[sin(x)+sin(y)+sin(z)]",
				"sin(x) + sin(y) + sin(z) = 0");
	}

	@Test
	public void testIntersect() {
		intersect("x^4+y^4+z^4=2", "x=y", false, "(-1, -1, 0)",
				"(1, 1, 0)");
	}
}
