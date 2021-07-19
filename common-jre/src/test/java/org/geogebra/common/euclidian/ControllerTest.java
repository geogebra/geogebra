package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.test.TestEvent;
import org.geogebra.test.TestStringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

@SuppressWarnings("javadoc")
public class ControllerTest extends BaseControllerTest {
	private static ArrayList<TestEvent> events = new ArrayList<>();
	private static String[] lastCheck;
	private static boolean lastVisibility;

	private void t(String s) {
		TestEvent evt = new TestEvent(0, 0);
		evt.setCommand(s);
		events.add(evt);
		add(s);
	}

	@Before
	public void clearEvents() {
		// TODO AlgebraTest.enableCAS(app, true);
		events.clear();
		getApp().setAppletFlag(false);
	}

	/**
	 * Repeat last test with dragging.
	 */
	@After
	public void repeatWithDrag() {
		AppCommon app = getApp();
		if (!events.isEmpty()) {
			reset();
			for (TestEvent evt : events) {
				if (evt.getInputs() != null) {
					app.initDialogManager(false, evt.getInputs());
				} else if (evt.getCommand() != null) {
					app.getKernel().getAlgebraProcessor()
							.processAlgebraCommand(evt.getCommand(), false);
				} else {
					resetMouseLocation();
					app.getActiveEuclidianView().getEuclidianController()
							.wrapMouseMoved(evt);
					app.getActiveEuclidianView().getEuclidianController()
							.wrapMousePressed(evt);
					app.getActiveEuclidianView().getEuclidianController()
							.wrapMouseReleased(evt);
				}
			}

			checkContentWithVisibility(lastVisibility, lastCheck);
		}
	}

	@Test
	public void moveTool() {
		setMode(EuclidianConstants.MODE_MOVE); // TODO 0
	}

