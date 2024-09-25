package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.algos.AlgoJoinPointsRay;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoParabolaPointLine;
import org.geogebra.common.kernel.algos.AlgoRayPointVector;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.statistics.AlgoFitLineX;
import org.geogebra.common.kernel.statistics.AlgoFitLineY;
import org.junit.Test;

/**
 * @implNote This partially overlaps and supercedes
 * {@link org.geogebra.common.kernel.geos.ForceInputFormTest ForceInputFormTest}
 *
 * @See <a href="https://docs.google.com/spreadsheets/d/1nL071WJP2qu-n1LafYGLoKbcrz486PTYd8q7KgnvGhA/edit?gid=1442218852#gid=1442218852">Equation form matrix</a>
 * @See <a href="https://geogebra-jira.atlassian.net/wiki/spaces/A/pages/836141057/Standalone+Graphing">Standalone Graphing Wiki</a>
 */
public class EquationBehaviourTest extends BaseUnitTest {

	// Standalone Graphing

	@Test
	public void testStandaloneGraphingLineEquationBehaviour() {
		getApp().setGraphingConfig();

		// Line created from equation
		GeoLine algebraLine = getElementFactory().createGeoLine(); // "x = y"
		assertEquals(GeoLine.EQUATION_USER, algebraLine.getEquationForm());

		// Line created with Line command from two points
		GeoPoint a = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint b = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), a, b);
		GeoLine lineTwoPoints = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_EXPLICIT, lineTwoPoints.getEquationForm());

		// Line created with FitLineX/Y command
		GeoList points = new GeoList(getConstruction());
		points.add(a);
		points.add(b);
		AlgoFitLineX algoFitLineX = new AlgoFitLineX(getConstruction(), points);
		GeoLine fitLineX = algoFitLineX.getFitLineX();
		assertEquals(GeoLine.EQUATION_EXPLICIT, fitLineX.getEquationForm());

		AlgoFitLineY algoFitLineY = new AlgoFitLineY(getConstruction(), points);
		GeoLine fitLineY = algoFitLineY.getFitLineY();
		assertEquals(GeoLine.EQUATION_EXPLICIT, fitLineY.getEquationForm());
	}

	@Test
	public void testStandaloneGraphingRayEquationBehaviour() {
		getApp().setGraphingConfig();

		// Ray created with Ray command from two points
		GeoPoint a = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint b = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPointsRay algoJoinPointsRay = new AlgoJoinPointsRay(getConstruction(), "ray", a, b);
		GeoRay ray1 = algoJoinPointsRay.getRay();
		assertEquals(GeoLine.EQUATION_USER, ray1.getEquationForm());

		// Ray created with Ray command from point and vector
		GeoVector vec = new GeoVector(getConstruction(), "vec", 1, 1, 0);
		AlgoRayPointVector algoRayPointVector = new AlgoRayPointVector(getConstruction(), a, vec);
		GeoRay ray2 = algoRayPointVector.getRay();
		assertEquals(GeoLine.EQUATION_USER, ray2.getEquationForm());
	}

	// no test for Segment command in standalone Graphing (disabled)

	@Test
	public void testStandaloneGraphingConicEquationBehaviour() {
		getApp().setGraphingConfig();

		// Parabola created from equation
		GeoConic algebraParabola = (GeoConic) getElementFactory().create("y=xx");
		assertEquals(GeoConicND.EQUATION_USER, algebraParabola.getEquationForm());

		// Parbola command disabled in standalone Graphing
	}

	@Test
	public void testStandaloneGraphingLineEquationBehaviourWithCustomizedConstructionDefaults() {
		getApp().setGraphingConfig();

		// change the equation form for lines in the construction defaults
		GeoLine constructionDefaultsLine = (GeoLine) getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		constructionDefaultsLine.setEquationForm(GeoLine.EQUATION_GENERAL);

		GeoLine algebraLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_GENERAL, algebraLine.getEquationForm());

		// check if the Graphing equation forms are still satisfied (i.e., overriding the changes
		// to the construction defaults above)
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_EXPLICIT, toolLine.getEquationForm());
	}

	// Unrestricted Graphing (Suite)

	@Test
	public void testUnrestrictedGraphingConicEquationBehaviour() {
		getApp().setGraphingConfig();

		// Parabola created from equation
		GeoConic algebraParabola = (GeoConic) getElementFactory().create("y=xx");
		assertEquals(GeoConicND.EQUATION_USER, algebraParabola.getEquationForm());

		// Parabola created from point and line
		GeoPoint center = new GeoPoint(getConstruction(), 0, 1, 0);
		GeoPoint a = new GeoPoint(getConstruction(), -1, 0, 0);
		GeoPoint b = new GeoPoint(getConstruction(), 1, 0, 0);
		GeoLine line = lineThrough(a, b);
		AlgoParabolaPointLine algoParabola = new AlgoParabolaPointLine(getConstruction(),
				"parabola", center, line);
		GeoConicND parabola = algoParabola.getParabola();
		assertEquals(GeoConic.EQUATION_IMPLICIT, parabola.getEquationForm());
	}

	// Classic

	@Test
	public void testClassicLineEquationBehaviour() {
		getApp().setDefaultConfig(); // Default = Classic

		GeoLine algebraLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_IMPLICIT, algebraLine.getEquationForm());

		// Line created with tool
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_IMPLICIT, toolLine.getEquationForm());

		// Ray created with tool
		// TODO APPS-5867
	}

	@Test
	public void testClassicLineEquationBehaviourWithCustomizedConstructionDefaults() {
		getApp().setDefaultConfig();

		// change the equation form for lines in the construction defaults
		GeoLine constructionDefaultsLine = (GeoLine) getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		constructionDefaultsLine.setEquationForm(GeoLine.EQUATION_GENERAL);

		GeoLine defaultLine = new GeoLine(getConstruction());
		assertEquals(GeoLine.EQUATION_GENERAL, defaultLine.getEquationForm());

		// check if lines created with a tool or command have the equation form as defined in
		// the construction defaults
		GeoPoint pointA = new GeoPoint(getConstruction(), 0, 0, 0);
		GeoPoint pointB = new GeoPoint(getConstruction(), 1, 1, 0);
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), pointA, pointB);
		GeoLine toolLine = algoJoinPoints.getLine();
		assertEquals(GeoLine.EQUATION_GENERAL, toolLine.getEquationForm());
	}

	private GeoLine lineThrough(GeoPoint a, GeoPoint b) {
		AlgoJoinPoints algoJoinPoints = new AlgoJoinPoints(getConstruction(), a, b);
		return algoJoinPoints.getLine();
	}
}
