package org.geogebra.common.kernel.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.Feature;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class NoExceptionsTest {
	static AppCommon app;
	static AlgebraProcessor ap;

	/**
	 * Create app + basic test objects
	 */
	@BeforeClass
	public static void setupApp() {
		AwtFactory awt = new AwtFactoryCommon();
		app = new AppCommon3D(new LocalizationCommon(3), awt);
		app.setLanguage("en_US");
		ap = app.getKernel().getAlgebraProcessor();
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
		// try this before an object named i is created
		t("1+i");
		t("Pt1=(1,1)");
		t("Pt2=(2,1/2)");
		t("Pt3=(3,1/3)");
		t("Pt4=(4,1/4)");
		t("Pt5=(5,1/5)");
		t("Pt3D1=(5,1/5,-1)");
		t("Pt3D2=(5,2,-8)");
		t("Pt3D3=(-5,4,-3)");
		t("Pt3D4=(9,-2,3)");
		t("v1=(1,1)");
		t("v2=(2,1/2)");
		t("v3=(3,1/3)");
		t("v4=(4,1/4)");
		t("v5=(5,1/5)");
		t("v3D1=(5,1/5,-1)");
		t("c1:x^2+y^2=1");
		t("c2:x^2+y^2/2=1");
		t("f1(x)=sin(x)");
		t("polynom1:x^2");
		t("polynom2:x^4+4");
		t("n1=42");
		t("n2=4");
		t("complex1=4+3*i");
		t("n3=17");
		t("n4=50");
		t("n5=5");
		t("n6=7");
		t("prob=0.05");
		t("prob2=0.5");
		t("nd=n2+n3");
		t("letter=\"X\"");
		t("txt=\"GeoGebra\"");
		t("obj=Polygon[Pt1,Pt2,4]");
		t("obj2=Polygon[Pt1,Pt2,4]");
		t("curve1=Curve[sin(t),cos(t),t,0,3]");
		t("matrix1={{1,2},{3,4}}");
		t("seg1=Segment[Pt1,Pt2]");
		t("seg2=Segment[Pt3,Pt4]");
		t("l1:x+y=17");
		t("l2:x=4");
		t("b1=n1==n2");
		t("b2=n2==4");
		t("impl1:x^4+y^4=1");
		t("comp=2+3*i");
		t("a1=30" + Unicode.DEGREE_STRING);
		t("list1 = {1,2,3,4,5}");
		t("list2 = {2,3,4}");
		t("list3 = list2");
		t("ptlist1 = {(1,1),(2,2),(3,3),Pt4,Pt5}");
		t("red=0.1");
		t("green=1");
		t("blue=0.2");
		t("gv=1");
		t("twovar(x,y)=x*y");
		t("GeoGebra=(1,1)");
		t("notes=\"CDEFGAHC\"");
		t("cellrange1=A1:A2");
		t("parabola1:(x+y)^2=x-y");
		t("interval1:1<x<2");
		t("ptonpath1=Point[c1]");
		t("poly1=Polygon[Pt1,Pt2,Pt3]");
		t("Depoint=ptonpath1*2");
		t("loc=Locus[Depoint,ptonpath1]");
		t("slid=Slider[-1,1,0.1]");
		t("Depointslid=(slid,slid)");
		t("bf1: x>3");
		t("ptxt=\"n1+n2\"");
		t("turtle1=Turtle[]");
		t("object=(1,1)");
		t("listSpline={(2,3),(1,4),(2,5),(3,1)}");
		t("degree=4");
		t("P=(2,3)");
		t("c3:circle[P,2]");
		t("alog=1");
		t("freehandFunc=Function[{1,2,1,2,1,2,1,2,1,2,1}]");
	}

	public static int syntaxes;

	@Before
	public void resetSyntaxes() {
		syntaxes = -1000;
	}

	@After
	public void checkSyntaxes() {
		Assert.assertTrue("unchecked syntaxes: " + syntaxes, syntaxes <= 0);
	}

	private static void t(String s) {
		testSyntax(s, app, ap);
	}

	private static void testSyntax(String s, App app, AlgebraProcessor ap) {
		if (syntaxes == -1000) {
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			List<Integer> signature = CommandSignatures.getSigneture(cmdName);
			syntaxes = 0;
			if (signature != null) {
				syntaxes = signature.size();
				AlgebraTestHelper.dummySyntaxesShouldFail(cmdName, signature,
						app);
			}

			System.out.println();
			System.out.print(cmdName + " ");

			/*
			 * // This code helps to force timeout for each syntax. Not used at
			 * the moment. GeoGebraCAS cas = (GeoGebraCAS) app.getKernel()
			 * .getGeoGebraCAS(); try { cas.getCurrentCAS().evaluateRaw(
			 * "caseval(\"timeout 8\")"); } catch (Throwable e) { App.error(
			 * "CAS error " + e); }
			 */

		}
		try {
			Assert.assertNotNull(ap.processAlgebraCommandNoExceptionHandling(s,
					false, TestErrorHandler.INSTANCE, false, null));
			syntaxes--;
			System.out.print("+");
		} catch (final Throwable e) {
			System.out.println("error occured:" + e.getClass().getName());
			Throwable t = e;
			while (t.getCause() != null) {
				t = t.getCause();
			}

			syntaxes--;
			System.out.print("-");
			Assert.assertNull(e.getMessage() + "," + e.getClass(), e);
		}
	}


	/**
	 * @param a
	 *            command
	 * @return whether only is in beta
	 */
	public static boolean betaCommand(Commands a, App app) {
		return a == Commands.MatrixPlot || a == Commands.DensityPlot
				|| a == Commands.Polyhedron
				|| (a == Commands.Holes && !app.has(Feature.COMMAND_HOLES))
				|| (a == Commands.ImplicitSurface
						&& !app.has(Feature.IMPLICIT_SURFACES));
	}

	@Test
	public void cmdAreCollinear() {
		t("AreCollinear[ Pt1,Pt2,Pt3 ]");
	}

	@Test
	public void cmdIsTangent() {
		t("IsTangent[ l1,c1 ]");
	}

	@Test
	public void cmdAreParallel() {
		t("AreParallel[ l1,l2 ]");
	}

	@Test
	public void cmdAreConcyclic() {
		t("AreConcyclic[ Pt1,Pt2,Pt3,Pt4 ]");
	}

	@Test
	public void cmdExpression() {
		t("(1,2)+{(2,3),(4,5)}");
	}

	@Test
	public void cmdAffineRatio() {
		t("AffineRatio[ Pt1,Pt2,Pt3 ]");
	}

	@Test
	public void cmdAngle() {
		t("Angle[ l1, l2 ]");
		t("Angle[ Pt1 ]");
		t("Angle[ Pt1, Pt3, Pt2 ]");
		t("Angle[ Pt1, Pt4, a1 ]");
		t("Angle[ seg1, l1 ]");
	}

	@Test
	public void cmdAngularBisector() {
		t("anbisA=AngleBisector[ l1, l2 ]");
		t("anbisB=AngleBisector[ Pt1,Pt2,Pt3 ]");
	}

	@Test
	public void cmdANOVA() {
		t("ANOVA[ list1, list2]");
	}

	@Test
	public void cmdAppend() {
		t("Append[ list1, obj ]");
		t("Append[ obj, list1 ]");
	}

	@Test
	public void cmdApplyMatrix() {
		t("ApplyMatrix[ matrix1, obj ]");
	}

	@Test
	public void cmdArc() {
		t("Arc[ c1, prob, prob2 ]");
		t("Arc[ c1, Pt1,Pt2 ]");
		t("Arc[ c2, prob, prob2 ]");
		t("Arc[ c2, Pt1,Pt2 ]");
	}

	@Test
	public void cmdArea() {
		t("Area[ c1 ]");
		t("Area[ poly1 ]");
		t("Area[ Pt1, Pt3, Pt1 ]");
	}

	@Test
	public void cmdAxes() {
		t("Axes[ c1 ]");
	}

	@Test
	public void cmdAxisStepX() {
		t("AxisStepX[]");
	}

	@Test
	public void cmdAxisStepY() {
		t("AxisStepY[]");
	}

	@Test
	public void cmdBarChart() {
		t("BarChart[ list2, list3 ]");
		t("BarChart[ list2, list3, n2 ]");
		t("BarChart[ list2, n2 ]");
		t("BarChart[ n1, n4, list1 ]");
		t("BarChart[ n1, n4, t^2, t, n2, n2 ]");
		t("BarChart[ n1, n4, t^2, t, n2, n2, prob ]");
	}

	@Test
	public void cmdBarycenter() {
		t("Barycenter[{Pt1,Pt2,Pt3},{n1,n2,n3}]");
	}

	@Test
	public void cmdBoxPlot() {
		t("BoxPlot[ n2, n4, list2 ]");
		t("BoxPlot[ n2, n4, list2, true ]");
		t("BoxPlot[ n2, n4, list2, list1, false ]");
		t("BoxPlot[ n4, n1, n1, n2, n1, n3, n4 ]");
	}

	@Test
	public void cmdButton() {
		t("Button[ ]");
		t("Button[ txt ]");
	}

	@Test
	public void cmdCell() {
		t("Cell[ n1, n2 ]");
	}

	@Test
	public void cmdCellRange() {
		t("CellRange[ A1, A1 ]");
	}

	@Test
	public void cmdCenter() {
		t("Center[ c1 ]");
	}

	@Test
	public void cmdCentroid() {
		t("Centroid[ poly1 ]");
	}

	@Test
	public void cmdCircleArc() {
		t("CircularArc[ Pt5, Pt1,Pt2 ]");
	}

	@Test
	public void cmdCircle() {
		t("Circle[ Pt1, n1 ]");
		t("Circle[ Pt1,Pt2 ]");
		t("Circle[ Pt1,Pt2,Pt3 ]");
		t("Circle[ Pt1, seg1 ]");
	}

	@Test
	public void cmdCircleSector() {

		t("CircularSector[ Pt5, Pt1,Pt2 ]");
	}

	@Test
	public void cmdCircumcircleArc() {
		t("CircumcircularArc[ Pt1,Pt2,Pt3 ]");
	}

	@Test
	public void cmdCircumcircleSector() {

		t("CircumcircularSector[ Pt1,Pt2,Pt3 ]");
	}

	@Test
	public void cmdCircumference() {
		t("Circumference[ c1 ]");
	}

	@Test
	public void cmdClasses() {
		t("Classes[ list2, n1 ]");
		t("Classes[ list2, n3, n2 ]");
	}

	@Test
	public void cmdClosestPoint() {
		t("ClosestPoint[ c1, Pt1 ]");
		t("ClosestPoint[ poly1, Pt1 ]");
		t("ClosestPoint[ xAxis, yAxis ]");
	}

	@Test
	public void cmdCoefficients() {
		t("Coefficients[ c1 ]");
		t("Coefficients[ polynom1 ]");
	}

	@Test
	public void cmdColumn() {
		t("Column[ A1 ]");
	}

	@Test
	public void cmdColumnName() {
		t("ColumnName[ A1 ]");
	}

	/*
	 * @Test public void cmdCompetitionRank() { t("CompetitionRank[list1]"); }
	 */

	@Test
	public void cmdCompleteSquare() {
		t("CompleteSquare[ polynom1 ]");
	}

	@Test
	public void cmdConic() {
		t("Conic[ {n1,n2,n3,n4,n5,n6} ]");
		t("Conic[ Pt1,Pt2,Pt3,Pt4,Pt5 ]");
	}

	@Test
	public void cmdConstructionStep() {
		t("ConstructionStep[]");
		t("ConstructionStep[ obj ]");
	}

	@Test
	public void cmdConvexHull() {
		t("ConvexHull[ ptlist1 ]");
	}

	@Test
	public void cmdCopyFreeObject() {
		t("CopyFreeObject[ obj ]");
	}

	@Test
	public void cmdCorner() {
		t("Corner[ gv, n1 ]");
		t("Corner[ n1 ]");
		t("Corner[ txt, n1 ]");
		GeoImage ge = new GeoImage(app.getKernel().getConstruction());
		ge.setLabel("img1");
		t("Corner[ img1, n2 ]");
	}

	@Test
	public void cmdCountIf() {
		t("CountIf[ bf1(x), list1 ]");
		t("CountIf[ bf1(A),A, list1 ]");
	}

	@Test
	public void cmdCovariance() {
		t("Covariance[ list1, list1 ]");
		t("Covariance[ ptlist1 ]");
	}

	@Test
	public void cmdCrossRatio() {
		t("CrossRatio[ Pt1,Pt2,Pt3,Pt4 ]");
	}

	@Test
	public void cmdCurvature() {
		t("Curvature[ Pt1, curve1 ]");
		t("Curvature[ Pt1, f1 ]");
		t("Curvature[ Pt1, c3 ]");
	}

	@Test
	public void cmdCurvatureVector() {
		t("CurvatureVector[ Pt1, curve1 ]");
		t("CurvatureVector[ Pt1, f1 ]");
		t("CurvatureVector[ Pt1, c3 ]");
	}

	@Test
	public void cmdCurveCartesian() {

		t("Curve[ t^2, t^2, t, n1, n4 ]");
	}

	@Test
	public void cmdDefined() {
		t("IsDefined[ obj ]");
	}

	@Test
	public void cmdDegree() {
		t("Degree[f1]");
	}

	@Test
	public void cmdDelauneyTriangulation() {
		t("DelaunayTriangulation[ ptlist1 ]");
	}

	@Test
	public void cmdDelete() {
		t("Delete[ obj2 ]");
	}

	@Test
	public void cmdDeterminant() {
		t("Determinant[ matrix1 ]");
	}

	@Test
	public void cmdDiameter() {
		t("ConjugateDiameter[ l1, c1 ]");
		t("ConjugateDiameter[v1 , c1 ]");
	}

	@Test
	public void cmdDirectrix() {
		t("Directrix[ c1 ]");
	}

	@Test
	public void cmdDistance() {
		t("Distance[ Pt1, obj ]");
		t("Distance[ xAxis,yAxis ]");
		t("Distance[x+y+z=1, x+y+z=2]");
		t("Distance[Line[Pt1,Pt2],Line[Pt3,Pt4]]");
		t("Distance[Line[Pt3D1,Pt3D2],Line[Pt3,Pt4]]");
		t("Distance[Line[Pt1,Pt2],Line[Pt3D3,Pt3D4]]");
		t("Distance[Line[Pt3D1,Pt3D2],Line[Pt3D3,Pt3D4]]");
	}

	@Test
	public void cmdDiv() {
		t("Div[ n2, n2 ]");
		t("Div[ polynom2, polynom1 ] ");
	}

	@Test
	public void cmdDynamicCoordinates() {
		t("DynamicCoordinates[ Pt1, n1, n2 ]");
		t("DynamicCoordinates[ Pt1, n1, n2, n3 ]");
	}

	@Test
	public void cmdEccentricity() {
		t("Eccentricity[ c1 ]");
	}

	@Test
	public void cmdElement() {
		t("Element[ {{list1}}, n1, n2,n3]");
		t("Element[ list1, n2 ]");
		t("Element[ matrix1, n2, n3 ]");
	}

	@Test
	public void cmdEllipse() {
		t("Ellipse[ Pt1,Pt2,Pt3 ]");
		t("Ellipse[ Pt3, Pt3, n2 ]");
		t("Ellipse[ Pt3, Pt3, seg1 ]");
	}

	@Test
	public void cmdExcentricity() {
		t("LinearEccentricity[ c1 ]");
	}

	@Test
	public void cmdExecute() {
		t("Execute[ {\"Midpoint[%1,%2]\"}, Pt1, Pt2]");
		t("Execute[ {\"n1=n1-1\"}]");
	}

	@Test
	public void cmdExpand() {
		t("Expand[ x^2 ]");
	}

	@Test
	public void cmdFactor() {
		t("Factor[ polynom1 ]");
	}

	@Test
	public void cmdFactors() {
		t("Factors[ n1 ]");
		t("Factors[ polynom1 ]");
	}

	@Test
	public void cmdFillCells() {
		t("FillCells[ A1, list1 ]");
		t("FillCells[ A1, matrix1 ]");
		t("FillCells[ A1:A10, obj ]");
	}

	@Test
	public void cmdFillColumn() {
		t("FillColumn[ n3, list1 ]");
	}

	@Test
	public void cmdFillRow() {
		t("FillRow[ n2, list1 ]");
	}

	@Test
	public void cmdFirstAxis() {
		t("MajorAxis[ c1 ]");
	}

	@Test
	public void cmdFirstAxisLength() {
		t("SemiMajorAxisLength[ c1 ]");
	}

	@Test
	public void cmdFirst() {
		t("First[ list1 ]");
		t("First[ list1 , n1 ]");
		t("First[ loc , n1 ]");
		t("First[ freehandFunc , 2 ]");

		t("First[ txt ]");
		t("First[ txt , n1 ]");
	}

	@Test
	public void cmdFitExp() {
		t("FitExp[ ptlist1 ]");
	}

	@Test
	public void cmdFitGrowth() {
		t("FitGrowth[ ptlist1 ]");
	}

	@Test
	public void cmdFitLineX() {
		t("FitLineX[ ptlist1 ]");
	}

	@Test
	public void cmdFitLineY() {
		t("FitLine[ ptlist1 ]");
	}

	@Test
	public void cmdFitLog() {
		t("FitLog[ ptlist1 ]");
	}

	@Test
	public void cmdFitLogistic() {
		t("FitLogistic[ ptlist1 ]");
	}

	@Test
	public void cmdFitPow() {
		t("FitPow[ ptlist1 ]");
	}

	@Test
	public void cmdFitSin() {
		t("FitSin[ ptlist1 ]");
	}

	@Test
	public void cmdFlatten() {
		t("Flatten[ {} ]");
		t("Flatten[ {{},{{{}}}} ]");
		t("Flatten[ {{Pt1},{{{n2}}}} ]");
	}

	@Test
	public void cmdFocus() {
		t("Focus[ c1 ]");
	}

	@Test
	public void cmdFractionText() {
		t("FractionText[ n1 ]");
		t("FractionText[ Pt1 ]");
	}

	@Test
	public void cmdFrequency() {
		t("Frequency[ b1, list1, list2]");
		t("Frequency[ b1, list1, list2, b2 , n2 ]");
		t("Frequency[ b1, list2]");
		t("Frequency[ list1, list2,b1]");
		t("Frequency[ list1, list2, b2 , n2 ]");
		t("Frequency[ list2 ]");
		t("Frequency[ {\"GeoGebra\",\"rocks\"},{\"X\",\"Y\"} ]");
	}

	@Test
	public void cmdFrequencyPolygon() {
		t("FrequencyPolygon[ b1, list1, list2, b1 , n2 ]");
		t("FrequencyPolygon[ list1, list1 ]");
		t("FrequencyPolygon[ list1, list2, b1 , n4 ]");
	}

	@Test
	public void cmdFrequencyTable() {
		t("FrequencyTable[ b1, list1, list2]");
		t("FrequencyTable[ b1, list1, list2, b2 , n2 ]");
		t("FrequencyTable[ b1, list2]");
		t("FrequencyTable[ list1, list2 ]");
		t("FrequencyTable[ list1, list2, b2 , n2 ]");
		t("FrequencyTable[ list2 ]");
	}

	@Test
	public void cmdFunction() {
		t("Function[ f1, n2, n3 ]");
		t("Function[ list1 ]");
	}

	@Test
	public void cmdGCD() {
		t("GCD[ list1 ]");
		t("GCD[ n1, n1 ]");
	}

	@Test
	public void cmdGeometricMean() {
		t("GeometricMean[ list1 ]");
	}

	@Test
	public void cmdGetTime() {
		t("GetTime[]");
		t("GetTime[\"h:i:s\"]");
	}

	@Test
	public void cmdHarmonicMean() {
		t("HarmonicMean[ list1 ]");
	}

	@Test
	public void cmdHideLayer() {
		t("HideLayer[ n1 ]");
	}

	@Test
	public void cmdHistogram() {
		t("Histogram[ b1, list1, list2, b2 , n2 ]");
		t("Histogram[ list1, list1 ]");
		t("Histogram[ list1, list2, b2 , n2  ]");
	}

	@Test
	public void cmdHistogramRight() {
		t("HistogramRight[ b1, list1, list2, b2 , n2 ]");
		t("HistogramRight[ list1, list1 ]");
		t("HistogramRight[ list1, list2, b2 , n2 ]");
	}

	@Test
	public void cmdHull() {
		t("Hull[ ptlist1 , prob ]");
	}

	@Test
	public void cmdHyperbola() {
		t("Hyperbola[ Pt1,Pt2,Pt3 ]");
		t("Hyperbola[ Pt3, Pt3, n2 ]");
		t("Hyperbola[ Pt3, Pt3, seg1 ]");
	}

	@Test
	public void cmdCheckbox() {
		t("Checkbox[]");
		t("Checkbox[ list1 ]");
		t("Checkbox[ txt ]");
		t("Checkbox[ txt, list1 ]");
	}

	@Test
	public void cmdIdentity() {
		t("Identity[ n1 ]");
	}

	@Test
	public void cmdIf() {
		t("If[ b1, Pt1, Pt2 ]");
		t("If[ b1,Pt3 ]");
	}

	@Test
	public void cmdIFactor() {
		t("IFactor[ x^4-2 ]");
	}

	@Test
	public void cmdImplicitCurve() {
		t("ImplicitCurve[ ptlist1 ]");
		t("ImplicitCurve[ twovar ]");
	}

	@Test
	public void cmdIncircle() {
		t("Incircle[ Pt1,Pt2,Pt3 ]");
	}

	@Test
	public void cmdIndexOf() {
		t("IndexOf[ obj, list1 ]");
		t("IndexOf[ obj, list1, n2 ]");
		t("IndexOf[ txt, txt]");
		t("IndexOf[ txt, txt, n2 ]");
	}

	@Test
	public void cmdInsert() {
		t("Insert[ list1, list1, n2 ]");
		t("Insert[ obj, list1, n2 ]");
	}

	@Test
	public void cmdIntegralBetween() {
		t("IntegralBetween[ f1, f1, n2, n3 ]");
		t("IntegralBetween[ f1, f1, n2, n3, b1 ]");
	}

	@Test
	public void cmdIntersection() {
		t("Intersection[ list1, list1 ]");
	}

	@Test
	public void cmdIntersectRegion() {
		t("IntersectRegion[ poly1, poly1 ]");
	}

	@Test
	public void cmdInverseBinomial() {
		t("InverseBinomial[ n1, prob2, prob ]");
	}

	@Test
	public void cmdInverseCauchy() {
		t("InverseCauchy[ n1, n3, prob ]");
	}

	@Test
	public void cmdInverseExponential() {
		t("InverseExponential[ n2, prob ]");
	}

	@Test
	public void cmdInverseFDistribution() {
		t("InverseFDistribution[ n3, n3, prob ]");
	}

	@Test
	public void cmdInverseGamma() {
		t("InverseGamma[ n1, n2, prob ]");
	}

	@Test
	public void cmdInverseHyperGeometric() {
		t("InverseHyperGeometric[ n3, n1, n2, prob ]");
	}

	@Test
	public void cmdInverseChiSquared() {
		t("InverseChiSquared[ n2, prob ]");
	}

	@Test
	public void cmdInverseNormal() {
		t("InverseNormal[ n1, n2, prob ]");
	}

	@Test
	public void cmdInversePascal() {
		t("InversePascal[ n1, prob2, prob ]");
	}

	@Test
	public void cmdInversePoisson() {
		t("InversePoisson[ n1, prob ]");
	}

	@Test
	public void cmdInverseTDistribution() {
		t("InverseTDistribution[ n2, prob ]");
	}

	@Test
	public void cmdInverseWeibull() {
		t("InverseWeibull[ n2, n3, prob ]");
	}

	@Test
	public void cmdInverseZipf() {
		t("InverseZipf[ n1, n2, prob ]");
	}

	@Test
	public void cmdIsInRegion() {
		t("IsInRegion[ Pt1, c1 ]");
	}

	@Test
	public void cmdIsInteger() {
		t("IsInteger[ n1 ]");
	}

	@Test
	public void cmdJoin() {
		t("Join[ list1, list1]");
		t("Join[ matrix1 ]");
	}

	@Test
	public void cmdKeepIf() {
		t("KeepIf[ bf1(x), list1 ]");
		t("KeepIf[ bf1(A),A, list1 ]");
		syntaxes++;
		t("KeepIf[ Distance[A,(0,0)]>4,A, {(1,1)} ]");
	}

	@Test
	public void cmdLast() {
		t("Last[ list1 ]");
		t("Last[ list1 , n1 ]");

		t("Last[ txt ]");
		t("Last[ txt , n1 ]");
	}

	@Test
	public void cmdLaTeX() {
		t("FormulaText[ obj ]");
		t("FormulaText[ obj, b1 ]");
		t("FormulaText[ obj, b1, b1 ]");
	}

	@Test
	public void cmdLCM() {
		t("LCM[ list1 ]");
		t("LCM[ n1, n1 ]");
	}

	@Test
	public void cmdLeftSum() {
		t("LeftSum[ f1, n2, n3, n1 ]");
	}

	@Test
	public void cmdLetterToUnicode() {
		t("LetterToUnicode[ letter ]");
	}

	@Test
	public void cmdLimitAbove() {
		t("LimitAbove[ f1, n3 ]");
	}

	@Test
	public void cmdLimitBelow() {
		t("LimitBelow[ f1, n3 ]");
	}

	@Test
	public void cmdLimit() {
		t("Limit[ f1, n3 ]");
	}

	@Test
	public void cmdLine() {
		t("Line[ Pt1, l1 ]");
		t("Line[ Pt1,Pt2 ]");
		t("Line[ Pt1, v1 ]");
		t("Line[ Pt1, v3D1 ]");
		t("Line[ Pt3D1, v1 ]");
		t("Line[ Pt3D1, v3D1 ]");
		t("Line[ Pt1, Pt3D1 ]");
		t("Line[ Pt3D1, Pt1 ]");
		t("Line[ Pt3D1, Pt3D2 ]");
	}

	@Test
	public void cmdLocus() {
		t("Locus[ Depoint, ptonpath1 ]");
		t("Locus[ x+y, ptonpath1 ]");
		t("Locus[ SlopeField[ x/y ], ptonpath1 ]");
		t("Locus[ Depointslid, slid ]");
	}

	@Test
	public void cmdLowerSum() {
		t("LowerSum[ f1, n2, n3, n1 ]");
	}

	@Test
	public void cmdMax() {
		t("Max[ f1, n2, n3 ]");
		t("Max[ interval1 ]");
		t("Max[ list1 ]");
		t("Max[ list1, list1 ]");
		t("Max[ n1, n1 ]");
	}

	@Test
	public void cmdMeanX() {
		t("MeanX[ ptlist1 ]");
	}

	@Test
	public void cmdMeanY() {
		t("MeanY[ ptlist1 ]");
	}

	@Test
	public void cmdMedian() {
		t("Median[ list1 ]");
		t("Median[ list1, list1 ]");
	}

	@Test
	public void cmdMidpoint() {
		t("Midpoint[ c1 ]");
		t("MidPoint[ interval1 ]");
		t("Midpoint[ Pt1,Pt2 ]");
		t("Midpoint[ seg1 ]");
	}

	@Test
	public void cmdMin() {
		t("Min[ f1, n2, n3 ]");
		t("Min[ interval1 ]");
		t("Min[ list1 ]");
		t("Min[ list1, list1 ]");
		t("Min[ n1, n1 ]");
	}

	@Test
	public void cmdMinimumSpanningTree() {
		t("MinimumSpanningTree[ ptlist1 ]");
	}

	@Test
	public void cmdMirror() {
		t("Reflect[ obj, c1 ]");
		t("Reflect[ obj, l1 ]");
		t("Reflect[ obj, Pt1 ]");
	}

	@Test
	public void cmdMode() {
		t("Mode[ list1 ]");
	}

	@Test
	public void cmdMod() {
		t("Mod[ n2, n2 ]");
		t("Mod[ polynom2, polynom1 ] ");
	}

	@Test
	public void cmdName() {
		t("Name[ obj ]");
	}

	@Test
	public void cmdNormalQuantilePlot() {
		t("NormalQuantilePlot[ list2]");
	}

	/*
	 * @Test public void cmdNyquist() { t("Nyquist[ {1,1},{1,1,1} ]");
	 * t("Nyquist[ {-1,3},{3,4,1},6 ]"); }
	 */

	@Test
	public void cmdObject() {
		t("Object[ txt ]");
	}

	@Test
	public void cmdOrdinal() {
		t("Ordinal[ n2 ]");
	}

	@Test
	public void cmdOrdinalRank() {
		t("OrdinalRank[ list1 ]");
	}

	@Test
	public void cmdOsculatingCircle() {
		t("OsculatingCircle[ Pt1, curve1 ]");
		t("OsculatingCircle[ Pt1, f1 ]");
		t("OsculatingCircle[ Pt1, c3 ]");
	}

	@Test
	public void cmdPan() {
		t("Pan[ n1, n2 ]");
	}

	@Test
	public void cmdParabola() {
		t("Parabola[ Pt1, l1 ]");
	}

	@Test
	public void cmdParameter() {
		t("Parameter[ parabola1 ]");
	}

	@Test
	public void cmdParseToFunction() {
		// t("ParseToFunction[ f1, txt ]");
	}

	@Test
	public void cmdParseToNumber() {
		t("ParseToNumber[ n1, ptxt ]"); // valid
		t("ParseToNumber[ n1, txt ]"); // invalid, but all exceptions should be
										// caught
	}

	@Test
	public void cmdPartialFractions() {
		t("PartialFractions[ f1 ]");
	}

	@Test
	public void cmdPathParameter() {
		t("PathParameter[ ptonpath1 ]");
	}

	@Test
	public void cmdPercentile() {
		t("Percentile[ list1, prob ]");
	}

	@Test
	public void cmdPerimeter() {
		t("Perimeter[ c1 ]");
		t("Perimeter[ loc ]");
		t("Perimeter[ poly1 ]");
	}

	@Test
	public void cmdLineBisector() {
		t("PerpendicularBisector[ Pt1,Pt2 ]");
		t("PerpendicularBisector[ seg1 ]");
	}

	@Test
	public void cmdPlaySound() {
		t("PlaySound[ b1 ]");
		t("PlaySound[ f1, n1, n4 ]");
		t("PlaySound[ f1, n1, n4, n3, n2]");
		t("PlaySound[ b1 ]"); // test this twice instead of playing file
		t("PlaySound[ notes, n1 ]");
		t("PlaySound[ n3, n2, n1 ]");
	}

	@Test
	public void cmdPMCC() {
		t("CorrelationCoefficient[ list1, list1 ]");
		t("CorrelationCoefficient[ ptlist1 ]");
	}

	@Test
	public void cmdPointIn() {
		t("PointIn[ poly1 ]");
	}

	@Test
	public void cmdPointList() {
		t("PointList[ list1 ]");
	}

	@Test
	public void cmdPolar() {
		t("Polar[ Pt1, c1 ]");
		t("Polar[ l1, c1 ]");
	}

	@Test
	public void cmdPolygon() {
		t("Polygon[ ptlist1 ]");
		t("Polygon[ Pt1,Pt2, n1 ]");
		t("Polygon[ Pt1, Pt2, Pt3 ]");
	}

	@Test
	public void cmdPolyLine() {
		t("PolyLine[ ptlist1 ]");
		t("PolyLine[ Pt1, Pt3, Pt5 ]");
	}

	@Test
	public void cmdPrimeFactors() {
		t("PrimeFactors[ n1 ]");
	}

	@Test
	public void cmdQ1() {
		t("Q1[ list1 ]");
		t("Q1[ list1, list1 ]");
	}

	@Test
	public void cmdQ3() {
		t("Q3[ list1 ]");
		t("Q3[ list1, list1 ]");
	}

	@Test
	public void cmdRadius() {
		t("Radius[ c1 ]");
	}

	@Test
	public void cmdRandomBinomial() {
		t("RandomBinomial[ n1, prob ]");
	}

	@Test
	public void cmdRandomElement() {
		t("RandomElement[ list1 ]");
	}

	@Test
	public void cmdRandom() {
		t("RandomBetween[ n1, n4 ]");
		t("RandomBetween[ n1, n4, true ]");
	}

	@Test
	public void cmdRandomNormal() {
		t("RandomNormal[ n1, n2 ]");
	}

	@Test
	public void cmdRandomPoisson() {
		t("RandomPoisson[ n1 ]");
	}

	@Test
	public void cmdRandomUniform() {
		t("RandomUniform[ n1, n4 ]");
		t("RandomUniform[ n1, n4, n1 ]");
	}

	@Test
	public void cmdRay() {
		t("Ray[ Pt3, Pt1 ]");
		t("Ray[ Pt3,v1]");
	}

	@Test
	public void cmdRectangleSum() {
		t("RectangleSum[ f1, n2, n3, n1, prob ]");
	}

	@Test
	public void cmdReducedRowEchelonForm() {
		t("ReducedRowEchelonForm[matrix1]");
	}

	/*
	 * I have to disable this temporarily for autotest, until there is no way to
	 * automatically push the OK button or just close the Relation Tool window.
	 * --- Zoltan, 2012-03-09
	 * 
	 * @Test public void cmdRelation() { t("Relation[ l1, l2 ]"); }
	 */
	@Test
	public void cmdRemoveUndefined() {
		t("RemoveUndefined[ list1 ]");
	}

	@Test
	public void cmdResidualPlot() {
		t("ResidualPlot[ ptlist1, f1 ]");
	}

	@Test
	public void cmdReverse() {
		t("Reverse[ list1 ]");
	}

	@Test
	public void cmdRigidPolygon() {
		t("RigidPolygon[ (0,0), (1,0), (0,1) ]");
		t("RigidPolygon[ poly1 ]");
		t("RigidPolygon[ poly1, n1,n2 ]");
	}

	@Test
	public void cmdRootList() {
		t("RootList[ list1 ]");
	}

	@Test
	public void cmdRootMeanSquare() {
		t("RootMeanSquare[ list1 ]");
	}

	@Test
	public void cmdRoots() {
		t("Roots[ f1, n2, n3 ]");
	}

	@Test
	public void cmdRotate() {
		t("Rotate[ obj, a1 ]");
		t("Rotate[ obj, a1, Pt1 ]");
	}

	@Test
	public void cmdRotateText() {
		t("RotateText[ txt, a1 ]");
	}

	@Test
	public void cmdRow() {
		t("Row[ A1 ]");
	}

	@Test
	public void cmdRSquare() {
		t("RSquare[ ptlist1, f1 ]");
	}

	@Test
	public void cmdSample() {
		t("Sample[ list1, n2, b1 ]");
		t("Sample[ list1, n4 ]");
	}

	@Test
	public void cmdSampleSD() {
		t("SampleSD[ list1 ]");
		t("SampleSD[ list1, list1 ]");
	}

	@Test
	public void cmdstdev() {
		t("stdev[ list1 ]");
		t("stdev[ list1, list1 ]");
	}

	@Test
	public void cmdstdevp() {
		t("stdevp[ list1 ]");
		t("stdevp[ list1, list1 ]");
	}

	@Test
	public void cmdSampleSDX() {
		t("SampleSDX[ ptlist1 ]");
	}

	@Test
	public void cmdSampleSDY() {
		t("SampleSDY[ ptlist1 ]");
	}

	@Test
	public void cmdSampleVariance() {
		t("SampleVariance[ list1 ]");
		t("SampleVariance[ list1, list1 ]");
	}

	@Test
	public void cmdSD() {
		t("SD[ list1 ]");
		t("SD[ list1, list1 ]");
	}

	@Test
	public void cmdSDX() {
		t("SDX[ ptlist1 ]");
	}

	@Test
	public void cmdSDY() {
		t("SDY[ ptlist1 ]");
	}

	@Test
	public void cmdSecondAxis() {
		t("MinorAxis[ c1 ]");
	}

	@Test
	public void cmdSecondAxisLength() {
		t("SemiMinorAxisLength[ c1 ]");
	}

	@Test
	public void cmdSector() {
		t("Sector[ c1, prob, prob2 ]");
		t("Sector[ c1, Pt1,Pt2 ]");
	}

	@Test
	public void cmdSegment() {
		t("Segment[ Pt1, n2 ]");
		t("Segment[ Pt1,Pt2 ]");
	}

	@Test
	public void cmdSelectedElement() {
		t("SelectedElement[ list1 ]");
	}

	@Test
	public void cmdSelectedIndex() {
		t("SelectedIndex[ list1 ]");
	}

	@Test
	public void cmdSelectObjects() {
		t("SelectObjects[]");
		t("SelectObjects[ obj, obj, pt1 ]");
	}

	@Test
	public void cmdSemicircle() {
		t("Semicircle[ Pt1,Pt2 ]");
	}

	@Test
	public void cmdSetActiveView() {
		t("SetActiveView[ gv ]");
		t("SetActiveView[ Plane[(1,0),(2,3),(0,1)] ]");
	}

	@Test
	public void cmdSetAxesRatio() {
		t("SetAxesRatio[ n1, n1 ]");
	}

	@Test
	public void cmdSetCaption() {
		t("SetCaption[ obj, txt ]");
	}

	@Test
	public void cmdSetConditionToShowObject() {
		t("SetConditionToShowObject[ obj, b1 ]");
	}

	@Test
	public void cmdSetDynamicColor() {
		t("SetDynamicColor[ obj, red, green, blue ]");
		t("SetDynamicColor[ obj, red, green, blue, prob ]");
	}

	@Test
	public void cmdSetFilling() {
		t("SetFilling[ obj, n1 ]");
	}

	@Test
	public void cmdSetFixed() {
		t("SetFixed[ obj, b1 ]");
		t("SetFixed[ obj, b1, b2 ]");
	}

	@Test
	public void cmdSetLabelMode() {
		t("SetLabelMode[ obj, n1 ]");
	}

	@Test
	public void cmdSetLayer() {
		t("SetLayer[ obj, n1 ]");
	}

	@Test
	public void cmdSetLineStyle() {
		t("SetLineStyle[ l1, n1 ]");
	}

	@Test
	public void cmdSetDecoration() {
		t("SetDecoration[ l1, n2 ]");
	}

	@Test
	public void cmdSetLineThickness() {
		t("SetLineThickness[ l1, n1 ]");
	}

	@Test
	public void cmdSetPointSize() {
		t("SetPointSize[ Pt1, n1 ]");
	}

	@Test
	public void cmdSetPointStyle() {
		t("SetPointStyle[ Pt1, n1 ]");
	}

	@Test
	public void cmdSetTooltipMode() {
		t("SetTooltipMode[ obj, n1 ]");
	}

	@Test
	public void cmdSetValue() {
		t("SetValue[ b1, 0 ]");
		t("SetValue[ list1, n2, obj ]");
		t("SetValue[ obj, obj ]");
	}

	@Test
	public void cmdSetVisibleInView() {
		t("SetVisibleInView[ obj, gv, b1 ]");
	}

	@Test
	public void cmdShear() {
		t("Shear[ obj, l1, n2 ]");
	}

	@Test
	public void cmdShortestDistance() {
		t("ShortestDistance[ list1, Pt3, Pt5, b1 ]");
	}

	@Test
	public void cmdShowLabel() {
		t("ShowLabel[ obj, b1 ]");
	}

	@Test
	public void cmdShowLayer() {
		t("ShowLayer[ n1 ]");
	}

	@Test
	public void cmdShuffle() {
		t("Shuffle[ list1 ]");
	}

	@Test
	public void cmdSigmaXX() {
		t("SigmaXX[ list1 ]");
		t("SigmaXX[ list1, list1 ]");
		t("SigmaXX[ ptlist1 ]");
	}

	@Test
	public void cmdSigmaXY() {
		t("SigmaXY[ list1, list1 ]");
		t("SigmaXY[ ptlist1 ]");
	}

	@Test
	public void cmdSigmaYY() {
		t("SigmaYY[ ptlist1 ]");
	}

	@Test
	public void cmdSlider() {
		t("Slider[ n1, n4, n3, n5, n2, b1, !b1, b2, !b1 ]");
	}

	@Test
	public void cmdSlope() {
		t("Slope[ l1 ]");
	}

	@Test
	public void cmdSlowPlot() {
		t("SlowPlot[ f1 ]");
		t("SlowPlot[ f1, false ]");
	}

	@Test
	public void cmdSolveODE() {
		t("SolveODE[ f1(x), f1(x+n1), polynom1(x),n1, n3, n4, n4, prob ]");
		t("SolveODE[ f1(x), polynom1(y), n1, n2, n3, prob2 ]");
		t("SolveODE[ twovar(x,y), n1, n2, n4, prob2 ]");
		t("SolveODE[ -x]");
		t("SolveODE[ -x,Pt1]");
	}

	@Test
	public void cmdSort() {
		t("Sort[ list1 ]");
		t("Sort[ list1, list2 ]");
	}

	@Test
	public void cmdSpearman() {
		t("Spearman[ list1, list1 ]");
		t("Spearman[ ptlist1 ]");
	}

	@Test
	public void cmdStartAnimation() {
		t("StartAnimation[]");
		t("StartAnimation[ b1 ]");
		t("StartAnimation[ ptonpath1 ]");
		t("StartAnimation[ ptonpath1, b1 ]");
	}

	@Test
	public void cmdStemPlot() {
		t("StemPlot[ list1 ]");
		t("StemPlot[ list1, 0 ]");
	}

	@Test
	public void cmdStickGraph() {
		t("StickGraph[list1, list2]");
		t("StickGraph[ptlist1]");
		t("StickGraph[list1, list2,b2]");
		t("StickGraph[ptlist1,b2]");
	}

	@Test
	public void cmdStepGraph() {
		t("StepGraph[list1, list2]");
		t("StepGraph[ptlist1]");
		t("StepGraph[list1, list2,b2]");
		t("StepGraph[ptlist1,b2]");
		t("StepGraph[list1, list2,b2,n1]");
		t("StepGraph[ptlist1,b2,n2]");
	}

	@Test
	public void cmdStretch() {
		t("Stretch[ obj, l1, n2 ]");
		t("Stretch[ obj, v1]");
	}

	@Test
	public void cmdSumSquaredErrors() {
		t("SumSquaredErrors[ ptlist1, f1 ]");
	}

	@Test
	public void cmdSurdText() {
		t("SurdText[ n1 ]");
		t("SurdText[ n1, list1 ]");
		t("SurdText[ Pt1 ]");
	}

	@Test
	public void cmdSXX() {
		t("Sxx[ list1 ]");
		t("Sxx[ ptlist1 ]");
	}

	@Test
	public void cmdSXY() {
		t("Sxy[ list1, list1 ]");
		t("Sxy[ ptlist1 ]");
	}

	@Test
	public void cmdSYY() {
		t("Syy[ ptlist1 ]");
	}

	@Test
	public void cmdTake() {
		t("Take[ list1 , n1, n4 ]");
		t("Take[ list1 , n4 ]");
		t("Take[ txt , n1, n4 ]");
		t("Take[ txt , n1 ]");
	}

	@Test
	public void cmdTangent() {
		t("Tangent[ c1, c1 ]");
		t("Tangent[ l1, c1 ]");
		t("Tangent[ l1, c1 ]");
		t("Tangent[ n1, f1 ]");
		t("Tangent[ n1, f1 ]");
		t("Tangent[ Point[curve1,prob], curve1 ]");
		t("Tangent[ Pt1, c1 ]");
		t("Tangent[ Pt1, c1 ]");
		t("Tangent[ Pt1, f1 ]");
		t("Tangent[ Pt1, f1 ]");
		t("Tangent[ Pt1, Spline[listSpline]]");
	}

	@Test
	public void cmdTextfield() {
		t("InputBox[]");
		t("InputBox[ Pt2 ]");
	}

	@Test
	public void cmdText() {
		t("Text[ obj ]");
		t("Text[ obj, b1 ]");
		t("Text[ obj, Pt1 ]");
		t("Text[ obj, Pt1, b1 ]");
		t("Text[ obj, Pt1, b1, b1 ] ");
	}

	@Test
	public void cmdTextToUnicode() {
		t("TextToUnicode[ txt ]");
	}

	@Test
	public void cmdTiedRank() {
		t("TiedRank[ list1 ]");
	}

	@Test
	public void cmdTMeanEstimate() {
		t("TMeanEstimate[ list1, n2]");
		t("TMeanEstimate[ n1, n3, n2, prob]");
	}

	@Test
	public void cmdTMean2Estimate() {
		t("TMean2Estimate[ list1, list1, n3, b1 ]");
		t("TMean2Estimate[ n1, n2, n3, n4, n1, n2,  n3, b1]");
	}

	@Test
	public void cmdToolImage() {
		t("ToolImage[ n1 ]");
		t("ToolImage[ n1, Pt1 ]");
		t("ToolImage[ n1, Pt1, Pt2 ]");
	}

	@Test
	public void cmdTranslate() {
		t("Translate[ obj,v1]");
		t("Translate[v1 , Pt3 ]");
	}

	@Test
	public void cmdTranspose() {
		t("Transpose[ matrix1 ]");
	}

	@Test
	public void cmdTrapezoidalSum() {
		t("TrapezoidalSum[ f1, n2, n3, n1 ]");
	}

	@Test
	public void cmdTravelingSalesman() {
		t("TravelingSalesman[ ptlist1 ]");
	}

	@Test
	public void cmdTriangleCenter() {
		t("TriangleCenter[ Pt1,Pt2,Pt3,n1 ]");
	}

	@Test
	public void cmdCubic() {
		t("Cubic[ Pt1,Pt2,Pt3,n1 ]");
	}

	@Test
	public void cmdTriangleCurve() {
		t("TriangleCurve[ Pt1,Pt2,Pt3,A+B=C ]");
	}

	@Test
	public void cmdTrilinear() {
		t("Trilinear[Pt1,Pt2,Pt3,n1,n2,n3]");
	}

	@Test
	public void cmdTTest() {
		t("TTest[ list1, n1, \">\" ]");
		t("TTest[ n2, n3, n2, n4, \">\"]");
	}

	@Test
	public void cmdTTestPaired() {
		t("TTestPaired[ list1, list1, \">\"]");
	}

	@Test
	public void cmdTTest2() {
		t("TTest2[ list1, list1, \">\", b1 ]");
		t("TTest2[ n3, n2, n3, n4, n1, n2, \">\", b1]");
	}

	@Test
	public void cmdTurningPoint() {
		t("InflectionPoint[ polynom1 ]");
	}

	@Test
	public void cmdUnicodeToLetter() {
		t("UnicodeToLetter[ n2 ]");
	}

	@Test
	public void cmdUnicodeToText() {
		t("UnicodeToText[ list1 ]");
	}

	@Test
	public void cmdUnique() {
		t("Unique[ list1]");
	}

	@Test
	public void cmdUpperSum() {
		t("UpperSum[ f1, n2, n3, n1 ]");
	}

	@Test
	public void cmdVariance() {
		t("Variance[ list1 ]");
		t("Variance[ list1, list1 ]");
	}

	@Test
	public void cmdVector() {
		t("Vector[ Pt1 ]");
		t("Vector[ Pt3, Pt5 ]");
	}

	@Test
	public void cmdVerticalText() {
		t("VerticalText[ txt ]");
		t("VerticalText[ txt, Pt1 ]");
	}

	@Test
	public void cmdVoronoi() {
		t("Voronoi[ ptlist1 ]");
	}

	@Test
	public void cmdZip() {
		t("Zip[ t^2, t, list1]");
	}

	@Test
	public void cmdZoomIn() {
		t("ZoomIn[ n1 ]");
		t("ZoomIn[ n1, Pt1 ]");
		t("ZoomIn[ -1, -1, 1, 1 ]");
	}

	@Test
	public void cmdZoomOut() {
		t("ZoomOut[ n1 ]");
		t("ZoomOut[ n1, Pt1 ]");
	}

	@Test
	public void cmdSlopeField() {
		t("SlopeField[ x/y ]");
		t("SlopeField[ -y/x, 5 ]");
		t("SlopeField[ -y/x, 5, 0.1 ]");
		t("SlopeField[ -y/x, 5, 0.1,0,0,1,1]");
	}

	@Test
	public void cmdScientificText() {
		t("ScientificText[pi,10]");
		t("ScientificText[pi]");
	}

	@Test
	public void cmdArePerpendicular() {
		t("ArePerpendicular[l1,l2]");
	}

	@Test
	public void cmdAreConcurrent() {
		t("AreConcurrent[l1,l2, x=0]");
	}

	@Test
	public void cmdAreEqual() {
		t("AreEqual[Pt1,Pt2]");
	}

	@Test
	public void cmdToBase() {
		t("ToBase[1000000,2]");
	}

	@Test
	public void cmdFromBase() {
		t("FromBase[\"FFA23\",16]");
	}

	@Test
	public void cmdInverseLogistic() {
		t("InverseLogistic[1,2,3]");
	}

	@Test
	public void cmdInverseLogNormal() {
		t("InverseLogNormal[1,2,3]");
	}

	@Test
	public void cmdContinuedFraction() {
		t("ContinuedFraction[(sqrt(5)-1)/2]");
		t("ContinuedFraction[(sqrt(5)-1)/2,true]");
		t("ContinuedFraction[(sqrt(5)-1)/2,10]");
		t("ContinuedFraction[(sqrt(5)-1)/2,10,true]");
	}

	@Test
	public void cmdAttachCopyToView() {
		t("AttachCopyToView[Pt1,1]");
		t("AttachCopyToView[Pt1,2,Pt2,Pt3,(123,0),(0,123)]");
	}

	@Test
	public void cmdChiSquaredTest() {
		t("ChiSquaredTest[{list1}]");
		t("ChiSquaredTest[list1,list1]");
		t("ChiSquaredTest[{list1},{list1}]");
	}

	@Test
	public void cmdDivisorsSum() {
		t("DivisorsSum[3.7]");
	}

	@Test
	public void cmdDivisors() {
		t("Divisors[37]");
	}

	@Test
	public void cmdDimension() {
		t("Dimension[(3,7)]");
	}

	@Test
	public void cmdDivisorsList() {
		t("DivisorsList[42]");
	}

	@Test
	public void cmdDivision() {
		t("Division[3,7]");
		t("Division[x^2, x+1]");
	}

	@Test
	public void cmdnPr() {
		t("nPr[8,7]");
	}

	@Test
	public void cmdIsPrime() {
		t("IsPrime[0]");
	}

	@Test
	public void cmdNextPrime() {
		t("NextPrime[-1.7]");
	}

	@Test
	public void cmdPreviousPrime() {
		t("PreviousPrime[2.3]");
	}

	@Test
	public void cmdMatrixRank() {
		t("MatrixRank[{{1}}]");
	}

	@Test
	public void cmdLeftSide() {
		t("LeftSide[x^2=y^2]");
	}

	@Test
	public void cmdRightSide() {
		t("RightSide[x^2=y^2]");
	}

	@Test
	public void cmdToPolar() {
		t("ToPolar[(1,2)]");
		t("ToPolar[complex1]");
	}

	@Test
	public void cmdToComplex() {
		t("ToComplex[(1,2)]");
	}

	@Test
	public void cmdTrigSimplify() {
		t("TrigSimplify[sin(x+y)]");
	}

	@Test
	public void cmdTrigCombine() {
		t("TrigCombine[sin(x+y)]");
		t("TrigCombine[sin(x+y),sin(x)]");
	}

	@Test
	public void cmdTrigExpand() {
		t("TrigExpand[sin(x+y)]");
		t("TrigExpand[sin(x+y),sin(x)]");
	}

	@Test
	public void cmdCommonDenominator() {
		t("CommonDenominator[1/2,1/3]");
		t("CommonDenominator[1/(x-1),1/(x^2-1)]");
	}

	@Test
	public void cmdNIntegral() {
		t("NIntegral[x^2,-1,1]");
		t("NIntegral[x^2]");
		AlgebraTestHelper.shouldFail("Nintegral[exp(x),x,0,1]", "x", app);
	}

	@Test
	public void cmdRunClickScript() {
		t("RunClickScript[object]");
	}

	@Test
	public void cmdRunUpdateScript() {
		t("RunUpdateScript[object]");
	}

	@Test
	public void cmdToPoint() {
		t("ToPoint[(1,1)]");
	}

	@Test
	public void cmdTurtle() {
		t("Turtle[]");
	}

	@Test
	public void cmdTurtleForward() {
		t("TurtleForward[turtle1, 2]");
	}

	@Test
	public void cmdTurtleBack() {
		t("TurtleBack[turtle1, 2]");
	}

	@Test
	public void cmdTurtleLeft() {
		t("TurtleLeft[turtle1, 3.14]");
	}

	@Test
	public void cmdTurtleRight() {
		t("TurtleRight[turtle1, 3.14]");
	}

	@Test
	public void cmdZProportionTest() {
		t("ZProportionTest[ n1, n1, n2, \">\" ]");
	}

	@Test
	public void cmdZMeanTest() {
		t("ZMeanTest[ list1, n1, n2, \">\" ]");
		t("ZMeanTest[ n1, n1, n2, n3, \">\" ]");
	}

	@Test
	public void cmdZMean2Test() {
		t("ZMean2Test[ list1, n1, list1,n3, \">\" ]");
		t("ZMean2Test[ n1, n1, n2, n3,n1,n2,\">\" ]");
	}

	@Test
	public void cmdZProportion2Test() {
		t("ZProportion2Test[ n3,n4,n1, n1, \">\" ]");
	}

	@Test
	public void cmdZProportionEstimate() {
		t("ZproportionEstimate[n1,n2,n3]");
	}

	@Test
	public void cmdZProportion2Estimate() {
		t("Zproportion2Estimate[n1,n2,n3,n1,n2]");
	}

	@Test
	public void cmdZMeanEstimate() {
		t("ZMeanEstimate[ list1, n1, n1 ]");
		t("ZMeanEstimate[ n1, n3,n4,n1 ]");
	}

	@Test
	public void cmdZMean2Estimate() {
		t("ZMean2Estimate[ list1, list1, n3,n4, n1 ]");
		t("ZMean2Estimate[ n1, n1, n2, n3,n4,n1,n2 ]");
	}

	/**
	 * Check that all objects can be saved and reloaded.
	 */
	@AfterClass
	public static void testSaving() {
		// System.out.println(app.getXML());
		XmlTestUtil.testCurrentXML(app);

		app.getKernel().getConstruction().initUndoInfo();
		app.getKernel().getConstruction().undo();
		app.getKernel().getConstruction().redo();
	}

	@Test
	public void cmdUpdateConstruction() {
		t("UpdateConstruction[]");
		t("UpdateConstruction[n2]");
	}

	/*
	 * @Test public void cmdDensityPlot() { t("DensityPlot[sin(x)*sin(y)]");
	 * t("DensityPlot[sin(x)*sin(y),-1,1,-1,1]"); }
	 */
	@Test
	public void cmdShowAxes() {
		t("ShowAxes[]");
		t("ShowAxes[false]");
		t("ShowAxes[2,true]");
	}

	@Test
	public void cmdShowGrid() {
		t("ShowGrid[]");
		t("ShowGrid[false]");
		t("ShowGrid[2,true]");
	}

	@Test
	public void cmdCenterView() {
		t("CenterView[Pt1]");
	}

	@Test
	public void cmdSetTrace() {
		t("SetTrace[Pt1,true]");
		t("SetTrace[Pt2,false]");
	}

	@Test
	public void cmdRelation() {
		// don't test; user interaction needed
	}

	/*
	 * @Test public void cmdContourPlot(){ t("ContourPlot[x^2+y^2]"); }
	 */

	/*
	 * @Test public void cmdMatrixPlot(){
	 * t("MatrixPlot[{{0.1,0.2,0.3},{0.5,0.6,0.1}}]"); }
	 */

	@Test
	public void cmdSetSeed() {
		t("SetSeed[42]");
	}

	@Test
	public void cmdSetPerspective() {
		t("SetPerspective[\"SAG/C\"]");
	}

	@Test
	public void cmdStartLogging() {
		t("StartLogging[\"Ax\",alog]");
	}

	@Test
	public void cmdStopLogging() {
		t("StopLogging[]");
	}

	@Test
	public void cmdRemove() {
		t("Remove[{1,2,2},{2}]");
	}

	@Test
	public void cmdClosestPointRegion() {
		t("ClosestPointRegion[c1,Pt1]");
	}

	@Test
	public void cmdRate() {
		t("Rate[ n1, n2, n3]");
		t("Rate[ n1, n2, n3,1]");
		t("Rate[ n1, n2, n3,0,1]");
		t("Rate[ n1, n2, n3,0,1,42]");
	}

	@Test
	public void cmdPeriods() {
		t("Periods[ n1, n2, n3]");
		t("Periods[ n1, n2, n3,1]");
		t("Periods[ n1, n2, n3,0,1]");
	}

	@Test
	public void cmdPayment() {
		t("Payment[ n1, n2, n3]");
		t("Payment[ n1, n2, n3,1]");
		t("Payment[ n1, n2, n3,0,1]");
	}

	@Test
	public void cmdFutureValue() {
		t("FutureValue[ n1, n2, n3]");
		t("FutureValue[ n1, n2, n3,1]");
		t("FutureValue[ n1, n2, n3,0,1]");
	}

	@Test
	public void cmdPresentValue() {
		t("PresentValue[ n1, n2, n3]");
		t("PresentValue[ n1, n2, n3,1]");
		t("PresentValue[ n1, n2, n3,0,1]");
	}

	@Test
	public void cmdIntersectConic() {
		t("IntersectConic[x+z=0, x^2+y^2+z^2=1]");
		t("IntersectConic[x^2+y^2+(z-1)^2=0, x^2+y^2+z^2=0]");
	}

	@Test
	public void cmdSetSpinSpeed() {
		t("SetSpinSpeed[n1]");
	}

	@Test
	public void cmdTurtleUp() {
		t("TurtleUp[turtle1]");
	}

	@Test
	public void cmdTurtleDown() {
		t("TurtleDown[turtle1]");
	}

	@Test
	public void cmdStartRecord() {
		t("StartRecord[]");
		t("StartRecord[false]");
		t("StartRecord[true]");
	}

	@Test
	public void cmdRepeat() {
		t("Repeat[2, UpdateConstruction[]]");
	}

	@Test
	public void cmdRandomPointIn() {
		t("RandomPointIn[x^2+y^2=1]");
		t("RandomPointIn[Polygon[(0,0),(1,0),(0,1)]]");
		t("RandomPointIn[0,0,1,1]");
	}

	@Test
	public void cmdRandomDiscrete() {
		t("RandomDiscrete[{1,2,3},{4,5,6}]");
		t("RandomDiscrete[{1,2,3},{}]");
		t("RandomDiscrete[{1,2,3},{4,5,-9}]");
	}

	@Test
	public void cmdSetViewDirection() {
		t("SetViewDirection[]");
		t("SetViewDirection[(1,1,1)]");
		t("SetViewDirection[Vector[(1,1,1)]]");
	}

	@Test
	public void cmdCornerThreeD() {
		for (int i = 1; i < 12; i++) {
			t("Corner[-1," + i + "]");
		}
	}

	@Test
	public void cmdReflect3D() {
		t("Reflect[sin(x)+sin(y), x+y+z=0]");
	}

	@Test
	public void implicitSurface() {
		t("x^3+y^3+z^3=1");
	}

	@Test
	public void cmdExportImage() {
		t("ZoomIn(-10,-10,10,10)");
		t("ExportImage[\"type\",\"png\"]");
		t("ExportImage[\"scale\",0.5]");
		t("ExportImage[\"scalecm\",1]");
		t("ExportImage[\"dpi\",72]");
		t("ExportImage[\"dpi\",72, \"scalecm\", 2]");
		t("ExportImage[\"height\",300]");
		t("ExportImage[\"width\",300]");
		t("ExportImage[\"transparent\",true]");
	}

	// @Test
	// public void runLast() {
	//
	// try {
	// for (Method m : this.getClass().getMethods()) {
	// if (!"runLast".equals(m.getName())
	// && !"wait".equals(m.getName())
	// && !"notify".equals(m.getName())
	// && !"notifyAll".equals(m.getName())) {
	// safeInvoke(m);
	// }
	// }
	// Method mean = this.getClass().getMethod("cmdMeanY");
	// safeInvoke(mean);
	// } catch (NoSuchMethodException e) {
	// e.printStackTrace();
	// }
	// }

	private void safeInvoke(Method m) {
		try {
			if (m.getParameterTypes().length == 0) {
				System.out.println(m.getName());
				m.invoke(this);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			Assert.fail();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void localizationTest() {
		Assert.assertNull(app.getLocalization().getReverseCommand("x"));
	}
}