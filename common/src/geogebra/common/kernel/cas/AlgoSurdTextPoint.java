/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.cas;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;

/**
 * Algorithm for SurdText(Point)
 *
 */
public class AlgoSurdTextPoint extends AlgoSurdText {

	private GeoPoint p; //input
    private GeoText text; //output	
    private StringBuilder sbp;
    
    /**
     * @param cons construction
     * @param label label for output
     * @param p input point
     */
    public AlgoSurdTextPoint(Construction cons, String label, GeoPoint p) {
    	this(cons, p);
        text.setLabel(label);
    }
    /**
     * @param cons construction
     * @param p input point
     */
    AlgoSurdTextPoint(Construction cons, GeoPoint p) {
        super(cons);
        this.p = p;
        sbp=new StringBuilder(50);       
        text = new GeoText(cons);
		text.setLaTeX(true, false);
		text.setIsTextCommand(true); // stop editing as text
		try {
			text.setStartPoint(p, 0);
		} catch (CircularDefinitionException e) {
			//should never happen
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
    	StringTemplate tpl = StringTemplate.maxPrecision;
		if (input[0].isDefined()) {
			
			sbp.setLength(0);
			sbp.append(" \\left( ");
			PSLQappendQuadratic(sbp, p.inhomX,tpl);
			sbp.append(" , ");
			PSLQappendQuadratic(sbp, p.inhomY,tpl);
			sbp.append(" \\right) ");
			
			text.setTextString(sbp.toString());
			text.setLaTeX(true, false);
			
		} else {
			text.setUndefined();
		}			
	}
    
	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

}
