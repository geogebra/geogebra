package org.geogebra.common.geogebra3D.kernel3D.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;

public class AlgoPolyhedronPointsTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void sequencePyramid() {
		for (int initialSides: Arrays.asList(3, 5)) {
			getKernel().clearConstruction(false);
			add("sidesNum=" + initialSides);
			add("basePri=Polygon(Sequence[Rotate[(1,0,0), (k * 2pi / sidesNum),"
					+ " (-2, 0, 0)], k, 1, sidesNum])");
			add("basePir=Translate(basePri,(1,0,0))");
			add("pyramid=Pyramid(basePir,5)");
			add("prism=Prism(basePir,5)");
			add("SetValue(sidesNum,4)");
			assertThat(lookup("pyramid"), hasValue("30"));
			assertThat(lookup("prism"), hasValue("90"));
		}
	}
}
