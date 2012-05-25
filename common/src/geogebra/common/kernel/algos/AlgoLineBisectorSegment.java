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
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;


public class AlgoLineBisectorSegment extends AlgoElement implements 
	SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

    private GeoSegment s;  // input   
    private GeoLine  g;     // output        
    
    private GeoPoint2 midPoint;
    private Polynomial[] polynomials;
    private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
	
        
    /** Creates new AlgoLineBisector */
    public AlgoLineBisectorSegment(Construction cons, String label, GeoSegment s) {
        super(cons);
        this.s = s;             
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
        return Algos.AlgoLineBisectorSegment;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_LINE_BISECTOR;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = s;        
  
        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    GeoSegment getSegment() {return s;  }
    
    // line through P normal to v
    @Override
	public final void compute() { 
    	 GeoPoint2 A = s.getStartPoint();     
    	 GeoPoint2 B = s.getEndPoint();
    	
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
        return app.getPlain("LineBisectorOfA",s.getLabel(tpl));

    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		GeoPoint2 A = (GeoPoint2) s.getStartPointAsGeoElement();
		GeoPoint2 B = (GeoPoint2) s.getEndPointAsGeoElement();
		// TODO: Common code with AlgoLineBisector.java, maybe commonize.
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
		GeoPoint2 A = (GeoPoint2) s.getStartPointAsGeoElement();
		GeoPoint2 B = (GeoPoint2) s.getEndPointAsGeoElement();
		// TODO: Common code with AlgoLineBisector.java, maybe commonize.
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
		GeoPoint2 A = (GeoPoint2) s.getStartPointAsGeoElement();
		GeoPoint2 B = (GeoPoint2) s.getEndPointAsGeoElement();
		// TODO: Common code with AlgoLineBisector.java, maybe commonize.
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
		return botanaVars;
	}

	/* This is mostly the same as in AlgoLineBisector.java. TODO: maybe commonize.
	 * (non-Javadoc)
	 * @see geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo#getBotanaPolynomials()
	 */
	public Polynomial[] getBotanaPolynomials()
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (s != null){
			Variable[] v = s.getBotanaVars(); // A, B
			
			if (botanaVars==null){
				botanaVars = new Variable[4]; // storing 4 new variables (C, D)
				botanaVars[0]=new Variable();
				botanaVars[1]=new Variable();
				botanaVars[2]=new Variable();
				botanaVars[3]=new Variable();
			}
			Polynomial c1=new Polynomial(botanaVars[0]);
			Polynomial c2=new Polynomial(botanaVars[1]);
			Polynomial d1=new Polynomial(botanaVars[2]);
			Polynomial d2=new Polynomial(botanaVars[3]);
			Polynomial a1=new Polynomial(v[0]);
			Polynomial a2=new Polynomial(v[1]);
			
			botanaPolynomials = new Polynomial[4];
			// C will be the midpoint of AB  
			// 2*c1-a1-b1, 2*c2-a2-b2 (same as for AlgoMidPoint, TODO: maybe commonize)
			botanaPolynomials[0] = (new Polynomial(2)).multiply(c1).
					subtract(new Polynomial(v[0])).subtract(new Polynomial(v[2]));
			botanaPolynomials[1] = (new Polynomial(2)).multiply(c2).
					subtract(new Polynomial(v[1])).subtract(new Polynomial(v[3]));
		
			// D will be the rotation of A around C by 90 degrees
			// d2=c2+(c1-a1), d1=c1-(c2-a2) => d2-c2-c1+a1, d1-c1+c2-a2
			botanaPolynomials[2] = ((d2.subtract(c2)).subtract(c1)).add(a1);
			botanaPolynomials[3] = ((d1.subtract(c1)).add(c2)).subtract(a2);
					
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

    
}
