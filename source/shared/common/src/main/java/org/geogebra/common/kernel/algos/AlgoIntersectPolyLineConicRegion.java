package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

public class AlgoIntersectPolyLineConicRegion extends AlgoIntersect {

	protected GeoPoly poly;
	protected GeoConic conic;

	protected boolean polyClosed;
	protected boolean hasLabels;

	protected int numOfOutputSegments;
	protected int segCountOfPoly;
	protected int polyPointCount;

	protected OutputHandler<GeoSegment> outputSegments;

	private ArrayList<CalcDetails> intersectPaths;
	private GeoPoint[] tempSegEndPoints;
	private GeoSegment tempSeg;

	private GeoPoint[] intersectPoints;
	private GeoPoint[] endPoints;
	private GeoPoint[] closureIntersect;
	private GeoPoint midPoint;
	private GeoSegment[] closureSegments;

	/**
	 * constructor with label assignment for output
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for the output
	 * @param poly
	 *            input poly, can be polyLine or polyGon
	 * @param conic
	 *            input conic
	 * @param isPolyClosed
	 *            indicate whether the input poly is polygon(closed) or
	 *            polyLine(not closed)
	 */
	public AlgoIntersectPolyLineConicRegion(Construction cons, String[] labels,
			GeoPoly poly, GeoConic conic, boolean isPolyClosed) {
		this(cons, poly, conic, isPolyClosed);

		if (!cons.isSuppressLabelsActive()) {
			setLabels(labels);
			hasLabels = true;
		}

		update();
	}

	/**
	 * Common constructor for AlgoIntersectPolyLineConicRegion.
	 * 
	 * @param cons
	 *            construction
	 * @param poly
	 *            input poly, can be polyLine or polyGon
	 * @param conic
	 *            input conic
	 * @param isPolyClosed
	 *            indicate whether the input poly is polygon(closed) or
	 *            polyLine(no closed)
	 */
	public AlgoIntersectPolyLineConicRegion(Construction cons, GeoPoly poly,
			GeoConic conic, boolean isPolyClosed) {
		super(cons);

		this.poly = poly;
		this.conic = conic;

		this.polyClosed = isPolyClosed;

		initElements();

		setInputOutput();

		setDependencies();

		compute();
	}

	@Override
	public void compute() {

		numOfOutputSegments = 0;
		intersectPaths = new ArrayList<>();

		// calculate intersectpaths between poly and conic
		for (int index = 0; index < segCountOfPoly; index++) {

			tempSegEndPoints[0] = getPoly().getPoint(index);
			tempSegEndPoints[1] = getPoly()
					.getPoint((index + 1) % polyPointCount);
			GeoVec3D.lineThroughPoints(tempSegEndPoints[0], tempSegEndPoints[1],
					tempSeg);
			tempSeg.setPoints(tempSegEndPoints[0], tempSegEndPoints[1]);

			calcIntersectPaths(tempSeg, index);
		}

		// assign output segments
		outputSegments.adjustOutputSize(numOfOutputSegments, false);
		int count = 0;
		for (int index = 0; index < segCountOfPoly; index++) {
			CalcDetails cd = intersectPaths.get(index);
			GeoSegment seg;
			if (index == cd.segmentIndex && cd.intersectPathcount != -1) {
				for (int i = 0; i < (cd.intersectPathcount * 2); i += 2) {
					seg = outputSegments.getElement(count);
					count++;
					GeoVec3D.lineThroughPointsCoords(cd.intersectPathCoords[i],
							cd.intersectPathCoords[i + 1], seg);
					seg.getStartPoint().setCoords(cd.intersectPathCoords[i],
							false);
					seg.getEndPoint().setCoords(cd.intersectPathCoords[i + 1],
							true);
					seg.calcLength();
					Log.debug("intersectPathcount: " + count
							+ "   segmentindex: " + index);
					Log.debug("outputSegmentDefined: " + seg.isDefined());
				}
			}
		}

		if (hasLabels) {
			outputSegments.updateLabels();
		}
	}

