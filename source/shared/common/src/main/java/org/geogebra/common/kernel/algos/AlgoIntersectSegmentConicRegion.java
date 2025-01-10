package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * @author thilina
 *
 */
public class AlgoIntersectSegmentConicRegion extends AlgoIntersect {

	protected OutputHandler<GeoSegment> outputSegments;

	protected GeoPoint[] intersectPoints;
	protected GeoPoint[] closureIntersect;

	/**
	 * when the Conic is a conic part. (Upto today(13/06/2015) only circle parts
	 * are available. so this won't work with other conic parts)
	 */
	protected GeoPoint midPoint;
	protected GeoPoint[] endPoints;
	protected GeoSegment[] closureSegments;

	protected GeoSegment segment;
	protected GeoConic conic;

	private int numberOfLineParts;
	private int numberOfOutputLines;

	protected boolean hasLabels;

	/**
	 * constructor with label assignment for output
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            label array
	 * @param segment
	 *            input segment
	 * @param conic
	 *            input conic
	 */
	public AlgoIntersectSegmentConicRegion(Construction cons, String[] labels,
			GeoSegment segment, GeoConic conic) {
		this(cons, segment, conic);

		setLabels(labels);
		hasLabels = true;

		update();
	}

	/**
	 * Common constructor for AlgoIntersectSegmentConicRegion.
	 * 
	 * @param cons
	 *            construction
	 * @param segment
	 *            input segment
	 * @param conic
	 *            input conic
	 */
	public AlgoIntersectSegmentConicRegion(Construction cons,
			GeoSegment segment, GeoConic conic) {
		super(cons);
		this.segment = segment;
		this.conic = conic;

		initElements();

		setInputOutput();

		setDependencies();

		compute();
	}

	/**
	 * sets the labels of the output segments
	 * 
	 * @param labels
	 *            String[]
	 */
	protected void setLabels(String[] labels) {

		if (labels != null && labels.length == 1 && outputSegments.size() > 1
				&& labels[0] != null && !labels[0].equals("")) {
			outputSegments.setIndexLabels(labels[0]);

		} else {
			outputSegments.setLabels(labels);
		}

	}

