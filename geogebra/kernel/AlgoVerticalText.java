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

	private static final long serialVersionUID = 1L;
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

    public String getClassName() {
        return "AlgoVerticalText";
    }

    protected void setInputOutput(){
	    input = new GeoElement[1];
	    input[0] = args;


        output = new GeoElement[1];
        output[0] = text;
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

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
