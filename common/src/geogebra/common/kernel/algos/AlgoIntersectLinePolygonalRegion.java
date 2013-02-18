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

package geogebra.common.kernel.algos;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.TreeMap;

/**
 * Algo for intersection of a line with the interior of a polygon
 * 
 * @author matthieu
 * @version
 */
public class AlgoIntersectLinePolygonalRegion extends AlgoElement {

	protected GeoLineND g; // input
	protected GeoPolygon p; // input
	protected OutputHandler<GeoElement> outputSegments; // output
	protected OutputHandler<GeoElement> outputPoints;
	protected int spaceDim = 2;

	private TreeMap<Double, Coords> newCoords;
	private TreeMap<Double, Coords[]> newSegmentCoords;

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param g
	 * @param p
	 */
	public AlgoIntersectLinePolygonalRegion(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {

		super(c);

		outputPoints = createOutputPoints();
		outputSegments = createOutputSegments();

		this.g = g;
		this.p = p;

		newCoords = new TreeMap<Double, Coords>(
				Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
		newSegmentCoords = new TreeMap<Double, Coords[]>(
				Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));

		init();
		setInputOutput(); // for AlgoElement

		compute();

		setLabels(labels);
		// TODO: actually no need to update
		// update();

	}

	 protected void init() {
		// TODO Auto-generated method stub
		spaceDim = 2;
	}

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolygonalRegion.this);
				return p;
			}
		});
	}

	protected OutputHandler<GeoElement> createOutputSegments() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoSegment newElement() {
				GeoSegment a = new GeoSegment(cons);
				GeoPoint aS = new GeoPoint(cons);
				aS.setCoords(0, 0, 1);
				GeoPoint aE = new GeoPoint(cons);
				aE.setCoords(0, 0, 1);
				a.setPoints(aS, aE);
				a.setParentAlgorithm(AlgoIntersectLinePolygonalRegion.this);
				setStyle(a); // TODO move to ConstructionDefaults
				return a;
			}
		});
	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectionPaths;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECTION_CURVE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = p;

		setDependencies(); // done by AlgoElement
	}

	protected void intersectionsCoords(GeoLineND g, GeoPolygon p,
			TreeMap<Double, Coords> newCoords) {

		double min = g.getMinParameter();
		double max = g.getMaxParameter();

		for (int i = 0; i < p.getSegments().length; i++) {
			GeoSegment seg = (GeoSegment) p.getSegments()[i];
			Coords coords = GeoVec3D.cross((GeoLine) g, seg);
			if (Kernel.isZero(coords.getLast())) {
				Coords segStart = seg.getPointInD(2, 0);
				Coords segEnd = seg.getPointInD(2, 1);
				if (((GeoLine) g).isOnPath(segStart, Kernel.EPSILON)
						&& ((GeoLine) g).isOnPath(segEnd, Kernel.EPSILON)) {
					newCoords.put(((GeoLine) g).getPossibleParameter(segStart),
							segStart);
					newCoords.put(((GeoLine) g).getPossibleParameter(segEnd),
							segEnd);
				}
			} else if (seg.respectLimitedPath(coords, Kernel.MIN_PRECISION)) {
				double t = ((GeoLine) g).getPossibleParameter(coords);
				// Application.debug("parameter("+i+") : "+t);
				if (t >= min && t <= max)// TODO optimize that
					newCoords.put(t, coords);
			}
		}
	}

	protected void intersectionsSegments(GeoLineND g, GeoPolygon p,
			TreeMap<Double, Coords> newCoords,
			TreeMap<Double, Coords[]> newSegmentCoords) {
		
    	
    	if (newCoords==null )
    		return;

    	if (newCoords.isEmpty()) {
    		//TODO: inverse fill
    		if (!p.isInverseFill() &&
    				g instanceof GeoSegmentND &&
    				p.isInRegion(((GeoSegmentND)g).getStartPoint(),false))
    			newSegmentCoords.put(0.0,  new Coords[] {
    					((GeoSegmentND)g).getStartPoint().getInhomCoordsInD(spaceDim),
    					((GeoSegmentND)g).getEndPoint().getInhomCoordsInD(spaceDim)});
    		
    		return;
    	}
    	//traversing algorithm for dealing with degenerated case
    	
       	double minKey = newCoords.firstKey();
    	double maxKey = newCoords.lastKey();
    	double tOld, tFirst, tLast;
    	Coords coordsOld, coordsFirst, coordsLast;
    	
    	boolean isEnteringRegion = true; // true: entering; false: quitting;
    	
    	//the following variables will be used only
    	//when the line enters the region through a vertex of the polygon
    	boolean segmentAlongLine = false; //true when a positive length of segment is going to be
    	//contained in the result.
    	
 
    	//initiate tOld and coordsOld
    	if (g.getMinParameter()>=maxKey){
    		tFirst = g.getMinParameter();
    		//coordsFirst = ((GeoLine) g).startPoint.getCoordsInD(2);
    		tLast = g.getMaxParameter();
    		//coordsLast = ((GeoLine) g).endPoint.getCoordsInD(2);
    	} else {
    		tFirst = g.getMaxParameter();
    		//coordsFirst = ((GeoLine) g).endPoint.getCoordsInD(2);
    		tLast = g.getMinParameter();
    		//coordsLast = ((GeoLine) g).startPoint.getCoordsInD(2);
    	}
    	coordsFirst = ((GeoLine)g).getPointInD(spaceDim, tFirst);
    	coordsLast = ((GeoLine)g).getPointInD(spaceDim, tLast);
    	
    	//deal with the first point
    	tOld = tFirst;
   		coordsOld = ((GeoLine)g).getPointInD(spaceDim, tOld);//TODO optimize it

   		isEnteringRegion = (p.isInRegion(coordsOld.get(1),coordsOld.get(2))
    			&& !Kernel.isEqual(tOld, maxKey));
    	if (isEnteringRegion) 
    		newSegmentCoords.put(tOld,
    				new Coords[] {coordsFirst, newCoords.get(maxKey)}
    		);
    	isEnteringRegion = !isEnteringRegion;
    	
    	tOld = maxKey;
    	coordsOld = newCoords.get(maxKey);

    	//loop for all possible change of region
    	while (tOld > minKey) {
    		double tNew = newCoords.subMap(minKey, tOld).lastKey();

    		//Check multiplicity at tLast
    		//i.e. how many times g crosses the border at tLast
    		int tOld_m = 0;
    		int tOld_mRight = 0;
    		int tOld_mLeft = 0;
    		
    		Coords gRight = g.getDirectionInD3().crossProduct(p.getCoordSys().getNormal());
    		
    		for (int i = 0; i<p.getPointsLength(); i++) {
    			GeoSegmentND currSeg = p.getSegments()[i];
    			
    			if (currSeg.isOnPath(coordsOld, Kernel.EPSILON)) {
    				tOld_m++;
    			} else {
    				continue;
    			}
    				
    			if (coordsOld.getInhomCoords().isEqual(currSeg.getStartInhomCoords())
    					|| coordsOld.getInhomCoords().isEqual(currSeg.getEndInhomCoords()) ) {
    				tOld_m--;
    				
    				double currSegIncline = currSeg.getDirectionInD3().dotproduct(gRight);
    				if (Kernel.isGreater(currSegIncline, 0))
    					tOld_mRight++;
    				else if (Kernel.isGreater(0, currSegIncline))
    					tOld_mLeft++;
    				else {//logically saying currSeg is along the line; can have potential computational problem unknown
    					segmentAlongLine = true;
    					tOld_m--;
    				}
    			}
    		}
    		
    		if (tOld_mRight != 0 || tOld_mLeft != 0)
    			if (tOld_mRight >= tOld_mLeft) {
    				tOld_mRight -= tOld_mLeft;
    				tOld_m += tOld_mLeft;
    				tOld_mLeft=0;
    			} else if (tOld_mRight < tOld_mLeft) {
    				tOld_mLeft -= tOld_mRight;
    				tOld_m += tOld_mRight;
    				tOld_mRight=0;
    			}
    				
    		//This is the main equation of this algorithm.
    		//When the line meeting some segment,
    		//default assumes that only one segment is involved,
    		//and the line must be entering (isEnteringRegion=true)
    		//or quitting (isEnteringRegion=false) the region.
    		//If it turns out that the crossing is even number of  times,
    		//revert isEnteringRegion.
    		isEnteringRegion ^= (tOld_m % 2 == 0);
    				
    		if (segmentAlongLine) { //unconditionally add the segment
    			//isEnteringRegion won't change
    			newSegmentCoords.put(tOld,  new Coords[] {
		    			newCoords.get(tOld), 
		    			newCoords.get(tNew)
		    			});
    		} else {
    			if (isEnteringRegion) //add the segment only if it is entering the region
    				newSegmentCoords.put(tOld,  new Coords[] {
    						newCoords.get(tOld), 
    						newCoords.get(tNew)
    						});
    			isEnteringRegion = !isEnteringRegion;
    		}
    		tOld = tNew;
    		coordsOld = newCoords.get(tOld);
    	}
    	
    	if(!Kernel.isEqual(tOld, tLast)) {
    		int tOld_m = 0;
    		for (int i = 0; i<p.getPointsLength(); i++) {
    			GeoSegmentND currSeg = p.getSegments()[i];
    			if (currSeg.isOnPath(coordsOld, Kernel.EPSILON)) {
    				tOld_m++;
    			} else {
    				continue;
    			}
    		}
    		isEnteringRegion ^= (tOld_m % 2 == 0);
    			if (isEnteringRegion) //add the segment only if it is entering the region
    				newSegmentCoords.put(tOld,  new Coords[] {
    						newCoords.get(tOld), 
    						coordsLast
    						});
    			isEnteringRegion = !isEnteringRegion;
    	}
   
	}

	private GColor BLUE_VIOLET = geogebra.common.factories.AwtFactory.prototype
			.newColor(153, 0, 255);
	private int THICK_LINE_WITHIN_LINE = 4;

	@Override
	public void compute() {

		// clear the points map
		newCoords.clear();

		// fill a new points map
		intersectionsCoords(g, p, newCoords);

		// update and/or create points
		int index = 0;
		// affect new computed points
		outputPoints.adjustOutputSize(newCoords.size());
		outputPoints.updateLabels();
		for (Coords coords : newCoords.values()) {
			GeoPointND point = (GeoPointND) outputPoints.getElement(index);
			point.setCoords(coords, false);
			point.updateCoords();
			((GeoElement) point).update(); // TODO optimize it
			index++;
		}

		// calculate segments
		newSegmentCoords.clear();

		if (newCoords.isEmpty()) {
			outputSegments.adjustOutputSize(1);
			setStyle((GeoSegmentND) outputSegments.getElement(0)); // TODO
																	// remove
																	// this
			outputSegments.getElement(0).setUndefined();
		} else {

			intersectionsSegments(g, p, newCoords, newSegmentCoords);

			// update and/or create segments
			int indexSegment = 0;
			// affect new computed segments
			outputSegments.adjustOutputSize(newSegmentCoords.size());
			outputSegments.updateLabels();

			for (Coords[] segmentCoords : newSegmentCoords.values()) {

				GeoSegmentND segment = (GeoSegmentND) outputSegments
						.getElement(indexSegment);
				segment.setTwoPointsCoords(segmentCoords[0], segmentCoords[1]);
				((GeoElement) segment).update(); // TODO optimize it
				setStyle(segment);
				indexSegment++;
			}
		}
	}

	protected void setStyle(GeoSegmentND segment) {
		// TODO: set styles in somewhere else
		segment.setLineThickness(THICK_LINE_WITHIN_LINE);
		segment.setObjColor(BLUE_VIOLET);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return loc.getPlain("IntersectionOfAandB",
				((GeoElement) g).getLabel(tpl), p.getLabel(tpl));
	}

	String labelPrefix = null;

	protected void setLabels(String[] labels) {

		if (outputPoints.size() > 0) {
			outputPoints.setLabels(null);
			labelPrefix = outputPoints.getElement(0).getLabelSimple()
					.toLowerCase();
		}

		if (labels != null && labels.length == 1 && outputSegments.size() > 1
				&& labels[0] != null && !labels[0].equals("")) {
			labelPrefix = labels[0];
			outputSegments.setIndexLabels(labels[0]);
		} else
			outputSegments.setIndexLabels(labelPrefix);

	}

	// TODO Consider locusequability

}
