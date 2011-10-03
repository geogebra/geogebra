/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoConicFivePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoConicFivePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint[] P; // input  five points      
	private GeoPoint[] Ppert;   
    private GeoConic conic; // output             
    private double delta;
    
    private double[][] A, B, C, Cpert, Cmin;
    private double l, m;
    private GeoVec3D[] line;
    private int i, j;

    AlgoConicFivePoints(Construction cons, String label, GeoPoint[] P) {
        super(cons);
        this.P = P;
        conic = new GeoConic(cons);
        
        for (int i=0; i < P.length; i++) {
        	conic.addPointOnConic(P[i]); //TODO: move into setIncidence()
        }
        setIncidence();
        
        setInputOutput(); // for AlgoElement

        line = new GeoVec3D[4];
        for (i = 0; i < 4; i++) {
            line[i] = new GeoLine(cons);
        }
        A = new double[3][3];
        B = new double[3][3];
        C = new double[3][3];
        Cpert = new double[3][3];
        Cmin = new double[3][3];

        compute();
        conic.setLabel(label);
    }

    private void setIncidence() {
		for (int i=0; i< P.length; ++i) {
			P[i].addIncidence(conic);
		}
		
	}

	public String getClassName() {
        return "AlgoConicFivePoints";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_CONIC_FIVE_POINTS;
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = P;

        output = new GeoElement[1];
        output[0] = conic;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return conic;
    }
    GeoPoint[] getPoints() {
        return P;
    }

    // compute conic through five points P[0] ... P[4]
    // with Pl�cker � method
    protected final void compute() {
        // compute lines P0 P1, P2 P3, 
        //               P0 P2, P1 P3
        GeoVec3D.cross(P[0], P[1], line[0]);
        GeoVec3D.cross(P[2], P[3], line[1]);
        GeoVec3D.cross(P[0], P[2], line[2]);
        GeoVec3D.cross(P[1], P[3], line[3]);

        // compute degenerate conics A = line[0] u line[1],
        //                           B = line[2] u line[3]
        degCone(line[0], line[1], A);
        degCone(line[2], line[3], B);
        l = evalMatrix(B, P[4]);
        m = -evalMatrix(A, P[4]);
        linComb(A, B, l, m, C);
        
        // compute a perturbed Cpert
    	kernel.setSilentMode(true);
    	Ppert = new GeoPoint[5];
    	for (int i=0; i<5; i++) {
    		Ppert[i] = new GeoPoint(P[i]);
    		
    	}
    	
    	
    	/*
    	double maxDistSqr = 0;
    	double temp;
    	int maxDistP1 = 0;
    	int maxDistP2 = 0;
    	
    	
    	for (int i=0; i<5; i++) {
    		temp = P[0].distanceSqr(P[i]);
    		if ( temp > maxDistSqr) {
    			maxDistP2 = i;
    			maxDistSqr = temp;
    		}
    	}
    	for (int i=0; i<5; i++) {
    		temp = P[i].distanceSqr(P[maxDistP2]);
    		if (temp > maxDistSqr) {
    			maxDistP1 = i;
    			maxDistSqr = temp;
    		}
    	}
    	
    	if (maxDistSqr!=0) {
    		double t = Kernel.EPSILON/Math.sqrt(maxDistSqr);
    		Ppert[maxDistP1].setCoords(
    				P[maxDistP1].inhomX * (1+t) - P[maxDistP2].inhomX * t,
    				P[maxDistP1].inhomY * (1+t) - P[maxDistP2].inhomY * t,
    				1
    			);
       		Ppert[maxDistP2].setCoords(
    				P[maxDistP2].inhomX * (1+t) - P[maxDistP1].inhomX * t,
    				P[maxDistP2].inhomY * (1+t) - P[maxDistP1].inhomY * t,
    				1
    			);
    	 */

    	delta = 0;
    	int repetition = 5;
    	for (int m=0; m<3; m++)
    		for (int n=0; n<3; n++)
    			Cmin[m][n]=Double.POSITIVE_INFINITY;
    	
    	for (int k=0; k<repetition; k++) {
    		for (int i=0; i<5; i++) {
    			Ppert[i].randomizeForErrorEstimation();
    		}

            GeoVec3D.cross(Ppert[0], Ppert[1], line[0]);
            GeoVec3D.cross(Ppert[2], Ppert[3], line[1]);
            GeoVec3D.cross(Ppert[0], Ppert[2], line[2]);
            GeoVec3D.cross(Ppert[1], Ppert[3], line[3]);
            degCone(line[0], line[1], A);
            degCone(line[2], line[3], B);
            l = evalMatrix(B, Ppert[4]);
            m = -evalMatrix(A, Ppert[4]);
            linComb(A, B, l, m, Cpert);
            
            //calculate the estimation of error of detS
            delta = Math.min(delta, Math.abs(
            		Cpert[0][0]*Cpert[1][1] - (Cpert[0][1]+Cpert[1][0])*(Cpert[0][1]+Cpert[1][0])/4));
        	for (int m=0; m<3; m++)
        		for (int n=m; n<3; n++)
        			Cmin[m][n]=Math.min(Cmin[m][n],Math.abs((Cpert[m][n]+Cpert[n][m])/2));
        	        
    	}
    
        conic.errDetS = delta;
        //TODO: this is not reasonable enough.
        if (Math.abs(C[0][0])< Math.abs(Cmin[0][0])/10)
        	C[0][0]=0;
        if (Math.abs(C[1][1])< Math.abs(Cmin[1][1])/10)
    		C[1][1]=0;
        if (Math.abs(C[0][1] + C[1][0])< Math.abs( Cmin[0][1]) /5 ) {
    		C[0][1]=0;
    		C[1][0]=0;
        }
        conic.setMatrix(C);
        kernel.setSilentMode(false);
        
    }

    // compute degenerate conic from lines a, b
    // the result is written into A as a NON-SYMMETRIC Matrix
    final private void degCone(GeoVec3D a, GeoVec3D b, double[][] A) {
        // A = a . b^t
        A[0][0] = a.x * b.x;
        A[0][1] = a.x * b.y;
        A[0][2] = a.x * b.z;
        A[1][0] = a.y * b.x;
        A[1][1] = a.y * b.y;
        A[1][2] = a.y * b.z;
        A[2][0] = a.z * b.x;
        A[2][1] = a.z * b.y;
        A[2][2] = a.z * b.z;
    }

    // computes P.A.P, where A is a (possibly not symmetric) 3x3 matrix
    final private double evalMatrix(double[][] A, GeoPoint P) {
        return A[0][0] * P.x * P.x
            + A[1][1] * P.y * P.y
            + A[2][2] * P.z * P.z
            + (A[0][1] + A[1][0]) * P.x * P.y
            + (A[0][2] + A[2][0]) * P.x * P.z
            + (A[1][2] + A[2][1]) * P.y * P.z;
    }

    // computes the linear combination C = l * A + m * B    
    final private void linComb(
        double[][] A,
        double[][] B,
        double l,
        double m,
        double[][] C) {
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                C[i][j] = l * A[i][j] + m * B[i][j];
            }
        }
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ConicThroughABCDE",P[0].getLabel(),P[1].getLabel(),P[2].getLabel(),P[3].getLabel(),P[4].getLabel());
    }
}
