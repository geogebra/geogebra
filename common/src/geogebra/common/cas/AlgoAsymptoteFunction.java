/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Find asymptotes
 * 
 * @author Michael Borcherds
 */
public class AlgoAsymptoteFunction extends AlgoElement {

	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    /**
     * Asymptotes for function
     * @param cons construction
     * @param label label for output
     * @param f function
     */
    public AlgoAsymptoteFunction(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);    	
		g.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
	
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    @Override
	public Commands getClassName() {
        return Commands.Asymptote;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        setOutputLength(1);
        setOutput(0,g);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return list of asymptotes
     */
    public GeoList getResult() {
        return g;
    }

    @Override
	public final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
        
	    try {
		    sb.setLength(0);
		    sb.append("{");
			f.getHorizontalPositiveAsymptote(f, sb);
			f.getHorizontalNegativeAsymptote(f, sb);
		    
			f.getDiagonalPositiveAsymptote(f, sb);
			f.getDiagonalNegativeAsymptote(f, sb);
			
	    	f.getVerticalAsymptotes(f, sb, false);
	
		    sb.append("}");
			
		    //Application.debug(sb.toString());
			g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));	
	    }
	    catch (Throwable th) {
	    	g.setUndefined();
	    }		
    } 
    
    @Override
	final public String toString(StringTemplate tpl) {
    	return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
 

}
