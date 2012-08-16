/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Try to expand the given function
 * 
 * @author Michael Borcherds
 */
public class AlgoCoefficients extends AlgoElement implements AsynchronousCommand{

	private GeoFunction f; // input
	private GeoList g; // output
	private String casInput;

	public AlgoCoefficients(Construction cons, String label, GeoFunction f) {
		this(cons, f);
		g.setLabel(label);
	}

	public AlgoCoefficients(Construction cons, GeoFunction f) {
		super(cons);
		this.f = f;

		g = new GeoList(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoCoefficients;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		setOutputLength(1);
		setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}

		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		String[] funVarStr = f.getTempVarCASString(false);

		StringBuilder sb = new StringBuilder();
		sb.append("Coefficients(");
		sb.append(funVarStr[0]); // function expression
		sb.append(",");
		sb.append(funVarStr[1]); // function variable
		sb.append(")");
		g.setUndefined();
		casInput = sb.toString();
		kernel.evaluateGeoGebraCASAsync(this);			

	}
	
	public String getCasInput(){
		return casInput;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	public void handleCASoutput(String output, int requestID) {
		if(kernel.getAlgebraProcessor().evaluateToList(output)==null){
			g.setUndefined();
		}else{
			g.set(kernel.getAlgebraProcessor().evaluateToList(output));
			g.setDefined(true);
			if(USE_ASYNCHRONOUS)
				g.updateCascade();
		}
		
	}

	public void handleException(Throwable exception,int id) {
		g.setUndefined();
		
	}

	public boolean useCacheing() {
		return true;
	}

	// TODO Consider locusequability

}
