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
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;

import java.util.ArrayList;


/**
 * Returns a description of a GeoElement as a GeoText in LaTeX format.
 * @author  Markus
 * @version 
 */
public class AlgoLaTeX extends AlgoElement {

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
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoLaTeX;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		geos.add(geo);
		if (substituteVars != null) 
			geos.add(substituteVars);
		if (showName != null) 
			geos.add(showName);	

		input = new GeoElement[geos.size()];
		for(int i=0; i<input.length; i++) {
			input[i] = geos.get(i);
		}

        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {  
    	
    	boolean useLaTeX = true;
		
    	if (!geo.isDefined() 
				|| (substituteVars != null && !substituteVars.isDefined())
				|| showName != null && !showName.isDefined()) {
    		text.setTextString("");		
    		
		} else {
    		boolean substitute = substituteVars == null ? true : substituteVars.getBoolean();
    		boolean show = showName == null ? false : showName.getBoolean();
    		
    		if (!geo.isLabelSet()) {
    			// eg FormulaText[(1,1), true, true]
    			show = false;
    		}
    		
    		text.setLaTeX(true, false);
    		
    		//Application.debug(geo.getFormulaString(StringType.LATEX, substitute ));
    		if(show){
    			text.setTextString(geo.getLaTeXAlgebraDescription(substitute,text.getStringTemplate()));
    			if(text.getTextString() == null){
    				String desc = geo.getAlgebraDescription(text.getStringTemplate());
    				if(geo.hasIndexLabel())
    					desc = GeoElement.indicesToHTML(desc, true);
    				text.setTextString(desc);
    				useLaTeX = false;
    			}
    		}else{
    			if (geo.isGeoText()) {
    				// needed for eg Text commands eg FormulaText[Text[
    				text.setTextString(((GeoText)geo).getTextString());
    			} else {
    				text.setTextString(geo.getFormulaString(text.getStringTemplate(), substitute ));   
    			}
    		}

    		
		}	
    	
    	text.setLaTeX(useLaTeX, false);
    	
    	/*
    	int tempCASPrintForm = kernel.getCASPrintForm();
    	kernel.setCASPrintForm(StringType.LATEX);
    	text.setTextString(geo.getCommandDescription());	    	
    	kernel.setCASPrintForm(tempCASPrintForm);*/
    }         
    
	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
