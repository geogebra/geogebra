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
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.AbstractApplication;

public class AlgoSurdTextPoint extends AlgoSurdText {

	private GeoPoint2 p; //input
    private GeoText text; //output	
    
    public AlgoSurdTextPoint(Construction cons, String label, GeoPoint2 p) {
    	this(cons, p);
        text.setLabel(label);
    }

    AlgoSurdTextPoint(Construction cons, GeoPoint2 p) {
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
	public Algos getClassName() {
        return Algos.AlgoSurdTextPoint;
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
			AbstractApplication.debug(sb.toString());
			PSLQappend(sb, p.inhomX);
			AbstractApplication.debug(sb.toString());
			sb.append(" , ");
			AbstractApplication.debug(sb.toString());
			PSLQappend(sb, p.inhomY);
			AbstractApplication.debug(sb.toString());
			sb.append(" \\right) ");
			
			text.setTextString(sb.toString());
			text.setLaTeX(true, false);
			
		} else {
			text.setUndefined();
		}			
	}
    
}
