/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.MoveMode;
import org.geogebra.common.euclidian.UpdateActionStore;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;
import org.geogebra.test.UndoRedoTester;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoLocusStrokeTest extends BaseUnitTest {

	private static final double r2 = sqrt(2);
	private static final double r5 = sqrt(5);
	private static final double r8 = sqrt(8);

	@Test
	public void rotateLocusStrokeTest() {
		GeoLocusStroke stroke = getInitialStroke();

		stroke.rotate(new MyDouble(getKernel(), PI / 3));

		double arg1 = -3 * PI / 4 + PI / 3;
		double arg2 = -PI / 4 + PI / 3;
		double arg3 = atan(0.5) + PI / 3;
		double arg4 = PI / 2 + PI / 3;

		MyPoint[] rotatedPoints = new MyPoint[] {
				new MyPoint(r2 * cos(arg1), r2 * sin(arg1)),
				new MyPoint(r2 * cos(arg2), r2 * sin(arg2)),
				new MyPoint(r5 * cos(arg3), r5 * sin(arg3)),
				new MyPoint(cos(arg4), sin(arg4)),
				new MyPoint(0, 0)
		};

		assertPointsEqual(rotatedPoints, stroke.getPoints());
	}

	@Test
	public void rotateLocusStrokeAroundPointTest() {
		GeoLocusStroke stroke = getInitialStroke();

		stroke.rotate(new MyDouble(getKernel(), PI / 6),
				new GeoPoint(getConstruction(), 1.0, 1.0, 1.0));

		double arg1 = -3 * PI / 4 + PI / 6;
		double arg2 = -PI / 2 + PI / 6;
		double arg3 = PI / 6;
		double arg4 = PI + PI / 6;
		double arg5 = -3 * PI / 4 + PI / 6;

		MyPoint[] rotatedPoints = new MyPoint[] {
				new MyPoint(r8 * cos(arg1) + 1, r8 * sin(arg1) + 1),
				new MyPoint(2 * cos(arg2) + 1, 2 * sin(arg2) + 1),
				new MyPoint(cos(arg3) + 1, sin(arg3) + 1),
				new MyPoint(cos(arg4) + 1, sin(arg4) + 1),
				new MyPoint(r2 * cos(arg5) + 1, r2 * sin(arg5) + 1)
		};

		assertPointsEqual(rotatedPoints, stroke.getPoints());
	}

	@Test
	public void mirrorLocusStrokeOnPointTest() {
		GeoLocusStroke stroke = getInitialStroke();

		stroke.mirror(new Coords(3, 2));

		MyPoint[] mirroredPoints = new MyPoint[] {
				new MyPoint(7, 5),
				new MyPoint(5, 5),
				new MyPoint(4, 3),
				new MyPoint(6, 3),
				new MyPoint(6, 4)
		};

		assertPointsEqual(mirroredPoints, stroke.getPoints());
	}

	@Test
	public void mirrorLocusStrokeOnLineTest() {
		GeoLocusStroke stroke = getInitialStroke();

		GeoLine line = new GeoLine(getConstruction());
		GeoVec3D.lineThroughPointVector(
				new GeoPoint(getConstruction(), 2, 0, 1.0),
				new GeoVector(getConstruction(), null, 1, 1, 1),
				line);

		stroke.mirror(line);

		MyPoint[] mirroredPoints = new MyPoint[] {
				new MyPoint(1, -3),
				new MyPoint(1, -1),
				new MyPoint(3, 0),
				new MyPoint(3, -2),
				new MyPoint(2, -2)
		};

		assertPointsEqual(mirroredPoints, stroke.getPoints());
	}

	@Test
	public void dilateLocusStrokeTest() {
		GeoLocusStroke stroke = getInitialStroke();

		stroke.dilate(new MyDouble(getKernel(), 2.0), new Coords(1, 0));

		MyPoint[] dilatedPoints = new MyPoint[] {
				new MyPoint(-3, -2),
				new MyPoint(1, -2),
				new MyPoint(3, 2),
				new MyPoint(-1, 2),
				new MyPoint(-1, 0)
		};

		assertPointsEqual(dilatedPoints, stroke.getPoints());
	}

	@Test
	public void undoRedoTest() {
		getApp().setGraphingConfig();
		UndoRedoTester undoRedoTester = new UndoRedoTester(getApp());
		undoRedoTester.setupUndoRedo();

		addAvInput("stroke = PenStroke((1, 3), (4, 3))");
		undoRedoTester.undo();
		GeoLocusStroke stroke = undoRedoTester.getAfterRedo("stroke");
		assertThat(stroke, is(notNullValue()));
	}

	@Issue("APPS-5775")
	@Test
	public void testUndoDrag() {
		GeoElement stroke = addAvInput("stroke = PenStroke((-3, 3), (4, 3), (2,5))");
		activateUndo();
		SelectionManager selectionManager = getApp().getSelectionManager();
		selectionManager.addSelectedGeo(stroke);
		UpdateActionStore updateActionStore = new UpdateActionStore(selectionManager,
				getConstruction().getUndoManager());
		updateActionStore.storeSelection(MoveMode.DEPENDENT);

		MoveGeos.moveObjects(List.of(stroke), new Coords(1, 0), null, null,
				getApp().getActiveEuclidianView());
		updateActionStore.storeUndo();
		assertThat(stroke.toValueString(StringTemplate.defaultTemplate),
				startsWith("PenStrokeBezier[-2"));

		getConstruction().getUndoManager().undo();
		assertThat(stroke.toValueString(StringTemplate.defaultTemplate),
				startsWith("PenStrokeBezier[-3"));
	}

	@Test
	@Issue("MOW-1826")
	public void undoDragShouldPreserveBezierPoints() {
		GeoLocusStroke stroke = addAvInput("stroke = PenStroke()");
		stroke.appendPointArray(List.of(new MyPoint(-3, 3), new MyPoint(4, 3),
				new MyPoint(2, 5), new MyPoint(5, 2)), null);
		String originalPath = toSvg(stroke.getPoints());
		activateUndo();

		SelectionManager selectionManager = getApp().getSelectionManager();
		selectionManager.addSelectedGeo(stroke);
		UpdateActionStore updateActionStore = new UpdateActionStore(selectionManager,
				getConstruction().getUndoManager());
		updateActionStore.storeSelection(MoveMode.DEPENDENT);

		MoveGeos.moveObjects(List.of(stroke), new Coords(1, 0), null, null,
				getApp().getActiveEuclidianView());
		updateActionStore.storeUndo();

		getConstruction().getUndoManager().undo();
		GeoLocusStroke restored = (GeoLocusStroke) lookup("stroke");
		assertEquals(originalPath, toSvg(restored.getPoints()));
	}

	@Test
	public void locusBasedOnStrokeShouldHaveEnoughPoints() {
		addAvInput("stroke = PenStroke((1, 3), (4, 3), (2,5))");
		add("A=Point(stroke)");
		add("B=A-(1,1)");
		add("loc=Locus(B,A)");
		GeoElement perimeter = add("Perimeter(loc)");
		assertThat(perimeter, hasValue("8.22"));
	}

	@Test
	public void testDeletePart() {
		GeoLocusStroke stroke = add("stroke=PenStroke()");
		stroke.getPoints().addAll(List.of(
				new MyPoint(0, 0, SegmentType.MOVE_TO),
				new MyPoint(1, 0, SegmentType.LINE_TO),
				new MyPoint(Double.NaN, Double.NaN, SegmentType.LINE_TO),
				new MyPoint(2, 0, SegmentType.MOVE_TO),
				new MyPoint(3, 0, SegmentType.MOVE_TO)
		));
		stroke.deletePart(new Rectangle2D.Double(1.9, -0.1, .2, .2));
		List<MyPoint> expected = List.of(
				new MyPoint(0, 0, SegmentType.MOVE_TO),
				new MyPoint(1, 0, SegmentType.LINE_TO),
				new MyPoint(Double.NaN, Double.NaN, SegmentType.LINE_TO),
				new MyPoint(2.1, -0.0, SegmentType.MOVE_TO),
				new MyPoint(3, 0, SegmentType.MOVE_TO),
				new MyPoint(Double.NaN, Double.NaN, SegmentType.LINE_TO)
		);
		assertEquals(expected.stream().map(Object::toString).collect(Collectors.joining()),
				stroke.getPoints().stream().map(Object::toString).collect(Collectors.joining()));
	}

	@Test
	public void testDeletePartSvg() {
		String svgPath = "M-4.07 5.69C-4.216666666666667 5.5566666666666675,-4.333333333333334 "
				+ "5.433333333333334,-4.42 5.32L-4.678279142647833 4.930000000000002M-2.05 "
				+ "1.5600000000000094L-2.05 1.56C-1.7699999999999996 1.433333333333333,"
				+ "-1.5133333333333328 1.3266666666666662,-1.28 "
				+ "1.2400000000000002C-1.0466666666666673 "
				+ "1.1533333333333342,-0.853333333333334 1.0866666666666676,-0.7 "
				+ "1.04C-0.546666666666666 0.9933333333333326,-0.44333333333333264 "
				+ "0.9599999999999993,-0.39 0.94C-0.33666666666666745 "
				+ "0.9200000000000008,-0.31000000000000083 0.8933333333333342,-0.31 "
				+ "0.8600000000000001C-0.3099999999999993 0.8266666666666662,-0.31999999999999923 "
				+ "0.7866666666666662,-0.33999999999999997 0.74C-0.36000000000000076 "
				+ "0.6933333333333339,-0.3933333333333341 0.6366666666666673,-0.44 "
				+ "0.5700000000000001C-0.48666666666666597 0.503333333333333,-0.526666666666666 "
				+ "0.42999999999999955,-0.56 0.35C-0.5933333333333342 "
				+ "0.27000000000000046,-0.6366666666666675 0.18000000000000047,-0.69 "
				+ "0.08C-0.7433333333333325 -0.020000000000000434,-0.7833333333333325 "
				+ "-0.11666666666666708,-0.81 -0.21C-0.8366666666666677 "
				+ "-0.3033333333333329,-0.8566666666666676 -0.3966666666666662,-0.87 "
				+ "-0.49C-0.8833333333333324 -0.5833333333333338,-0.8966666666666658 "
				+ "-0.6700000000000005,-0.91 -0.75C-0.9233333333333344 "
				+ "-0.8299999999999996,-0.930000000000001 -0.9033333333333329,-0.93 "
				+ "-0.97C-0.929999999999999 -1.0366666666666673,-0.9266666666666658 "
				+ "-1.1066666666666674,-0.92 -1.1800000000000002C-0.9133333333333343 "
				+ "-1.2533333333333332,-0.900000000000001 -1.3233333333333333,-0.88 "
				+ "-1.3900000000000001C-0.8599999999999991 -1.4566666666666672,-0.8333333333333324 "
				+ "-1.5200000000000005,-0.8 -1.58C-0.7666666666666677 "
				+ "-1.6399999999999997,-0.7433333333333344 -1.6899999999999997,-0.73 "
				+ "-1.73C-0.7166666666666657 -1.7700000000000005,-0.699999999999999 "
				+ "-1.8033333333333337,-0.6799999999999999 -1.83L-0.65 -1.87";
		GeoLocusStroke stroke = fromSvg(svgPath);
		stroke.deletePart(new Rectangle2D.Double(-2.43, 1.5100000000000005,
				0.4, 0.40000000000000013));

		assertEquals("M-4.0700E0 5.6900E0C-4.2167E0 5.5567E0,-4.3333E0 5.4333E0,-4.4200E0"
				+ " 5.3200E0L-4.6783E0 4.9300E0M-2.0300E0 1.5510E0L-1.2800E0 1.2400E0C-1.0467E0"
				+ " 1.1533E0,-8.5333E-1 1.0867E0,-7.0000E-1 1.0400E0C-5.4667E-1 9.9333E-1,"
				+ "-4.4333E-1 9.6000E-1,-3.9000E-1 9.4000E-1C-3.3667E-1 9.2000E-1,-3.1000E-1"
				+ " 8.9333E-1,-3.1000E-1 8.6000E-1C-3.1000E-1 8.2667E-1,-3.2000E-1 7.8667E-1,"
				+ "-3.4000E-1 7.4000E-1C-3.6000E-1 6.9333E-1,-3.9333E-1 6.3667E-1,-4.4000E-1 "
				+ "5.7000E-1C-4.8667E-1 5.0333E-1,-5.2667E-1 4.3000E-1,-5.6000E-1 3.5000E-1C"
				+ "-5.9333E-1 2.7000E-1,-6.3667E-1 1.8000E-1,-6.9000E-1 8.0000E-2C-7.4333E-1"
				+ " -2.0000E-2,-7.8333E-1 -1.1667E-1,-8.1000E-1 -2.1000E-1C-8.3667E-1 -3.0333E-1,"
				+ "-8.5667E-1 -3.9667E-1,-8.7000E-1 -4.9000E-1C-8.8333E-1 -5.8333E-1,-8.9667E-1"
				+ " -6.7000E-1,-9.1000E-1 -7.5000E-1C-9.2333E-1 -8.3000E-1,-9.3000E-1 -9.0333E-1,"
				+ "-9.3000E-1 -9.7000E-1C-9.3000E-1 -1.0367E0,-9.2667E-1 -1.1067E0,-9.2000E-1"
				+ " -1.1800E0C-9.1333E-1 -1.2533E0,-9.0000E-1 -1.3233E0,-8.8000E-1 -1.3900E0C"
				+ "-8.6000E-1 -1.4567E0,-8.3333E-1 -1.5200E0,-8.0000E-1 -1.5800E0C-7.6667E-1"
				+ " -1.6400E0,-7.4333E-1 -1.6900E0,-7.3000E-1 -1.7300E0C-7.1667E-1 -1.7700E0,"
				+ "-7.0000E-1 -1.8033E0,-6.8000E-1 -1.8300E0L-6.5000E-1 -1.8700E0",
				toSvg(stroke.getPoints()));
	}

	private GeoLocusStroke getInitialStroke() {
		ArrayList<MyPoint> initialPoints = new ArrayList<>(Arrays.asList(
				new MyPoint(-1, -1),
				new MyPoint(1, -1),
				new MyPoint(2, 1),
				new MyPoint(0, 1),
				new MyPoint(0, 0)
		));

		GeoLocusStroke stroke = new GeoLocusStroke(getConstruction());
		stroke.setPoints(initialPoints);

		return stroke;
	}

	private GeoLocusStroke fromSvg(String svgPath) {
		GeoLocusStroke stroke = add("stroke=PenStroke()");
		String[] tokens = svgPath.split("(?=[MCL ,])|(?<=[MCL ,])");
		int controls = 0;
		for (int i = 0; i < tokens.length; i += 4) {
			String type = tokens[i];
			double x = Double.parseDouble(tokens[i + 1]);
			double y = Double.parseDouble(tokens[i + 3]);
			switch (type) {
			case "M" -> {
				stroke.ensureTrailingNaN(stroke.getPoints());
				stroke.getPoints().add(new MyPoint(x, y, SegmentType.MOVE_TO));
			}
			case "L" -> stroke.getPoints().add(new MyPoint(x, y, SegmentType.LINE_TO));
			case "C" -> {
				stroke.getPoints().add(new MyPoint(x, y, SegmentType.CONTROL));
				controls = 1;
			}
			case "," -> {
				stroke.getPoints().add(new MyPoint(x, y,
						controls == 2 ? SegmentType.CURVE_TO : SegmentType.CONTROL));
				controls = (controls + 1) % 3;
			}
			}
		}
		return stroke;
	}

	private String toSvg(List<MyPoint> pts) {
		StringBuilder path = new StringBuilder();
		StringBuilder control = new StringBuilder();
		ScientificFormatAdapter format = FormatFactory.getPrototype().getFastScientificFormat(5);
		boolean skip = false;
		for (MyPoint pt: pts) {
			assert !skip || pt.getSegmentType() == SegmentType.MOVE_TO;
			skip = false;
			if (!pt.isDefined()) {
				skip = true;
				continue;
			}
			switch (pt.getSegmentType()) {
			case MOVE_TO, LINE_TO -> path.append(pt.getSegmentType().name()
					.charAt(0)).append(format.format(pt.x)).append(" ").append(format.format(pt.y));
			case CONTROL -> control.append(format.format(pt.x)).append(" ")
					.append(format.format(pt.y)).append(",");
			case CURVE_TO -> {
				assert !control.isEmpty();
				path.append("C").append(control).append(format.format(pt.x)).append(" ")
						.append(format.format(pt.y));
				control.setLength(0);
			}
			}
		}
		return path.toString();
	}

	private void assertPointsEqual(MyPoint[] expected, ArrayList<MyPoint> actual) {
		for (int i = 0; i < expected.length; i++) {
			assertEquals("differ at element " + i + ".x", expected[i].x,
					actual.get(i).x, Kernel.MAX_PRECISION);
			assertEquals("differ at element " + i + ".x", expected[i].y,
					actual.get(i).y, Kernel.MAX_PRECISION);
		}
	}
}
