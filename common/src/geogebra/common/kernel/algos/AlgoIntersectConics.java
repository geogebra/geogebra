/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectConics.java
 *
 * Created on 1. Dezember 2001
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PointPair;
import geogebra.common.kernel.PointPairList;
import geogebra.common.kernel.SystemOfEquationsSolver;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.locusequ.elements.EquationIntersectConicsRestriction;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Computes intersection points of two conic sections
 *  
 * @author  Markus Hohenwarter
 */
public class AlgoIntersectConics extends AlgoIntersect  implements SymbolicParametersBotanaAlgo,
	RestrictionAlgoForLocusEquation {
      
	/** number of old distances that are used to 
     compute the mean distance change of one point **/
    static final int DIST_MEMORY_SIZE = 8;
    
    private GeoConic A, B;
    private GeoPoint [] P, D, Q;     // points  
    ArrayList<GeoPoint> preexistPoints; // pre-existing intersection points before this Algo is constructed
    ArrayList<GeoPoint> newPoints;
    
    private HashMap<GeoElement,Polynomial[]> botanaPolynomials;
	private HashMap<GeoElement,Variable[]> botanaVars;
    
    private GeoConic degConic;  
    private GeoLine tempLine;
    private int [] age; // for points in D   
    private int originalPermutation[]; //for backward compatibility. See #945
    private int permutation[]; // of computed intersection points Q to output points P
    private double [][] distTable;   
    private boolean [] isQonPath;
    private boolean [] isPalive; // has P ever been defined?
    private boolean firstIntersection = true;
    //private int i;
    private boolean isLimitedPathSituation;
    private boolean isPermutationNeeded = true;
    private boolean possibleSpecialCase = false;
    private int specialCasePointOnCircleIndex = 0; // index of point on both circles
      
    private PointPairList pointList = new PointPairList();
    
    private EquationSolverInterface eqnSolver;
    private SystemOfEquationsSolver sysSolver;
        
	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectConics;
	}
	
	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
	/**
	 * @param cons construction
	 */
	public AlgoIntersectConics(Construction cons) {           
    	super(cons); 
    	
		eqnSolver = cons.getKernel().getEquationSolver();
		sysSolver = cons.getKernel().getSystemOfEquationsSolver(eqnSolver);
		
		degConic = new GeoConic(cons);  
	       
	}
    
    /**
     * @param cons construction
     * @param A first conic
     * @param B second conic
     */
    public AlgoIntersectConics(Construction cons, GeoConic A, GeoConic B) {           
    	this(cons);     
    	
        this.A = A;
        this.B = B;   
        isLimitedPathSituation = A.isLimitedPath() || B.isLimitedPath();        
        
        // init temp vars        
        P  = new GeoPoint[4]; // output
        D  = new GeoPoint[4];
        Q  = new GeoPoint[4];       
        
        preexistPoints = new ArrayList<GeoPoint>();
        newPoints = new ArrayList<GeoPoint>();
        originalPermutation = new int[] {0,1,2,3};
        isQonPath = new boolean[4];    
        isPalive = new boolean[4];
        age = new int[4];
        permutation = new int[] {0,1,2,3};
        distTable = new double[4][4];
        for (int i=0; i < 4; i++) {
            P[i] = new GeoPoint(cons);                    
            Q[i] = new GeoPoint(cons);      
            D[i] = new GeoPoint(cons);
        }                                   
        

        
        // check possible special case
        possibleSpecialCase = handleSpecialCase();
        
        setInputOutput(); // for AlgoElement
        
    	ArrayList<GeoPoint> list1 = A.getPointsOnConic();
    	ArrayList<GeoPoint> list2 = B.getPointsOnConic();
    	ArrayList<GeoPoint> temp;
    	
    	if (list1!=null && list2!=null) {
    		for (int i = 0; i<list1.size(); i++) {
    			if (list1.get(i).getIncidenceList().contains(B))
    				preexistPoints.add(list1.get(i));
    		}
    	}
    	
        
        initForNearToRelationship();
        compute(); 
        addIncidence();

    }
    
	 /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
        for (int i=0; i < 4; i++) {
        	P[i].addIncidence(A);
    		P[i].addIncidence(B);
        }
	}

	// for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = B;
        
        super.setOutput(P);
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
        
	@Override
	public GeoPoint [] getIntersectionPoints() {
		return P;
	}
	
    /**
     * Made public for LocusEqu
     * @return first conic
     */
    public GeoConic getA() { return A; }
    /**
     * Made public for LocusEqu
     * @return second conic
     */
    public GeoConic getB() { return B; }
	
	@Override
	protected GeoPoint [] getLastDefinedIntersectionPoints() {
		return D;
	}
	
	@Override
	public boolean isNearToAlgorithm() {
    	return true;
    }
	
	@Override
	public
	final void initForNearToRelationship() {     
		isPermutationNeeded = true;
    	for (int i=0; i < P.length; i++) {        	 	 
    	 	 age[i] = 0; 
             isQonPath[i] = true;
             isPalive[i] = false;             
        }
    }
	
	 // calc intersections of conics A and B
	@Override
	public final void compute() {
		
		
		if (permutation[3] == 0) {
			App.debug("error");
		}
		/*
		if (this.getA().getLabelSimple().equals("c") && getB().getLabelSimple().equals("g"))*/
		{
			App.debug("");
			for (int i=0; i<4; i++) {
			System.out.print(permutation[i]+"\t");
			}
			System.out.println("");
			for (int i=0; i<4; i++) {
			System.out.println(D[i].toString() + "\t" + P[i].toString() + "\t" + Q[i].toString());
			}
		}
		
		
    	// check if conics A and B are defined	   
   	   	if (!(A.isDefined() && B.isDefined())) {
   	   		for (int i=0; i < P.length; i++) {
   	   			P[i].setUndefined();
   	   		}
   	   		return;
   	   	}            
   	   	
   	   	/*
   	   	 * for debugging
   	   	
        if (this.getA().getLabelSimple().getBytes()[0] == 'c' && getB().getLabelSimple().getBytes()[0] == 'd') {
        	AbstractApplication.debug("");

        }
         */
    	
    	// check for special case of two circles with common point
        if (possibleSpecialCase) {
            if (handleSpecialCase()) return;
        }     	
    	
        // continous: use near-to-heuristic between old and new intersection points
        // non-continous: use computeContinous() to init a permutation and then
        //                always use this permutation
        boolean continuous = isPermutationNeeded || kernel.isContinuous();   
        if (continuous) {
        	computeContinuous();
        } else {
        	computeNonContinous();
        }        	

        matchExistingIntersections();
        avoidDoubleTangentPoint();
    }	   
    
    private void matchExistingIntersections() {
		// TODO Auto-generated method stub

    	if (preexistPoints.size()==0)
    		return;
    	
    	newPoints.clear();
    	
        for (int i=0; i < 4; i++) {
        	if (P[i].isDefined())
        		newPoints.add(P[i]);
        } 
        
        if (newPoints.size()==0)
        	return;
        
        double gap = Double.POSITIVE_INFINITY;
        double minDistance = Double.POSITIVE_INFINITY;
        double d = Double.POSITIVE_INFINITY;
        int closestPointIndex = 0; // for preexist point
        
        for (int i=0; i< 4; i++) {
        	for (int j=i+1; j<4; j++) {
        		if (P[i].isDefined()&& P[j].isDefined()) {
        			d = P[i].distance(P[j]);
        			if (d<gap )
        				gap = d;
        		}
        	}
        }
        
        
        for (int i=0; i<4; i++) {
        	if (P[i].isDefined()) {
        		minDistance = Double.POSITIVE_INFINITY;
        		for (int j=0; j< preexistPoints.size(); j++) {
    				d = preexistPoints.get(j).distance(P[i]);
    				if (d < minDistance) {
    					minDistance = d;
    					closestPointIndex = j;
    				}
    			}    	
            	
            	if (Kernel.isGreaterEqual(gap/2, minDistance)  )
            		P[i].setCoords(preexistPoints.get(closestPointIndex));
        	}

        		
        }
	}

	/**
     * There is an important special case we handle separately:
     * Both conic sections are circles and one is defined through a 
     * point A on the other one. In this case the first intersection 
     * point should always be A. 
     * @return true if this special case was handled.
     */
    private boolean handleSpecialCase() {
    	// we need two circles
    	if (A.type != GeoConicNDConstants.CONIC_CIRCLE ||
    		B.type != GeoConicNDConstants.CONIC_CIRCLE)
    		return false;
    	
    		
		// check if we have a point on A that is also on B
    	GeoPoint pointOnConic = getPointFrom1on2(A, B);
    	if (pointOnConic == null) 
    		// check if we have a point on B that is also on A
    		 pointOnConic = getPointFrom1on2(B, A); 
    	
    	// if we didn't have a common point, there's no special case
    	if (pointOnConic == null) 
    		return false;				    	
    		    
    	// intersect the two circles
        intersectConicsWithEqualSubmatrixS(A, B, Q);  
                          
	    // pointOnConic should be first intersection point
        // Note: if the first intersection point was already set when a file
        //       was loaded, then we need to make sure that we don't lose this information
        int firstIndex = specialCasePointOnCircleIndex;
        int secondIndex = (firstIndex + 1) % 2;
                
        if (firstIntersection && didSetIntersectionPoint(firstIndex)) {           
        	if (!P[firstIndex].isEqual(pointOnConic)) {
            	// pointOnConic is NOT equal to the loaded intersection point:
        		// we need to swap the indices
        		int temp = firstIndex;
        		firstIndex = secondIndex;
        		secondIndex = temp;
        		
        		specialCasePointOnCircleIndex = firstIndex;        		     
        	}  
        	firstIntersection = false;
        } 
        
        P[firstIndex].setCoords(pointOnConic);         

        // the other intersection point should be the second one
        boolean didSetP1 = false;
        for (int i=0; i < 2; i++) {  
	   		if (!Q[i].isEqual(P[firstIndex])) {
	   			P[secondIndex].setCoords(Q[i]);
	   			didSetP1 = true;
	   			break;
	   		}
	    }   
        if (!didSetP1) { // this happens when both intersection points are equal 
        	P[secondIndex].setCoords(pointOnConic);          	
        }
	   	 
	   	if (isLimitedPathSituation) {
	   		// make sure the points are on a limited path
	   		for (int i=0; i < 2; i++) {  
	   			if (!pointLiesOnBothPaths(P[i]))
	   				P[i].setUndefined();    			          
	   	    }     	 
	   	}	   		   	
	   	
	   	//Application.debug("circle-circle special case: took point " + pointOnConic);	   	
	   	return true;
    }
    
    private static GeoPoint getPointFrom1on2(GeoConic A, GeoConic B) {
    	GeoPoint pointOnConic = null;
    	
    	// check if a point on A is also on B
		// get points on conic and see if one of them is on line g
		ArrayList<GeoPoint> pointsOnConic = A.getPointsOnConic();
		if (pointsOnConic != null) {
			int size = pointsOnConic.size();
			for (int i=0; i < size; i++) {
				GeoPoint p = pointsOnConic.get(i);
				//if (B.isOnPath(p, AbstractKernel.MIN_PRECISION)) {
				if (p.isLabelSet() && 
						p.getIncidenceList()!=null && 
						p.getIncidenceList().contains(B)) {
					
					//TODO: a potential temporary fix for #94.
    				if (A.isOnPath(p, Kernel.EPSILON) && B.isOnPath(p, Kernel.EPSILON))
    					pointOnConic = p;
    				
					
					//pointOnConic = p;
					break;
				}
			}
		}
		
		return pointOnConic;
    }
    
    /**
     * Use the current permutation to set output points P from computed points Q.        
     */  
     private void computeNonContinous() {    
    	 // calc new intersection points Q
    	 intersectConics(A, B, Q);            
    	     	
         // use fixed permutation to set output points P
    	 for (int i=0; i < P.length; i++) {        	
         	P[i].setCoords(Q[permutation[i]]);           	
         }   
    	 
    	 if (isLimitedPathSituation) {
        	 // make sure the points are on a limited path
    		 for (int i=0; i < P.length; i++) {  
    			 if (!pointLiesOnBothPaths(P[i]))
    				 P[i].setUndefined();    			          
    	     }     	 
    	 }
     }
    
    // calc intersections of conics A and B, with the permutation
    // according to a near-to relationship with the old permutation
    // then store the current permutation to int[] storePermutation.
    final private void computeContinuous() {     
        /* D ... old defined points
         * P ... current points
         * Q ... new points
         *
         * We want to find a permutation of Q, so that the sum of squared 
         * distances between old point Di and new Point Qi is minimal.
         * The distances are weighed by Di's age (i.e. how long it has not
         * been reset by a finit intersection point).      
         */        
    	
    	// if there are only two points P[i] that are defined and equal
    	// we are in a singularity situation     
    	boolean noSingularity = !isSingularitySituation();    	    

		// remember the defined points D, so that Di = Pi if Pi is finite        
		// and set age
		for (int i=0; i < 4; i++) {
			boolean finite = P[i].isFinite();
		
			
			if (noSingularity && finite)  { 
				D[i].setCoords(P[i]);   
				age[i] = 0;
			} else {                
				age[i]++;
			}				
			
			// update alive state
			isPalive[i] = isPalive[i] || finite || P[i].labelSet;
		}                 
                     
        // calc new intersection Points Q        
        intersectConics(A, B, Q);      
        
        // for limited paths we have to distinguish between intersection points Q
        // that lie on both limited paths or not. This is important for choosing
        // the right permutation in setNearTo()
        if (isLimitedPathSituation) {
        	updateQonPath();
        }
                 
        if (firstIntersection) {
        // init points in order P[0], P[1] , ...
            int count=0;
            for (int i=0; i < Q.length; i++) {
            	// 	make sure intersection points lie on limited paths   
                if (Q[i].isDefined() && pointLiesOnBothPaths(Q[i])) {              
                    P[count].setCoords(Q[i]);
                    D[count].setCoords(P[count]);
                    firstIntersection = false;
                    count++;
                }
            }   
            return;
        }
        
        // calc distance table of defined points D and new points Q
        distanceTable(D, age, Q, distTable);           
        
        // find permutation     
        setNearTo(P, isPalive, Q, isQonPath, distTable, pointList, permutation,
        		!isPermutationNeeded,
        		1.0/Math.min(getKernel().getXscale(), getKernel().getYscale())
        		);

        isPermutationNeeded = false;
        
        /*
    	System.out.print("permutation: ");
    	for (int i=0; i < permutation.length; i++) {
    		System.out.print(permutation[i] + " ");
    	}
    	Application.debug();
    	*/  
    	
		if ("c".equals(getA().getLabelSimple()) && "g".equals(getB().getLabelSimple()))
		{
			App.debug("");
			for (int i=0; i<2; i++) {
			System.out.print(permutation[i]+"\t");
			}
			System.out.println("");
			for (int i=0; i<2; i++) {
			System.out.println(D[i].toString() + "\t" + P[i].toString() + "\t" + Q[i].toString());
			}
			for (int i=0; i<2; i++) {
				for (int j=0; j<2; j++) {
					System.out.print(distTable[i][j] + "\t");
				}
				System.out.println("");
			}
		}
        
        // 	make sure intersection points lie on limited paths
        if (isLimitedPathSituation)   
        	handleLimitedPaths();        
    }    
    
    /**
     * Checks whether the computed intersection points really lie on
     * the limited paths. Note: points D[] and P[] may be changed here.     
     */
    private void handleLimitedPaths() {
    	//  singularity check
        boolean noSingularity = !isSingularitySituation();
                              
    	for (int i=0; i < P.length; i++) {
            if (P[i].isDefined()) {
            	if (!pointLiesOnBothPaths(P[i])) {
            		// the intersection point should be undefined as it doesn't lie
            		// on both (limited) paths. However, we want to keep the information
            		// of P[i]'s position for our near-to-approach to achieve continous movements.
            		// That's why we remember D[i] now                		                   		                	    
            	    if (noSingularity && P[i].isFinite())  { 
            	    	D[i].setCoords(P[i]);   
            	    	// the age will be increased by 1 at the
            	    	// next call of compute() as P[i] will be undefined
                        age[i] = -1;                                	
                    }    
            	    P[i].setUndefined(); 
            	}
            }
        }
    }
    
    /**
     * Checks wether Q[i] lies on g and c and sets isQonPath[] accordingly. 
     */
    private void updateQonPath() {
    	for (int i=0; i < Q.length; i++) {
    		isQonPath[i] = pointLiesOnBothPaths(Q[i]);
    	}
    }
    
    
    private boolean pointLiesOnBothPaths(GeoPoint Pt) {
    	return A.isIntersectionPointIncident(Pt, Kernel.MIN_PRECISION) 
				&& B.isIntersectionPointIncident(Pt, Kernel.MIN_PRECISION);
    }
    
	/**
	 * Returns wheter we are in a singularity situation. This is the case whenever
	 *  there are only two points P[i] that are defined and equal.	 
	 */ 
    private boolean isSingularitySituation() {
    	int count = 0;
    	int index[] = new int[P.length];
    	
    	for (int i=0; i < P.length; i++) {
    		if (P[i].isDefined()) {
    			index[count] = i;
    			count++;
    			if (count > 2) return false;
    		}	
    	}
    	
    	// we have a singularity if there are two defined points
    	// that are equal
    	boolean ret = (count == 2 && P[index[0]].isEqual(P[index[1]]));
    	
    	//if (ret)
    	//	Application.debug("Singularity at " + P[index[0]]);    	
    	
    	return ret;
    }
    
    /** calc four intersection Points of conics A and B.
     *    write result into points
     * @param conic1 first conic
     * @param conic2 second conic
     * @param points output array
     */
    final public void intersectConics(GeoConic conic1, GeoConic conic2, 
                                        GeoPoint[] points) {
    	    	 
    	if (!(conic1.isDefined() && conic2.isDefined())) {
    		 for (int i=0; i < points.length; i++) 
    		 	points[i].setUndefined();
    		 return;
    	}
    	
    	boolean ok = false;
    	int i = 0;    	
    	
        // equal conics have no intersection points, unless they are themselves single points.
        if (conic1.equals(conic2)) {
	
            for (i=0; i < points.length; i++) {
                points[i].setUndefined();
            } /* TODO
        	if (conic1.type == GeoConicNDConstants.CONIC_SINGLE_POINT){
        		points[0].setCoords(conic1.getSinglePoint());
        	} */
            return;
        } 
    	
        // input is already degenerate
        if (conic1.isLineConic()) {
            intersectWithDegenerate(conic2, conic1, points);
            ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
        }
        else if (conic2.isLineConic()) {
            intersectWithDegenerate(conic1, conic2, points);
            ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
        }
        
        // STANDARD PROCEDURE
        double epsilon = Kernel.STANDARD_PRECISION;
        while (!ok && epsilon <= Kernel.MIN_PRECISION) { 
            Kernel.setEpsilon(epsilon);                        
            
            // find intersection points conics through intersection points
        	ok = calcIntersectionPoints(conic1, conic2, points, epsilon);  
                                         
            // try it with lower precision     
            epsilon *= 10.0;                        
        }            
        Kernel.resetPrecision();
        
        // did not find intersections
        if (!ok) {
            //Application.debug("INTERSECTING CONICS FAILED (epsilon = " + epsilon/10.0 + ")");        
            // INTERSECTION FAILED
            for (i=0; i < points.length; i++) points[i].setUndefined();                                       
        }
        
        // for non-continous kernel: move defined intersection points to front
        else if (!kernel.isContinuous()) {
        	moveDefinedPointsToFront(points);
        }
    }   
    
    /** 
     * Arranges intersection points Q so that all defined
     * intersection points are at the beginning of the array.
     */  
    private static void moveDefinedPointsToFront(GeoPoint [] points) {
    	for (int i=0; i < points.length; i++) {
   			if (points[i].isDefined()) {
         		 // move defined intersection point as far to the front as possible
        		 int j=i-1;
        		 boolean move = false;
        		 while (j >= 0 && !points[j].isDefined()) {
        			 move = true;
        			 j--;
        		 }
        		 if (move) {
        			 j++;
        			 points[j].setCoords(points[i]);
        			 points[i].setUndefined();
        		 }
        	}
        }      	     	   	 
    }
    
    /**
     * Intersect conic with degenerate conic degConic. Write result into
     * points.
     */
    final static private void intersectWithDegenerate(GeoConic conic, GeoConic degConic,
                                               GeoPoint [] points) {
        if (degConic.isDefined()) {
            switch (degConic.getType()) {
                case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
                case GeoConicNDConstants.CONIC_PARALLEL_LINES:                                    
                    AlgoIntersectLineConic.intersectLineConic(degConic.lines[0], conic, points);
                    points[2].setCoords(points[0]);
                    points[3].setCoords(points[1]);
                    AlgoIntersectLineConic.intersectLineConic(degConic.lines[1], conic, points);
                    return;

				case GeoConicNDConstants.CONIC_EMPTY: 
					// this shouldn't happen: try it with doubleline conic
					degConic.enforceDoubleLine();					
					//Application.debug("intersectWithDegenerate: empty degenerate conic, try double line");	
					//degConic.setToSpecific();
					//Application.debug("degConic: " + degConic);
					
                case GeoConicNDConstants.CONIC_DOUBLE_LINE:                    
                    AlgoIntersectLineConic.intersectLineConic(degConic.lines[0], conic, points);
                    points[2].setUndefined();
                    points[3].setUndefined();
                    return;
                    
                case GeoConicNDConstants.CONIC_SINGLE_POINT:                                        
                    //Application.debug("intersectConics: single point: " + p);                    
                    points[0].setCoords(degConic.getSinglePoint());                    
                    points[1].setUndefined();
                    points[2].setUndefined();
                    points[3].setUndefined();
                    return;                                                            
            }        
        }   
                
        // something went wrong: no intersections
        //Application.debug("intersectWithDegenerate: undefined degenerate conic, type:  " + degConic.type);
        for (int i=0; i < 4; i++) points[i].setUndefined();                   
        		
        //Application.debug("intersect conics: invalid degerate conic type: " + degConic.getTypeString());
        // for (int i=0; i < 6; i++) 
        //    Application.debug(" A[" + i + "] = " +degConic.A[i]);
    }
    
    /**
     * Tests if at least one point lies on conics A and B.
     */
    final private static boolean testPoints(GeoConic A, GeoConic B, GeoPoint[] P, double eps) {
        boolean foundPoint = false;      
        for (int i=0; i < P.length; i++) {
            if (P[i].isDefined()) {                                         
                if (!(A.isOnFullConic(P[i], eps) && B.isOnFullConic(P[i], eps))) 
                	P[i].setUndefined();                
                else
                	foundPoint = true;
            }            
        }            
        return foundPoint;        
    }
    
    /**
     * Caculates the intersection points of the conic sections A and B.
     */      
    final private boolean calcIntersectionPoints(GeoConic A, GeoConic B, GeoPoint [] points, double eps) {
    	/* 
    	 * Pluecker mu method:
    	 * Solves the cubic equation det(A + x B) = 0 or det(x A + B) = 0 
    	 * to get degenerate conics C = A + x B or C = x A + B that pass through
    	 * all intersection points of A and B.
         */
       
        double [] flatDeg = new double[6]; // flat matrix of degenerate conic                
                
        // test wheter conics A and B have same submatrix S
        // => degnerate is single line
        // (e.g. for circles)
        if ( (Math.abs(A.matrix[0] - B.matrix[0]) <= eps) &&
             (Math.abs(A.matrix[1] - B.matrix[1]) <= eps) &&
             (Math.abs(A.matrix[3] - B.matrix[3]) <= eps) ) 
        {
        	/*
            //sol[0] = -1.0;            
            // set single line matrix
            flatDeg[0] = 0.0;
            flatDeg[1] = 0.0;
            flatDeg[3] = 0.0;
            flatDeg[2] = A.matrix[2] - B.matrix[2];
            flatDeg[4] = A.matrix[4] - B.matrix[4];
            flatDeg[5] = A.matrix[5] - B.matrix[5];

            // classify degenerate conic
            degConic.setDegenerateMatrixFromArray(flatDeg); 
            
        	// try first conic
        	intersectWithDegenerate(A, degConic, points);
        	if (testPoints(A, B, points, AbstractKernel.MIN_PRECISION))
        		return true;
        	
        	// try second conic
        	intersectWithDegenerate(B, degConic, points);
        	if (testPoints(A, B, points, AbstractKernel.MIN_PRECISION))
        		return true;
        		*/
        	        	
        	return intersectConicsWithEqualSubmatrixS(A, B, points);        	        	
        }
              
        
        // STANDARD CASE
        // We search for det(A + x B) = 0 to get a degenerate conic section C
        // with C = A + x B that includes all intersection points of A and B.
        // This leads to a cubic equation for x.             
        double [] eqn = new double[4];
        double [] sol = new double[3];
        double [] flatA = new double[6]; // flat matrix of conic A
        double [] flatB = new double[6]; // flat matrix of conic B       
        
        // copy and normalize flat matrices             
        for (int i=0; i<6; i++) {
        	flatA[i] = A.matrix[i];        	
        	flatB[i] = B.matrix[i];        	
        }        
        normalizeArray(flatA);
        normalizeArray(flatB);
                                          
        // compute coefficients of cubic equation        
        // sol[0] + sol[1] x + sol[2] x^2 + sol[3] x^3 = 0        
        // constant
        eqn[0] =    flatA[2] * (flatA[0] * flatA[1] - flatA[3] * flatA[3])
                  + flatA[4] * (2.0 * flatA[3] * flatA[5] - flatA[1] * flatA[4])                                             
                  - flatA[0] * flatA[5] * flatA[5];        
        // x
        eqn[1] =    flatB[0] * (flatA[1] * flatA[2] - flatA[5] * flatA[5])
                  + flatB[1] * (flatA[0] * flatA[2] - flatA[4] * flatA[4])                                  
                  + flatB[2] * (flatA[0] * flatA[1] - flatA[3] * flatA[3])
                  + 2.0 * (
                      flatB[3] * (flatA[4] * flatA[5] - flatA[2] * flatA[3])
                    + flatB[4] * (flatA[3] * flatA[5] - flatA[1] * flatA[4])
                    + flatB[5] * (flatA[3] * flatA[4] - flatA[0] * flatA[5])
                  );                              
        // x^2
        eqn[2] =    flatA[0] * (flatB[1] * flatB[2] - flatB[5] * flatB[5])              
                  + flatA[1] * (flatB[0] * flatB[2] - flatB[4] * flatB[4])
                  + flatA[2] * (flatB[0] * flatB[1] - flatB[3] * flatB[3])
                  + 2.0 * (                                                                        
                        flatA[3] * (flatB[4] * flatB[5] - flatB[2] * flatB[3])                  
                      + flatA[4] * (flatB[3] * flatB[5] - flatB[1] * flatB[4])                  
                      + flatA[5] * (flatB[3] * flatB[4] - flatB[0] * flatB[5])
                  );                                                      
        // x^3
        eqn[3] =    flatB[2] * (flatB[0] * flatB[1] - flatB[3] * flatB[3])
                  + flatB[4] * (2.0 * flatB[3] * flatB[5] - flatB[1] * flatB[4]) 
                  - flatB[0] * flatB[5] * flatB[5];                                                  
              
      // Application.debug(eqn[3] + " x^3 + " + eqn[2] + " x^2 + " 
      //                  + eqn[1] + " x + "  + eqn[0] );
        
       // solve cubic equation and sort solutions       
       int solnr = eqnSolver.solveCubic(eqn, sol);
       if (solnr > -1)
    	   Arrays.sort(sol, 0, solnr);       

       // for (i=0;i<solnr;i++) {
       //    Application.debug("sol[" + i + "] = " + sol[i]);
       // }
       
       /*              
       if (!degConic.isLabelSet())  {              	
       	degConic.setLabel("deg");
       	degConic.setLabelVisible(true);
       }
       */
      
        
       // Go through cubic equation's solutions and take first degenerate conic
       // with det(A + x B) < eps.           
	   for (int i=0; i < solnr; i++) {  
 		   // A + x B
		   for (int j=0; j < 6; j++) {   
			   flatDeg[j] = (flatA[j] + sol[i] * flatB[j]);	          	          
		   }     	 

		   // check if det(A + x B) = 0			   			  			   
		   degConic.setDegenerateMatrixFromArray(flatDeg);
			   	
		    // try first conic
        	intersectWithDegenerate(A, degConic, points);
        	if (testPoints(A, B, points, Kernel.MIN_PRECISION))
        		return true;
        	
        	// try second conic
        	intersectWithDegenerate(B, degConic, points);
        	if (testPoints(A, B, points, Kernel.MIN_PRECISION))
        		return true;			   		   		   	               	
	   }
	  	  	   
	   // DESPARATE MODE
	   // we did not find a degenerate conic with the solutions from above
	   // so we try det(x A + B) now 
      
        // change equation from {0, 1, 2, 3} to {3, 2, 1, 0}
        // i.e. intersect(A,B) = intersect(B,A)                	
    	//Application.debug("CHANGE EQUATION");   
    	
        double temp = eqn[0];
        eqn[0] = eqn[3];
        eqn[3] = temp;
        temp = eqn[1];
        eqn[1] = eqn[2];
        eqn[2] = temp;                        
            
        // solve cubic equation and sort solutions        
        solnr = eqnSolver.solveCubic(eqn, sol); 
        if (solnr > -1)
        	Arrays.sort(sol, 0, solnr);
        
        // Go through cubic equation's solutions and take first degenerate conic
        // that gives us intersection points         
 	    for (int i=0; i < solnr; i++) {  
  		   // x A + B
 		   for (int j=0; j < 6; j++) {   
 			   flatDeg[j] = (sol[i] * flatA[j] + flatB[j]);	          	          
 		   }     	  				   			  			  
 		   degConic.setDegenerateMatrixFromArray(flatDeg);
			   		   
		   //degConic.update();
		   //Application.debug("degenerate found (2): " + degConic.getTypeString());				   				   			   
		   
		    // try first conic
        	intersectWithDegenerate(A, degConic, points);
        	if (testPoints(A, B, points, Kernel.MIN_PRECISION))
        		return true;
        	
        	// try second conic
        	intersectWithDegenerate(B, degConic, points);
        	if (testPoints(A, B, points, Kernel.MIN_PRECISION))
        		return true;			   	               
 	   }
 	   
 	    // If intersection points not found
 	    // try with another algorithm - solving system of algebraic equations of conics
 	    /* Author ddrakulic */
 	    
 	    double [] param1 = new double[6];
 	    param1[0] = A.matrix[0]; //x^2
 	    param1[1] = 2*A.matrix[3]; //xy
 	    param1[2] = A.matrix[1]; //y^2
 	    param1[3] = 2*A.matrix[4]; //x
	    param1[4] = 2*A.matrix[5]; //y
	    param1[5] = A.matrix[2]; //constant

 	    double [] param2 = new double[6];
 	    param2[0] = B.matrix[0]; //x^2
 	    param2[1] = 2*B.matrix[3]; //xy
 	    param2[2] = B.matrix[1]; //y^2
 	    param2[3] = 2*B.matrix[4]; //x
	    param2[4] = 2*B.matrix[5]; //y
	    param2[5] = B.matrix[2]; //constant
	    
	    double [][] res = new double[4][2];
	    
	    // Solving system of equations 
	    solnr = sysSolver.solveSystemOfQuadraticEquations(param1, param2, res);
	    
	    if (solnr == -1) {
	    	return false;
	    }

	    for(int i=0; i<solnr; i++)
	    	points[i].setCoords(res[i][0], res[i][1], 1.0d);
	    	
	    for(int i=solnr; i<4; i++)
	    	points[i].setUndefined();
	    
	    if (testPoints(A, B, points, Kernel.MIN_PRECISION))
    		return true;
		
        //Application.debug("no solutions found");
        //degConic.setUndefined();
        return false;
    }
    
    /**
     * If A and B have same submatrix S, the intersection points are on
     * a (double) line.
     * @param points resulting intersection points
     * @return true if points were found
     */
    private boolean intersectConicsWithEqualSubmatrixS(GeoConic c1, GeoConic c2, GeoPoint [] points) {    	
	    if (tempLine == null) {			
			tempLine = new GeoLine(cons);			
		}
		
		// set line passing through intersection points (e.g. of two circles)
	    tempLine.setCoords(        			
				2*(c1.matrix[4] - c2.matrix[4]),
				2*(c1.matrix[5] - c2.matrix[5]),
				c1.matrix[2] - c2.matrix[2]);
		        	        	        	
		// try first conic
		AlgoIntersectLineConic.intersectLineConic(tempLine, c1, points);        	
		if (testPoints(c1, c2, points, Kernel.MIN_PRECISION))
			return true;
		
		// try second conic
		AlgoIntersectLineConic.intersectLineConic(tempLine, c2, points);
		if (testPoints(c1, c2, points, Kernel.MIN_PRECISION))
			return true; 
		
		return false;
	}
    
    
    /**
     * Divides the given array by its maximum absolute value.
     */
    private static void normalizeArray(double [] array) {
    	// find max abs value in array
    	double max = 0;
    	for (int i=0; i < array.length; i++) {         	
         	double abs = Math.abs(array[i]);
         	if (abs > max) max = abs;
    	}
    	
    	// divide array by max    	
    	for (int i=0; i < array.length; i++) {         	
         	array[i] /= max;         	
    	}
    }
    
    
