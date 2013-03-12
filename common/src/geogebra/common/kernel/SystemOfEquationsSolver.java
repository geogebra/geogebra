package geogebra.common.kernel;



import java.util.ArrayList;
import java.util.Arrays;

/** Class for solving system of equations
 * a1 x^2 + b1 xy + c1 y^2 + d1 x + e1 y + d1 = 0
 * a2 x^2 + b2 xy + c2 y^2 + d2 x + e2 y + d2 = 0
 * 
 * @author ddrakulic
 *
 */
public class SystemOfEquationsSolver {
	
	private double epsilon = Kernel.STANDARD_PRECISION;
	
	private EquationSolverInterface eqnSolver;

	/**
	 * Creates new solver for systems of equations
	 * @param eqnSolver equation solver
	 */
	public SystemOfEquationsSolver(EquationSolverInterface eqnSolver) {
		this.eqnSolver = eqnSolver;
	}
	
	/**
	 * Set precision
	 * @param eps precision
	 */
	void setEpsilon(double eps) {
    	epsilon = eps;
    }
	
	/**
	 * Solves of system of equations whose coefficients in arrays eqn1 and eqn2
	 * and places result in two-dimensional array res.
	 * 
	 * Equations are represented by equations
	 * eqn1[0]*x^2 + eqn1[1]*xy + eqn1[2]*y^2 + eqn1[3]*x + eqn1[4]*y + eqn1[5] = 0
	 * eqn2[0]*x^2 + eqn2[1]*xy + eqn2[2]*y^2 + eqn2[3]*x + eqn2[4]*y + eqn2[5] = 0
	 * @param eqn1 coefficients in first equation
	 * @param eqn2 coefficients in second equation
	 * @param res array to store result
	 * @param eps precision
	 * 
	 * @return Number of real roots or -1 if equations are equal or coefficients are invalid
	 * 
	 */
	final public int solveSystemOfQuadraticEquations(double eqn1[], double eqn2[], double [][] res, double eps){
		
		ArrayList<Double> xs = new ArrayList<Double>();
        ArrayList<Double> ys = new ArrayList<Double>();
        
        if(eqn1[0] == 0 || eqn2[0] == 0 || eqn1[2] == 0 || eqn2[2] == 0)
        	return -1;
        
        double a20 = 1;
        double a11 = eqn1[1]/eqn1[0];
        double a02 = eqn1[2]/eqn1[0];
        double a10 = eqn1[3]/eqn1[0];
        double a01 = eqn1[4]/eqn1[0];
        double a00 = eqn1[5]/eqn1[0];

        double b20 = 1;
        double b11 = eqn2[1]/eqn2[0];
        double b02 = eqn2[2]/eqn2[0];
        double b10 = eqn2[3]/eqn2[0];
        double b01 = eqn2[4]/eqn2[0];
        double b00 = eqn2[5]/eqn2[0];
        
        if(a11 == b11 && a02 == b02 && a10 == b10 && a01 == b01 && a00 == b00)
        	return -1;

        double d00 = a20*b10 - b20*a10;
        double d01 = a20*b11 - b20*a11;
        double d10 = a10*b00 - b10*a00;
        double d11 = a11*b00 + a10*b01 - b11*a00 - b10*a01;
        double d12 = a11*b01 + a10*b02 - b11*a01 - b10*a02;
        double d13 = a11*b02 - b11*a02;
        double d20 = a20*b00 - b20*a00;
        double d21 = a20*b01 - b20*a01;
        double d22 = a20*b02 - b20*a02;

        double quarticParams[] = new double[5];
        quarticParams[0] = d00*d10 - d20*d20;
        quarticParams[1] = d01*d10 + d00*d11 - 2*d20*d21;
        quarticParams[2] = d01*d11 + d00*d12 - d21*d21 - 2*d20*d22;
        quarticParams[3] = d01*d12 + d00*d13 - 2*d21*d22;
        quarticParams[4] = d01*d13 - d22*d22;

        double [] quarticRoots = new double[4];
        
        // finding candidates for y
        
        int solnr = eqnSolver.solveQuartic(quarticParams, quarticRoots,eps); 
        Arrays.sort(quarticRoots, 0, solnr);
        
        for(int i=0; i<solnr; i++)
        {
        	double [] quadraticParams = new double[3];

        	quadraticParams[2] = a20;
        	quadraticParams[1] = a11*quarticRoots [i] + a10;
        	quadraticParams[0] = a02*quarticRoots [i]*quarticRoots [i] + a01*quarticRoots[i] + a00;

        	double [] quadraticRoots = new double[3];
        	
        	// finding candidates for x
            int solnr2 = eqnSolver.solveQuadratic(quadraticParams, quadraticRoots, eps); 
            Arrays.sort(quadraticRoots, 0, solnr2);
            
            for(int j=0; j<solnr2; j++)
            { // checking pairs (x, y)
                double x = quadraticRoots[j];
                double y = quarticRoots[i];
                double result = b02*y*y + b11*x*y + b20*x*x + b01*y + b10*x + b00;

                if(Math.abs(result) < epsilon)
                {
                    xs.add(x);
                    ys.add(y);
                }
            }
        }

        for(int i=0; i<xs.size(); i++)
            for(int j=i+1; j<xs.size(); j++)
            	// removing duplicates, pairs where x==y
                if(Math.abs(xs.get(i) - xs.get(j)) < epsilon && Math.abs(ys.get(i) - ys.get(j)) < epsilon)
                {
                    xs.remove(j);
                    ys.remove(j);
                    j--;
                }

        for(int i=0; i<xs.size(); i++)
        {
            res[i][0] = xs.get(i);
            res[i][1] = ys.get(i);
        }

        return xs.size();
	}
}
