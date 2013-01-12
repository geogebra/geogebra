/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointLine.java
 *
 * line through P parallel to l
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoLinePointLine extends AlgoElement implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo {

    private GeoPoint P; // input
    private GeoLine l; // input
    private GeoLine g; // output       
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
	
    /** Creates new AlgoLinePointLine */
    public AlgoLinePointLine(Construction cons, String label, GeoPoint P, GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();

        g.setLabel(label);
        addIncidence();
    }

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	P.addIncidence(g);
	}

    
    @Override
	public Commands getClassName() {
        return Commands.Line;
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARALLEL;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getLine() {
        return g;
    }
   
    // Made public for LocusEqu.
    public GeoPoint getP() {
        return P;
    }
    
    // Made public for LocusEqu.
    public GeoLine getl() {
        return l;
    }

    // calc the line g through P and parallel to l   
    @Override
	public final void compute() {
        // homogenous:
        GeoVec3D.cross(P, l.y, -l.x, 0.0, g);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("LineThroughAParallelToB",P.getLabel(tpl),l.getLabel(tpl));

    }

    // Simon Weitzhofer 2012-05-07
    
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		
		if (P != null && l != null){
			P.getFreeVariables(variables);
			l.getFreeVariables(variables);
			return;
		}
		
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		
		if (P != null && l != null){
			int[] degreeP = P.getDegrees();
			int[] degreeL = l.getDegrees();
			int[] degrees = new int[3];
			degrees[0]=degreeL[0]+degreeP[2];
			degrees[1]=degreeL[1]+degreeP[2];
			degrees[2]=Math.max(degreeL[0]+degreeP[0],degreeL[1]+degreeP[1]);
			return degrees;
		}
		
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		
		if (P != null && l != null){
			BigInteger[] coordsP = P.getExactCoordinates(values);
			BigInteger[] coordsL = l.getExactCoordinates(values);
			BigInteger[] coords=new BigInteger[3];
			coords[0]=coordsL[0].multiply(coordsP[2]);
			coords[1]=coordsL[1].multiply(coordsP[2]);
			coords[2]=coordsL[0].multiply(coordsP[0]).add(coordsL[1].multiply(coordsP[1])).negate();
			return coords;
		}
		
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		
		if (polynomials!=null){
			return polynomials;
		}
		
		if (P != null && l != null){
			Polynomial[] coordsP = P.getPolynomials();
			Polynomial[] coordsl = l.getPolynomials();
			polynomials=new Polynomial[3];
			polynomials[0]=coordsl[0].multiply(coordsP[2]);
			polynomials[1]=coordsl[1].multiply(coordsP[2]);
			polynomials[2]=coordsl[0].multiply(coordsP[0]).add(coordsl[1].multiply(coordsP[1])).negate();
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
		if (P != null && l != null){
			Variable[] vP = P.getBotanaVars(P); // c1,c2
			Polynomial c1 = new Polynomial(vP[0]);
			Polynomial c2 = new Polynomial(vP[1]);
			Variable[] vL = l.getBotanaVars(l); // a1,a2,b1,b2
			Polynomial a1 = new Polynomial(vL[0]);
			Polynomial a2 = new Polynomial(vL[1]);
			Polynomial b1 = new Polynomial(vL[2]);
			Polynomial b2 = new Polynomial(vL[3]);
			
			if (botanaVars==null){
				botanaVars = new Variable[4]; // storing 2 new variables, plus the coordinates of P
				botanaVars[0]=new Variable(); // d1
				botanaVars[1]=new Variable(); // d2
				botanaVars[2]=vP[0];
				botanaVars[3]=vP[1];
			}
			Polynomial d1 = new Polynomial(botanaVars[0]);
			Polynomial d2 = new Polynomial(botanaVars[1]);
			
			botanaPolynomials = new Polynomial[2];
			// d1=c1+(b1-a1), d2=c2+(b2-a2) => d1-c1-b1+a1, d2-c2-b2+a2  
			botanaPolynomials[0] = ((d1.subtract(c1)).subtract(b1)).add(a1);
			botanaPolynomials[1] = ((d2.subtract(c2)).subtract(b2)).add(a2);
					
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public boolean isLocusEquable(){
		return true;
	}
	
	@Override
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnLinePointLine(geo, this, scope);
	}
}