/***************************************************************
 * NEAREST DISTANCE RELATION
 ***************************************************************/              
    
    /**
     * set tabel[i][j] to square distance between D[i] and Q[j]. 
     * distSqr(D[i], Q[j]) := (D[i] - Q[j])^2 + age[i].
     * age[i] tells for every D[i], how long it has been undefined
     * (old points' distances should be larger).     
     * Undefined (NaN) or infinite distances are set to max of all defined
     * distances + 1. If there are no defined distances, all distances
     * are set to 0.
     * @param D 
     * @param age how long corresponding D has been undefined
     * @param Q 
     * @param table 
     */
    final public static void distanceTable(GeoPoint [] D, int[] age, 
                                           GeoPoint [] Q, double[][] table) {
        int i, j;        
        boolean foundUndefined = false;
        double dist, max = -1.0;
        
        // calc all distances and maximum distance (max)
        for (i=0; i < D.length; i++) {
        //	checkFixedPoint = meanDistance[i] == 0.0;
        	for (j=0; j < Q.length; j++) {            
                dist = D[i].distanceSqr(Q[j]) + age[i];
                
                if (Double.isInfinite(dist) || Double.isNaN(dist)) {
                    dist = -1; // mark as undefined
                    foundUndefined = true;
                }          
                else if (dist > max) {                                      
                    max = dist;                    
                }
                table[i][j] = dist;                
            }            
        }
        
        if (foundUndefined) {                                    
            max = max + 1; 
            // set undefined distances to max (marked as -1)
            for (j=0; j < Q.length; j++) {
                for (i=0; i < D.length; i++) {
                // check if entry is marked as undefined (Q[j])
                    if (table[i][j] == -1) {
                        // set all distances to Q[j] to max+1
                        table[i][j] = max;            
                    } 
                }
            }
        }
                
        /*
        for (i=0; i < D.length; i++) {
            Application.debug("D[" + i + "] = " + D[i] +
            "   Q[" + i + "] = " + Q[i]);
        }        
        
        // print table
           for (i=0; i < D.length; i++) {                
                for (j=0; j < Q.length; j++) {             
                // check if entry is marked as undefined (Q[j])                    
                        // set all distances to Q[j] to max+1
                        System.out.print(table[i][j] + "\t");            
                }
                Application.debug();                
            }        
        Application.debug();                
         */
    }
    

    /**
     * Sets Pi = Qj according to near to heuristic (using the closest
     * pairs of points in ascending distance order).               
     *    
     *  For limitedPaths we also have to make sure that we only use points from Q 
     *  to set P that really lie on both paths.
     * @param P 
     * @param isPalive 
     * @param Q 
     * @param isQonPath 
     * @param distTable 
     * @param pointList 
     *  
     *  @param permutation is an output parameter for the permutation
     *  of points Q used to set points P, e.g. permuation {1,0} 
     *  means that P[0]=Q[1] and P[1]=Q[0]
     */
    final static void setNearTo(GeoPoint[] P, boolean [] isPalive,
    							GeoPoint[] Q, boolean [] isQonPath,    							    							
    							double[][] distTable, 
    							PointPairList pointList,
								int [] permutation,
								boolean needStrict,
								double eps) {
    	int indexP, indexQ;
    	
    	pointList.clear();
    	for (indexP = 0; indexP < P.length; indexP++) {        		
    		for (indexQ = 0; indexQ < Q.length; indexQ++) {    		    		    	        	
        		// sorted inserting
        		pointList.insertPointPair(
        				indexP, 
        				isPalive[indexP],
						indexQ, 						
						isQonPath[indexQ],
        				distTable[indexP][indexQ]);
        	}        	
    	}    	    
    	
    	//Application.debug(pointList.toString());
    	//System.out.flush();
   
        double gap = Double.POSITIVE_INFINITY;
        double temp;
        for (int i=0; i<Q.length; i++) {
        	for (int j=i+1; j<Q.length; j++) {
        		temp = Q[i].distanceSqr(Q[j]);
        		if (temp < gap)
        			gap = temp;
        	} 
        }
        
    	
    	if ( !needStrict ||
    			(gap > eps * eps) ) {
    //		AbstractApplication.debug("strict list");
    		
    	PointPair pair;    	
    	int currentSize=-1; 
        //while (!pointList.isEmpty()) {
    	
    	while (!pointList.isEmpty() && pointList.size()!=currentSize) {
    		currentSize = pointList.size();
        	// take first pair from pointList
        	pair = pointList.getHead();	
        	indexP = pair.indexP;
        	indexQ = pair.indexQ;
        	
        	
        	if (pair.isPalive && pair.isQonPath &&
        			pointList.getClosestPWithindexQ(pair.indexQ) == pair.indexP 
        			&& pointList.getClosestQWithindexP(pair.indexP) == pair.indexQ) {
        		//workingList.insertPointPair(pair.indexP, isPalive, indexQ, isQonPath, distance)
        	
        	// remove all other pairs with P[indexP] or Q[indexQ] from list
        	pointList.removeAllPairs(pair);
         	
        	}
        	// P[indexP] = Q[indexQ]
            P[indexP].setCoords(Q[indexQ]);
            permutation[indexP] = indexQ;                      
        }   
    	
    	while (!pointList.isEmpty()) {
    		
        	// take first pair from pointList
        	pair = pointList.getHead();	
        	indexP = pair.indexP;
        	indexQ = pair.indexQ;
        	
        	// remove all other pairs with P[indexP] or Q[indexQ] from list
        	pointList.removeAllPairs(pair);             	        	
         	
        	// P[indexP] = Q[indexQ]
            P[indexP].setCoords(Q[indexQ]);
            permutation[indexP] = indexQ;                      
        }   
        

    	} else {
    		//AbstractApplication.debug("non strict list");
    		
    		//keep permutations
    		for (int i = 0; i< P.length; i++ ) {
    			P[i].setCoords(Q[permutation[i]]);
    		}
    	}
    		
    	
    }
    /**
     * Sets Pi = Qj according to near to heuristic (using the closest
     * pairs of points in ascending distance order).               
     *    
     *  For limitedPaths we also have to make sure that we only use points from Q 
     *  to set P that really lie on both paths.
     * @param P 
     * @param isPalive 
     * @param Q 
     * @param isQonPath 
     * @param distTable 
     * @param pointList 
     *  
     *  @param permutation is an output parameter for the permutation
     *  of points Q used to set points P, e.g. permuation {1,0} 
     *  means that P[0]=Q[1] and P[1]=Q[0]
     */
    final static void setNearTo(GeoPoint[] P, boolean [] isPalive,
    							GeoPoint[] Q, boolean [] isQonPath,    							    							
    							double[][] distTable, 
    							PointPairList pointList,
								int [] permutation) {
    	int indexP, indexQ;
    	pointList.clear();
    	for (indexP = 0; indexP < P.length; indexP++) {        		
    		for (indexQ = 0; indexQ < Q.length; indexQ++) {    		    		    	        	
        		// sorted inserting
        		pointList.insertPointPair(
        				indexP, 
        				isPalive[indexP],
						indexQ, 						
						isQonPath[indexQ],
        				distTable[indexP][indexQ]);
        	}        	
    	}    	    
    	
    	//Application.debug(pointList.toString());
    	//System.out.flush();
   
    	if (pointList.isStrict()) {
    		App.debug("strict list");
    		
    	PointPair pair;    	
        while (!pointList.isEmpty()) {
        	// take first pair from pointList
        	pair = pointList.getHead();	
        	indexP = pair.indexP;
        	indexQ = pair.indexQ;          	
        	
        	// remove all other pairs with P[indexP] or Q[indexQ] from list
        	pointList.removeAllPairs(pair);             	        	
         	
        	// P[indexP] = Q[indexQ]
            P[indexP].setCoords(Q[indexQ]);
            permutation[indexP] = indexQ;                      
        }   
        

    	} else {
    		App.debug("non strict list");
    		
    		//keep permutations
    		for (int i = 0; i< P.length; i++ ) {
    			P[i] = Q[permutation[i]];
    		}
    	}
    		
    	
    }
 

    /* This code is very similar to AlgoIntersetLineConics.
     * TODO: Maybe commonize.
     */
	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars.get(geo);
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			Polynomial[] ret = botanaPolynomials.get(geo);
			if (ret != null)
				return ret;
		}
		
		// We cannot decide a statement properly if the "line" is a segment or the conic is not a circle:
		if (A != null && B != null && A.isCircle() && B.isCircle()) {
			Variable[] botanaVarsThis = new Variable[2];
			if (botanaVars == null) {
				botanaVars = new HashMap<GeoElement, Variable[]>();
			}
			if (botanaVars.containsKey(geo)) {
				botanaVarsThis = botanaVars.get(geo);
			} else {
				// Intersection point (we create only one):
				botanaVarsThis = new Variable[2];
				botanaVarsThis[0] = new Variable();
				botanaVarsThis[1] = new Variable();
				botanaVars.put(geo, botanaVarsThis);
			}

			Polynomial[] botanaPolynomialsThis = null;
			// Force NDG that the two intersection points must differ:
			// TODO: This is very ugly.
			Variable[] botanaVarsOther = new Variable[2];
			Iterator<GeoElement> it = botanaVars.keySet().iterator();
			boolean found = false;
			while (it.hasNext()) {
				GeoElement otherGeo = it.next();
				// This should be one element:
				if (!otherGeo.equals(geo)) {
					botanaPolynomialsThis = new Polynomial[3];
					botanaVarsOther = botanaVars.get(otherGeo);
					botanaPolynomialsThis[2] = (Polynomial.sqrDistance(botanaVarsThis[0], botanaVarsThis[1], botanaVarsOther[0], botanaVarsOther[1])
							.multiply(new Polynomial(new Variable()))).subtract(new Polynomial(1));
					found = true;
				}
			}
			if (!found) {
				botanaPolynomialsThis = new Polynomial[2];
			}
			
			Variable[] vA = A.getBotanaVars(geo); // 4 variables from the first circle
			Variable[] vB = B.getBotanaVars(geo); // 4 variables from the first circle

			botanaPolynomialsThis[0] = Polynomial.equidistant(vA[2], vA[3], vA[0], vA[1], botanaVarsThis[0], botanaVarsThis[1]);
			botanaPolynomialsThis[1] = Polynomial.equidistant(vB[2], vB[3], vB[0], vB[1], botanaVarsThis[0], botanaVarsThis[1]);
   			
   			if (botanaPolynomials == null) {
				botanaPolynomials = new HashMap<GeoElement, Polynomial[]>();
			}
			botanaPolynomials.put(geo, botanaPolynomialsThis);
			
			return botanaPolynomialsThis;
			
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return new EquationIntersectConicsRestriction(element, this, scope);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
    
}


