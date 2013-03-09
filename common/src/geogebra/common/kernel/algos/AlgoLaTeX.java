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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoCasCell;
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
		
		text.setFormulaType(app.getPreferredFormulaRenderingType());
		text.setLaTeX(true, false);
		
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

		text.setFormulaType(app.getPreferredFormulaRenderingType());
		text.setLaTeX(true, false);

		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();      
		text.setLabel(label);

		// set sans-serif LaTeX default
		text.setSerifFont(false);
	}   
    
	@Override
	public Commands getClassName() {
		return Commands.LaTeX;
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
		if(geo.isGeoText())
			((GeoText)geo).addTextDescendant(text);
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
    	
    	// whether to use a formula renderer
    	boolean useLaTeX = true;
    	
    	// LaTeX or MathML
    	StringType formulaRendererType = app.getPreferredFormulaRenderingType();
		
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
    		
     		StringTemplate tpl = text.getStringTemplate().deriveReal();
    		//Application.debug(geo.getFormulaString(StringType.LATEX, substitute ));
    		if (show){
    			if (geo.isGeoCasCell()){
    				text.setTextString(((GeoCasCell)geo).getOutput(StringTemplate.numericLatex));	
    				formulaRendererType = StringType.LATEX;
    			} else {
    				text.setTextString(geo.getLaTeXAlgebraDescription(substitute,tpl));
    			}
    			if (text.getTextString() == null){
    				String desc = geo.getAlgebraDescription(text.getStringTemplate());
    				if(geo.hasIndexLabel())
    					desc = GeoElement.indicesToHTML(desc, true);
    				text.setTextString(desc);
    				useLaTeX = false;
    			}
    		} else {
    			if (geo.isGeoText()) {
    				// needed for eg Text commands eg FormulaText[Text[
    				text.setTextString(((GeoText)geo).getTextString());
    				formulaRendererType = StringType.LATEX;
    			} else if (geo.isGeoCasCell()){
    				text.setTextString(((GeoCasCell)geo).getOutput(StringTemplate.numericLatex));	
    				formulaRendererType = StringType.LATEX;
    			} else {
    				text.setTextString(geo.getFormulaString(tpl, substitute ));   
    			}
    		}


		}	
 		
 		text.setFormulaType(formulaRendererType);
    	
    	text.setLaTeX(useLaTeX, false);

    }         
    
	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
