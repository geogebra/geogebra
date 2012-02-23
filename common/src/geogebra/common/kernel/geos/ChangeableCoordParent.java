package geogebra.common.kernel.geos;

import java.util.ArrayList;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;

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
	private GeoElement child;
	
	/**
	 * constructor
	 * @param child 
	 * @param number number
	 * @param director director
	 */
	public ChangeableCoordParent(GeoElement child, GeoNumeric number, GeoElement director){
		this.child = child;
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
	
	
	final public boolean move(Coords rwTransVec,
			Coords endPosition, Coords viewDirection,
			ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList) {

		GeoNumeric var = getNumber();

		if (var == null)
			return false;
		if (endPosition == null) { // comes from arrows keys -- all is added
			var.setValue(var.getValue() + rwTransVec.getX() + rwTransVec.getY()
					+ rwTransVec.getZ());
			child.addChangeableCoordParentNumberToUpdateList(var, updateGeos,
					tempMoveObjectList);
			return true;
		}
		// else: comes from mouse
		Coords direction2 = direction.sub(viewDirection.mul(viewDirection
				.dotproduct(direction)));
		double ld = direction2.dotproduct(direction2);
		if (Kernel.isZero(ld))
			return false;
		double val = direction2.dotproduct(rwTransVec);
		var.setValue(getStartValue() + val / ld);
		child.addChangeableCoordParentNumberToUpdateList(var, updateGeos,
				tempMoveObjectList);
		return true;

	}


}