	@Test
	public void pointTool() {
		setMode(EuclidianConstants.MODE_POINT);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)");
	}

	@Test
	public void joinTool() {
		setMode(EuclidianConstants.MODE_JOIN);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: x + y = 0");

	}

	@Test
	public void parallelTool() {
		setMode(EuclidianConstants.MODE_PARALLEL);
		t("a:x=1");
		click(100, 100);
		click(50, 100);
		checkContent("a: x = 1", "A = (2, -2)", "f: x = 2");
	}

	@Test
	public void orthogonalTool() {
		setMode(EuclidianConstants.MODE_ORTHOGONAL);
		t("a:x=1");
		click(100, 100);
		click(50, 100);
		checkContent("a: x = 1", "A = (2, -2)", "f: y = -2");
	}

	@Test
	public void intersectTool() {
		setMode(EuclidianConstants.MODE_INTERSECT);
		t("a:x=1");
		t("b:y=-1");
		click(50, 50);
		checkContent("a: x = 1", "b: y = -1", "A = (1, -1)");
	}

	@Test
	public void intersectToolAbs() {
		setMode(EuclidianConstants.MODE_INTERSECT);
		// TODO AlgebraTest.enableCAS(app, false);
		t("f:abs(x-2)-2");
		t("g:1-2x");
		click(100, 100);
		click(150, 250);
		checkContent("f(x) = abs(x - 2) - 2", "g(x) = 1 - 2x", "A = (1, -1)");
	}

	@Test
	public void deleteTool() {
		setMode(EuclidianConstants.MODE_DELETE);
		t("a:x=1");
		t("b:y=-1");
		click(50, 50);
		checkContent("a: x = 1");
		resetMouseLocation();
		click(50, 50);
		checkContent();
	}

	@Test
	public void deleteToolApplet() {
		getApp().setAppletFlag(true);
		setMode(EuclidianConstants.MODE_DELETE);
		t("a:x=1");
		t("SetFixed(a,true)");
		click(50, 50);
		checkContent("a: x = 1");
		getApp().setAppletFlag(false);
		resetMouseLocation();
		click(50, 50);
		checkContent();
	}

	@Test
	public void vectorTool() {
		setMode(EuclidianConstants.MODE_VECTOR);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "u = (2, -2)");
	}

	@Test
	public void lineBisectorTool() {
		setMode(EuclidianConstants.MODE_LINE_BISECTOR); // TODO: on the fly?
		t("A = (0, 0)");
		t("B = (2, -2)");
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: -x + y = -2");
	}

	@Test
	public void angularBisectorTool() {
		setMode(EuclidianConstants.MODE_ANGULAR_BISECTOR); // TODO: on the
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
		setMode(EuclidianConstants.MODE_CIRCLE_TWO_POINTS);
		click(50, 50);
		click(100, 100);
		checkContent("A = (1, -1)", "B = (2, -2)",
				TestStringUtil.unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	public void circle3Tool() {
		setMode(EuclidianConstants.MODE_CIRCLE_THREE_POINTS);
		click(0, 0);
		click(100, 100);
		click(100, 0);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (2, 0)",
				TestStringUtil.unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	public void conic5Tool() {
		setMode(EuclidianConstants.MODE_CONIC_FIVE_POINTS);
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
		setMode(EuclidianConstants.MODE_TANGENTS);
		t("c: x^2+y^2=25");
		t("A=(3,-4)");
		click(150, 200);
		resetMouseLocation();
		click(200, 150);
		checkContent(TestStringUtil.unicode("c: x^2 + y^2 = 25"), "A = (3, -4)",
				"f: 3x - 4y = 25");
	}

	@Test
	public void relationTool() {
		setMode(EuclidianConstants.MODE_RELATION); // TODO 14
	}

	@Test
	public void segmentTool() {
		setMode(EuclidianConstants.MODE_SEGMENT);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f = 2.82843");
	}

	@Test
	public void polygonTool() {
		setMode(EuclidianConstants.MODE_POLYGON);
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
		setMode(EuclidianConstants.MODE_TEXT); // TODO 17
	}

	@Test
	public void rayTool() {
		setMode(EuclidianConstants.MODE_RAY);
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: 2x + 2y = 0");
	}

	@Test
	public void midpointTool() {
		setMode(EuclidianConstants.MODE_MIDPOINT);
		click(0, 0);
		click(100, 100);
		String circle = "c: x^2 + y^2 = 25";
		t(circle);
		click(150, 200);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (1, -1)",
				TestStringUtil.unicode(circle), "D = (0, 0)");
	}

	@Test
	public void circleArc3Tool() {
		setMode(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS);
		click(100, 100);
		click(100, 0);
		click(0, 0);

		checkContent("A = (2, -2)", "B = (2, 0)", "C = (0, 0)", "c = 1.5708");
	}

	@Test
	public void circleSector3Tool() {
		setMode(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS);
		click(0, 0);
		click(100, 100);
		click(150, 0);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (3, 0)", "c = 3.14159");
	}

	@Test
	public void circumcircleArc3Tool() {
		setMode(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
		click(0, 0);
		click(50, 50);
		click(100, 0);
		checkContent("A = (0, 0)", "B = (1, -1)", "C = (2, 0)",
				"c = " + Unicode.pi);
	}

	@Test
	public void circumcircleSector3Tool() {
		setMode(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
		click(0, 0);
		click(100, 100);
		click(200, 0);
		checkContent("A = (0, 0)", "B = (2, -2)", "C = (4, 0)", "c = 6.28319");

	}

	@Test
	public void semicircleTool() {
		setMode(EuclidianConstants.MODE_SEMICIRCLE);
		click(100, 100);
		click(100, 0);
		checkContent("A = (2, -2)", "B = (2, 0)", "c = " + Unicode.pi);
	}

	@Test
	public void sliderTool() {
		setMode(EuclidianConstants.MODE_SLIDER); // TODO 25
	}

	@Test
	public void imageTool() {
		setMode(EuclidianConstants.MODE_IMAGE); // TODO 26
	}

	@Test
	public void showHideObjectTool() {
		setMode(EuclidianConstants.MODE_SHOW_HIDE_OBJECT); // TODO 27
	}

	@Test
	public void showHideLabelTool() {
		setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL); // TODO 28
	}

	@Test
	public void mirrorAtPointTool() {
		setMode(EuclidianConstants.MODE_MIRROR_AT_POINT);
		click(0, 0);
		click(100, 100);
		String circle = "c: x^2 + y^2 = 25";
		t(circle);
		click(150, 200);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "A' = (4, -4)",
				TestStringUtil.unicode(circle),
				TestStringUtil.unicode("c': (x - 4)^2 + (y + 4)^2 = 25"));
	}

	@Test
	public void mirrorAtLineTool() {
		setMode(EuclidianConstants.MODE_MIRROR_AT_LINE);
		String line = "f: x - y = 4";
		t(line);
		click(0, 0);
		click(100, 100);
		String circle = "c: x^2 + y^2 = 25";
		t(circle);
		click(150, 200);
		click(100, 100);
		checkContent(line, "A = (0, 0)", "A' = (4, -4)",
				TestStringUtil.unicode(circle),
				TestStringUtil.unicode("c': (x - 4)^2 + (y + 4)^2 = 25"));
	}

	@Test
	public void translateByVectorTool() {
		setMode(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR); // TODO 31
	}

	@Test
	public void rotateByAngleTool() {
		setMode(EuclidianConstants.MODE_ROTATE_BY_ANGLE); // TODO 32
	}

	@Test
	public void dilateFromPointTool() {
		setMode(EuclidianConstants.MODE_DILATE_FROM_POINT); // TODO 33
	}

	@Test
	public void circlePointRadiusTool() {
		setMode(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS); // TODO 34
	}

	@Test
	public void copyVisualStyleTool() {
		setMode(EuclidianConstants.MODE_COPY_VISUAL_STYLE); // TODO 35
	}

	@Test
	public void angleTool() {
		setMode(EuclidianConstants.MODE_ANGLE);
		click(100, 100);
		click(0, 0);
		click(150, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "C = (3, 0)",
				Unicode.alpha + " = 45" + Unicode.DEGREE_STRING);
	}

	@Test
	public void vectorFromPointTool() {
		setMode(EuclidianConstants.MODE_VECTOR_FROM_POINT); // TODO 37
	}

	@Test
	public void distanceTool() {
		setMode(EuclidianConstants.MODE_DISTANCE); // TODO 38
		t("A=(0,0)");
		t("B=(0,-2)");
		t("p=Polygon(A,B,4)");
		click(50, 50);
		checkContent("A = (0, 0)", "B = (0, -2)", "p = 4", "f = 2", "g = 2",
				"C = (2, -2)", "D = (2, 0)", "h = 2", "i = 2",
				"Textp = \"Perimeter of p = 8\"");
		checkHiddenContent("perimeterp = 8", "Pointp = (1, -1)");
	}

	@Test
	public void moveRotateTool() {
		setMode(EuclidianConstants.MODE_MOVE_ROTATE); // TODO 39
	}

	@Test
	public void translateViewTool() {
		setMode(EuclidianConstants.MODE_TRANSLATEVIEW); // TODO 40
		t("C:Corner[4]");
		checkHiddenContent("C = (-0.02, 0.02)");
		dragStart(100, 100);
		dragEnd(200, 100);
		checkHiddenContent("C = (-2.02, 0.02)");
		events.clear();
	}

	@Test
	public void zoomInTool() {
		setMode(EuclidianConstants.MODE_ZOOM_IN);
		t("C:Corner[4]");
		checkHiddenContent("C = (-0.02, 0.02)");
		click(400, 300);
		checkHiddenContent("C = (2.65333, -1.98667)");
		events.clear();

	}

	@Test
	public void zoomOutTool() {
		setMode(EuclidianConstants.MODE_ZOOM_OUT);
		t("C:Corner[4]");
		checkHiddenContent("C = (-0.02, 0.02)");
		click(400, 300);
		checkHiddenContent("C = (-4.03, 3.03)");
		events.clear();
	}

	@Test
	public void selectionListenerTool() {
		setMode(EuclidianConstants.MODE_SELECTION_LISTENER); // TODO 43
	}

	@Test
	public void polarDiameterTool() {
		setMode(EuclidianConstants.MODE_POLAR_DIAMETER); // TODO 44
	}

	@Test
	public void segmentFixedTool() {
		setMode(EuclidianConstants.MODE_SEGMENT_FIXED);
		prepareInput("2");
		click(100, 100);
		checkContent("A = (2, -2)", "B = (4, -2)", "f = 2");
	}

	@Test
	public void angleFixedTool() {
		setMode(EuclidianConstants.MODE_ANGLE_FIXED); // TODO 46
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
		setMode(EuclidianConstants.MODE_LOCUS); // TODO 47
	}

	@Test
	public void areaTool() {
		setMode(EuclidianConstants.MODE_AREA);
		t("A=(0,0)");
		t("B=(0,-2)");
		t("p=Polygon(A,B,4)");
		click(50, 50);
		checkContent("A = (0, 0)", "B = (0, -2)", "p = 4", "f = 2", "g = 2",
				"C = (2, -2)", "D = (2, 0)", "h = 2", "i = 2",
				"Textp = \"Area of p = 4\"");
		checkHiddenContent("Pointp = (1, -1)");
	}

	@Test
	public void slopeTool() {
		setMode(EuclidianConstants.MODE_SLOPE);
		t("f:y=-3x");
		click(50, 150);
		checkContent("f: y = -3x", "m = -3");
	}

	@Test
	public void regularPolygonTool() {
		setMode(EuclidianConstants.MODE_REGULAR_POLYGON);
		prepareInput("4");
		click(100, 100);
		click(0, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "poly1 = 8", "f = 2.82843",
				"g = 2.82843", "C = (-2, -2)", "D = (0, -4)", "h = 2.82843",
				"i = 2.82843");
	}

	private void prepareInput(String... string) {
		getApp().initDialogManager(false, string);
		events.add(new TestEvent(0, 0).withInput(string));
	}

	@Test
	public void showCheckBoxTool() {
		setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX); // TODO 52
	}

	@Test
	public void compassesTool() {
		t("A = (2, -2)");
		t("B = (0, 0)");
		setMode(EuclidianConstants.MODE_COMPASSES);
		click(100, 100);
		click(0, 0);
		click(150, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "C = (3, 0)",
				TestStringUtil.unicode("c: (x - 3)^2 + y^2 = 8"));
	}

	@Test
	public void mirrorAtCircleTool() {
		t("c:x^2+y^2=8");
		t("A=(1, -1)");
		setMode(EuclidianConstants.MODE_MIRROR_AT_CIRCLE);
		click(50, 50);
		click(100, 100);

		checkContent(TestStringUtil.unicode("c: x^2 + y^2 = 8"), "A = (1, -1)",
				"A' = (4, -4)");
	}

	@Test
	public void ellipse3Tool() {
		setMode(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS);
		click(0, 0);
		click(100, 0);
		click(50, 150);
		checkContent("A = (0, 0)", "B = (2, 0)", "C = (1, -3)",
				"c: " + explicit("(x - 1)^2 * 9 + y^2 * 10 = 9 *10"));
	}

	@Test
	public void hyperbola3Tool() {
		setMode(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS);
		click(0, 0);
		click(500, 0);
		click(100, 0);
		checkContent("A = (0, 0)", "B = (10, 0)", "C = (2, 0)",
				"c: " + explicit("-(x - 5)^2 * 16 + y^2 * 9 = -16 * 9"));
	}

	@Test
	public void parabolaTool() {
		setMode(EuclidianConstants.MODE_PARABOLA);
		t("y = -1");
		t("A = (2, -2)");
		click(50, 50);
		click(100, 100);
		checkContent("f: y = -1", "A = (2, -2)",
				TestStringUtil.unicode("c: x^2 - 4x + 2y = -7"));
	}

	@Test
	public void fitLineTool() {
		setMode(EuclidianConstants.MODE_FITLINE);
		t("A = (2, -2)");
		t("B = (3, -5)");
		t("C = (4, -8)");
		dragStart(50, 50);
		dragEnd(500, 500);
		checkContent("A = (2, -2)", "B = (3, -5)", "C = (4, -8)",
				"f: y = -3x + 4");
		events.clear();
	}

	@Test
	public void buttonActionTool() {
		setMode(EuclidianConstants.MODE_BUTTON_ACTION); // TODO 60
	}

	@Test
	public void textFieldActionTool() {
		setMode(EuclidianConstants.MODE_TEXTFIELD_ACTION); // TODO 61
	}

	@Test
	public void penTool() {
		setMode(EuclidianConstants.MODE_PEN); // TODO 62
	}

	@Test
	public void rigidPolygonTool() {
		setMode(EuclidianConstants.MODE_RIGID_POLYGON); // TODO 64
	}

	@Test
	public void polyLineTool() {
		setMode(EuclidianConstants.MODE_POLYLINE);
		click(50, 50);
		click(100, 50);
		click(100, 100);
		click(50, 50);
		checkContent("A = (1, -1)", "B = (2, -1)", "C = (2, -2)", "f = 2");

	}

	@Test
	public void probabilityCalculatorTool() {
		setMode(EuclidianConstants.MODE_PROBABILITY_CALCULATOR); // TODO 66
	}

	@Test
	public void attachDetachPointTool() {
		setMode(EuclidianConstants.MODE_ATTACH_DETACH); // TODO 67
	}

	@Test
	public void functionInspectorTool() {
		setMode(EuclidianConstants.MODE_FUNCTION_INSPECTOR); // TODO 68
	}

	@Test
	public void intersectionCurveTool() {
		setMode(EuclidianConstants.MODE_INTERSECTION_CURVE); // TODO 69
	}

	@Test
	public void vectorPolygonTool() {
		setMode(EuclidianConstants.MODE_VECTOR_POLYGON); // TODO 70
	}

	@Test
	public void createListTool() {
		setMode(EuclidianConstants.MODE_CREATE_LIST); // TODO 71
	}

	@Test
	public void complexNumberTool() {
		setMode(EuclidianConstants.MODE_COMPLEX_NUMBER);
		click(100, 100);
		checkContent("z_{1} = 2 - 2" + Unicode.IMAGINARY);
	}

	@Test
	public void freehandShapeTool() {
		setMode(EuclidianConstants.MODE_FREEHAND_SHAPE); // TODO 73
	}

	@Test
	public void extremumTool() {
		setMode(EuclidianConstants.MODE_EXTREMUM);
		t("x*(x-2)");
		click(50, 50);
		checkContent("f(x) = x (x - 2)", "A = (1, -1)");
	}

	@Test
	public void rootsTool() {
		setMode(EuclidianConstants.MODE_ROOTS);
		t("x*(x-2)");
		click(50, 50);
		checkContent("f(x) = x (x - 2)", "A = (0, 0)", "B = (2, 0)");
	}

	@Test
	public void selectTool() {
		setMode(EuclidianConstants.MODE_SELECT); // TODO 77
	}

	@Test
	public void shapeTriangleTool() {
		setMode(EuclidianConstants.MODE_SHAPE_TRIANGLE); // TODO 102
	}

	@Test
	public void shapeSquareTool() {
		setMode(EuclidianConstants.MODE_SHAPE_SQUARE); // TODO 103
	}

	@Test
	public void shapeRectangleTool() {
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		dragStart(50, 50);
		dragEnd(200, 150);
		checkContent("q1 = 6");
		GeoElement rectangle = lookup("q1");
		assertEquals(0, rectangle.getAlphaValue(), Kernel.MIN_PRECISION);
	}

	@Test
	public void shapeRoundedRectangleTool() {
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES); // TODO
																			// 105
	}

	@Test
	public void shapePolygonTool() {
		setMode(EuclidianConstants.MODE_SHAPE_PENTAGON); // TODO 106
	}

	@Test
	public void shapeFreeformTool() {
		setMode(EuclidianConstants.MODE_SHAPE_FREEFORM); // TODO 107
	}

	@Test
	public void circleTool() {
		setMode(EuclidianConstants.MODE_SHAPE_CIRCLE); // TODO 108
	}

	@Test
	public void ellipseTool() {
		setMode(EuclidianConstants.MODE_SHAPE_ELLIPSE); // TODO 109
	}

	@Test
	public void highlighterTool() {
		setMode(EuclidianConstants.MODE_HIGHLIGHTER); // TODO 111
	}

	@Test
	public void penPanelTool() {
		setMode(EuclidianConstants.MODE_PEN_PANEL); // TODO 112
	}

	@Test
	public void toolsPanelTool() {
		setMode(EuclidianConstants.MODE_TOOLS_PANEL); // TODO 113
	}

	@Test
	public void mediaPanelTool() {
		setMode(EuclidianConstants.MODE_MEDIA_PANEL); // TODO 114
	}

	@Test
	public void videoTool() {
		setMode(EuclidianConstants.MODE_VIDEO); // TODO 115
	}

	@Test
	public void audioTool() {
		setMode(EuclidianConstants.MODE_AUDIO); // TODO 116
	}

	@Test
	public void geoGebraTool() {
		setMode(EuclidianConstants.MODE_GRAPHING); // TODO 117
	}

	@Test
	public void cameraTool() {
		setMode(EuclidianConstants.MODE_CAMERA); // TODO 118
	}

	@Test
	public void inlineTextTool() {
		setMode(EuclidianConstants.MODE_MEDIA_TEXT);
		click(30, 40);
		setMode(EuclidianConstants.MODE_MEDIA_TEXT);
		dragStart(70, 80);
		dragEnd(140, 220);
		events.clear();

		checkContent("a", "b");

		Construction cons = getApp().getKernel().getConstruction();
		GeoInlineText a = (GeoInlineText) cons.lookupLabel("a");
		GeoInlineText b = (GeoInlineText) cons.lookupLabel("b");

		Assert.assertEquals(a.getLocation().getX(), 0.6, Kernel.MAX_PRECISION);
		Assert.assertEquals(a.getLocation().getY(), -0.8, Kernel.MAX_PRECISION);

		Assert.assertEquals(b.getLocation().getX(), 1.4, Kernel.MAX_PRECISION);
		Assert.assertEquals(b.getLocation().getY(), -1.6, Kernel.MAX_PRECISION);

		Assert.assertEquals(100, a.getWidth(), Kernel.MAX_PRECISION);
		Assert.assertEquals(30, a.getHeight(), Kernel.MAX_PRECISION);

		Assert.assertEquals(100, b.getWidth(), Kernel.MAX_PRECISION);
		Assert.assertEquals(140, b.getHeight(), Kernel.MAX_PRECISION);
	}

	@Override
	protected void click(int x, int y) {
		super.click(x, y);
		events.add(new TestEvent(x, y));
	}

	@Override
	protected void checkContentWithVisibility(boolean visibility,
			String... desc) {
		lastVisibility = visibility;
		lastCheck = desc;
		super.checkContentWithVisibility(visibility, desc);
	}

	private String explicit(String string) {
		GeoConic c = (GeoConic) getApp().getKernel().getAlgebraProcessor()
				.evaluateToGeoElement(string, false);
		c.setToImplicit();
		return TestStringUtil
				.unicode(c.toValueString(StringTemplate.editTemplate));
	}

}