	/**
	 * calculate intersectpaths between given segment and conic. Then add those
	 * details into intersectPaths arrayList
	 * 
	 * @param segment
	 *            interested temporary segment
	 * @param segIndex
	 *            index of the segment (indicate the corresponding segment no.
	 *            in the poly
	 */
	private void calcIntersectPaths(GeoSegment segment, int segIndex) {
		CalcDetails cd = new CalcDetails();
		cd.segmentIndex = segIndex;
		int intersectPathIndex = 0;

		// calculate intersect point between input segment and input Conic curve
		AlgoIntersectSegmentConicRegion.intersectSegmentConic(segment,
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
					.intersectSegmentConicClosureSegments(segment, getConic(),
							closureSegments, closureIntersect, intersectPoints);
		}

		int numberOfLineParts = 1;
		// decide number of line parts using calculated intersection points
		for (int i = 0; i < 2; i++) {
			if (intersectPoints[i].isDefined()) {
				numberOfLineParts += 1;
			}
			if (closureIntersect[i].isDefined()) {
				numberOfLineParts += 1;
			}
		}

		GeoPoint P;
		GeoPoint Q;

		switch (numberOfLineParts) {
		case 1: {

			P = segment.getStartPoint();
			Q = segment.getEndPoint();

			if (getConic().isInRegion((P.inhomX + Q.inhomX) / 2.0d,
					(P.inhomY + Q.inhomY) / 2.0d)) {

				cd.intersectPathCoords[intersectPathIndex] = P.getCoords();
				cd.intersectPathCoords[++intersectPathIndex] = Q.getCoords();
				intersectPathIndex++;

				cd.intersectPathcount++;

				Log.debug("case 1");
				break;
			}

			Log.debug("case 1");
			break;
		}
		case 2: {

			P = segment.getStartPoint();
			Q = segment.getEndPoint();

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

					cd.intersectPathCoords[intersectPathIndex] = P.getCoords();
					cd.intersectPathCoords[++intersectPathIndex] = pnt
							.getCoords();
					intersectPathIndex++;

					cd.intersectPathcount++;

					Log.debug("case 2");
					break;
				}
			}
			if (!DoubleUtil.isZero(pnt.distance(Q))) {
				if (getConic().isInRegion((pnt.inhomX + Q.inhomX) / 2.0d,
						(pnt.inhomY + Q.inhomY) / 2.0d)) {

					cd.intersectPathCoords[intersectPathIndex] = pnt
							.getCoords();
					cd.intersectPathCoords[++intersectPathIndex] = Q
							.getCoords();
					intersectPathIndex++;

					cd.intersectPathcount++;

					Log.debug("case 2");
					break;
				}
			}

			Log.debug("case 2");
			break;
		}

