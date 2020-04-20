package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.kernel.advanced.AlgoDynamicCoordinates;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class CoordsTest {

	static AppDNoGui app;
	static Construction cons;

	@BeforeClass
	public static void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		cons = app.getKernel().getConstruction();
	}

	@Test
	public void testProduct() {
		Coords v1 = new Coords(2);
		v1.val[0] = 3.0;
		v1.val[1] = 4.0;

		assertEquals(v1.dotproduct(v1), 25, 1E-8);
	}

	@Test
	public void testToString() {
		FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
		Coords v1 = new Coords(4);
		v1.set(.5, .31, -.17);
		assertEquals(v1.toString(2), "(+0.50  +0.31  -0.17  +0.00)");
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
		AlgoDynamicCoordinates dynCoordAlgo = new AlgoDynamicCoordinates(cons, "L", J,
				new GeoNumeric(cons, K.getCoords().getX()), new GeoNumeric(cons,
				K.getCoords().getY()));
		I.setCoords(10, -6, 1);
		cons.updateConstruction(false);
		assertEquals(true, J.isDefined());
	}
}
