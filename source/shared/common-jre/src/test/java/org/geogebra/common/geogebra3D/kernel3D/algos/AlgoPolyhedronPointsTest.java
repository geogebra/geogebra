package org.geogebra.common.geogebra3D.kernel3D.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoPolyhedronPointsTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	@Issue("APPS-5577")
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

	@Test
	@Issue("APPS-6128")
	public void baseCopy() {
		add("n=3");
		add("vert=Sequence(Rotate[(0,3), ((t * 2) * pi / n), (0,0)],t,1,n)");
		add("polyBase=Polygon(vert)");
		GeoElement pyrBase = add("pyrBase=Translate(polyBase,Vector(2*(3,3)))");
		// similar to APPS-5577 testcase, but emulate file loading by passing (unused) labels
		List<String> labels = List.of("b", "B", "face8", "face10", "face11",
				"face12", "face13", "face14", "face9", "face16", "edge8", "edge9", "edge10",
				"edge11", "edge12", "edge13", "edge14", "edge17");
		Command cmd = new Command(getKernel(), "Pyramid", false);
		cmd.addArgument(pyrBase.wrap());
		cmd.addArgument(add("5").wrap());
		cmd.setLabels(labels.toArray(new String[0]));
		getAlgebraProcessor().processCommand(cmd, new EvalInfo(true));
		add("SetValue(n,5)");
		add("SetValue(n,0)");
		add("SetValue(n,4)");
		assertThat(lookup("b"), hasValue("30"));
	}
}
