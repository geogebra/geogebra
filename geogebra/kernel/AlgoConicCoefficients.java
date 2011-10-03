/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoConicCoefficients extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoList g; // output        
    
    public AlgoConicCoefficients(Construction cons, String label, GeoConic c) {
    	super(cons);
        this.c = c;        	
    	
        g = new GeoList(cons);         
        g.add(new GeoNumeric(cons, c.matrix[0]));
        g.add(new GeoNumeric(cons, c.matrix[1]));
        g.add(new GeoNumeric(cons, c.matrix[2]));
        g.add(new GeoNumeric(cons, c.matrix[3] * 2));
        g.add(new GeoNumeric(cons, c.matrix[4] * 2));
        g.add(new GeoNumeric(cons, c.matrix[5] * 2));

        setInputOutput(); // for AlgoElement        
        //compute();
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoConicCoefficients";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
        if (!c.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        
        ((GeoNumeric)g.get(0)).setValue(c.matrix[0]);
        ((GeoNumeric)g.get(1)).setValue(c.matrix[1]);
        ((GeoNumeric)g.get(2)).setValue(c.matrix[2]);
        ((GeoNumeric)g.get(3)).setValue(c.matrix[3] * 2);
        ((GeoNumeric)g.get(4)).setValue(c.matrix[4] * 2);
        ((GeoNumeric)g.get(5)).setValue(c.matrix[5] * 2);

        
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
