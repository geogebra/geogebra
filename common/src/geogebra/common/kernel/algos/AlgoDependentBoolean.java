/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.plugin.Operation;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentBoolean extends AlgoElement implements SymbolicParametersAlgo, 
	SymbolicParametersBotanaAlgoAre {

    private ExpressionNode root;  // input
    private GeoBoolean bool;     // output              
        
    public AlgoDependentBoolean(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;  
        
        bool = new GeoBoolean(cons);
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        bool.setLabel(label);
    }
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentBoolean;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        super.setOutputLength(1);
        super.setOutput(0, bool);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoBoolean getGeoBoolean() { return bool; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {	
    	try {
    		
    		// needed for eg Sequence[If[liste1(i) < a
    		boolean oldLabelStatus = cons.isSuppressLabelsActive();
    		kernel.getConstruction().setSuppressLabelCreation(true);
    		
    		ExpressionValue ev = root.evaluate(StringTemplate.defaultTemplate);
    		kernel.getConstruction().setSuppressLabelCreation(oldLabelStatus);
    		
    		if (ev.isGeoElement())
        		bool.setValue(((GeoBoolean) ev).getBoolean());
    		else
    			bool.setValue(((MyBoolean) ev).getBoolean());
    	} catch (Exception e) {
    		bool.setUndefined();
    	}
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
        // was defined as e.g.  c = a & b
        return root.toString(tpl);
    }
    
    @Override
	final public String toRealString(StringTemplate tpl) {
        // was defined as e.g.  c = a & b
        return root.toRealString(tpl);
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();
		
		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();
		
		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "", (GeoLine) left, (GeoLine) right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", (GeoLine) left, (GeoLine) right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			algo.getFreeVariables(variables);
			algo.remove();
			return;
		}

		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();
		
		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();
		
		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "", (GeoLine) left, (GeoLine) right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", (GeoLine) left, (GeoLine) right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			int[] ret = algo.getDegrees();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();
		
		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();
		
		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "", (GeoLine) left, (GeoLine) right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", (GeoLine) left, (GeoLine) right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			BigInteger[] ret = algo.getExactCoordinates(values);
			algo.remove();
			return ret;
		}
		
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();
		
		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();
		
		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "", (GeoLine) left, (GeoLine) right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", (GeoLine) left, (GeoLine) right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			Polynomial[] ret = algo.getPolynomials();
			algo.remove();
			return ret;
		}
		
		throw new NoSymbolicParametersException();
	}
    
	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (!root.getLeft().isGeoElement() || !root.getRight().isGeoElement())
			throw new NoSymbolicParametersException();
		
		GeoElement left = (GeoElement) root.getLeft();
		GeoElement right = (GeoElement) root.getRight();
		
		if (root.getOperation().equals(Operation.PERPENDICULAR)) {
			AlgoArePerpendicular algo = new AlgoArePerpendicular(cons, "", (GeoLine) left, (GeoLine) right);
			Polynomial[][] ret = algo.getBotanaPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.PARALLEL)) {
			AlgoAreParallel algo = new AlgoAreParallel(cons, "", (GeoLine) left, (GeoLine) right);
			Polynomial[][] ret = algo.getBotanaPolynomials();
			algo.remove();
			return ret;
		}
		if (root.getOperation().equals(Operation.EQUAL_BOOLEAN)) {
			AlgoAreEqual algo = new AlgoAreEqual(cons, "", left, right);
			Polynomial[][] ret = algo.getBotanaPolynomials();
			algo.remove();
			return ret;
		}

		throw new NoSymbolicParametersException();
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}
}
