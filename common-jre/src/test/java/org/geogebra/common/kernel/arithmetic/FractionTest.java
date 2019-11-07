package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.TestErrorHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FractionTest {

	private App app;

	@Before
	public void setup() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	@Test
	public void functionWithFractions() {
		t("frac(x)=(3/2)^x", "(3 / 2)^(x)");
		t("frac(2)", "9 / 4");
		t("frac(-1)", "2 / 3");
		t("frac(-2)", "4 / 9");
	}

	@Test
	public void scientificNotation() {
		t("5*10^(-2)", "1 / 20");
	}

	private void t(String string, String string2) {
		GeoElementND[] geos = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(string, false,
						TestErrorHandler.INSTANCE,
						new EvalInfo(true).withFractions(true), null);
		Assert.assertEquals(string2,
				geos[0].toValueString(StringTemplate.testTemplate));
	}
}
