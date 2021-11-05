package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.junit.Assert;
import org.junit.Test;

public class EditorVectorTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void testSimpleVector3D() {
		add("u: (1, 2, 3)");
		add("ib = InputBox(u)");
		GeoInputBox inputBox = (GeoInputBox) lookup("ib");
		inputBox.setSymbolicMode(true, false);
		inputBox.updateRepaint();
		Assert.assertEquals("{{1}, {2}, {3}}", inputBox.getTextForEditor());
	}
}
