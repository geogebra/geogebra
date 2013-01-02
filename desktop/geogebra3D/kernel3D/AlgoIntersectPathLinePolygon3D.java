/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoIntersectPathLinePolygon;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.TreeMap;

/**
 * Algo for intersection of a line with the interior of a polygon
 * 
 * @author matthieu
 */
public class AlgoIntersectPathLinePolygon3D extends AlgoIntersectPathLinePolygon {


	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param g
	 * @param p
	 */
	public AlgoIntersectPathLinePolygon3D(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {

		super(c,labels,g,p);

	}



	@Override
	protected OutputHandler<GeoElement> createOutputSegments() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoSegment3D newElement() {
				GeoSegment3D a = new GeoSegment3D(cons);
				GeoPoint3D aS = new GeoPoint3D(cons);
				aS.setCoords(0, 0, 0, 1);
				GeoPoint3D aE = new GeoPoint3D(cons);
				aE.setCoords(0, 0, 0, 1);
				a.setPoints(aS, aE);
				a.setParentAlgorithm(AlgoIntersectPathLinePolygon3D.this);
				return a;
			}
		});
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectPathLinePolygon3D;
	}




    /**
     * calc all intersection points between line and polygon p
     * @param p polygon
     * @param newCoords intersection points
     */
	@Override
	protected void intersectionsCoords(GeoPolygon p, TreeMap<Double, Coords> newCoords){


		//line origin and direction
		setIntersectionLine();


		for(int i=0; i<p.getSegments().length; i++){
			GeoSegmentND seg = p.getSegments()[i];

			Coords o2 = seg.getPointInD(3, 0);
			Coords d2 = seg.getPointInD(3, 1).sub(o2);

			Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(
					o1,d1,o2,d2
					);

			//check if projection is intersection point
			if (project!=null && project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){

				double t1 = project[2].get(1); //parameter on line
				double t2 = project[2].get(2); //parameter on segment


				if (checkParameter(t1) && seg.respectLimitedPath(t2))
					newCoords.put(t1, project[0]);

			}
		}

	}
	
	@Override
	protected void addStartEndPoints(TreeMap<Double, Coords> newCoords){
		if (g instanceof GeoSegmentND){
			newCoords.put(0.0,g.getStartInhomCoords());
			newCoords.put(1.0,g.getEndInhomCoords());
		}else if (g instanceof GeoRayND)
			newCoords.put(0d,g.getStartInhomCoords());
	}
	
	@Override
	protected boolean checkMidpoint(Coords a, Coords b){
		Coords midpoint = p.getNormalProjection(a.add(b).mul(0.5))[1];
		return  p.isInRegion(midpoint.getX(), midpoint.getY());
	}

	@Override
	protected void setSegment(GeoSegmentND seg, Coords start, Coords end){
		seg.setTwoPointsCoords(start, end);
	}

}
