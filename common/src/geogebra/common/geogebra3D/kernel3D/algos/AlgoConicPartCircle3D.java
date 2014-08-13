/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoConicPart;
import geogebra.common.kernel.algos.EquationElementInterface;
import geogebra.common.kernel.algos.EquationScopeInterface;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoConicPartND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Circular arc or sector defined by the circle's center, one point on the
 * circle (start point) and another point (angle for end-point).
 */
public class AlgoConicPartCircle3D extends AlgoConicPart {

	private GeoPointND center, startPoint, endPoint;
	
	
	private PathParameter paramP, paramQ;

	
	
	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartCircle3D(Construction cons, String label,
			GeoPointND center, GeoPointND startPoint, GeoPointND endPoint, int type) {

		this(cons, label, center, startPoint, endPoint, null, type);
	}
	
	public AlgoConicPartCircle3D(Construction cons, String label,
			GeoPointND center, GeoPointND startPoint, GeoPointND endPoint, 
			GeoDirectionND orientation,
			int type) {
		this(cons, center, startPoint, endPoint, orientation, type);
		conicPart.setLabel(label);
	}

	private AlgoConicPartCircle3D(Construction cons, GeoPointND center,
			GeoPointND startPoint, GeoPointND endPoint, 
			GeoDirectionND orientation,
			int type) {
		super(cons, type);
		this.center = center;
		this.startPoint = startPoint;
		this.endPoint = endPoint;

		// create circle with center through startPoint
		AlgoCircle3DCenterPointPoint algo = new AlgoCircle3DCenterPointPoint(cons, center,
				startPoint, endPoint);
		cons.removeFromConstructionList(algo);
		conic = algo.getCircle();

		// orientation
		setOrientation(orientation);
		
		// temp Points
		paramP = new PathParameter();
		paramQ = new PathParameter();

		conicPart = new GeoConicPart3D(cons, type);
		conicPart.addPointOnConic(startPoint);

		setInputOutput(); // for AlgoElement
		compute();
		setIncidence();
	}
	
	/**
	 * set orientation for the arc/sector
	 * @param orientation orientation
	 */
	protected void setOrientation(GeoDirectionND orientation){
		// not used here
	}

	private void setIncidence() {
		startPoint.addIncidence(conicPart, false);
		// endPoint.addIncidence(conicPart);

	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public GeoPointND getEndPoint() {
		return endPoint;
	}

	public GeoPointND getCenter() {
		return center;
	}

	@Override
	public Commands getClassName() {
		switch (type) {
		case GeoConicPart.CONIC_PART_ARC:
			return Commands.CircleArc;
		default:
			return Commands.CircleSector;
		}
	}

	@Override
	public int getRelatedModeID() {
		switch (type) {
		case GeoConicPart.CONIC_PART_ARC:
			return EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
		default:
			return EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
		}
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInput();

		super.setOutputLength(1);
		super.setOutput(0, conicPart);

		setDependencies();
	}
	
	/**
	 * set input
	 */
	protected void setInput(){
		setInput(3);
	}
	
	/**
	 * set input
	 * @param n input length
	 */
	protected void setInput(int n){
		input = new GeoElement[n];
		input[0] = (GeoElement) center;
		input[1] = (GeoElement) startPoint;
		input[2] = (GeoElement) endPoint;
	}
	
	
	
	/**
	 * set arc/sector when center is aligned between start and end points
	 * @param center1 center coords
	 * @param v1 radius vector
	 */
	protected void semiCircle(Coords center1, Coords v1){
		// points aligned, undefined
		conicPart.setUndefined();
	}
	

	@Override
	public final void compute() {
		
		CoordSys cs = conic.getCoordSys();
		
		if (!cs.isDefined()){
			Coords centerCoords = center.getInhomCoordsInD(3);
			Coords startCoords =  startPoint.getInhomCoordsInD(3);
			Coords v1 = startCoords.sub(centerCoords);
			Coords v2 = endPoint.getInhomCoordsInD(3).sub(centerCoords);
			if (Kernel.isGreater(0, v1.dotproduct(v2))){
				semiCircle(centerCoords, v1);
				return;
			}
			
			// points aligned, single point
			GeoConic3D.setSinglePoint((GeoConic3D) conicPart, startCoords);
			((GeoConicPart3D) conicPart).setParametersToSinglePoint();
			conicPart.setType(GeoConicNDConstants.CONIC_SINGLE_POINT);
			return;
		}
		
		// the temp points P and Q should lie on the conic
		Coords p2d = startPoint.getInhomCoordsInD(3).projectPlane(cs.getMatrixOrthonormal())[1];
		p2d.setZ(1);
		conic.pointChanged(p2d, paramP);

		p2d = endPoint.getInhomCoordsInD(3).projectPlane(cs.getMatrixOrthonormal())[1];
		p2d.setZ(1);
		conic.pointChanged(p2d, paramQ);


		// now take the parameters from the temp points
		setConicPart(paramP.t, paramQ.t);
	}
	
	/**
	 * set conic part coord sys and parameters
	 * @param start start parameter
	 * @param end end parameter
	 */
	protected void setConicPart(double start, double end){
		conicPart.set(conic);
		((GeoConicPartND) conicPart).setParameters(start, end,
				getPositiveOrientation());
	}
	
	/**
	 * 
	 * @return positive orientation
	 */
	protected boolean getPositiveOrientation(){
		return true;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnCircleArc(geo, this, scope);
	}
	

	@Override
	public GeoConicPart3D getConicPart() {
        return (GeoConicPart3D) super.getConicPart();
    }

}
