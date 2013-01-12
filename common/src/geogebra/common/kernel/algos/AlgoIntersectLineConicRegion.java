/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLineConic.java
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
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;

/**
 *
 * @version 
 */
public class AlgoIntersectLineConicRegion extends AlgoIntersectLineConic {    

	private GeoLine[] lines ; //output
	private int numberOfPoints;
	private int numberOfLineParts;
	private int numberOfOutputLines;
	String labelPrefixForLines;
	//private SortedSet<Double> paramSet; 
	private Double tMin, tMax;
	//private GeoPoint[] outputPoints;
	private boolean currentPartIsInRegion;
	private GeoPoint[] outputPoints;
	private GeoLine[] outputLines;
	
	@Override
	public Commands getClassName() {
		return Commands.IntersectionPaths;
	}

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECTION_CURVE;
    }
       
    public AlgoIntersectLineConicRegion(Construction cons, String[] labels, GeoLine g, GeoConic c) {
        super(cons, g, c);
        
        //setLabels(labels);
    }
    
    @Override
	protected void initElements() {
    	super.initElements();
   	
        for (int i = 0; i<P.length; i++) {
        	setOutputDependencies(P[i]);
        }
    	
        tMin = g.getMinParameter();
        tMax = g.getMaxParameter();
        
        GColor BLUE_VIOLET= geogebra.common.factories.AwtFactory.prototype.newColor(153,0,255);
        int THICK_LINE_WITHIN_LINE = 4;
        
        //TODO: this initialization of input assumes the type 
        // of the line and the conic
        // and will NOT update anymore.
        // should change to a more dynamic version.
        lines = new GeoLine[4];
        lines[0] = (GeoLine) g.copyInternal(cons);
        if (Double.isInfinite(tMin))
        	lines[1] = new GeoRay(cons);
        else
        	lines[1] = new GeoSegment(cons);
        lines[2] = new GeoSegment(cons);
        if (Double.isInfinite(tMax)) 
        	lines[3] = new GeoRay(cons);
        else
        	lines[3] = new GeoSegment(cons);

        for (int i = 0; i<4; i++) {
        	setOutputDependencies(lines[i]);
           	lines[i].setLineThickness(THICK_LINE_WITHIN_LINE); 
            lines[i].setObjColor(BLUE_VIOLET);
        }
        
        //paramSet.add(tMin);
        //paramSet.add(tMax);
    }

	public GeoLine[] getIntersectionLines() {
		GeoLine[] ret = new GeoLine[numberOfOutputLines];
		for (int i = 0; i<numberOfOutputLines; i++){
			ret[i] = (GeoLine) super.getOutput(numberOfPoints + i);
		}
		return ret;
	}
   
	public int getNumOfLineParts() {
		return numberOfLineParts;
	}
	
	public int getOutputSize() {
		return numberOfOutputLines;
	}
	
	@Override
	public void compute() {
        super.compute();
        
        numberOfPoints=0;
        for (int i = 0; i<P.length; i++) {
        	if (P[i].isDefined())
        		numberOfPoints++;
        }
        
        //build lines
        numberOfOutputLines = 0;
        initCurrentPartIsInRegion();
        
        switch (intersectionType) {
        case INTERSECTION_PRODUCING_LINE: //contained in degenerate conic
		case INTERSECTION_ASYMPTOTIC_LINE: //intersect at no point
		case INTERSECTION_PASSING_LINE: //intersect at no point
    		lines[1].setUndefined();
    		lines[2].setUndefined();
			lines[3].setUndefined();
			
			
			if (!currentPartIsInRegion)
				lines[0].setUndefined();
			else  {
				lines[0].set(g);
				numberOfOutputLines ++;
			}
    		break;
		case INTERSECTION_MEETING_LINE:
			lines[0].setUndefined();
			lines[2].setUndefined();
			
			
			if (currentPartIsInRegion)
			{
				lines[3].setUndefined();
				//set line[1]
				double t = g.getPossibleParameter(Q[0].getCoords());
				
				if (tMin.isInfinite()) {
					((GeoRay)lines[1]).set(Q[0], g);
					lines[1].changeSign();
					numberOfOutputLines ++;
				} else {
					if (Kernel.isGreater(tMin, t))
						lines[1].setUndefined();
					else {
						((GeoSegment)lines[3]).set(Q[0],g.endPoint, g);
						numberOfOutputLines ++;
					}
				}
				
			}else{
				lines[1].setUndefined();
				//set line[3]
				double t = g.getPossibleParameter(Q[0].getCoords());
				
				if (tMax.isInfinite()) {
					((GeoRay)lines[3]).set(Q[0], g);
					numberOfOutputLines ++;
				} else {
					if (Kernel.isGreater(tMin, t))
						lines[1].setUndefined();
					else {
						((GeoSegment)lines[3]).set(Q[0],g.endPoint, g);
						numberOfOutputLines ++;
					}
				}
			}
    		break;
    		
		case INTERSECTION_TANGENT_LINE: //tangent at one point
			
			lines[0].setUndefined();
			
			if (currentPartIsInRegion)
			{
				//set line[1] and line[3]
				double t = g.getPossibleParameter(Q[0].getCoords());
				
				if (tMin.isInfinite()) {
					((GeoRay)lines[1]).set(Q[0], g);
					lines[1].changeSign();
					
					numberOfOutputLines ++;
				} else {
					if (Kernel.isGreater(tMin, t))
						lines[1].setUndefined();
					else {
						((GeoSegment)lines[3]).set(Q[0],g.endPoint, g);
						numberOfOutputLines ++;
					}
				}
				
				if (tMax.isInfinite()) {
					((GeoRay)lines[3]).set(Q[0], g);
					numberOfOutputLines ++;
				} else {
					if (Kernel.isGreater(tMin, t))
						lines[1].setUndefined();
					else {
						((GeoSegment)lines[3]).set(Q[0],g.endPoint, g);
						numberOfOutputLines ++;
					}
				}
			}
			else{
				lines[1].setUndefined();
				lines[3].setUndefined();
			}
    		break;
		case INTERSECTION_SECANT_LINE: //intersect at two points
			
			//get parameters of two intersection points
			double t1 = g.getPossibleParameter(Q[0].getCoords());
			double t2 = g.getPossibleParameter(Q[1].getCoords());
			int j0 = 0;
			int j1 = 1;
			
			//let t1<=t2. if not, swap the index of the points
			if (t1>t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
				j1 = 0;
				j0 = 1;
			} 	
			
			lines[0].setUndefined();
			if (currentPartIsInRegion)
			{
				lines[2].setUndefined();
				
				//set line[1] and line[3]

				if (tMin.isInfinite()) {
					((GeoRay)lines[1]).set(Q[j0], g);
					lines[1].changeSign();
					numberOfOutputLines ++;
				} else {
					if (Kernel.isGreater(tMin, t1))
						lines[1].setUndefined();
					else {
						((GeoSegment)lines[1]).set(Q[j0],g.endPoint, g);
						numberOfOutputLines ++;
					}
				}
				
				if (tMax.isInfinite()) {
					((GeoRay)lines[3]).set(Q[j1], g);
					numberOfOutputLines ++;
				} else {
					if (Kernel.isGreater(t2, tMax))
						lines[3].setUndefined();
					else {
						((GeoSegment)lines[3]).set(Q[j1],g.endPoint, g);
						numberOfOutputLines ++;
					}
				}
			} else {
				lines[1].setUndefined();
				lines[3].setUndefined();
				
				//set line[2]
			
				if (Kernel.isGreater(t1,tMax)||Kernel.isGreater(tMin,t2))
					lines[2].setUndefined();
				else {
					((GeoSegment)lines[2]).set(
							Kernel.isGreater(tMin, t1)? g.startPoint : Q[j0],
							Kernel.isGreater(t2, tMax)? g.endPoint : Q[j1],
							g);
					numberOfOutputLines ++;
				}	
			}
			break;
        }

        refreshOutput();
        
        setLabelsForPointsAndLines();

        
	}
    private void setLabelsForPointsAndLines() {
        GeoElement.setLabels(new String[] {null}, outputPoints);
        
        
        if ( (labelPrefixForLines==null || "".equals(labelPrefixForLines)) &&
        		numberOfPoints!=0 && numberOfOutputLines!=0)
    				labelPrefixForLines = ((GeoElement)P[0]).getFreeLabel(P[0].getLabel(StringTemplate.defaultTemplate).toLowerCase());
        
       
        GeoElement.setLabels(labelPrefixForLines,outputLines);
	}

	@Override
	protected void refreshOutput() {
        super.setOutputLength(numberOfPoints + numberOfOutputLines);
        outputPoints =  new GeoPoint[numberOfPoints];
        outputLines =  new GeoLine[numberOfOutputLines];
        
        int index=0;
        for (int i = 0; i<P.length; i++) {
        	if (P[i].isDefined()) {
        		super.setOutput(index, P[i]);
        		outputPoints[index]=P[i];
        		index++;
        	}
        }
        for (int i = 0; i<lines.length; i++) {
        	if (lines[i].isDefined()) {
        		super.setOutput(index, lines[i]);
        		outputLines[index-numberOfPoints]=lines[i];
        		index++;
        	}
        }
	}

	// for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = c;
        input[1] = g;


       // output = lines;
        setDependencies(); // done by AlgoElement
    }    
    
	private static boolean inOpenInterval(double t, double a, double b) {
		return Kernel.isGreater(b, t) && Kernel.isGreater(t, a);
	}
	
	  void initCurrentPartIsInRegion() {
		  
		  switch (intersectionType) {
		  case INTERSECTION_PRODUCING_LINE: //contained in degenerate conic
		  case INTERSECTION_ASYMPTOTIC_LINE: //intersect at no point
		  case INTERSECTION_PASSING_LINE: //intersect at no point
			  numberOfLineParts = 1;
			  break;
		  case INTERSECTION_MEETING_LINE:
			  numberOfLineParts = 2;
			  break;
		  case INTERSECTION_TANGENT_LINE: //tangent at one point
		  case INTERSECTION_SECANT_LINE: //intersect at two points
			  numberOfLineParts = 3;
			  break;
		  default:
			  numberOfLineParts = -1;
		  }
	        
		  
      //decide whether the full line starts inside or outside the region
      currentPartIsInRegion = false;
      Coords ex = null;
      double t0, t1 = 0;
      switch (c.type){
      case GeoConicNDConstants.CONIC_PARABOLA:
      	ex = c.getEigenvec(0);
      	if (numberOfLineParts == 2) {
      	
      	currentPartIsInRegion = Kernel.isGreater(0, 
      				g.getCoords().dotproduct(ex));
      	}
      	break;
      case GeoConicNDConstants.CONIC_HYPERBOLA:
      	ex = c.getEigenvec(0);
      	
      	if (numberOfLineParts == 2) {
      		c.pointChanged(Q[0]);
      		t0 = Q[0].getPathParameter().getT();
      		currentPartIsInRegion = Kernel.isGreater(1, t0) ^
      				Kernel.isGreater(g.getCoords().dotproduct(ex), 0);
      	} else if (numberOfLineParts == 3) {
      		c.pointChanged(Q[0]);
      		c.pointChanged(Q[1]);
      		t0 = Q[0].getPathParameter().getT();
      		t1 = Q[1].getPathParameter().getT();
      		//infinite part is in region if and only if 
      		//two interesction points are on different branches
      		//namely: truth value of [t0 in (-1,1)] and [t1 in (-1,1)] should be different
      		currentPartIsInRegion = Kernel.isGreater(1, t0) ^
      				Kernel.isGreater(1, t1); 
      	}
      	break;
      case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
      	if (numberOfLineParts == 1) {
      		currentPartIsInRegion = true;
      	} else if (numberOfLineParts == 2) {
      		c.pointChanged(Q[0]);
      		t0 = Q[0].getPathParameter().getT();
      		currentPartIsInRegion = 
      			(inOpenInterval(t0,1,2) || inOpenInterval(t0,-1,0)) 
      			^ Kernel.isGreater(g.getCoords().dotproduct(ex), 0);
      	} else if (numberOfLineParts == 3) {
      		c.pointChanged(Q[0]);
      		c.pointChanged(Q[1]);
      		t0 = Q[0].getPathParameter().getT();
      		t1 = Q[1].getPathParameter().getT();
      		currentPartIsInRegion = 
      			(inOpenInterval(t0,-1,0) && inOpenInterval(t1,1,2)) ||
      			(inOpenInterval(t1,-1,0) && inOpenInterval(t0,1,2)) ||
      			(inOpenInterval(t0,0,1) && inOpenInterval(t1,2,3)) ||
      			(inOpenInterval(t1,0,1) && inOpenInterval(t0,2,3));
      	}
      	break;
      case GeoConicNDConstants.CONIC_PARALLEL_LINES:
      	if (numberOfLineParts == 1) {
      		if (Kernel.isGreater( -g.z/((g.x)*(g.x)+(g.y)*(g.y)), 0))
      			currentPartIsInRegion = true;
      	}
      	break;
      default:
      	//currentPartIsInRegion = false;
      	break;
      }
      currentPartIsInRegion ^= c.isInverseFill();
}
}