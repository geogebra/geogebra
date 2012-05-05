/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;


public class AlgoLineBisector extends AlgoElement implements SymbolicParametersAlgo{

	private GeoPoint2 A, B;  // input    
    private GeoLine  g;     // output   
    
    // temp
    private GeoPoint2 midPoint;
	private Polynomial[] polynomials;
        
    /** Creates new AlgoLineBisector */
    public AlgoLineBisector(Construction cons, String label,GeoPoint2 A,GeoPoint2 B) {
        super(cons);
        this.A = A;
        this.B = B;        
        g = new GeoLine(cons); 
        midPoint = new GeoPoint2(cons);
        g.setStartPoint(midPoint);
        setInputOutput(); // for AlgoElement
        
        // compute bisector of A, B
        compute();      
        g.setLabel(label);
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoLineBisector;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_LINE_BISECTOR;
    }
    
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = B;
        
        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    GeoPoint2 getA() { return A; }
    GeoPoint2 getB() { return B; }
    GeoPoint2 getMidPoint() {
        return midPoint;
    }
    
    // line through P normal to v
    @Override
	public final void compute() { 
        // get inhomogenous coords
        double ax = A.inhomX;
        double ay = A.inhomY;
        double bx = B.inhomX;
        double by = B.inhomY;
         
        // comput line
        g.x = ax - bx;
        g.y = ay - by;
        midPoint.setCoords( (ax + bx), (ay + by), 2.0);   
        g.z = -(midPoint.x * g.x + midPoint.y * g.y)/2.0;     
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineBisectorAB",A.getLabel(tpl),B.getLabel(tpl));
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (A != null && B != null) {
			int[] degree1=A.getFreeVariablesAndDegrees(variables);
			int[] degree2=B.getFreeVariablesAndDegrees(variables);
			int[] result=new int[3];
			result[0]=Math.max(degree1[0]+degree1[2]+2*degree2[2],2*degree1[2]+degree2[0]+degree2[2]);
			result[1]=Math.max(degree1[1]+degree1[2]+2*degree2[2],2*degree1[2]+degree2[1]+degree2[2]);
			result[2]=2*Math.max(Math.max(degree1[2]+degree2[0],degree1[2]+degree2[1]),Math.max(degree1[0]+degree2[2],degree1[1]+degree2[2]));			
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (A != null && B != null) {
			BigInteger[] coords1 = A.getExactCoordinates(values);
			BigInteger[] coords2 = B.getExactCoordinates(values);
			
			BigInteger[] result = new BigInteger[3];
			//2 az bz (-az bx + ax bz)
			result[0]=BigInteger.valueOf(2).multiply(coords1[2]).multiply(coords2[2]).multiply(
					coords1[0].multiply(coords2[2]).subtract(coords2[0].multiply(coords1[2])));
			//2 az bz (-az by + ay bz)
			result[1]=BigInteger.valueOf(2).multiply(coords1[2]).multiply(coords2[2]).multiply(
					coords1[1].multiply(coords2[2]).subtract(coords2[1].multiply(coords1[2])));
			//(az bx - ax bz) (az bx + ax bz) - (-az by + ay bz) (az by + ay bz)
			result[2]=coords1[2].multiply(coords2[0]).subtract(coords1[0].multiply(coords2[2])).multiply(
					coords1[2].multiply(coords2[0]).add(coords1[0].multiply(coords2[2]))).subtract(
					coords1[1].multiply(coords2[2]).subtract(coords1[2].multiply(coords2[1])).multiply(
					coords1[1].multiply(coords2[2]).add(coords1[2].multiply(coords2[1]))));
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials !=null){
			return polynomials;
		}
		if (A != null && B != null) {
			Polynomial[] coords1 = A.getPolynomials();
			Polynomial[] coords2 = B.getPolynomials();
			
			polynomials = new Polynomial[3];
			//2 az bz (-az bx + ax bz)
			polynomials[0]=(new Polynomial(2)).multiply(coords1[2]).multiply(coords2[2]).multiply(
					coords1[0].multiply(coords2[2]).subtract(coords2[0].multiply(coords1[2])));
			//2 az bz (-az by + ay bz)
			polynomials[1]=(new Polynomial(2)).multiply(coords1[2]).multiply(coords2[2]).multiply(
					coords1[1].multiply(coords2[2]).subtract(coords2[1].multiply(coords1[2])));
			//(az bx - ax bz) (az bx + ax bz) - (-az by + ay bz) (az by + ay bz)
			polynomials[2]=coords1[2].multiply(coords2[0]).subtract(coords1[0].multiply(coords2[2])).multiply(
					coords1[2].multiply(coords2[0]).add(coords1[0].multiply(coords2[2]))).subtract(
					coords1[1].multiply(coords2[2]).subtract(coords1[2].multiply(coords2[1])).multiply(
					coords1[1].multiply(coords2[2]).add(coords1[2].multiply(coords2[1]))));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars() {
		// TODO Auto-generated method stub
		return null;
	}

	public Polynomial[] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		// TODO Auto-generated method stub
		return null;
	}
}
