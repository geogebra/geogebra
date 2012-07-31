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
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathAlgo;
import geogebra.common.kernel.PathNormalizer;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.locusequ.elements.EquationPointOnPathRestriction;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;


public class AlgoPointOnPath extends AlgoElement implements PathAlgo, SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo, RestrictionAlgoForLocusEquation {

	private Path path; // input
    private GeoPoint P; // output      
    private NumberValue param;
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable variable;
	private Variable[] botanaVars;
	private BigInteger[] exactCoordinates;

    public AlgoPointOnPath(
        Construction cons,
        String label,
        Path path,
        double x,
        double y) {
    	
    	this(cons, path, x, y);
       
        P.setLabel(label);
    }

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	P.addIncidence((GeoElement) path);
		
	}

	public AlgoPointOnPath(Construction cons, String label, Path path, double x,
			double y, NumberValue param) {
    	this(cons,path,x,y,param);
		P.setLabel(label);
	}
    
    public AlgoPointOnPath(Construction cons,  Path path, double x,
			double y, NumberValue param) {
    	super(cons);
        this.path = path;
        // create point on path and compute current location
        P = new GeoPoint(cons);
        P.setPath(path);
        P.setCoords(x, y, 1.0);
		this.param = param;
		

		setInputOutput(); // for AlgoElement	       	        
		compute();		
		addIncidence();
	}

	public AlgoPointOnPath(Construction cons, Path path, double x, double y) {
        super(cons);
        this.path = path;
        
        // create point on path and compute current location
        P = new GeoPoint(cons);
        P.setPath(path);
        P.setCoords(x, y, 1.0); 
        

        setInputOutput(); // for AlgoElement
        addIncidence();
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoPointOnPath;
    }

	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POINT;
    }
    
	
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	if(param == null){
    		input = new GeoElement[1];
    		input[0] = path.toGeoElement();
    	}else {
    		input = new GeoElement[2];
    		input[0] = path.toGeoElement();
    		input[1] = param.toGeoElement();    		
    	}
        setOutputLength(1);
        setOutput(0, P);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint getP() {
        return P;
    }
    public Path getPath() {
        return path;
    }
      
    @Override
	public final void compute() {
    	if(param != null){
    		PathParameter pp = P.getPathParameter();
    		//Application.debug(param.getDouble()+" "+path.getMinParameter()+" "+path.getMaxParameter());
    		pp.setT(PathNormalizer.toParentPathParameter(param.getDouble(), path.getMinParameter(), path.getMaxParameter()));
    		//Application.debug(pp.t);
    	}
    	if (input[0].isDefined()) {	    	
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation        
        return app.getPlain("PointOnA", input[0].getLabel(tpl));
    }
    
	public boolean isChangeable() {
		return param == null;
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (input[0] instanceof GeoLine){
			((SymbolicParametersAlgo) input[0]).getFreeVariables(variables);
			if (variable==null){
				variable=new Variable(P);
			}
			variables.add(variable);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (input[0] instanceof GeoLine){
			int[] degreesLine = ((SymbolicParametersAlgo) input[0]).getDegrees();
			
			int[] result=new int[3];
			result[0]=degreesLine[2]+1;
			result[1]=degreesLine[2]+1;
			result[2]=Math.max(degreesLine[0]+1,degreesLine[1]+1);
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			HashMap<Variable, BigInteger> values) throws NoSymbolicParametersException {
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (input[0] instanceof GeoLine && variable != null){
			exactCoordinates=new BigInteger[3];
			BigInteger[] line=((SymbolicParametersAlgo) input[0]).getExactCoordinates(values);
			exactCoordinates[0]=line[2].multiply(values.get(variable));
			exactCoordinates[1]=line[2].multiply(BigInteger.ONE.subtract(values.get(variable)));
			exactCoordinates[2]=line[0].multiply(values.get(variable).negate()).add(line[1].multiply(values.get(variable).subtract(BigInteger.ONE)));
			return exactCoordinates;
		}
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (path instanceof GeoLine){
			if (variable==null){
				variable=new Variable(P);
			}
			polynomials=new Polynomial[3];
			Polynomial[] line=((SymbolicParametersAlgo) input[0]).getPolynomials();
			polynomials[0]=line[2].multiply(new Polynomial(variable));
			polynomials[1]=line[2].multiply((new Polynomial(1)).subtract(new Polynomial(variable)));
			polynomials[2]=line[0].multiply((new Polynomial(variable)).negate()).add(line[1].multiply((new Polynomial(variable)).subtract(new Polynomial(1))));
			return polynomials;
			
		}
		throw new NoSymbolicParametersException();
	}
	
	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (input[0] != null && input[0] instanceof GeoLine){
			if (botanaVars==null){
				botanaVars = new Variable[2];
				botanaVars[0]=new Variable();
				botanaVars[1]=new Variable();
			}
			Variable[] fv = ((SymbolicParametersBotanaAlgo) input[0]).getBotanaVars(input[0]); // 4 variables
			botanaPolynomials = new Polynomial[1];
			botanaPolynomials[0] = Polynomial.collinear(fv[0], fv[1], fv[2], fv[3], botanaVars[0], botanaVars[1]);
			return botanaPolynomials;
		}

		if (input[0] != null && input[0] instanceof GeoConic){
			if (((GeoConic) input[0]).isCircle()) {
				if (botanaVars==null){
					botanaVars = new Variable[2];
					botanaVars[0]=new Variable();
					botanaVars[1]=new Variable();
				}
				Variable[] fv = ((SymbolicParametersBotanaAlgo) input[0]).getBotanaVars(input[0]); // 4 variables
				botanaPolynomials = new Polynomial[1];
				// If this new point is D, and ABC is already a triangle with the circumcenter O,
				// then here we must claim that e.g. AO=OD:
				botanaPolynomials[0] = Polynomial.equidistant(fv[2], fv[3], fv[0], fv[1],
						botanaVars[0], botanaVars[1]);
				return botanaPolynomials;
			}
		}
		
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return new EquationPointOnPathRestriction(element, this, scope);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
