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

import geogebra.kernel.arithmetic.ExpressionNode;

import java.util.ArrayList;


/**
 * Returns a description of a GeoElement as a GeoText in LaTeX format.
 * @author  Markus
 * @version 
 */
public class AlgoLaTeX extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // input
	private GeoBoolean substituteVars; 
	private GeoBoolean showName; 
	private GeoText text;     // output              

	public AlgoLaTeX(Construction cons, String label, GeoElement geo, GeoBoolean substituteVars, GeoBoolean showName ) {
		super(cons);
		this.geo = geo;  
		this.substituteVars = substituteVars;
		this.showName = showName;
		text = new GeoText(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();      
		text.setLabel(label);
		
		// set sans-serif LaTeX default
		text.setSerifFont(false);
	}   

	public AlgoLaTeX(Construction cons, String label, GeoElement geo) {
		super(cons);
		this.geo = geo;  
		this.substituteVars = null;
		this.showName = null;
		text = new GeoText(cons);

		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();      
		text.setLabel(label);

		// set sans-serif LaTeX default
		text.setSerifFont(false);
	}   
    
	public String getClassName() {
		return "AlgoLaTeX";
	}
    
    // for AlgoElement
	protected void setInputOutput() {

		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		geos.add(geo);
		if (substituteVars != null) 
			geos.add(substituteVars);
		if (showName != null) 
			geos.add(showName);	

		input = new GeoElement[geos.size()];
		for(int i=0; i<input.length; i++)
			input[i] = geos.get(i);


        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {  
    	
    	boolean useLaTeX = true;
		
    	if (!geo.isDefined() 
				|| (substituteVars != null && !substituteVars.isDefined())
				|| showName != null && !showName.isDefined()) {
    		text.setTextString("");
    		
    		
		} else {
    		boolean substitute = substituteVars == null ? true : substituteVars.getBoolean();
    		boolean show = showName == null ? false : showName.getBoolean();
    		
    		text.setTemporaryPrintAccuracy();
    		
    		//Application.debug(geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, substitute ));
    		if(show){
    			text.setTextString(geo.getLaTeXAlgebraDescription(substitute));
    			if(text.getTextString() == null){
    				text.setTextString(geo.getAlgebraDescriptionTextOrHTML());
    				useLaTeX = false;
    			}
    		}else{
    			if (geo.isGeoText()) {
    				// needed for eg Text commands eg FormulaText[Text[
    				text.setTextString(((GeoText)geo).getTextString());
    			} else {
    				text.setTextString(geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, substitute ));   
    			}
    		}

    		text.restorePrintAccuracy();
		}
		
    	
    	text.setLaTeX(useLaTeX, false);
    	
    	/*
    	int tempCASPrintForm = kernel.getCASPrintForm();
    	kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_LATEX);
    	text.setTextString(geo.getCommandDescription());	    	
    	kernel.setCASPrintForm(tempCASPrintForm);*/
    }         
}