	@Override
	public void compute() {

		// calculate intersect point between input segment and input Conic curve
		AlgoIntersectSegmentConicRegion.intersectSegmentConic(getSegment(),
				getConic(), intersectPoints);

		// if conic is a conic part
		if (getConic().getType() == GeoConicNDConstants.CONIC_CIRCLE
				&& getConic().isLimitedPath()) {
			// derive midpoint, endpoint, closure segments of the input conic
			AlgoIntersectSegmentConicRegion.calcConicClosure(getConic(),
					midPoint, endPoints, closureSegments);

			// calculate intersection points between input segment and closure
			// segments
			AlgoIntersectSegmentConicRegion
					.intersectSegmentConicClosureSegments(getSegment(),
							getConic(), closureSegments, closureIntersect,
							intersectPoints);
		}

		numberOfLineParts = 1;
		calcNoOfLineParts();

		GeoPoint P;
		GeoPoint Q;

		numberOfOutputLines = 0;
		switch (numberOfLineParts) {
		case 1: {

			outputSegments.adjustOutputSize(1, false);

			P = getSegment().getStartPoint();
			Q = getSegment().getEndPoint();

			if (getConic().isInRegion((P.inhomX + Q.inhomX) / 2.0d,
					(P.inhomY + Q.inhomY) / 2.0d)) {
				outputSegments.getElement(0).set(P, Q, getSegment());
				numberOfOutputLines++;
				Log.debug("case 1");
				break;
			}

			outputSegments.getElement(0).setUndefined();

			Log.debug("case 1");
			break;
		}
		case 2: {

			outputSegments.adjustOutputSize(1, false);

			P = getSegment().getStartPoint();
			Q = getSegment().getEndPoint();

			// select intersection point
			GeoPoint pnt;
			if (intersectPoints[0].isDefined()) {
				pnt = intersectPoints[0];
			} else if (intersectPoints[1].isDefined()) {
				pnt = intersectPoints[1];
			} else if (closureIntersect[0].isDefined()) {
				pnt = closureIntersect[0];
			} else {
				pnt = closureIntersect[1];
			}

			// set output intersect segments
			if (!DoubleUtil.isZero(P.distance(pnt))) {
				if (getConic().isInRegion((P.inhomX + pnt.inhomX) / 2.0d,
						(P.inhomY + pnt.inhomY) / 2.0d)) {

					outputSegments.getElement(0).set(P, pnt, getSegment());
					numberOfOutputLines++;
					Log.debug("case 2");
					break;
				}
			}
			if (!DoubleUtil.isZero(pnt.distance(Q))) {
				if (getConic().isInRegion((pnt.inhomX + Q.inhomX) / 2.0d,
						(pnt.inhomY + Q.inhomY) / 2.0d)) {

					outputSegments.getElement(0).set(pnt, Q, getSegment());
					numberOfOutputLines++;
					Log.debug("case 2");
					break;
				}
			}

			outputSegments.getElement(0).setUndefined();
			Log.debug("case 2");
			break;
		}

		case 3: {
			int[] order = { 0, 1, 2, 3 };
			boolean[] isPartValid = { false, false, false };

			// select the intersecting points
			GeoPoint[] pnt = new GeoPoint[4];
			pnt[0] = getSegment().getStartPoint();
			int count = 1;
			if (intersectPoints[0].isDefined()) {
				pnt[count] = intersectPoints[0];
				count++;
			}
			if (intersectPoints[1].isDefined()) {
				pnt[count] = intersectPoints[1];
				count++;
			}
			if (closureIntersect[0].isDefined()) {
				pnt[count] = closureIntersect[0];
				count++;
			}
			if (closureIntersect[1].isDefined()) {
				pnt[count] = closureIntersect[1];
				count++;
			}
			pnt[3] = getSegment().getEndPoint();

			// sorting intersection points
			double t1 = getSegment().getPossibleParameter(pnt[1].getCoords());
			double t2 = getSegment().getPossibleParameter(pnt[2].getCoords());
			if (t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
				int intTemp = order[1];
				order[1] = order[2];
				order[2] = intTemp;
			}

			// counting no of output segments and assining them as output
			count = 0;
			for (int i = 0; i < 3; i++) {
				if (!DoubleUtil.isZero(pnt[order[i]].distance(pnt[order[i + 1]]))) {
					if (getConic().isInRegion(
							(pnt[order[i]].inhomX + pnt[order[i + 1]].inhomX)
									/ 2.0d,
							(pnt[order[i]].inhomY + pnt[order[i + 1]].inhomY)
									/ 2.0d)) {
						isPartValid[i] = true;
						count++;
					}
				}
			}

			outputSegments.adjustOutputSize(count, false);
			numberOfOutputLines = count;

			count = 0;
			for (int i = 0; i < 3; i++) {
				if (isPartValid[i]) {
					outputSegments.getElement(count).set(pnt[order[i]],
							pnt[order[i + 1]], getSegment());
					count++;
				}
			}

			Log.debug("case 3");
			break;
		}
		case 4: {
			int[] order = { 0, 1, 2, 3, 4 };
			boolean[] isPartValid = { false, false, false, false };

			// select the intersecting points
			GeoPoint[] pnt = new GeoPoint[5];
			int count = 1;
			pnt[0] = getSegment().getStartPoint();
			if (intersectPoints[0].isDefined()) {
				pnt[count] = intersectPoints[0];
				count++;
			}
			if (intersectPoints[1].isDefined()) {
				pnt[count] = intersectPoints[1];
				count++;
			}
			if (closureIntersect[0].isDefined()) {
				pnt[count] = closureIntersect[0];
				count++;
			}
			if (closureIntersect[1].isDefined()) {
				pnt[count] = closureIntersect[1];
				count++;
			}
			pnt[4] = getSegment().getEndPoint();

			// sorting intersection points order on input segment starting from
			// inputsegment.getStartPoint()
			double temp;
			double t1 = getSegment().getPossibleParameter(pnt[1].getCoords());
			double t2 = getSegment().getPossibleParameter(pnt[2].getCoords());
			double t3 = getSegment().getPossibleParameter(pnt[3].getCoords());

			int intTemp;
			if (t1 > t2) {
				temp = t1;
				t1 = t2;
				t2 = temp;
				intTemp = order[1];
				order[1] = order[2];
				order[2] = intTemp;
			}
			if (t1 > t3) {
				temp = t1;
				t1 = t3;
				t3 = temp;
				intTemp = order[1];
				order[1] = order[3];
				order[3] = intTemp;
			}
			if (t2 > t3) {
				temp = t2;
				t2 = t3;
				t3 = temp;
				intTemp = order[2];
				order[2] = order[3];
				order[3] = intTemp;
			}

			// counting no of output segments and assining them as output
			count = 0;
			for (int i = 0; i < 4; i++) {
				if (!DoubleUtil.isZero(pnt[order[i]].distance(pnt[order[i + 1]]))) {
					if (getConic().isInRegion(
							(pnt[order[i]].inhomX + pnt[order[i + 1]].inhomX)
									/ 2.0d,
							(pnt[order[i]].inhomY + pnt[order[i + 1]].inhomY)
									/ 2.0d)) {
						isPartValid[i] = true;
						count++;
					}
				}
			}

			outputSegments.adjustOutputSize(count, false);
			numberOfOutputLines = count;

			count = 0;
			for (int i = 0; i < 4; i++) {
				if (isPartValid[i]) {
					outputSegments.getElement(count).set(pnt[order[i]],
							pnt[order[i + 1]], getSegment());
					count++;
				}
			}

			Log.debug("case 4");
			break;
		}
		case 5: {

			double t1, t2, t3, t4, temp;
			int[] order = { 0, 1, 2, 3, 4 };
			boolean[] isPartValid = { false, false, false, false, false };

			// get intersect point on segment
			GeoPoint[] pnt = { getSegment().getStartPoint(), intersectPoints[0],
					intersectPoints[1], closureIntersect[0],
					closureIntersect[1], getSegment().getEndPoint() };

			// sorting intersection points order on input segment starting from
			// inputsegment.getStartPoint()
			t1 = getSegment().getPossibleParameter(pnt[1].getCoords());
			t2 = getSegment().getPossibleParameter(pnt[2].getCoords());
			t3 = getSegment().getPossibleParameter(pnt[3].getCoords());
			t4 = getSegment().getPossibleParameter(pnt[4].getCoords());
			int intTemp;
			if (t1 > t2) {
				temp = t1;
				t1 = t2;
				t2 = temp;
				intTemp = order[1];
				order[1] = order[2];
				order[2] = intTemp;
			}
			if (t1 > t3) {
				temp = t1;
				t1 = t3;
				t3 = temp;
				intTemp = order[1];
				order[1] = order[3];
				order[3] = intTemp;
			}
			if (t1 > t4) {
				temp = t1;
				t1 = t4;
				t4 = temp;
				intTemp = order[1];
				order[1] = order[4];
				order[4] = intTemp;
			}
			if (t2 > t3) {
				temp = t2;
				t2 = t3;
				t3 = temp;
				intTemp = order[2];
				order[2] = order[3];
				order[3] = intTemp;
			}
			if (t2 > t4) {
				temp = t2;
				t2 = t4;
				t4 = temp;
				intTemp = order[2];
				order[2] = order[4];
				order[4] = intTemp;
			}
			if (t3 > t4) {
				temp = t3;
				t3 = t4;
				t4 = temp;
				intTemp = order[3];
				order[3] = order[4];
				order[4] = intTemp;
			}

			// counting no of output segments and assining them as output
			int count = 0;
			for (int i = 0; i < 4; i++) {
				if (!DoubleUtil.isZero(pnt[order[i]].distance(pnt[order[i + 1]]))) {
					if (getConic().isInRegion(
							(pnt[order[i]].inhomX + pnt[order[i + 1]].inhomX)
									/ 2.0d,
							(pnt[order[i]].inhomY + pnt[order[i + 1]].inhomY)
									/ 2.0d)) {
						isPartValid[i] = true;
						count++;
					}
				}
			}

			outputSegments.adjustOutputSize(count, false);
			numberOfOutputLines = count;

			count = 0;
			for (int i = 0; i < 4; i++) {
				if (isPartValid[i]) {
					outputSegments.getElement(count).set(pnt[order[i]],
							pnt[order[i + 1]], getSegment());
					count++;
				}
			}

			Log.debug("case 5");
			break;
		}
		default: {
			outputSegments.adjustOutputSize(1, false);
			outputSegments.getElement(0).setUndefined();
			Log.debug("case default");
		}
		}

		if (hasLabels) {
			outputSegments.updateLabels();
		}
	}

