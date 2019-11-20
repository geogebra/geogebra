package org.geogebra.common.io;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.AppCommon3D;
import org.junit.Assert;
import org.junit.Test;

public class EditorVectorTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
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
