package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgoDynamicCoordinatesTest {

	static AppCommon app;
	static Construction cons;

	/**
	 * Setup the app
	 */
	@BeforeClass
	public static void setup() {
		app = AppCommonFactory.create();
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
				"DynamicCoordinates(J, x(K), y(K))", false);
		I.setCoords(10, -6, 1);
		cons.updateConstruction(false);
		assertTrue(J.isDefined());
		assertFalse(dynCoords[0].isDefined());
	}
}