	/**
	 * initialize Elements. CreateOutput is called inside
	 */
	protected void initElements() {

		numberOfLineParts = 1;
		numberOfOutputLines = 0;
		hasLabels = false;

		intersectPoints = new GeoPoint[2];
		endPoints = new GeoPoint[2];
		closureSegments = new GeoSegment[2];
		closureIntersect = new GeoPoint[2];
		for (int i = 0; i < intersectPoints.length; i++) {
			intersectPoints[i] = new GeoPoint(getConstruction());
			endPoints[i] = new GeoPoint(getConstruction());
			closureSegments[i] = new GeoSegment(getConstruction());
			closureIntersect[i] = new GeoPoint(getConstruction());
		}

		midPoint = new GeoPoint(getConstruction());

		outputSegments = createOutputSegments();

	}

	/**
	 * calculates the numberOfLineParts and initialize the output points
	 */
	private void calcNoOfLineParts() {

		// decide number of line parts using calculated intersection points
		for (int i = 0; i < 2; i++) {
			if (intersectPoints[i].isDefined()) {
				numberOfLineParts += 1;
			}
			if (closureIntersect[i].isDefined()) {
				numberOfLineParts += 1;
			}
		}
	}

	/**
	 * /** calculates and assigns the intersection point between given segment
	 * and conic curve
	 * 
	 * @param segment
	 *            GeoSegment
	 * @param conic
	 *            GeoConic
	 * @param intersectPoints
	 *            GeoPoint[2]
	 */
	public static void intersectSegmentConic(GeoSegment segment, GeoConic conic,
			GeoPoint[] intersectPoints) {
		/*
		 * calculate and assign intersection point between segment(as line) and
		 * conic(full conic)
		 */
		AlgoIntersectLineConic.intersectLineConic(segment, conic,
				intersectPoints, Kernel.MIN_PRECISION);

		for (int i = 0; i < 2; i++) {
			if (intersectPoints[i].isDefined()) {

				// checks the validity and the incidence on both conic and
				// segment of intersection point
				if (!(segment.isOnPath(intersectPoints[i], Kernel.MIN_PRECISION)
						&& conic.isOnPath(intersectPoints[i],
								Kernel.MIN_PRECISION))) {
					intersectPoints[i].setUndefined();
				}
			}
		}

	}

