/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
/**
 * Find asymptotes
 * 
 * @author Michael Borcherds
 */
public class AlgoAsymptoteFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoAsymptoteFunction(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);    	
		g.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
	
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoAsymptoteFunction";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        setOutputLength(1);
        setOutput(0,g);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
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
    
    
    final public String toString() {
    	return getCommandDescription();
    }
 

}
