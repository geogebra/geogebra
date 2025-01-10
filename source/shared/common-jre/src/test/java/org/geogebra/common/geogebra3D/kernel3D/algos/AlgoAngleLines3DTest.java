package org.geogebra.common.geogebra3D.kernel3D.algos;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.geogebra3D.euclidianFor3D.DrawAngleFor3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgoAngleLines3DTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void angleShouldWorkInIf() {
		t("a=If(true,Angle(xAxis,zAxis))", "90*" + Unicode.DEGREE_STRING);
		t("SetVisibleInView(a,1,true)");
		DrawableND drawable = getDrawable(lookup("a"));
		GGraphics2D g2 = mock(GGraphics2D.class);
		assertNotNull(drawable);
		((DrawAngleFor3D) drawable).draw(g2);
		Mockito.verifyNoInteractions(g2);
	}
}
