package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Parent (number+direction) for changing prism, cylinder, etc.
 * 
 * @author Mathieu
 *
 */
public class ChangeableParent {

	private GeoNumeric changeableNumber = null;
	private GeoElementND directorGeo = null;
	private double startValue;
	private Coords direction;
	private Coords centroid;
	private boolean forPolyhedronNet = false;
	private GeoPolyhedronInterface parent;
	private final CoordConverter converter;

	/**
	 * 
	 * @param v
	 *            value
	 * @return v as GeoNumeric if instance of and independent (return null
	 *         otherwise)
	 */
	static public GeoNumeric getGeoNumeric(NumberValue v) {

		if (v instanceof GeoNumeric) {
			GeoNumeric geo = (GeoNumeric) v;
			if (geo.isIndependent()) {
				return geo;
			}
		}
		return null;
	}

	/**
	 * set changeable parent to the polygon as part of polyhedron net (check
	 * first if num is not null)
	 * 
	 * @param polygon
	 *            polyhedron net face
	 * @param num
	 *            value that fold/unfold the net
	 * @param polyhedron
	 *            polyhedron parent
	 */
	static public void setPolyhedronNet(GeoPolygon polygon, GeoNumeric num,
			GeoPolyhedronInterface polyhedron) {
		if (num != null) {
			ChangeableParent cp = new ChangeableParent(polygon, num,
					polyhedron);
			polygon.setChangeableParent(cp);

			// set segments (if not already done)
			for (GeoSegmentND segment : polygon.getSegments()) {
				segment.setChangeableParentIfNull(cp);
			}

			// set points (if not already done)
			for (GeoPointND point : polygon.getPointsND()) {
				point.setChangeableParentIfNull(cp);
			}
		}
	}

	/**
	 * constructor
	 * 
	 * @param number
	 *            number
	 * @param director
	 *            director
	 * @param converter
	 *            converts mouse movement to parameter value
	 */
	public ChangeableParent(GeoNumeric number, GeoElementND director, CoordConverter converter) {
		changeableNumber = number;
		directorGeo = director;
		forPolyhedronNet = false;
		this.converter = converter;
	}

	/**
	 * constructor
	 * 
	 * @param child
	 *            child
	 * @param number
	 *            number
	 * @param parent
	 *            parent polyhedron
	 */
	public ChangeableParent(GeoElement child, GeoNumeric number,
			GeoPolyhedronInterface parent) {
		changeableNumber = number;
		directorGeo = child;
		forPolyhedronNet = true;
		this.converter = new PolyhedronNetConverter();
		this.parent = parent;
	}

	/**
	 * 
	 * @return number
	 */
	final public GeoNumeric getNumber() {
		return changeableNumber;
	}

	/**
	 * 
	 * @return value of the number
	 */
	final public double getValue() {
		return changeableNumber.getValue();
	}

	/**
	 * 
	 * @return director
	 */
	final public GeoElementND getDirector() {
		return directorGeo;
	}

	/**
	 * record number value and direction
	 * 
	 * @param view
	 *            view calling
	 * @param startPoint
	 *            start point
	 */
	final public void record(EuclidianView view, Coords startPoint) {
		startValue = getValue();
		if (direction == null) {
			direction = new Coords(3);
		}
		if (forPolyhedronNet) {
			if (view instanceof EuclidianView3D) {
				if (centroid == null) {
					centroid = new Coords(3);
                }
                parent.pseudoCentroid(centroid);
                direction.setSub3(startPoint, centroid);
                converter.record(this, startPoint);
                direction.normalize();
            } else {
				direction.set(0, 0, 0);
			}
		} else {
			direction.set3(

					directorGeo.getMainDirection());
			converter.record(this, startPoint);
		}
	}

	/**
	 * 
	 * @return start value
	 */
	final public double getStartValue() {
		return startValue;
	}

	/**
	 * @param rwTransVec
	 *            real world translation vector
	 * @param endPosition
	 *            end position
	 * @param viewDirection
	 *            view direction
	 * @param updateGeos
	 *            list of geos
	 * @param tempMoveObjectList
	 *            temporary list
	 * @param view
	 *            view where the move occurs (if not keyboard)
	 * @return true on success
	 */
	final public boolean move(Coords rwTransVec, Coords endPosition,
			Coords viewDirection, ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList, EuclidianView view) {

		GeoNumeric var = getNumber();

		if (var == null) {
			return false;
		}

		if (endPosition == null) { // comes from arrows keys -- all is added
			var.setValue(var.getValue() + rwTransVec.getX() + rwTransVec.getY()
					+ rwTransVec.getZ());
			GeoElement.addParentToUpdateList(var,
					updateGeos, tempMoveObjectList);
			return true;
		}

		if (viewDirection == null) { // may come from 2D view, e.g.
										// EuclidianController.moveDependent()
			// see
			// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&sh=false&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException:+Attempt+to+invoke+virtual+method+'double+org.geogebra.a.m.a.j.e(org.geogebra.a.m.a.j)'+on+a+null+object+reference&tf=SourceFile&tc=%2509at+org.geogebra.common.kernel.geos.ChangeableCoordParent.move(ChangeableCoordParent.java:202)&tm=a&nid&an&c&s=new_status_desc&ed=1480452507515
			return false;
		}

		// else: comes from mouse

        double val = converter.translationToValue(direction, rwTransVec,
                getStartValue(), view);
        if (needsSnap(view)) {
            val = converter.snap(val, view);
        }
        if (!MyDouble.isFinite(val)) {
            return false;
        }

		var.setValue(val);
		GeoElement.addParentToUpdateList(var, updateGeos, tempMoveObjectList);

		return true;
	}

	private static boolean needsSnap(EuclidianView view) {
		switch (view.getPointCapturingMode()) {
		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			// TODO
			return false;
		default:
		case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
			return view.isGridOrAxesShown();
		case EuclidianStyleConstants.POINT_CAPTURING_ON:
		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
			return true;
		}
	}

	/**
	 * @return current move direction
	 */
	public Coords getDirection() {
		return direction;
	}

	/**
	 * @return converter for mouse movement to value
	 */
	public CoordConverter getConverter() {
		return converter;
	}

}
