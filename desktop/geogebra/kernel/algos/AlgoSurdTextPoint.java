/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.main.Application;

public class AlgoSurdTextPoint extends AlgoSurdText {

	private GeoPoint p; //input
    private GeoText text; //output	
    
    public AlgoSurdTextPoint(Construction cons, String label, GeoPoint p) {
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

    @Override
	public String getClassName() {
        return "AlgoSurdTextPoint";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = p;

        setOutputLength(1);
        setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    @Override
	public
	GeoText getResult() {
        return text;
    }

    @Override
	public final void compute() {
    	  	
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
