/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.Unicode;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Michael
 * @version 
 */
public class AlgoSimplifyText extends AlgoElement {

	private GeoText textIn;  // input
    private GeoText text;     // output              
        
    public AlgoSimplifyText(Construction cons, String label, GeoText textIn) {
    	super(cons);
        this.textIn = textIn;  

       text = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }     
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoSimplifyText;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
		
		input = new GeoElement[1];
		input[0] = textIn;				
                
        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {    
    	
    	// eg Simplify["+1x++x--x+-1x-+1x++x"]
    	

    	String ret = textIn.getTextString();
    	// ++ -> +
    	ret = ret.replaceAll("\\+\\+", "+");
    	// -- -> +
    	ret = ret.replaceAll("--", "+");
    	// +- -> -
    	ret = ret.replaceAll("\\+-", "-");
    	// -+ -> -
    	ret = ret.replaceAll("-\\+", "-");
    	// +1x -> +x
    	ret = ret.replaceAll("\\+1x", "+x");
    	// -1x -> -x
    	ret = ret.replaceAll("-1x", "-x");
    	
    	// replace "+" with " + "
    	// needs to be called twice for eg x+x+x+x
     	ret = ret.replaceAll("([^ ])\\+([^ ])","$1 + $2");
     	ret = ret.replaceAll("([^ ])\\+([^ ])","$1 + $2");
     	
    	// replace "-" with " - "
    	// needs to be called twice for eg x-x-x-x
     	ret = ret.replaceAll("([^ ])\\-([^ ])","$1 - $2");
     	ret = ret.replaceAll("([^ ])\\-([^ ])","$1 - $2");
   	
    	// replace "=" with " = "
    	// needs to be called twice for eg x=x=x=x
     	ret = ret.replaceAll("([^ ])\\=([^ ])","$1 = $2");
     	ret = ret.replaceAll("([^ ])\\=([^ ])","$1 = $2");
   	
     	// remove + and 1 at the start
    	if (ret.charAt(0)== '+') ret = ret.substring(1);
    	if (ret.startsWith("1x")) ret = ret.substring(1);
    	
    	// replace "-" with unicode minus
    	ret = ret.replaceAll(" -", " "+Unicode.minus);
    	
    	text.setTextString(ret); 	
    }         

	// TODO Consider locusequability
}
