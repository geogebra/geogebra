/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;
import geogebra.util.Unicode;
import org.apache.commons.math.util.MathUtils;
import org.mathpiper.builtin.functions.core.Gcd;

public class AlgoSurdTextPoint extends AlgoSurdText {

	private static final long serialVersionUID = 1L;
	private GeoPoint p; //input
    private GeoText text; //output	
    
    AlgoSurdTextPoint(Construction cons, String label, GeoPoint p) {
    	this(cons, p);
        text.setLabel(label);
    }

    AlgoSurdTextPoint(Construction cons, GeoPoint p) {
        super(cons);
        this.p = p;
               
        text = new GeoText(cons);
		text.setLaTeX(true, false);
		text.setIsTextCommand(true); // stop editing as text
		try {
			text.setStartPoint(p, 0);
		} catch (CircularDefinitionException e) {
		}
		
        setInputOutput();
        compute();
    }

    public String getClassName() {
        return "AlgoSurdTextPoint";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = p;

        setOutputLength(1);
        setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

    protected final void compute() {
    	
    	
		if (input[0].isDefined()) {
			
			sb.setLength(0);
			sb.append(" \\left( ");
			Application.debug(sb.toString());
			PSLQappend(sb, p.inhomX);
			Application.debug(sb.toString());
			sb.append(" , ");
			Application.debug(sb.toString());
			PSLQappend(sb, p.inhomY);
			Application.debug(sb.toString());
			sb.append(" \\right) ");
			
			text.setTextString(sb.toString());
			text.setLaTeX(true, false);
			
		} else {
			text.setUndefined();
		}
			
	}
    
}
