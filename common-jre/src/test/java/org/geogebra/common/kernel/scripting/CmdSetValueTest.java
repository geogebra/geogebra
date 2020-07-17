package org.geogebra.common.kernel.scripting;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Assert;
import org.junit.Test;

public class CmdSetValueTest extends BaseUnitTest {

	@Test
	public void setValueShouldMakeImplicitCurvesUndefined() {
		GeoImplicitCurve curve = add("eq1:x^2=y^3");
		add("SetValue(eq1,?)");
		Assert.assertEquals("?", curve.toValueString(StringTemplate.defaultTemplate));
		getApp().setXML(getApp().getXML(), true);
		GeoElement curveReloaded = lookup("eq1");
		Assert.assertEquals("?", curveReloaded.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void setValueShouldKeepImplicitCurveFormInXML() {
		GeoImplicitCurve curve = add("eq1:x^2=y^3");
		add("SetValue(eq1,?x^2=?y^3)");
		String curveXMLform = "(NaN * x^(2)) = (NaN * y^(3))";
		assertThat(curve.getXML(), containsString(curveXMLform));
		getApp().setXML(getApp().getXML(), true);
		GeoElement curveReloaded = lookup("eq1");
		assertThat(curveReloaded.getXML(), containsString(curveXMLform));
	}
}