	/**
	 * calculate and set closure of the conic part. here midpoint,endPoints, and
	 * closure segments will be set
	 * 
	 * @param conic
	 *            GeoConic
	 * @param midPoint
	 *            GeoPoint
	 * @param endPoints
	 *            GeoPoint[2]
	 * @param closureSegments
	 *            GeoSegment[2]
	 */
	public static void calcConicClosure(GeoConic conic, GeoPoint midPoint,
			GeoPoint[] endPoints, GeoSegment[] closureSegments) {

		if (conic.isLimitedPath()) {

			double startParameter = ((GeoConicPartND) conic)
					.getParameterStart();
			double endParameter = ((GeoConicPartND) conic).getParameterEnd();
			midPoint.setCoords(conic.getMidpoint(), true);
			double[] halfAxes = conic.getHalfAxes();

			// set end points
			double x = midPoint.getX() + halfAxes[0] * Math.cos(startParameter);
			double y = midPoint.getY() + halfAxes[0] * Math.sin(startParameter);
			endPoints[0].setCoords(x, y, 1);
			// Log.debug("closureSegment1-endPoints" + x + " , " + y);

			x = midPoint.getX() + halfAxes[0] * Math.cos(endParameter);
			y = midPoint.getY() + halfAxes[0] * Math.sin(endParameter);
			endPoints[1].setCoords(x, y, 1);
			// Log.debug("closureSegment1-endPoints" + x + " , " + y);

			// set closure segments
			switch (((GeoConicPartND) conic).getConicPartType()) {
			case GeoConicNDConstants.CONIC_PART_ARC:
				GeoVec3D.lineThroughPoints(endPoints[0], endPoints[1],
						closureSegments[0]);
				closureSegments[0].setPoints(endPoints[0], endPoints[1]);
				closureSegments[1].setCoords(Double.NaN, Double.NaN,
						Double.NaN);
				break;
			case GeoConicNDConstants.CONIC_PART_SECTOR:
				GeoVec3D.lineThroughPoints(midPoint, endPoints[0],
						closureSegments[0]);
				closureSegments[0].setPoints(midPoint, endPoints[0]);
				GeoVec3D.lineThroughPoints(midPoint, endPoints[1],
						closureSegments[1]);
				closureSegments[1].setPoints(midPoint, endPoints[1]);
				break;
			default:
				for (int i = 0; i < 2; i++) {
					closureSegments[i].setCoords(Double.NaN, Double.NaN,
							Double.NaN);
				}
			}

		} else { // if full conic no endpoints or closure segments
			for (int i = 0; i < 2; i++) {
				endPoints[i].setUndefined();
				closureSegments[i].setCoords(Double.NaN, Double.NaN,
						Double.NaN);
			}
		}
	}

