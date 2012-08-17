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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;


/**
 * Algorithm for vertical text
 * @author Michael
 */
public class AlgoVerticalText extends AlgoElement {

	private GeoText text; //output	
    private GeoText args; //input	
    
    private StringBuilder sb = new StringBuilder();
    
    /**
     * Creates new algo for vertical text
     * @param cons construction
     * @param label label for output
     * @param args input text
     */
    public AlgoVerticalText(Construction cons, String label, GeoText args) {
    	this(cons,  args);
        text.setLabel(label);
    }

    /**
     * Creates new unlabeled algo for vertical text
     * @param cons construction
     * @param args input text
     */
    AlgoVerticalText(Construction cons, GeoText args) {
        super(cons);
        this.args = args;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();    
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoVerticalText;
    }

    @Override
	protected void setInputOutput(){
	    input = new GeoElement[1];
	    input[0] = args;

        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return resulting text
     */
    public GeoText getResult() {
        return text;
    }

    @Override
	public final void compute() {
    	if (!args.isDefined()) {
    		text.setTextString("");
    		return;
    	}
    	
    	sb.setLength(0);
    	AlgoRotateText.appendRotatedText(sb, args, 90);
    	
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
