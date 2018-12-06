package org.geogebra.euclidian;

import java.util.ArrayList;

import org.geogebra.commands.AlgebraTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

@SuppressWarnings("javadoc")
public class ControllerTest {
	private static AppDNoGui app;
	private static EuclidianController ec;
	private static ArrayList<TestEvent> events = new ArrayList<>();

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		ec = app.getActiveEuclidianView().getEuclidianController();
	}

	private static String[] lastCheck;

	@Before
	public void clear() {
		AlgebraTest.enableCAS(app, true);
		events.clear();
		reset();
	}

	private static void t(String s) {
		TestEvent evt = new TestEvent(0, 0);
		evt.command = s;
		events.add(evt);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(s, false);
	}

	/**
	 * Repeat last test with dragging.
	 */
	@After
	public void repeatWithDrag() {
		if (!events.isEmpty()) {
			reset();
			for (TestEvent evt : events) {
				if (evt.inputs != null) {
					app.initDialogManager(false, evt.inputs);
				} else if (evt.command != null) {
					app.getKernel().getAlgebraProcessor()
							.processAlgebraCommand(evt.command, false);
				} else {
					ec.setLastMouseUpLoc(null);
					app.getActiveEuclidianView().getEuclidianController()
							.wrapMouseMoved(evt);
					app.getActiveEuclidianView().getEuclidianController()
							.wrapMousePressed(evt);
					app.getActiveEuclidianView().getEuclidianController()
							.wrapMouseReleased(evt);
				}
			}

			checkContent(lastCheck);
		}
	}

	private static void reset() {
		app.getKernel().clearConstruction(true);
		app.initDialogManager(true);
		app.getActiveEuclidianView().clearView();
		app.getSettings().beginBatch();
		app.getActiveEuclidianView().getSettings().reset();
		app.getActiveEuclidianView().getSettings().setShowAxes(false, false);

		app.getActiveEuclidianView().getSettings().setCoordSystem(0, 0, 50, 50,
				true);
		app.getActiveEuclidianView().getSettings()
				.setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_OFF);
		app.getSettings().endBatch();
		Log.debug(app.getActiveEuclidianView().getXmin());
		ec.setLastMouseUpLoc(null);
	}

	@Test
	public void moveTool() {
		app.setMode(EuclidianConstants.MODE_MOVE); // TODO 0
	}

	@Test
	public void pointTool() {
		app.setMode(EuclidianConstants.MODE_POINT);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)");
	}

	@Test
	public void joinTool() {
		app.setMode(EuclidianConstants.MODE_JOIN);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: x + y = 0");

	}

	@Test
	public void parallelTool() {
		app.setMode(EuclidianConstants.MODE_PARALLEL);
		t("a:x=1");
		click(100, 100);
		click(50, 100);
		checkContent("a: x = 1", "A = (2, -2)", "f: x = 2");
	}

	@Test
	public void orthogonalTool() {
		app.setMode(EuclidianConstants.MODE_ORTHOGONAL);
		t("a:x=1");
		click(100, 100);
		click(50, 100);
		checkContent("a: x = 1", "A = (2, -2)", "f: y = -2");
	}

	@Test
	public void intersectTool() {
		app.setMode(EuclidianConstants.MODE_INTERSECT);
		t("a:x=1");
		t("b:y=-1");
		click(50, 50);
		checkContent("a: x = 1", "b: y = -1", "A = (1, -1)");
	}

	@Test
	public void intersectToolAbs() {
		app.setMode(EuclidianConstants.MODE_INTERSECT);
		AlgebraTest.enableCAS(app, false);
		t("f:abs(x-2)-2");
		t("g:1-2x");
		click(100, 100);
		click(150, 250);
		checkContent("f(x) = abs(x - 2) - 2", "g(x) = 1 - 2x", "A = (1, -1)");
	}

	@Test
	public void deleteTool() {
		app.setMode(EuclidianConstants.MODE_DELETE);
		t("a:x=1");
		t("b:y=-1");
		click(50, 50);
		checkContent("a: x = 1");
		ec.setLastMouseUpLoc(null);
		click(50, 50);

		checkContent();

	}

	@Test
	public void vectorTool() {
		app.setMode(EuclidianConstants.MODE_VECTOR);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "u = (2, -2)");
	}

	@Test
	public void lineBisectorTool() {
		app.setMode(EuclidianConstants.MODE_LINE_BISECTOR); // TODO: on the fly?
		t("A = (0, 0)");
		t("B = (2, -2)");
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: -x + y = -2");
	}

	@Test
	public void angularBisectorTool() {
		app.setMode(EuclidianConstants.MODE_ANGULAR_BISECTOR); // TODO: on the
																// fly?
		t("A = (0, 0)");
		t("B = (0, -2)");
		t("C = (2, -2)");
		click(0, 0);
		click(0, 100);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (0, -2)", "C = (2, -2)",
				"f: -0.70711x + 0.70711y = -1.41421");
	}

	@Test
	public void circle2Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		click(50, 50);
		click(100, 100);
		checkContent("A = (1, -1)", "B = (2, -2)",
				AlgebraTest.unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	public void circle3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		click(0, 0);
		click(100, 100);
		click(100, 0);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (2, 0)",
				AlgebraTest.unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	public void conic5Tool() {
		app.setMode(EuclidianConstants.MODE_CONIC_FIVE_POINTS);
		click(50, 50);
		click(100, 50);
		click(50, 100);
		click(50, 0);
		click(0, 50);
		checkContent("A = (1, -1)", "B = (2, -1)", "C = (1, -2)", "D = (1, 0)",
				"E = (0, -1)", "c: x y + x - y = 1");
	}

	@Test
	public void tangentTool() {
		app.setMode(EuclidianConstants.MODE_TANGENTS);
		t("c: x^2+y^2=25");
		t("A=(3,-4)");
		click(150, 200);
		ec.setLastMouseUpLoc(null);
		click(200, 150);
		checkContent(AlgebraTest.unicode("c: x^2 + y^2 = 25"), "A = (3, -4)",
				"f: 3x - 4y = 25");
	}

	@Test
	public void relationTool() {
		app.setMode(EuclidianConstants.MODE_RELATION); // TODO 14
	}

	@Test
	public void segmentTool() {
		app.setMode(EuclidianConstants.MODE_SEGMENT);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f = 2.82843");
	}

	@Test
	public void polygonTool() {
		app.setMode(EuclidianConstants.MODE_POLYGON);
		click(0, 0);
		click(100, 0);
		click(100, 100);
		click(0, 100);
		click(0, 0);
		checkContent("A = (0, 0)", "B = (2, 0)", "C = (2, -2)", "D = (0, -2)",
				"q1 = 4", "a = 2", "b = 2", "c = 2", "d = 2");
	}

	@Test
	public void textTool() {
		app.setMode(EuclidianConstants.MODE_TEXT); // TODO 17
	}

	@Test
	public void rayTool() {
		app.setMode(EuclidianConstants.MODE_RAY);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: 2x + 2y = 0");
	}

	@Test
	public void midpointTool() {
		app.setMode(EuclidianConstants.MODE_MIDPOINT);
		click(0, 0);
		click(100, 100);
		String circle = "c: x^2 + y^2 = 25";
		t(circle);
		click(150, 200);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (1, -1)",
				AlgebraTest.unicode(circle), "D = (0, 0)");
	}

	@Test
	public void circleArc3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		click(100, 100);
		click(100, 0);
		click(0, 0);

		checkContent("A = (2, -2)", "B = (2, 0)", "C = (0, 0)", "c = 1.5708");
	}

	@Test
	public void circleSector3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		click(0, 0);
		click(100, 100);
		click(150, 0);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (3, 0)", "c = 3.14159");
	}

	@Test
	public void circumcircleArc3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		click(0, 0);
		click(50, 50);
		click(100, 0);
		checkContent("A = (0, 0)", "B = (1, -1)", "C = (2, 0)",
				"c = " + Unicode.pi);
	}

	@Test
	public void circumcircleSector3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
		click(0, 0);
		click(100, 100);
		click(200, 0);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (4, 0)", "c = 6.28319");

	}

	@Test
	public void semicircleTool() {
		app.setMode(EuclidianConstants.MODE_SEMICIRCLE);
		click(100, 100);
		click(100, 0);
		checkContent("A = (2, -2)", "B = (2, 0)", "c = " + Unicode.pi);
	}

	@Test
	public void sliderTool() {
		app.setMode(EuclidianConstants.MODE_SLIDER); // TODO 25
	}

	@Test
	public void imageTool() {
		app.setMode(EuclidianConstants.MODE_IMAGE); // TODO 26
	}

	@Test
	public void showHideObjectTool() {
		app.setMode(EuclidianConstants.MODE_SHOW_HIDE_OBJECT); // TODO 27
	}

	@Test
	public void showHideLabelTool() {
		app.setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL); // TODO 28
	}

	@Test
	public void mirrorAtPointTool() {
		app.setMode(EuclidianConstants.MODE_MIRROR_AT_POINT);
		click(0, 0);
		click(100, 100);
		String circle = "c: x^2 + y^2 = 25";
		t(circle);
		click(150, 200);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "A' = (4, -4)",
				AlgebraTest.unicode(circle),
				AlgebraTest.unicode("c': (x - 4)^2 + (y + 4)^2 = 25"));
	}

	@Test
	public void mirrorAtLineTool() {
		app.setMode(EuclidianConstants.MODE_MIRROR_AT_LINE);
		String line = "f: x - y = 4";
		t(line);
		click(0, 0);
		click(100, 100);
		String circle = "c: x^2 + y^2 = 25";
		t(circle);
		click(150, 200);
		click(100, 100);
		checkContent(line, "A = (0, 0)", "A' = (4, -4)",
				AlgebraTest.unicode(circle),
				AlgebraTest.unicode("c': (x - 4)^2 + (y + 4)^2 = 25"));
	}

	@Test
	public void translateByVectorTool() {
		app.setMode(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR); // TODO 31
	}

	@Test
	public void rotateByAngleTool() {
		app.setMode(EuclidianConstants.MODE_ROTATE_BY_ANGLE); // TODO 32
	}

	@Test
	public void dilateFromPointTool() {
		app.setMode(EuclidianConstants.MODE_DILATE_FROM_POINT); // TODO 33
	}

	@Test
	public void circlePointRadiusTool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS); // TODO 34
	}

	@Test
	public void copyVisualStyleTool() {
		app.setMode(EuclidianConstants.MODE_COPY_VISUAL_STYLE); // TODO 35
	}

	@Test
	public void angleTool() {
		app.setMode(EuclidianConstants.MODE_ANGLE);
		click(100, 100);
		click(0, 0);
		click(150, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "C = (3, 0)",
				Unicode.alpha + " = 45" + Unicode.DEGREE_STRING);
	}

	@Test
	public void vectorFromPointTool() {
		app.setMode(EuclidianConstants.MODE_VECTOR_FROM_POINT); // TODO 37
	}

	@Test
	public void distanceTool() {
		app.setMode(EuclidianConstants.MODE_DISTANCE); // TODO 38
		t("A=(0,0)");
		t("B=(0,-2)");
		t("p=Polygon(A,B,4)");
		click(50, 50);
		checkContent("A = (0, 0)", "B = (0, -2)", "p = 4", "f = 2", "g = 2",
				"C = (2, -2)", "D = (2, 0)", "h = 2", "i = 2", "perimeterp = 8",
				"Textp = \"Perimeter of p = 8\"", "Pointp = (1, -1)");
	}

	@Test
	public void moveRotateTool() {
		app.setMode(EuclidianConstants.MODE_MOVE_ROTATE); // TODO 39
	}

	@Test
	public void translateViewTool() {
		app.setMode(EuclidianConstants.MODE_TRANSLATEVIEW); // TODO 40
		t("C:Corner[4]");
		checkContent("C = (-0.02, 0.02)");
		ec.setDraggingDelay(0);
		ec.wrapMousePressed(new TestEvent(100, 100));
		ec.wrapMouseDragged(new TestEvent(200, 100), false);
		ec.wrapMouseReleased(new TestEvent(200, 100));
		checkContent("C = (-2.02, 0.02)");
		events.clear();
	}

	@Test
	public void zoomInTool() {
		app.setMode(EuclidianConstants.MODE_ZOOM_IN);
		t("C:Corner[4]");
		checkContent("C = (-0.02, 0.02)");
		click(400, 300);
		checkContent("C = (2.65333, -1.98667)");
		events.clear();

	}

	@Test
	public void zoomOutTool() {
		app.setMode(EuclidianConstants.MODE_ZOOM_OUT);
		t("C:Corner[4]");
		checkContent("C = (-0.02, 0.02)");
		click(400, 300);
		checkContent("C = (-4.03, 3.03)");
		events.clear();
	}

	@Test
	public void selectionListenerTool() {
		app.setMode(EuclidianConstants.MODE_SELECTION_LISTENER); // TODO 43
	}

	@Test
	public void polarDiameterTool() {
		app.setMode(EuclidianConstants.MODE_POLAR_DIAMETER); // TODO 44
	}

	@Test
	public void segmentFixedTool() {
		app.setMode(EuclidianConstants.MODE_SEGMENT_FIXED);
		prepareInput("2");
		click(100, 100);
		checkContent("A = (2, -2)", "B = (4, -2)", "f = 2");
	}

	@Test
	public void angleFixedTool() {
		app.setMode(EuclidianConstants.MODE_ANGLE_FIXED); // TODO 46
		t("A=(0,0)");
		t("B=(0,-2)");
		prepareInput("90deg");
		click(0, 0);
		click(0, 100);
		checkContent("A = (0, 0)", "B = (0, -2)", "A' = (2, -2)",
				Unicode.alpha + " = 90" + Unicode.DEGREE_STRING);
	}

	@Test
	public void locusTool() {
		app.setMode(EuclidianConstants.MODE_LOCUS); // TODO 47
	}

	@Test
	public void areaTool() {
		app.setMode(EuclidianConstants.MODE_AREA);
		t("A=(0,0)");
		t("B=(0,-2)");
		t("p=Polygon(A,B,4)");
		click(50, 50);
		checkContent("A = (0, 0)", "B = (0, -2)", "p = 4", "f = 2", "g = 2",
				"C = (2, -2)", "D = (2, 0)", "h = 2", "i = 2",
				"Textp = \"Area of p = 4\"", "Pointp = (1, -1)");
	}

	@Test
	public void slopeTool() {
		app.setMode(EuclidianConstants.MODE_SLOPE);
		t("f:y=-3x");
		click(50, 150);
		checkContent("f: y = -3x", "m = -3");
	}

	@Test
	public void regularPolygonTool() {
		app.setMode(EuclidianConstants.MODE_REGULAR_POLYGON);
		prepareInput("4");
		click(100, 100);
		click(0, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "poly1 = 8", "f = 2.82843",
				"g = 2.82843", "C = (-2, -2)", "D = (0, -4)", "h = 2.82843",
				"i = 2.82843");
	}

	private static void prepareInput(String... string) {
		app.initDialogManager(false, string);
		events.add(new TestEvent(0, 0).withInput(string));
	}

	@Test
	public void showCheckBoxTool() {
		app.setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX); // TODO 52
	}

	@Test
	public void compassesTool() {
		t("A = (2, -2)");
		t("B = (0, 0)");
		app.setMode(EuclidianConstants.MODE_COMPASSES);
		click(100, 100);
		click(0, 0);
		click(150, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "C = (3, 0)",
				AlgebraTest.unicode("c: (x - 3)^2 + y^2 = 8"));
	}

	@Test
	public void mirrorAtCircleTool() {
		t("x^2+y^2=8");
		t("A=(1, -1)");
		app.setMode(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		click(50, 50);
		click(100, 100);

		checkContent(AlgebraTest.unicode("c: x^2 + y^2 = 8"), "A = (1, -1)",
				"A' = (4, -4)");
	}

	@Test
	public void ellipse3Tool() {
		app.setMode(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		click(0, 0);
		click(100, 0);
		click(50, 150);
		checkContent("A = (0, 0)", "B = (2, 0)", "C = (1, -3)",
				AlgebraTest.unicode("c: (x - 1)^2 / 10 + y^2 / 9 = 1"));
	}

	@Test
	public void hyperbola3Tool() {
		app.setMode(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		click(0, 0);
		click(500, 0);
		click(100, 0);
		checkContent("A = (0, 0)", "B = (10, 0)", "C = (2, 0)",
				AlgebraTest.unicode("c: (x - 5)^2 / 9 - y^2 / 16 = 1"));
	}

	@Test
	public void parabolaTool() {
		app.setMode(EuclidianConstants.MODE_PARABOLA);
		t("y = -1");
		t("A = (2, -2)");
		click(50, 50);
		click(100, 100);
		checkContent("f: y = -1", "A = (2, -2)",
				AlgebraTest.unicode("c: x^2 - 4x + 2y = -7"));
	}

	@Test
	public void fitLineTool() {
		app.setMode(EuclidianConstants.MODE_FITLINE);
		t("A = (2, -2)");
		t("B = (3, -5)");
		t("C = (4, -8)");
		ec.setDraggingDelay(0);
		pointerDown(50, 50);
		drag(500, 500);
		checkContent("A = (2, -2)", "B = (3, -5)", "C = (4, -8)",
				"f: y = -3x + 4");
		events.clear();
	}

	@Test
	public void buttonActionTool() {
		app.setMode(EuclidianConstants.MODE_BUTTON_ACTION); // TODO 60
	}

	@Test
	public void textFieldActionTool() {
		app.setMode(EuclidianConstants.MODE_TEXTFIELD_ACTION); // TODO 61
	}

	@Test
	public void penTool() {
		app.setMode(EuclidianConstants.MODE_PEN); // TODO 62
	}

	@Test
	public void rigidPolygonTool() {
		app.setMode(EuclidianConstants.MODE_RIGID_POLYGON); // TODO 64
	}

	@Test
	public void polyLineTool() {
		app.setMode(EuclidianConstants.MODE_POLYLINE);
		click(50, 50);
		click(100, 50);
		click(100, 100);
		click(50, 50);
		checkContent("A = (1, -1)", "B = (2, -1)", "C = (2, -2)", "f = 2");

	}

	@Test
	public void probabilityCalculatorTool() {
		app.setMode(EuclidianConstants.MODE_PROBABILITY_CALCULATOR); // TODO 66
	}

	@Test
	public void attachDetachPointTool() {
		app.setMode(EuclidianConstants.MODE_ATTACH_DETACH); // TODO 67
	}

	@Test
	public void functionInspectorTool() {
		app.setMode(EuclidianConstants.MODE_FUNCTION_INSPECTOR); // TODO 68
	}

	@Test
	public void intersectionCurveTool() {
		app.setMode(EuclidianConstants.MODE_INTERSECTION_CURVE); // TODO 69
	}

	@Test
	public void vectorPolygonTool() {
		app.setMode(EuclidianConstants.MODE_VECTOR_POLYGON); // TODO 70
	}

	@Test
	public void createListTool() {
		app.setMode(EuclidianConstants.MODE_CREATE_LIST); // TODO 71
	}

	@Test
	public void complexNumberTool() {
		app.setMode(EuclidianConstants.MODE_COMPLEX_NUMBER);
		click(100, 100);
		checkContent("z_1 = 2 - 2" + Unicode.IMAGINARY);
	}

	@Test
	public void freehandShapeTool() {
		app.setMode(EuclidianConstants.MODE_FREEHAND_SHAPE); // TODO 73
	}

	@Test
	public void extremumTool() {
		app.setMode(EuclidianConstants.MODE_EXTREMUM);
		t("x*(x-2)");
		click(50, 50);
		checkContent("f(x) = x (x - 2)", "A = (1, -1)");
	}

	@Test
	public void rootsTool() {
		app.setMode(EuclidianConstants.MODE_ROOTS);
		t("x*(x-2)");
		click(50, 50);
		checkContent("f(x) = x (x - 2)", "A = (0, 0)", "B = (2, 0)");
	}

	@Test
	public void selectTool() {
		app.setMode(EuclidianConstants.MODE_SELECT); // TODO 77
	}

	@Test
	public void shapeTriangleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_TRIANGLE); // TODO 102
	}

	@Test
	public void shapeSquareTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_SQUARE); // TODO 103
	}

	@Test
	public void shapeRectangleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE); // TODO 104
	}

	@Test
	public void shapeRoundedRectangleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES); // TODO
																			// 105
	}

	@Test
	public void shapePolygonTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_POLYGON); // TODO 106
	}

	@Test
	public void shapeFreeformTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_FREEFORM); // TODO 107
	}

	@Test
	public void circleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_CIRCLE); // TODO 108
	}

	@Test
	public void ellipseTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_ELLIPSE); // TODO 109
	}

	@Test
	public void eraserTool() {
		app.setMode(EuclidianConstants.MODE_ERASER); // TODO 110
	}

	@Test
	public void highlighterTool() {
		app.setMode(EuclidianConstants.MODE_HIGHLIGHTER); // TODO 111
	}

	@Test
	public void penPanelTool() {
		app.setMode(EuclidianConstants.MODE_PEN_PANEL); // TODO 112
	}

	@Test
	public void toolsPanelTool() {
		app.setMode(EuclidianConstants.MODE_TOOLS_PANEL); // TODO 113
	}

	@Test
	public void mediaPanelTool() {
		app.setMode(EuclidianConstants.MODE_MEDIA_PANEL); // TODO 114
	}

	@Test
	public void videoTool() {
		app.setMode(EuclidianConstants.MODE_VIDEO); // TODO 115
	}

	@Test
	public void audioTool() {
		app.setMode(EuclidianConstants.MODE_AUDIO); // TODO 116
	}

	@Test
	public void geoGebraTool() {
		app.setMode(EuclidianConstants.MODE_GRAPHING); // TODO 117
	}

	@Test
	public void cameraTool() {
		app.setMode(EuclidianConstants.MODE_CAMERA); // TODO 118
	}

	private static void click(int x, int y) {
		TestEvent evt = new TestEvent(x, y);
		events.add(evt);
		ec.wrapMousePressed(evt);
		ec.wrapMouseReleased(evt);
	}

	private static void pointerDown(int x, int y) {
		TestEvent evt = new TestEvent(x, y);
		ec.wrapMousePressed(evt);
	}

	private static void drag(int x, int y) {
		TestEvent evt = new TestEvent(x, y);
		ec.wrapMouseDragged(evt, true);
		ec.wrapMouseDragged(evt, true);
		ec.wrapMouseReleased(evt);
	}

	private static void checkContent(String... desc) {
		lastCheck = desc;
		int i = 0;
		for (String label : app.getGgbApi().getAllObjectNames()) {
			GeoElement geo = app.getKernel().lookupLabel(label);

			if (i >= desc.length) {
				Assert.assertEquals("",
						geo.toString(StringTemplate.editTemplate));
			}
			if (desc[i].contains("/") && geo instanceof GeoConic) {
				((GeoConic) geo).setToSpecific();
			}
			Assert.assertEquals(desc[i],
					geo.toString(StringTemplate.editTemplate));
			i++;
		}
		Assert.assertEquals(desc.length, app.getGgbApi().getObjectNumber());
	}

}