	/**
	 * calculate and assign intersection point between closure segments and the
	 * segment
	 * 
	 * @param segment
	 *            GeoSegment
	 * @param conic
	 *            GeoConinc
	 * @param closureSegments
	 *            GeoSegment[2]
	 * @param closureIntersect
	 *            GeoPoint[2]
	 * @param intersectPoints
	 *            calculated intersecting point between conic curve and input
	 *            segment GeoPoint[2]
	 */
	public static void intersectSegmentConicClosureSegments(GeoSegment segment,
			GeoConic conic, GeoSegment[] closureSegments,
			GeoPoint[] closureIntersect, GeoPoint[] intersectPoints) {

		switch (((GeoConicPartND) conic).getConicPartType()) {
		case GeoConicNDConstants.CONIC_PART_ARC:
			if (!Double.isNaN(closureSegments[0].getX())) {
				// calc intersection points
				GeoVec3D.cross(closureSegments[0], segment,
						closureIntersect[0]);

				// checks whether the cross product(intersection point) is
				// actually an intersection point
				if (closureIntersect[0].isDefined()) {
					if (!(DoubleUtil
							.isZero(closureSegments[0]
									.distance(closureIntersect[0]))
							&& DoubleUtil.isZero(
									segment.distance(closureIntersect[0])))) {
						closureIntersect[0].setUndefined();
					}
				}

				if (closureIntersect[0].isDefined()) {
					for (int i = 0; i < 2; i++) {
						if (DoubleUtil.isZero(closureIntersect[0]
								.distanceSqr(intersectPoints[i]))) {
							closureIntersect[0].setUndefined();
						}
					}
				}
			} else {
				closureIntersect[0].setUndefined();
				closureIntersect[1].setUndefined();
			}
			break;
		case GeoConicNDConstants.CONIC_PART_SECTOR:
			if (!Double.isNaN(closureSegments[0].getX())
					&& !Double.isNaN(closureSegments[1].getX())) {

				for (int i = 0; i < closureIntersect.length; i++) {
					// calc intersection points
					GeoVec3D.cross(closureSegments[i], segment,
							closureIntersect[i]);

					// checks whether the cross product(intersection point) is
					// actually an intersection point
					if (closureIntersect[i].isDefined()) {
						if (!(DoubleUtil
								.isZero(closureSegments[i]
										.distance(closureIntersect[i]))
								&& DoubleUtil.isZero(segment
										.distance(closureIntersect[i])))) {
							closureIntersect[i].setUndefined();
						}
					}

					if (closureIntersect[i].isDefined()) {
						for (int j = 0; j < 2; j++) {
							if (DoubleUtil.isZero(closureIntersect[i]
									.distanceSqr(intersectPoints[j]))) {
								closureIntersect[i].setUndefined();
							}
						}
					}
				}
			} else {
				closureIntersect[0].setUndefined();
				closureIntersect[1].setUndefined();
			}

			break;
		default:
			closureIntersect[0].setUndefined();
			closureIntersect[1].setUndefined();
		}
	}

