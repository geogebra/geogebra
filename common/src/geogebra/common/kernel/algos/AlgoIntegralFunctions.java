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
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Area between two functions (GeoFunction) f(x) and g(x) over an interval [a, b].
 * The value equals Integral[f(x) - g(x), a, b] = Integral[f(x), a, b] - Integral[g(x), a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralFunctions extends AlgoElement  implements DrawInformationAlgo{

	private GeoFunction f, g; // input
	private NumberValue a, b; //input
	private GeoBoolean evaluate; //input
	private GeoElement ageo, bgeo;
	private GeoNumeric n; // output n = integral(f(x) - g(x), x, a, b)   

	private GeoNumeric intF, intG;						

	
	public AlgoIntegralFunctions(Construction cons, String label, 
			GeoFunction f, GeoFunction g,
			NumberValue a, NumberValue b) {
		this(cons, label,f, g, a, b, null);
        n.setLabel(label);
	}
	
	public AlgoIntegralFunctions(Construction cons, String label, 
							GeoFunction f, GeoFunction g,
							NumberValue a, NumberValue b, 
							GeoBoolean evaluate) {
		super(cons);
		this.f = f;
		this.g = g;		
		this.a = a;
		this.b = b;
		this.evaluate = evaluate;
		ageo = a.toGeoElement();		
		bgeo = b.toGeoElement();
		
		
		// helper algorithms for integral f and g		
		AlgoIntegralDefinite algoInt = new AlgoIntegralDefinite(cons, f, a, b, evaluate);
		cons.removeFromConstructionList(algoInt);
		intF = algoInt.getIntegral();
		
		algoInt = new AlgoIntegralDefinite(cons, g, a, b, evaluate);
		cons.removeFromConstructionList(algoInt);
		intG = algoInt.getIntegral();
		
		// output: intF - intG
		n = new GeoNumeric(cons);				
				
		setInputOutput(); // for AlgoElement		
		compute();
		n.setLabel(label);
	}
	
	public AlgoIntegralFunctions(GeoFunction f, GeoFunction g,
			MyDouble a, MyDouble b, GeoBoolean evaluate) {
		super(f.getConstruction(), false);
		this.f = f;
		this.g = g;		
		this.a = a;
		this.b = b;
		this.evaluate = evaluate;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntegralFunctions;
	}
	
    public AlgoIntegralFunctions copy(){
    	return new AlgoIntegralFunctions((GeoFunction)f.copy(),
    			(GeoFunction)g.copy(), 
    			new MyDouble(kernel,a.getDouble()), 
    			new MyDouble(kernel,b.getDouble()), 
    			(GeoBoolean)evaluate.copy());

    }

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if(evaluate == null){
		input = new GeoElement[4];
		input[0] = f;
		input[1] = g;
		input[2] = ageo;
		input[3] = bgeo;
		}
		else
		{
		input = new GeoElement[5];
		input[0] = f;
		input[1] = g;
		input[2] = ageo;
		input[3] = bgeo;
		input[4] = evaluate;
		}
		setOutputLength(1);
		setOutput(0,n);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getIntegral() {
		return n;
	}
	
	public GeoFunction getF() {
		return f;
	}
	
	public GeoFunction getG() {
		return g;
	}	
	
	public NumberValue getA() {
		return a;
	}
	
	public NumberValue getB() {
		return b;
	}
	
	@Override
	public final void compute() {	
		if (!f.isDefined() || !g.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
			n.setUndefined();
			return;
		}
		
		 // return if it should not be evaluated (i.e. is shade-only)
        if(evaluate !=null && !evaluate.getBoolean()){
        	n.setValue(0);
        	return;
        }
		
		
		// Integral[f(x) - g(x), a, b] = Integral[f(x), a, b] - Integral[g(x), a, b]
		n.setValue(intF.getValue() - intG.getValue());		
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability
	

}
