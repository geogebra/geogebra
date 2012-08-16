/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Random number on file load, but fixed after that
 * @author  Michael
 * @version 
 */
public class AlgoRandomFixed extends AlgoElement {

	protected NumberValue a, b;  // input
	protected GeoNumeric num;     // output         

	double random, aLast = Double.NaN, bLast = Double.NaN;

	public AlgoRandomFixed(Construction cons, String label, NumberValue a, NumberValue b) {       
		this(cons, a, b);
		num.setLabel(label);
	}   

	protected AlgoRandomFixed(Construction cons, NumberValue a, NumberValue b) {       
		super(cons); 
		this.a = a;
		this.b = b;
		num = new GeoNumeric(cons); 
		setInputOutput();

		compute();
	}   

	@Override
	public Algos getClassName() {
		return Algos.AlgoRandomFixed;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input =  new GeoElement[3];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = new GeoBoolean(cons, true); // dummy

		super.setOutputLength(1);
		super.setOutput(0, num);
		setDependencies(); // done by AlgoElement
	}    

	public GeoNumeric getResult() { return num; }


	@Override
	public void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			if (a.getDouble() != aLast || b.getDouble() != bLast) {
				
				// change random number only if a or b has changed
				aLast = a.getDouble();
				bLast = b.getDouble();
				random = cons.getApplication().getRandomIntegerBetween(a.getDouble(), b.getDouble());
				num.setValue(random);

			} else {
				// keep same value as before
				num.setValue(random);
			}
		} else {
			num.setUndefined();
		}

	}        

	// TODO Consider locusequability

}