	/**
	 * Returns output intersection segments
	 * 
	 * @return GeoSegment[]
	 */
	public GeoSegment[] getIntersectionSegments() {
		GeoSegment[] seg = new GeoSegment[numberOfOutputLines];
		for (int i = 0; i < numberOfOutputLines; i++) {
			seg[i] = outputSegments.getElement(i);
		}
		return seg;
	}

	/**
	 * create the necessary output handlers
	 * 
	 * @return output handler
	 */
	protected OutputHandler<GeoSegment> createOutputSegments() {
		return new OutputHandler<>(new ElementFactory<GeoSegment>() {
			@Override
			public GeoSegment newElement() {
				GeoSegment a = new GeoSegment(cons);
				GeoPoint aS = new GeoPoint(cons);
				aS.setCoords(0, 0, 1);
				GeoPoint aE = new GeoPoint(cons);
				aE.setCoords(0, 0, 1);
				a.setPoints(aS, aE);
				a.setParentAlgorithm(AlgoIntersectSegmentConicRegion.this);
				setSegmentVisualProperties(a);
				return a;
			}
		});
	}

	/**
	 * set visual style for new segments
	 * 
	 * @param segment
	 *            GeoElement segment
	 */
	public void setSegmentVisualProperties(GeoElement segment) {
		if (outputSegments.size() > 0) {
			GeoElement seg0 = outputSegments.getElement(0);
			segment.setAllVisualProperties(seg0, false);
			segment.setViewFlags(seg0.getViewSet());
			segment.setVisibleInView3D(seg0);
		}
	}

	/**
	 * set visual style for intersecting points passed to this method
	 * 
	 * @param pnt
	 *            GeoPoint pnt
	 */
	protected void setPointVisualProperties(GeoPoint pnt) {
		pnt.setAuxiliaryObject(true);
		pnt.setViewFlags(getConic().getViewSet());
	}

	/**
	 * Returns number of line Parts on the input segment created by intersecting
	 * points
	 * 
	 * @return int
	 */
	public int getNumOfLineParts() {
		return numberOfLineParts;
	}

	/**
	 * Returns number of intersection path segments
	 * 
	 * @return int
	 */
	public int getOutputSize() {
		return numberOfOutputLines;
	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectPath;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECTION_CURVE;
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
		return intersectPoints;
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		// TODO
		return null;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = getSegment();
		input[1] = getConic();
	}

	/**
	 * getter of input conic
	 * 
	 * @return GeoConic
	 */
	public GeoConic getConic() {
		return this.conic;
	}

	/**
	 * getter of input segment
	 * 
	 * @return GeoSegment
	 */
	public GeoSegment getSegment() {
		return this.segment;
	}
}