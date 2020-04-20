package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgoDynamicCoordinatesTest {

	static AppDNoGui app;
	static Construction cons;

	@BeforeClass
	public static void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		cons = app.getKernel().getConstruction();
	}

	@Test
	public void testDynamicCoordsCompute() {
		GeoPoint I = new GeoPoint(cons, "I", 6, -9, 1);
		GeoPoint J = new GeoPoint(cons, "J", 10, -6, 1);
		AlgoJoinPointsRay j = new AlgoJoinPointsRay(cons, "j", I, J);
		AlgoCirclePointRadius e = new AlgoCirclePointRadius(cons, I, new GeoNumeric(cons, 3));
		AlgoIntersectLineConic intersectionAlgo = new AlgoIntersectLineConic(cons, j.getRay(),
				e.getCircle());
		GeoPoint K = (GeoPoint) intersectionAlgo.getOutput(0);
		K.setLabel("K");
		GeoElementND[] dynCoords = cons.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"DynamicCoordinates(J,"
				+ " x"
				+ "(K), y(K))", false);
		I.setCoords(10, -6, 1);
		cons.updateConstruction(false);
		assertEquals(true, J.isDefined());
		assertEquals(false, dynCoords[0].isDefined());
	}
}
