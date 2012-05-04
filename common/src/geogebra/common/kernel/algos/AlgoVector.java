/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;


/**
 * Vector between two points P and Q.
 * 
 * @author  Markus
 * @version 
 */
public class AlgoVector extends AlgoElement implements SymbolicParametersAlgo{

	private GeoPointND P, Q;   // input
    private GeoVectorND  v;     // output     
	private Polynomial[] polynomials;
        
    /** Creates new AlgoVector */  
    public AlgoVector(Construction cons, String label, GeoPointND P, GeoPointND Q) {
        super(cons);
        this.P = P;
        this.Q = Q;         
        
        // create new vector
        v=createNewVector();      
        //v = new GeoVector(cons);   
        try {     
        	if (P.isLabelSet())
        		v.setStartPoint(P);
            else {
            	GeoPointND startPoint = newStartPoint();
            	//GeoPoint startPoint = new GeoPoint(P);
            	startPoint.set(P);
            	v.setStartPoint(startPoint);
            }        		
        } catch (CircularDefinitionException e) {}
        
        
                 
        setInputOutput();
        
        // compute vector PQ        
        compute();                          
        v.setLabel(label);
    }        
        
    protected GeoVectorND createNewVector(){
    	
    	return new GeoVector(cons);   	
    }   
   
    protected GeoPointND newStartPoint(){
    	
    	return new GeoPoint2((GeoPoint2) P);
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoVector;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_VECTOR;
    }
    
    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) P;
        input[1] = (GeoElement) Q;
        
        super.setOutputLength(1);
        super.setOutput(0, (GeoElement) v);
        setDependencies(); // done by AlgoElement
    }           
    
    public GeoVectorND getVector() { return v; }
    public GeoPointND getP() { return P; }
    public GeoPointND getQ() { return Q; }
    
    // calc the vector between P and Q    
    @Override
	public final void compute() {
        if (P.isFinite() && Q.isFinite()) {     
        	     	
           	setCoords();
                       
            // update position of unlabeled startpoint
            GeoPointND startPoint = v.getStartPoint();
            
            if (startPoint!=null)
            	if (!startPoint.isLabelSet())
            		startPoint.set(P);       
            		  
        } else {
            v.setUndefined();
        }
    }
    
    protected void setCoords(){
    	v.setCoords(P.vectorTo(Q));
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			int[] degree1 = ((SymbolicParametersAlgo) input[0])
					.getFreeVariablesAndDegrees(variables);
			int[] degree2 = ((SymbolicParametersAlgo) input[0])
					.getFreeVariablesAndDegrees(variables);
			int[] result=new int[3];
			result[0]=Math.max(degree1[0]+degree2[2],degree2[0]+degree1[2]);
			result[1]=Math.max(degree1[1]+degree2[2],degree2[1]+degree1[2]);
			result[2]=degree2[2]+degree1[2];
			
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(final HashMap<Variable,BigInteger> values) throws NoSymbolicParametersException {
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getExactCoordinates(values);
			BigInteger[] result = new BigInteger[3];
			result[0] = coords2[0].multiply(coords1[2]).subtract(
					coords1[0].multiply(coords2[2]));
			result[1] = coords2[1].multiply(coords1[2]).subtract(
					coords1[1].multiply(coords2[2]));
			result[2] = coords1[2].multiply(coords2[2]);
			return SymbolicParameters.reduce(result);
		}
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			Polynomial[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			Polynomial[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getPolynomials();
			polynomials = new Polynomial[3];
			polynomials[0] = coords2[0].multiply(coords1[2]).subtract(
					coords1[0].multiply(coords2[2]));
			polynomials[1] = coords2[1].multiply(coords1[2]).subtract(
					coords1[1].multiply(coords2[2]));
			polynomials[2] = coords1[2].multiply(coords2[2]);
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
