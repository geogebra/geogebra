package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Parent (number+direction) for changing coords of prism, cylinder, etc.
 * @author matthieu
 *
 */
public class ChangeableCoordParent {
	
	private GeoNumeric changeableCoordNumber = null;
	private GeoElement changeableCoordDirector = null;
	private double startValue;
	private Coords direction, direction2;
	private boolean forPolyhedronNet = false;
	private GeoPolyhedron parent;
	
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
			if (geo.getKernel().getApplication()
					.has(Feature.FOLD_POLYHEDRON_NET_BY_DRAGGING)) {
				if (geo.isIndependent()) {
					return geo;
				}
			}
		}
		return null;
	}

	/**
	 * set changeable coord parent to the polygon as part of polyhedron net
	 * (check first if num is not null)
	 * 
	 * @param polygon
	 *            polyhedron net face
	 * @param num
	 *            value that fold/unfold the net
	 * @param polyhedron
	 *            polyhedron parent
	 */
	static public void setPolyhedronNet(GeoPolygon polygon, GeoNumeric num,
			GeoPolyhedron polyhedron) {
		if (num != null) {
			polygon.setChangeableCoordParent(num, polyhedron);
		}
	}

	/**
	 * constructor
	 * @param child child
	 * @param number number
	 * @param director director
	 */
	public ChangeableCoordParent(GeoElement child, GeoNumeric number, GeoElement director){
		changeableCoordNumber = number;
		changeableCoordDirector = director;
		forPolyhedronNet = false;
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
	public ChangeableCoordParent(GeoElement child, GeoNumeric number,
			GeoPolyhedron parent) {
		changeableCoordNumber = number;
		changeableCoordDirector = child;
		forPolyhedronNet = true;
		this.parent = parent;
	}

	/**
	 * 
	 * @return number
	 */
	final public GeoNumeric getNumber() {
		return changeableCoordNumber;
	}
	
	/**
	 * 
	 * @return value of the number
	 */
	final public double getValue() {
		return changeableCoordNumber.getValue();
	}

	
	/**
	 * 
	 * @return director
	 */
	final public GeoElement getDirector() {
		return changeableCoordDirector;
	}
	
	/**
	 * record number value
	 */
	final public void record(){
		startValue = getValue();
		if (direction == null) {
			direction = new Coords(4);
		}
		direction.set(changeableCoordDirector.getMainDirection());
	}
	
	/**
	 * 
	 * @return start value
	 */
	final public double getStartValue(){
		return startValue;
	}
	
	
	/**
	 * @param rwTransVec real world translation vector
	 * @param endPosition end position
	 * @param viewDirection view direction
	 * @param updateGeos list of geos
	 * @param tempMoveObjectList temporary list
	 * @param view view where the move occurs (if not keyboard)
	 * @return true on success
	 */
	final public boolean move(Coords rwTransVec,
			Coords endPosition, Coords viewDirection,
			ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList, EuclidianView view) {

		GeoNumeric var = getNumber();

		if (var == null)
			return false;
		if (endPosition == null) { // comes from arrows keys -- all is added
			var.setValue(var.getValue() + rwTransVec.getX() + rwTransVec.getY()
					+ rwTransVec.getZ());
			GeoElement.addChangeableCoordParentNumberToUpdateList(var, updateGeos,
					tempMoveObjectList);
			return true;
		}
		// else: comes from mouse
		// Coords direction2 = direction.copy().addInsideMul(viewDirection,
		// -viewDirection.dotproduct(direction));
		if (direction2 == null) {
			direction2 = new Coords(4);
		}
		direction2.setAdd(direction, direction2.setMul(viewDirection,
				-viewDirection.dotproduct3(direction)));

		double ld = direction2.dotproduct3(direction2);

		if (Kernel.isZero(ld))
			return false;

		if (forPolyhedronNet) {
			// use parent polyhedron volume as base scale for translation vector
			double scale = Math.pow(parent.getVolume(), 1.0 / 3.0);
			ld = -ld * scale * 2;
		}

		double val = getStartValue() + direction2.dotproduct(rwTransVec) / ld;

		if (!forPolyhedronNet) {
			switch (view.getPointCapturingMode()) {
			case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
				// TODO
				break;
			case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
				if (!view.isGridOrAxesShown()) {
					break;
				}
			case EuclidianStyleConstants.POINT_CAPTURING_ON:
			case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
				double g = view.getGridDistances(0);
				double valRound = Kernel.roundToScale(val, g);
				if (view.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
						|| (Math.abs(valRound - val) < g
								* view.getEuclidianController()
										.getPointCapturingPercentage())) {
					val = valRound;
				}
				break;
			}
		}
		
		var.setValue(val);
		GeoElement.addChangeableCoordParentNumberToUpdateList(var, updateGeos,
				tempMoveObjectList);

		if (forPolyhedronNet) {
			view.getEuclidianController().setStartPointLocation();
			record();
		}

		return true;

	}


}
