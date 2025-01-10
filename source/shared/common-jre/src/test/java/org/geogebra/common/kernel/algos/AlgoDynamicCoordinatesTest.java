package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Test;

public class AlgoDynamicCoordinatesTest extends BaseUnitTest  {

	@Test
	public void testDynamicCoordsCompute() {
		Construction cons = getConstruction();
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
		assertThat(J, isDefined());
		assertThat(dynCoords[0], not(isDefined()));
	}
}
