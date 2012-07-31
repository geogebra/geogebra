/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.elements.EquationLineBisector;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;


public class AlgoLineBisector extends AlgoElement implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo {

	private GeoPoint A, B;  // input    
    private GeoLine  g;     // output   
    
    // temp
    private GeoPoint midPoint;
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
	
    /** Creates new AlgoLineBisector */
    public AlgoLineBisector(Construction cons, String label,GeoPoint A,GeoPoint B) {
        super(cons);
        this.A = A;
        this.B = B;        
        g = new GeoLine(cons); 
        midPoint = new GeoPoint(cons);
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
    // Made public for LocusEqu
    public GeoPoint getA() { return A; }
    // Made public for LocusEqu
    public GeoPoint getB() { return B; }
    // Made public for LocusEqu
    public GeoPoint getMidPoint() {
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

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (A != null && B != null) {
			A.getFreeVariables(variables);
			B.getFreeVariables(variables);		
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (A != null && B != null) {
			int[] degree1=A.getDegrees();
			int[] degree2=B.getDegrees();
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

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (A != null && B != null){
			Variable[] vA = A.getBotanaVars(A);
			Variable[] vB = B.getBotanaVars(B);
			
			if (botanaVars==null){
				botanaVars = new Variable[4]; // storing 4 new variables (C, D)
				botanaVars[0]=new Variable();
				botanaVars[1]=new Variable();
				botanaVars[2]=new Variable();
				botanaVars[3]=new Variable();
			}

			botanaPolynomials = SymbolicParameters.botanaPolynomialsLineBisector(vA[0], vA[1],
					vB[0], vB[1], botanaVars);
					
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return new EquationLineBisector(element, scope);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
