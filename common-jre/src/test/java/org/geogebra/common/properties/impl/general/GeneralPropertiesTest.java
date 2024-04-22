package org.geogebra.common.properties.impl.general;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Locale;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.test.TestStringUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeneralPropertiesTest extends BaseUnitTest {

	private void t(String s) {
		getKernel().getAlgebraProcessor().processAlgebraCommand(s, true);
	}

	@Before
	public void clean() {
		getKernel().clearConstruction(true);
		getApp().setRounding("2");
		getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
	}

	@Test
	public void roundingShouldUpdateAV() {
		RoundingIndexProperty rp = new RoundingIndexProperty(getApp(), getLocalization());
		t("a=1/3");
		t("b=4*a");
		valueTextShouldBe("a", "a = 0.33");
		valueTextShouldBe("b", "b = 1.33");
		rp.setIndex(5);
		valueTextShouldBe("a", "a = 0.33333");
		valueTextShouldBe("b", "b = 1.33333");
	}

	@Test
	public void roundingShouldBeLocalized() {
		RoundingIndexProperty rp = new RoundingIndexProperty(getApp(), getLocalization());
		assertThat(rp.getValueNames()[0], equalTo("0 Decimal Place"));
		getApp().setLocale(new Locale("de"));
		assertThat(rp.getValueNames()[0], equalTo("0 Dezimalstelle"));
	}

	@Test
	public void angleUnitShouldUpdateAV() {
		AngleUnitProperty rp = new AngleUnitProperty(getKernel(), getLocalization());
		t("a=90deg");
		t("b=Angle(xAxis,yAxis)");
		valueTextShouldBe("a", "a = 90deg");
		valueTextShouldBe("b", "b = 90deg");
		rp.setIndex(1);
		valueTextShouldBe("a", "a = 1.57 rad");
		valueTextShouldBe("b", "b = 1.57 rad");
	}

	private void valueTextShouldBe(String label, String expectedValue) {
		Assert.assertEquals(TestStringUtil.unicode(expectedValue),
				lookup(label).getAlgebraDescriptionDefault());
	}
}
