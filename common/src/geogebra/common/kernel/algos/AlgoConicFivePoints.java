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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;

import java.util.ArrayList;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoConicFivePoints extends AlgoElement {

    private GeoPoint[] P; // input  five points      
	private GeoConic conic; // output             
    private boolean criticalCase; // true when 5 points is on a parabola
    
    private double[][] A, B, C;
    private double l, m;
    private GeoVec3D[] line;
    private int i, j;

    public AlgoConicFivePoints(Construction cons, String label, GeoPoint[] P) {
        this(cons, P);
        conic.setLabel(label);
    }

    public AlgoConicFivePoints(Construction cons, GeoPoint[] P) {
        super(cons);
        this.P = P;
        conic = new GeoConic(cons);
        
      
        
        setInputOutput(); // for AlgoElement

        line = new GeoVec3D[4];
        for (i = 0; i < 4; i++) {
            line[i] = new GeoLine(cons);
        }
        A = new double[3][3];
        B = new double[3][3];
        C = new double[3][3];
        checkCriticalCase();
        compute();
        addIncidence();
        
        /* moved into addIncidence()
        for (int i=0; i < P.length; i++) {
        	conic.addPointOnConic(P[i]); 
        }
        */
        
    }

    private void checkCriticalCase() {
    	criticalCase = false;

    	for (int i=0; i<P.length; i++) {
    		if (P[i].getIncidenceList() == null)
    			return;
    	}
    	
    	ArrayList<GeoElement> firstList =  P[0].getIncidenceList();

      	
		for (int j=0; j<firstList.size(); j++ ){
			if (firstList.get(j).isGeoConic()) {
				GeoConic p = (GeoConic)firstList.get(j);
				if ( p.getType() == GeoConic.CONIC_PARABOLA) {
					criticalCase = true;
					for (int i = 1; i<5; i++) {
						if (!P[i].getIncidenceList().contains(p)) {
							criticalCase = false;
							break;
						}
					}
				}
			}

			if (criticalCase) {
				break;
			}
		}
	
    }

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
	private void addIncidence() {
		for (int i=0; i< P.length; ++i) {
			P[i].addIncidence(conic);
		}	
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoConicFivePoints;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_CONIC_FIVE_POINTS;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = P;

        super.setOutputLength(1);
        super.setOutput(0, conic);
        setDependencies(); // done by AlgoElement
    }

    public GeoConic getConic() {
        return conic;
    }
    
    GeoPoint[] getPoints() {
        return P;
    }
    
    /**
     * Method created for LocusEqu project.
     * @return a copy of inner array so it cannot be manipulated from outside.
     */
    public GeoPoint[] getAllPoints() {
    	GeoPoint[] copy = new GeoPoint[this.getPoints().length];
    	System.arraycopy(this.getPoints(), 0, copy, 0, copy.length);
    	return copy;
    }

    // compute conic through five points P[0] ... P[4]
    // with Pl���cker ��� method
    @Override
	public final void compute() {
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
        
        /***
         * Perturbation method to estimate the error of "detS" of the conic
         * 
         * The following is a random perturbation method to estimate detS
         * it is commented out because (1) it is not deterministic
         * (2) it still can't solve the problem of #1294
         * 
         * Tam 6/9/2012
         */
        
        /*
        // compute a perturbed Cpert
    	kernel.setSilentMode(true);
    	Ppert = new GeoPoint2[5];
    	for (int i=0; i<5; i++) {
    		Ppert[i] = new GeoPoint2(P[i]);
    		
    	}
    	*/
    	
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

    	/* perturbation for finding out deltaS
    	 * 
    	 */
    	/*
    	delta = Kernel.MIN_PRECISION;
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
        */
    	/**
    	 * perturbation method ends
    	 */
    	
        /***
         * Testing: use analytic method: 
         * 
         * solving system of linear equations
         * 
         * uses:
         * 
         * 	import org.apache.commons.math.linear.AbstractFieldMatrix;
			import org.apache.commons.math.linear.Array2DRowFieldMatrix;
			import org.apache.commons.math.linear.Array2DRowRealMatrix;
			import org.apache.commons.math.linear.FieldMatrix;
			import org.apache.commons.math.linear.DecompositionSolver;
			import org.apache.commons.math.linear.QRDecompositionImpl;
			import org.apache.commons.math.linear.RealMatrix;
			import org.apache.commons.math.linear.SingularValueDecompositionImpl;
			import org.apache.commons.math.util.BigReal;
         */
        /*
        RealMatrix coeffM = new Array2DRowRealMatrix(5, 6);
        
        for (int i = 0; i<5; i++) {
        	coeffM.setRow(i, new double[] {P[i].inhomX * P[i].inhomX,P[i].inhomX * P[i].inhomY,P[i].inhomY * P[i].inhomY, P[i].inhomX , P[i].inhomY, 1.0});
        }
        
        SingularValueDecompositionImpl solver = new SingularValueDecompositionImpl(coeffM);
        
       solver.getSolver().solve(new double[] {0,0,0,0,0});
       
       //RealMatrix test1 = solver.getV().multiply(solver.getVT());
       //RealMatrix test2 = solver.getVT().multiply(solver.getV());
       
       int key = -1;
       double keysum = 1;
       for (i=0; i<6; i++) {
    	   double sum = 0;
    	   for (j=0; j<5; j++) {
    		   sum += solver.getV().getEntry(i,j) * solver.getV().getEntry(i,j);
    	   }
    	   if (sum < keysum) {
    		   key = i;
    		   keysum = sum;
    	   }
       }
       
       double[] xx = new double[6];
       double[] v6 = new double[6];
       if (!Kernel.isZero(1-keysum)) {
    	   xx[5] = 1/(1-keysum);
    	   for (int j=0; j<5; j++) {
    		   xx[j] = -xx[5]*solver.getV().getEntry(key,j);
    	   }
       }
       
       for (int i=0; i<5; i++) {
    	   v6[i] = 0;
       }
       
       v6[key] = xx[5];
       
       for (int i=0; i<6; i++) {
    	   for (int j=0; j<5; j++) {
    		   v6[i] += xx[j] * solver.getV().getEntry(i,j);
    	   }
       }
       
       RealMatrix checkSol = new Array2DRowRealMatrix(6,1);
       checkSol.setColumn(0, v6);
       RealMatrix check = coeffM.multiply(checkSol);
       //double[] solution = solver.getg.getV().getColumn(5);
       for (int i=0; i<check.getRowDimension(); i++) {
       System.out.println(check.getRow(1).toString());
       }
        
        conic.setMatrix(new double[] {v6[0],v6[2],v6[5],v6[1]/2,v6[3]/2,v6[4]/2});
        */
        /***
        * solving system of linear equations test ends
        ***/
        
        /***
         * critical case: five points lie on an unstable conic
         * now only for parabola.
         * Need more tests for: one line; two lines; one point; two points
         */
        
        if (criticalCase) {
        	conic.errDetS = Double.POSITIVE_INFINITY;
        } else {
        	conic.errDetS = Kernel.MIN_PRECISION;
        }
       
        
        conic.setMatrix(C);
        //System.out.println(conic.getTypeString());               
        
    }

    // compute degenerate conic from lines a, b
    // the result is written into A as a NON-SYMMETRIC Matrix
    final private static void degCone(GeoVec3D a, GeoVec3D b, double[][] A) {
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
    final private static double evalMatrix(double[][] A, GeoPoint P) {
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

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ConicThroughABCDE",P[0].getLabel(tpl),
        		P[1].getLabel(tpl),P[2].getLabel(tpl),P[3].getLabel(tpl),P[4].getLabel(tpl));
    }

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