		case 3: {
			int[] order = { 0, 1, 2, 3 };

			// select the intersecting points
			GeoPoint[] pnt = new GeoPoint[4];
			pnt[0] = segment.getStartPoint();
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
			pnt[3] = segment.getEndPoint();

			// sorting intersection points
			double t1 = segment.getPossibleParameter(pnt[1].getCoords());
			double t2 = segment.getPossibleParameter(pnt[2].getCoords());
			if (t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
				int intTemp = order[1];
				order[1] = order[2];
				order[2] = intTemp;
			}

			// counting no of output segments and assigning them as output
			for (int i = 0; i < 3; i++) {
				if (!DoubleUtil.isZero(pnt[order[i]].distance(pnt[order[i + 1]]))) {
					if (getConic().isInRegion(
							(pnt[order[i]].inhomX + pnt[order[i + 1]].inhomX)
									/ 2.0d,
							(pnt[order[i]].inhomY + pnt[order[i + 1]].inhomY)
									/ 2.0d)) {
						cd.intersectPathCoords[intersectPathIndex] = pnt[order[i]]
								.getCoords();
						cd.intersectPathCoords[++intersectPathIndex] = pnt[order[i
								+ 1]].getCoords();
						intersectPathIndex++;

						cd.intersectPathcount++;
					}
				}
			}

			Log.debug("case 3");
			break;
		}
		case 4: {
			int[] order = { 0, 1, 2, 3, 4 };

			// select the intersecting points
			GeoPoint[] pnt = new GeoPoint[5];
			int count = 1;
			pnt[0] = segment.getStartPoint();
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
			pnt[4] = segment.getEndPoint();

			// sorting intersection points order on input segment starting from
			// inputsegment.getStartPoint()
			double temp;
			double t1 = segment.getPossibleParameter(pnt[1].getCoords());
			double t2 = segment.getPossibleParameter(pnt[2].getCoords());
			double t3 = segment.getPossibleParameter(pnt[3].getCoords());

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
			for (int i = 0; i < 4; i++) {
				if (!DoubleUtil.isZero(pnt[order[i]].distance(pnt[order[i + 1]]))) {
					if (getConic().isInRegion(
							(pnt[order[i]].inhomX + pnt[order[i + 1]].inhomX)
									/ 2.0d,
							(pnt[order[i]].inhomY + pnt[order[i + 1]].inhomY)
									/ 2.0d)) {

						cd.intersectPathCoords[intersectPathIndex] = pnt[order[i]]
								.getCoords();
						cd.intersectPathCoords[++intersectPathIndex] = pnt[order[i
								+ 1]].getCoords();
						intersectPathIndex++;

						cd.intersectPathcount++;
					}
				}
			}

			Log.debug("case 4");
			break;
		}
		case 5: {

			double t1, t2, t3, t4, temp;
			int[] order = { 0, 1, 2, 3, 4 };
			int intTemp;

			// get intersect point on segment
			GeoPoint[] pnt = { segment.getStartPoint(), intersectPoints[0],
					intersectPoints[1], closureIntersect[0],
					closureIntersect[1], segment.getEndPoint() };

			// sorting intersection points order on input segment starting from
			// inputsegment.getStartPoint()
			t1 = segment.getPossibleParameter(pnt[1].getCoords());
			t2 = segment.getPossibleParameter(pnt[2].getCoords());
			t3 = segment.getPossibleParameter(pnt[3].getCoords());
			t4 = segment.getPossibleParameter(pnt[4].getCoords());

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
			for (int i = 0; i < 4; i++) {
				if (!DoubleUtil.isZero(pnt[order[i]].distance(pnt[order[i + 1]]))) {
					if (getConic().isInRegion(
							(pnt[order[i]].inhomX + pnt[order[i + 1]].inhomX)
									/ 2.0d,
							(pnt[order[i]].inhomY + pnt[order[i + 1]].inhomY)
									/ 2.0d)) {

						cd.intersectPathCoords[intersectPathIndex] = pnt[order[i]]
								.getCoords();
						cd.intersectPathCoords[++intersectPathIndex] = pnt[order[i
								+ 1]].getCoords();
						intersectPathIndex++;

						cd.intersectPathcount++;

					}
				}
			}

			Log.debug("case 5");
			break;
		}
		default: {
			// outputSegments.adjustOutputSize(1, false);
			// outputSegments.getElement(0).setUndefined();
			Log.debug("case default-no intersectPaths");
		}
		}

		numOfOutputSegments += cd.intersectPathcount;

		intersectPaths.add(cd);

	}

	/**
	 * initialize Elements. CreateOutput is called inside
	 */
	protected void initElements() {

		numOfOutputSegments = 0;
		polyPointCount = (getPoly().getPoints()).length;
		segCountOfPoly = isPolyClosed() ? polyPointCount : polyPointCount - 1;

		outputSegments = createOutputSegments();
		intersectPaths = new ArrayList<>();

		tempSegEndPoints = new GeoPoint[2];
		intersectPoints = new GeoPoint[2];
		endPoints = new GeoPoint[2];
		closureSegments = new GeoSegment[2];
		closureIntersect = new GeoPoint[2];

		for (int i = 0; i < tempSegEndPoints.length; i++) {
			tempSegEndPoints[i] = new GeoPoint(getConstruction());
			intersectPoints[i] = new GeoPoint(getConstruction());
			endPoints[i] = new GeoPoint(getConstruction());
			closureSegments[i] = new GeoSegment(getConstruction());
			closureIntersect[i] = new GeoPoint(getConstruction());
		}
		tempSeg = new GeoSegment(getConstruction());
		midPoint = new GeoPoint(getConstruction());
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
				a.setParentAlgorithm(AlgoIntersectPolyLineConicRegion.this);
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

	@Override
	public GeoPoint[] getIntersectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) getPoly();
		input[1] = getConic();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.IntersectPath;
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
	 * getter of input poly
	 * 
	 * @return GeoPoly
	 */
	public GeoPoly getPoly() {
		return this.poly;
	}

	/**
	 * Returns number of intersection path segments
	 * 
	 * @return int
	 */
	public int getOutputSize() {
		return this.numOfOutputSegments;
	}

	/**
	 * Returns whether the poly closed or not
	 * 
	 * @return boolean
	 */
	public boolean isPolyClosed() {
		return this.polyClosed;
	}

	/**
	 * helping class for keeping calculated records
	 * 
	 * @author thilina
	 */
	private static class CalcDetails {
		int segmentIndex;
		int intersectPathcount;
		Coords[] intersectPathCoords;

		public CalcDetails() {
			segmentIndex = -1;
			intersectPathcount = 0;
			intersectPathCoords = new Coords[4];
		}
	}

}
