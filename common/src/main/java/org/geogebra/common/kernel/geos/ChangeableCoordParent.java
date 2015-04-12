package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
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
	private Coords direction;
	
	/**
	 * constructor
	 * @param child child
	 * @param number number
	 * @param director director
	 */
	public ChangeableCoordParent(GeoElement child, GeoNumeric number, GeoElement director){
		changeableCoordNumber = number;
		changeableCoordDirector = director;
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
		direction = changeableCoordDirector.getMainDirection();
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
		Coords direction2 = direction.sub(viewDirection.mul(viewDirection
				.dotproduct(direction)));
		double ld = direction2.dotproduct(direction2);
		if (Kernel.isZero(ld))
			return false;
		double val = getStartValue() + direction2.dotproduct(rwTransVec) / ld;
		switch (view.getPointCapturingMode()) {
		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			//TODO
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
					|| (Math.abs(valRound-val) < g * view.getEuclidianController().getPointCapturingPercentage())){
				val = valRound;
			}
			break;
		}
		
		var.setValue(val);
		GeoElement.addChangeableCoordParentNumberToUpdateList(var, updateGeos,
				tempMoveObjectList);
		return true;

	}


}
