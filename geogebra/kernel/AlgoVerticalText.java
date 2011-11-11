/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public class AlgoVerticalText extends AlgoElement {

	private GeoText text; //output	
    private GeoText args; //input	
    
    private StringBuffer sb = new StringBuffer();
    
    AlgoVerticalText(Construction cons, String label, GeoText args) {
    	this(cons,  args);
        text.setLabel(label);
    }

    AlgoVerticalText(Construction cons, GeoText args) {
        super(cons);
        this.args = args;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();    
    }

    @Override
	public String getClassName() {
        return "AlgoVerticalText";
    }

    @Override
	protected void setInputOutput(){
	    input = new GeoElement[1];
	    input[0] = args;

        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

    @Override
	protected final void compute() {
    	if (!args.isDefined()) {
    		text.setTextString("");
    		return;
    	}
    	
    	sb.setLength(0);
    	sb.append("\\rotatebox{90}{");
    	sb.append(args.getTextString());
    	sb.append("}");

    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }

}
