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

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.ExpressionNode;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentText extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoText text;     // output              
        
    public AlgoDependentText(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;  
        
       text = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }   
    
    public AlgoDependentText(Construction cons, ExpressionNode root) {
    	super(cons);
        this.root = root;  
        
       text = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();
    }   
    
	public String getClassName() {
		return "AlgoDependentText";
	}
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TEXT;
    }
	
    public ExpressionNode getRoot(){
    	return root;
    }
    
    
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {	
    	
    	text.setTemporaryPrintAccuracy();
    	
    	try {    	
	    	boolean latex = text.isLaTeX();
	    	root.setHoldsLaTeXtext(latex);
	    	
	    	String str;
	    	if (latex) {
	    		str = root.evaluate().toLaTeXString(false);
	    	} else {
	    		str = root.evaluate().toValueString();
	    	}
	    	
	        text.setTextString(str);	        
	    } catch (Exception e) {
	    	text.setUndefined();
	    }
	    
	    text.restorePrintAccuracy();
    }   
    
    final public String toString() {
        // was defined as e.g.  text0 = "Radius: " + r
        return root.toString();
    }
    
    final public String toRealString() {
        // was defined as e.g.  text0 = "Radius: " + r
        return root.toRealString();
    }
}
